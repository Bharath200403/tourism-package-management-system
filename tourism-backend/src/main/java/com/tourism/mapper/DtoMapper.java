package com.tourism.mapper;

import com.tourism.dto.response.*;
import com.tourism.entity.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Central place for entity -> response DTO conversion. Keeping this in one
 * class (rather than one mapper per entity) is a deliberate scope trade-off
 * for this project; entities are never returned directly from controllers.
 */
public final class DtoMapper {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private DtoMapper() {
    }

    public static UserResponseDTO toUserDTO(User u) {
        return UserResponseDTO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .phone(u.getPhone())
                .role(u.getRole().name())
                .status(u.getStatus().name())
                .createdAt(u.getCreatedAt() != null ? u.getCreatedAt().format(DATE_FMT) : null)
                .build();
    }

    public static DestinationResponseDTO toDestinationDTO(Destination d) {
        return DestinationResponseDTO.builder()
                .id(d.getId())
                .name(d.getName())
                .state(d.getState())
                .country(d.getCountry())
                .description(d.getDescription())
                .bestSeason(d.getBestSeason())
                .estimatedDuration(d.getEstimatedDuration())
                .status(d.getStatus().name())
                .build();
    }

    public static ItineraryResponseDTO toItineraryDTO(Itinerary i) {
        return ItineraryResponseDTO.builder()
                .id(i.getId())
                .dayNumber(i.getDayNumber())
                .title(i.getTitle())
                .description(i.getDescription())
                .activities(i.getActivities())
                .displayOrder(i.getDisplayOrder())
                .build();
    }

    public static PackageScheduleResponseDTO toScheduleDTO(PackageSchedule s) {
        return PackageScheduleResponseDTO.builder()
                .id(s.getId())
                .packageId(s.getTourPackage().getId())
                .startDate(s.getStartDate())
                .endDate(s.getEndDate())
                .capacity(s.getCapacity())
                .availableSeats(s.getAvailableSeats())
                .status(s.getStatus().name())
                .build();
    }

    public static TourPackageResponseDTO toPackageDTO(TourPackage p, Double avgRating, Long reviewCount) {
        List<String> inclusions = p.getInclusions() == null ? List.of() :
                p.getInclusions().stream().map(PackageInclusion::getDescription).collect(Collectors.toList());
        List<String> exclusions = p.getExclusions() == null ? List.of() :
                p.getExclusions().stream().map(PackageExclusion::getDescription).collect(Collectors.toList());
        List<ItineraryResponseDTO> itinerary = p.getItineraries() == null ? List.of() :
                p.getItineraries().stream()
                        .sorted((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()))
                        .map(DtoMapper::toItineraryDTO).collect(Collectors.toList());
        List<PackageScheduleResponseDTO> schedules = p.getSchedules() == null ? List.of() :
                p.getSchedules().stream().map(DtoMapper::toScheduleDTO).collect(Collectors.toList());

        return TourPackageResponseDTO.builder()
                .id(p.getId())
                .packageCode(p.getPackageCode())
                .name(p.getName())
                .destinationId(p.getDestination().getId())
                .destinationName(p.getDestination().getName())
                .description(p.getDescription())
                .durationDays(p.getDurationDays())
                .basePrice(p.getBasePrice())
                .travelType(p.getTravelType())
                .packageType(p.getPackageType())
                .status(p.getStatus().name())
                .inclusions(inclusions)
                .exclusions(exclusions)
                .itinerary(itinerary)
                .schedules(schedules)
                .averageRating(avgRating)
                .reviewCount(reviewCount)
                .build();
    }

    public static TravelerResponseDTO toTravelerDTO(Traveler t) {
        return TravelerResponseDTO.builder()
                .id(t.getId())
                .fullName(t.getFullName())
                .age(t.getAge())
                .gender(t.getGender())
                .contact(t.getContact())
                .specialRequirement(t.getSpecialRequirement())
                .build();
    }

    public static BookingResponseDTO toBookingDTO(Booking b, PaymentResponseDTO paymentDTO) {
        List<TravelerResponseDTO> travelers = b.getTravelers() == null ? List.of() :
                b.getTravelers().stream().map(DtoMapper::toTravelerDTO).collect(Collectors.toList());

        return BookingResponseDTO.builder()
                .id(b.getId())
                .bookingReference(b.getBookingReference())
                .customerId(b.getCustomer().getId())
                .customerName(b.getCustomer().getFullName())
                .scheduleId(b.getSchedule().getId())
                .packageId(b.getSchedule().getTourPackage().getId())
                .packageName(b.getSchedule().getTourPackage().getName())
                .scheduleStartDate(b.getSchedule().getStartDate())
                .scheduleEndDate(b.getSchedule().getEndDate())
                .travelerCount(b.getTravelerCount())
                .totalAmount(b.getTotalAmount())
                .bookingStatus(b.getBookingStatus().name())
                .bookingDate(b.getBookingDate())
                .travelers(travelers)
                .payment(paymentDTO)
                .build();
    }

    public static PaymentResponseDTO toPaymentDTO(Payment p) {
        return PaymentResponseDTO.builder()
                .id(p.getId())
                .bookingId(p.getBooking().getId())
                .transactionReference(p.getTransactionReference())
                .paymentMode(p.getPaymentMode().name())
                .paymentStatus(p.getPaymentStatus().name())
                .amount(p.getAmount())
                .paidAt(p.getPaidAt())
                .note("This is a local payment simulation. No real money moves and no real payment credentials are stored.")
                .build();
    }

    public static InvoiceResponseDTO toInvoiceDTO(Invoice inv, String paymentStatus) {
        Booking b = inv.getBooking();
        return InvoiceResponseDTO.builder()
                .id(inv.getId())
                .invoiceNumber(inv.getInvoiceNumber())
                .bookingReference(b.getBookingReference())
                .customerName(b.getCustomer().getFullName())
                .packageName(b.getSchedule().getTourPackage().getName())
                .scheduleStartDate(b.getSchedule().getStartDate())
                .travelerCount(b.getTravelerCount())
                .subtotal(inv.getSubtotal())
                .discount(inv.getDiscount())
                .charges(inv.getCharges())
                .total(inv.getTotal())
                .paymentStatus(paymentStatus)
                .invoiceDate(inv.getInvoiceDate())
                .build();
    }

    public static CancellationResponseDTO toCancellationDTO(Cancellation c) {
        return CancellationResponseDTO.builder()
                .id(c.getId())
                .bookingId(c.getBooking().getId())
                .cancellationReason(c.getCancellationReason())
                .refundStatus(c.getRefundStatus().name())
                .refundAmount(c.getRefundAmount())
                .cancelledAt(c.getCancelledAt())
                .build();
    }

    public static ReviewResponseDTO toReviewDTO(Review r) {
        return ReviewResponseDTO.builder()
                .id(r.getId())
                .packageId(r.getTourPackage().getId())
                .customerName(r.getCustomer().getFullName())
                .rating(r.getRating())
                .reviewText(r.getReviewText())
                .status(r.getStatus().name())
                .createdAt(r.getCreatedAt())
                .build();
    }

    public static FeedbackResponseDTO toFeedbackDTO(Feedback f) {
        return FeedbackResponseDTO.builder()
                .id(f.getId())
                .customerName(f.getCustomer().getFullName())
                .subject(f.getSubject())
                .message(f.getMessage())
                .createdAt(f.getCreatedAt())
                .build();
    }

    public static SupportTicketResponseDTO toTicketDTO(SupportTicket t) {
        return SupportTicketResponseDTO.builder()
                .id(t.getId())
                .customerName(t.getCustomer().getFullName())
                .subject(t.getSubject())
                .description(t.getDescription())
                .status(t.getStatus().name())
                .resolutionNotes(t.getResolutionNotes())
                .createdAt(t.getCreatedAt())
                .build();
    }

    public static NotificationResponseDTO toNotificationDTO(Notification n) {
        return NotificationResponseDTO.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }

    public static AuditLogResponseDTO toAuditDTO(AuditLog a) {
        return AuditLogResponseDTO.builder()
                .id(a.getId())
                .actorUsername(a.getActorUsername())
                .action(a.getAction())
                .entityType(a.getEntityType())
                .entityId(a.getEntityId())
                .details(a.getDetails())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
