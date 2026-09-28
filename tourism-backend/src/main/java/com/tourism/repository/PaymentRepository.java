package com.tourism.repository;

import com.tourism.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);
    boolean existsByTransactionReference(String reference);
    java.util.List<Payment> findByPaymentStatus(com.tourism.entity.enums.PaymentStatus status);
}
