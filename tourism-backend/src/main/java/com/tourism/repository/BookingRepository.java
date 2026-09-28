package com.tourism.repository;

import com.tourism.entity.Booking;
import com.tourism.entity.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String bookingReference);
    Page<Booking> findByCustomerId(Long customerId, Pageable pageable);
    List<Booking> findByCustomerIdAndBookingStatus(Long customerId, BookingStatus status);
    List<Booking> findBySchedule_TourPackage_Id(Long packageId);
    List<Booking> findByBookingStatus(BookingStatus status);
    boolean existsByBookingReference(String reference);
    long countByBookingStatus(BookingStatus status);
}
