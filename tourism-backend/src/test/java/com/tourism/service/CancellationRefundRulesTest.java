package com.tourism.service;

import com.tourism.dto.request.CancellationRequest;
import com.tourism.entity.*;
import com.tourism.entity.enums.BookingStatus;
import com.tourism.entity.enums.PaymentStatus;
import com.tourism.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancellationRefundRulesTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CancellationRepository cancellationRepository;
    @Mock
    private PackageScheduleRepository scheduleRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingService bookingService;
    @Mock
    private AuditService auditService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CancellationService cancellationService;

    private Booking booking;
    private PackageSchedule schedule;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cancellationService, "fullRefundDays", 7);
        ReflectionTestUtils.setField(cancellationService, "partialRefundDays", 3);
        ReflectionTestUtils.setField(cancellationService, "partialRefundPercentage", 50);

        TourPackage pkg = new TourPackage();
        pkg.setId(1L);

        schedule = new PackageSchedule();
        schedule.setId(10L);
        schedule.setTourPackage(pkg);
        schedule.setCapacity(30);
        schedule.setAvailableSeats(10);

        User customer = new User();
        customer.setId(100L);

        booking = new Booking();
        booking.setId(1L);
        booking.setBookingReference("BK-TEST-0001");
        booking.setCustomer(customer);
        booking.setSchedule(schedule);
        booking.setTravelerCount(2);
        booking.setTotalAmount(new BigDecimal("10000"));
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        lenient().when(bookingService.getOrThrow(1L)).thenReturn(booking);
        lenient().when(scheduleRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(schedule));
        lenient().when(paymentRepository.findByBookingId(1L)).thenReturn(Optional.empty());
        lenient().when(cancellationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void fullRefundWhenMoreThanThresholdDaysBeforeTrip() {
        schedule.setStartDate(LocalDate.now().plusDays(10));
        schedule.setEndDate(LocalDate.now().plusDays(15));

        var result = cancellationService.cancel(booking.getCustomer(), 1L, new CancellationRequest());

        assertEquals("FULL_REFUND", result.getRefundStatus());
        assertEquals(new BigDecimal("10000"), result.getRefundAmount());
        assertEquals(12, schedule.getAvailableSeats()); // seats restored
    }

    @Test
    void partialRefundBetweenThresholds() {
        schedule.setStartDate(LocalDate.now().plusDays(5));
        schedule.setEndDate(LocalDate.now().plusDays(9));

        var result = cancellationService.cancel(booking.getCustomer(), 1L, new CancellationRequest());

        assertEquals("PARTIAL_REFUND", result.getRefundStatus());
        assertEquals(new BigDecimal("5000.00"), result.getRefundAmount());
    }

    @Test
    void noRefundWhenWithinFinalWindow() {
        schedule.setStartDate(LocalDate.now().plusDays(1));
        schedule.setEndDate(LocalDate.now().plusDays(6));

        var result = cancellationService.cancel(booking.getCustomer(), 1L, new CancellationRequest());

        assertEquals("NO_REFUND", result.getRefundStatus());
        assertEquals(BigDecimal.ZERO.compareTo(result.getRefundAmount()), 0);
    }
}
