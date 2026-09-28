package com.tourism.repository;

import com.tourism.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByBookingId(Long bookingId);
    boolean existsByInvoiceNumber(String invoiceNumber);
}
