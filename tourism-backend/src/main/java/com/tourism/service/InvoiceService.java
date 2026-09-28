package com.tourism.service;

import com.tourism.dto.response.InvoiceResponseDTO;
import com.tourism.entity.Invoice;
import com.tourism.entity.User;
import com.tourism.exception.ResourceNotFoundException;
import com.tourism.mapper.DtoMapper;
import com.tourism.repository.InvoiceRepository;
import com.tourism.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;

    public InvoiceResponseDTO getById(Long invoiceId, User requester) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
        bookingService.assertOwnershipOrStaff(invoice.getBooking(), requester);
        return toDto(invoice);
    }

    public InvoiceResponseDTO getByBooking(Long bookingId, User requester) {
        var booking = bookingService.getOrThrow(bookingId);
        bookingService.assertOwnershipOrStaff(booking, requester);
        Invoice invoice = invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("No invoice has been generated for this booking yet."));
        return toDto(invoice);
    }

    private InvoiceResponseDTO toDto(Invoice invoice) {
        String paymentStatus = paymentRepository.findByBookingId(invoice.getBooking().getId())
                .map(p -> p.getPaymentStatus().name())
                .orElse("PENDING");
        return DtoMapper.toInvoiceDTO(invoice, paymentStatus);
    }
}
