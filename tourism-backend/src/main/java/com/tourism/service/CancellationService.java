package com.tourism.service;

import com.tourism.dto.request.CancellationRequest;
import com.tourism.dto.response.CancellationResponseDTO;
import com.tourism.entity.Booking;
import com.tourism.entity.Cancellation;
import com.tourism.entity.PackageSchedule;
import com.tourism.entity.Payment;
import com.tourism.entity.User;
import com.tourism.entity.enums.BookingStatus;
import com.tourism.entity.enums.CancellationStatus;
import com.tourism.entity.enums.PaymentStatus;
import com.tourism.exception.BadRequestException;
import com.tourism.mapper.DtoMapper;
import com.tourism.repository.BookingRepository;
import com.tourism.repository.CancellationRepository;
import com.tourism.repository.PackageScheduleRepository;
import com.tourism.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Centralizes the cancellation eligibility and refund-percentage rules so
 * they are not duplicated across controllers. Thresholds are configurable
 * via application.properties rather than hard-coded.
 */
@Service
@RequiredArgsConstructor
public class CancellationService {

    private final BookingRepository bookingRepository;
    private final CancellationRepository cancellationRepository;
    private final PackageScheduleRepository scheduleRepository;
    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Value("${app.cancellation.full-refund-days}")
    private int fullRefundDays;

    @Value("${app.cancellation.partial-refund-days}")
    private int partialRefundDays;

    @Value("${app.cancellation.partial-refund-percentage}")
    private int partialRefundPercentage;

    @Transactional
    public CancellationResponseDTO cancel(User requester, Long bookingId, CancellationRequest request) {
        Booking booking = bookingService.getOrThrow(bookingId);
        bookingService.assertOwnershipOrStaff(booking, requester);

        if (!com.tourism.service.BookingStatusRules.isAllowed(booking.getBookingStatus(), BookingStatus.CANCELLED)) {
            throw new BadRequestException("A booking with status " + booking.getBookingStatus() + " cannot be cancelled.");
        }

        long daysBeforeTrip = ChronoUnit.DAYS.between(LocalDate.now(), booking.getSchedule().getStartDate());

        CancellationStatus refundStatus;
        BigDecimal refundAmount;
        if (daysBeforeTrip >= fullRefundDays) {
            refundStatus = CancellationStatus.FULL_REFUND;
            refundAmount = booking.getTotalAmount();
        } else if (daysBeforeTrip >= partialRefundDays) {
            refundStatus = CancellationStatus.PARTIAL_REFUND;
            refundAmount = booking.getTotalAmount()
                    .multiply(BigDecimal.valueOf(partialRefundPercentage))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            refundStatus = CancellationStatus.NO_REFUND;
            refundAmount = BigDecimal.ZERO;
        }

        // Restore availability under a pessimistic lock, mirroring booking creation.
        PackageSchedule schedule = scheduleRepository.findByIdForUpdate(booking.getSchedule().getId())
                .orElseThrow(() -> new BadRequestException("Schedule not found."));
        schedule.setAvailableSeats(Math.min(schedule.getCapacity(), schedule.getAvailableSeats() + booking.getTravelerCount()));
        scheduleRepository.save(schedule);

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        Cancellation cancellation = new Cancellation();
        cancellation.setBooking(booking);
        cancellation.setCancellationReason(request != null ? request.getReason() : null);
        cancellation.setRefundStatus(refundStatus);
        cancellation.setRefundAmount(refundAmount);
        cancellationRepository.save(cancellation);

        paymentRepository.findByBookingId(booking.getId()).ifPresent(payment -> {
            payment.setPaymentStatus(refundAmount.compareTo(BigDecimal.ZERO) > 0 ? PaymentStatus.REFUNDED : payment.getPaymentStatus());
            paymentRepository.save(payment);
        });

        auditService.log("BOOKING_CANCELLED", "Booking", String.valueOf(bookingId),
                "Refund: " + refundStatus + " (" + refundAmount + "), " + daysBeforeTrip + " day(s) before trip");
        notificationService.notify(booking.getCustomer(), "Booking cancelled",
                "Booking " + booking.getBookingReference() + " was cancelled. Refund status: " + refundStatus + ".");

        return DtoMapper.toCancellationDTO(cancellation);
    }
}
