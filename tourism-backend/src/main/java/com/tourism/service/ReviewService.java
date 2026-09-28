package com.tourism.service;

import com.tourism.dto.request.ReviewRequest;
import com.tourism.dto.response.PageResponseDTO;
import com.tourism.dto.response.ReviewResponseDTO;
import com.tourism.entity.Booking;
import com.tourism.entity.Review;
import com.tourism.entity.User;
import com.tourism.entity.enums.BookingStatus;
import com.tourism.entity.enums.ReviewStatus;
import com.tourism.exception.BadRequestException;
import com.tourism.exception.ConflictException;
import com.tourism.exception.ForbiddenException;
import com.tourism.mapper.DtoMapper;
import com.tourism.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingService bookingService;
    private final AuditService auditService;

    @Transactional
    public ReviewResponseDTO submit(User customer, ReviewRequest request) {
        Booking booking = bookingService.getOrThrow(request.getBookingId());

        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new ForbiddenException("You can only review your own bookings.");
        }
        if (booking.getBookingStatus() != BookingStatus.COMPLETED) {
            throw new BadRequestException("Only completed bookings can be reviewed.");
        }
        if (reviewRepository.existsByBookingId(booking.getId())) {
            throw new ConflictException("DUPLICATE_REVIEW", "A review already exists for this booking.");
        }

        Review review = new Review();
        review.setBooking(booking);
        review.setCustomer(customer);
        review.setTourPackage(booking.getSchedule().getTourPackage());
        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText());
        review.setStatus(ReviewStatus.APPROVED);
        reviewRepository.save(review);

        auditService.log("REVIEW_SUBMITTED", "Review", String.valueOf(review.getId()),
                "Package " + review.getTourPackage().getId() + ", rating " + review.getRating());
        return DtoMapper.toReviewDTO(review);
    }

    public PageResponseDTO<ReviewResponseDTO> forPackage(Long packageId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByTourPackageIdAndStatus(packageId, ReviewStatus.APPROVED, pageable);
        return PageResponseDTO.of(page.map(DtoMapper::toReviewDTO));
    }

    @Transactional
    public void moderate(Long reviewId, ReviewStatus status) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new com.tourism.exception.ResourceNotFoundException("Review not found: " + reviewId));
        review.setStatus(status);
        reviewRepository.save(review);
        auditService.log("REVIEW_MODERATED", "Review", String.valueOf(reviewId), "New status: " + status);
    }
}
