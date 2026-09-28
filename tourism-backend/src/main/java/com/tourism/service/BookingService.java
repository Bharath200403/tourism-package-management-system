package com.tourism.service;

import com.tourism.dto.request.BookingRequest;
import com.tourism.dto.request.BookingStatusUpdateRequest;
import com.tourism.dto.request.TravelerRequest;
import com.tourism.dto.response.BookingResponseDTO;
import com.tourism.dto.response.PageResponseDTO;
import com.tourism.entity.*;
import com.tourism.entity.enums.BookingStatus;
import com.tourism.entity.enums.PackageStatus;
import com.tourism.entity.enums.ScheduleStatus;
import com.tourism.exception.BadRequestException;
import com.tourism.exception.ConflictException;
import com.tourism.exception.ForbiddenException;
import com.tourism.exception.ResourceNotFoundException;
import com.tourism.mapper.DtoMapper;
import com.tourism.repository.*;
import com.tourism.util.ReferenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Owns the booking lifecycle. The seat-reservation step runs inside a single
 * transaction that takes a pessimistic write lock on the target
 * PackageSchedule row (see PackageScheduleRepository#findByIdForUpdate), so
 * that two concurrent requests for the last remaining seats can never both
 * succeed: the second transaction blocks until the first commits (seats
 * reduced) or rolls back, then re-reads the now-current availableSeats.
 */
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PackageScheduleRepository scheduleRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Transactional
    public BookingResponseDTO createBooking(User customer, BookingRequest request) {
        int travelerCount = request.getTravelers().size();
        if (travelerCount <= 0) {
            throw new BadRequestException("At least one traveler is required.");
        }

        // Pessimistic write lock: no other transaction can read/modify this
        // schedule row until this transaction commits or rolls back.
        PackageSchedule schedule = scheduleRepository.findByIdForUpdate(request.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found: " + request.getScheduleId()));

        TourPackage pkg = schedule.getTourPackage();

        if (pkg.getStatus() != PackageStatus.ACTIVE) {
            throw new BadRequestException("This package is not currently bookable.");
        }
        if (schedule.getStatus() != ScheduleStatus.OPEN) {
            throw new BadRequestException("This schedule is not open for booking.");
        }
        if (!schedule.getStartDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("This schedule has already started or expired and cannot be booked.");
        }
        if (travelerCount > schedule.getAvailableSeats()) {
            throw new ConflictException("INSUFFICIENT_AVAILABILITY",
                    "Only " + schedule.getAvailableSeats() + " seat(s) are available.");
        }

        // ---- Backend is the sole source of truth for pricing ----
        BigDecimal totalAmount = pkg.getBasePrice().multiply(BigDecimal.valueOf(travelerCount));

        schedule.setAvailableSeats(schedule.getAvailableSeats() - travelerCount);
        scheduleRepository.save(schedule);

        Booking booking = new Booking();
        booking.setBookingReference(uniqueBookingReference());
        booking.setCustomer(customer);
        booking.setSchedule(schedule);
        booking.setTravelerCount(travelerCount);
        booking.setTotalAmount(totalAmount);
        booking.setBookingStatus(BookingStatus.PENDING);

        for (TravelerRequest tr : request.getTravelers()) {
            Traveler t = new Traveler();
            t.setBooking(booking);
            t.setFullName(tr.getFullName());
            t.setAge(tr.getAge());
            t.setGender(tr.getGender());
            t.setContact(tr.getContact());
            t.setSpecialRequirement(tr.getSpecialRequirement());
            booking.getTravelers().add(t);
        }

        bookingRepository.save(booking);

        auditService.log("BOOKING_CREATED", "Booking", String.valueOf(booking.getId()),
                "Ref " + booking.getBookingReference() + ", " + travelerCount + " traveler(s), amount " + totalAmount);
        notificationService.notify(customer, "Booking received",
                "Your booking " + booking.getBookingReference() + " is pending payment.");

        return DtoMapper.toBookingDTO(booking, null);
    }

    @Transactional
    public BookingResponseDTO changeStatus(Long bookingId, BookingStatusUpdateRequest request) {
        Booking booking = getOrThrow(bookingId);
        BookingStatus newStatus;
        try {
            newStatus = BookingStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Unknown booking status: " + request.getStatus());
        }

        if (!BookingStatusRules.isAllowed(booking.getBookingStatus(), newStatus)) {
            throw new BadRequestException("Cannot change booking status from " + booking.getBookingStatus() + " to " + newStatus + ".");
        }

        // Cancelling here (an operator/admin action, distinct from customer
        // self-service cancellation) must restore availability too.
        if (newStatus == BookingStatus.CANCELLED && booking.getBookingStatus() != BookingStatus.CANCELLED) {
            PackageSchedule schedule = scheduleRepository.findByIdForUpdate(booking.getSchedule().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
            schedule.setAvailableSeats(schedule.getAvailableSeats() + booking.getTravelerCount());
            scheduleRepository.save(schedule);
        }

        booking.setBookingStatus(newStatus);
        bookingRepository.save(booking);
        auditService.log("BOOKING_STATUS_CHANGED", "Booking", String.valueOf(bookingId), "New status: " + newStatus);
        return toDtoWithPayment(booking);
    }

    public BookingResponseDTO getById(Long bookingId, User requester) {
        Booking booking = getOrThrow(bookingId);
        assertOwnershipOrStaff(booking, requester);
        return toDtoWithPayment(booking);
    }

    public BookingResponseDTO getByReference(String reference, User requester) {
        Booking booking = bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + reference));
        assertOwnershipOrStaff(booking, requester);
        return toDtoWithPayment(booking);
    }

    public PageResponseDTO<BookingResponseDTO> myBookings(Long customerId, Pageable pageable) {
        Page<Booking> page = bookingRepository.findByCustomerId(customerId, pageable);
        return PageResponseDTO.of(page.map(this::toDtoWithPayment));
    }

    public List<BookingResponseDTO> forPackage(Long packageId) {
        return bookingRepository.findBySchedule_TourPackage_Id(packageId).stream()
                .map(this::toDtoWithPayment).toList();
    }

    public List<BookingResponseDTO> all() {
        return bookingRepository.findAll().stream().map(this::toDtoWithPayment).toList();
    }

    void assertOwnershipOrStaff(Booking booking, User requester) {
        boolean isOwner = booking.getCustomer().getId().equals(requester.getId());
        boolean isStaff = requester.getRole() == com.tourism.entity.enums.Role.ADMIN
                || requester.getRole() == com.tourism.entity.enums.Role.TOUR_OPERATOR;
        if (!isOwner && !isStaff) {
            throw new ForbiddenException("You do not have access to this booking.");
        }
    }

    private BookingResponseDTO toDtoWithPayment(Booking booking) {
        var payment = paymentRepository.findByBookingId(booking.getId()).map(DtoMapper::toPaymentDTO).orElse(null);
        return DtoMapper.toBookingDTO(booking, payment);
    }

    private String uniqueBookingReference() {
        String ref;
        do {
            ref = ReferenceGenerator.bookingReference();
        } while (bookingRepository.existsByBookingReference(ref));
        return ref;
    }

    Booking getOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
    }
}
