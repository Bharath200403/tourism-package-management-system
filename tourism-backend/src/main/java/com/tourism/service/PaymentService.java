package com.tourism.service;

import com.tourism.dto.request.PaymentRequest;
import com.tourism.dto.response.BookingResponseDTO;
import com.tourism.entity.Booking;
import com.tourism.entity.Invoice;
import com.tourism.entity.Payment;
import com.tourism.entity.User;
import com.tourism.entity.enums.BookingStatus;
import com.tourism.entity.enums.PaymentMode;
import com.tourism.entity.enums.PaymentStatus;
import com.tourism.exception.BadRequestException;
import com.tourism.exception.ConflictException;
import com.tourism.mapper.DtoMapper;
import com.tourism.repository.BookingRepository;
import com.tourism.repository.InvoiceRepository;
import com.tourism.repository.PaymentRepository;
import com.tourism.util.ReferenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Local payment simulation only - there is no real payment gateway involved.
 * No card numbers, CVV, PIN or banking passwords are ever accepted or stored.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final BookingService bookingService;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Transactional
    public BookingResponseDTO simulatePayment(User customer, PaymentRequest request) {
        Booking booking = bookingService.getOrThrow(request.getBookingId());
        bookingService.assertOwnershipOrStaff(booking, customer);

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Only a pending booking can be paid for. Current status: " + booking.getBookingStatus());
        }
        if (paymentRepository.findByBookingId(booking.getId()).isPresent()) {
            throw new ConflictException("ALREADY_PAID", "A payment already exists for this booking.");
        }

        PaymentMode mode;
        try {
            mode = PaymentMode.valueOf(request.getPaymentMode().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Unknown payment mode: " + request.getPaymentMode());
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setPaymentMode(mode);
        payment.setAmount(booking.getTotalAmount());
        payment.setTransactionReference(uniqueTransactionReference(mode));
        // Simulation always "succeeds" - this is explicitly a demo, never a
        // real payment gateway integration.
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(java.time.LocalDateTime.now());
        paymentRepository.save(payment);

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(uniqueInvoiceNumber());
        invoice.setBooking(booking);
        invoice.setSubtotal(booking.getTotalAmount());
        invoice.setDiscount(BigDecimal.ZERO);
        invoice.setCharges(BigDecimal.ZERO);
        invoice.setTotal(booking.getTotalAmount());
        invoiceRepository.save(invoice);

        auditService.log("PAYMENT_SIMULATED", "Payment", String.valueOf(payment.getId()),
                "Booking " + booking.getBookingReference() + " paid via " + mode);
        notificationService.notify(booking.getCustomer(), "Booking confirmed",
                "Payment received (simulated) for booking " + booking.getBookingReference() + ". Invoice " + invoice.getInvoiceNumber() + " generated.");

        return DtoMapper.toBookingDTO(booking, DtoMapper.toPaymentDTO(payment));
    }

    private String uniqueTransactionReference(PaymentMode mode) {
        String ref;
        do {
            ref = ReferenceGenerator.transactionReference(mode.name());
        } while (paymentRepository.existsByTransactionReference(ref));
        return ref;
    }

    private String uniqueInvoiceNumber() {
        String ref;
        do {
            ref = ReferenceGenerator.invoiceNumber();
        } while (invoiceRepository.existsByInvoiceNumber(ref));
        return ref;
    }
}
