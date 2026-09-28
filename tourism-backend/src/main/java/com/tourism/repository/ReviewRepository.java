package com.tourism.repository;

import com.tourism.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByTourPackageIdAndStatus(Long packageId, com.tourism.entity.enums.ReviewStatus status, Pageable pageable);
    boolean existsByBookingId(Long bookingId);
}
