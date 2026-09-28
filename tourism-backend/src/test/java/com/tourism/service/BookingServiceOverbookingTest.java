package com.tourism.service;

import com.tourism.dto.request.BookingRequest;
import com.tourism.dto.request.TravelerRequest;
import com.tourism.entity.*;
import com.tourism.entity.enums.PackageStatus;
import com.tourism.entity.enums.ScheduleStatus;
import com.tourism.exception.ConflictException;
import com.tourism.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Verifies the core availability rule: a booking request for more seats than
 * are currently available is rejected with 409 INSUFFICIENT_AVAILABILITY,
 * and the schedule's available_seats is decremented correctly on success.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceOverbookingTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PackageScheduleRepository scheduleRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuditService auditService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BookingService bookingService;

    private PackageSchedule schedule;
    private User customer;

    @BeforeEach
    void setUp() {
        Destination destination = new Destination();
        destination.setId(1L);
        destination.setName("Kerala Backwaters");

        TourPackage pkg = new TourPackage();
        pkg.setId(1L);
        pkg.setName("Kerala Explorer");
        pkg.setDestination(destination);
        pkg.setBasePrice(new BigDecimal("10000"));
        pkg.setStatus(PackageStatus.ACTIVE);

        schedule = new PackageSchedule();
        schedule.setId(10L);
        schedule.setTourPackage(pkg);
        schedule.setStartDate(LocalDate.now().plusDays(30));
        schedule.setEndDate(LocalDate.now().plusDays(35));
        schedule.setCapacity(30);
        schedule.setAvailableSeats(2);
        schedule.setStatus(ScheduleStatus.OPEN);

        customer = new User();
        customer.setId(100L);
        customer.setFullName("Test Customer");

        lenient().when(bookingRepository.existsByBookingReference(any())).thenReturn(false);
        lenient().when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(scheduleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void rejectsBookingWhenRequestedSeatsExceedAvailability() {
        when(scheduleRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(schedule));

        BookingRequest request = new BookingRequest();
        request.setScheduleId(10L);
        request.setTravelers(List.of(traveler("A"), traveler("B"), traveler("C"))); // 3 requested, only 2 available

        ConflictException ex = assertThrows(ConflictException.class,
                () -> bookingService.createBooking(customer, request));

        assertEquals("INSUFFICIENT_AVAILABILITY", ex.getCode());
        // Availability must remain untouched after a rejected request.
        assertEquals(2, schedule.getAvailableSeats());
    }

    @Test
    void acceptsBookingWithinAvailabilityAndDecrementsSeats() {
        when(scheduleRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(schedule));

        BookingRequest request = new BookingRequest();
        request.setScheduleId(10L);
        request.setTravelers(List.of(traveler("A"), traveler("B")));

        var response = bookingService.createBooking(customer, request);

        assertEquals(0, schedule.getAvailableSeats());
        assertEquals(new BigDecimal("20000"), response.getTotalAmount());
        assertEquals("PENDING", response.getBookingStatus());
    }

    private TravelerRequest traveler(String name) {
        TravelerRequest t = new TravelerRequest();
        t.setFullName(name);
        t.setAge(30);
        t.setGender("Other");
        return t;
    }
}
