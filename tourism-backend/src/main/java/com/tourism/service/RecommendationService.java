package com.tourism.service;

import com.tourism.dto.request.TripPlannerRequest;
import com.tourism.dto.response.TourPackageResponseDTO;
import com.tourism.entity.Booking;
import com.tourism.entity.CustomerProfile;
import com.tourism.entity.TourPackage;
import com.tourism.entity.enums.PackageStatus;
import com.tourism.mapper.DtoMapper;
import com.tourism.repository.BookingRepository;
import com.tourism.repository.CustomerProfileRepository;
import com.tourism.repository.ReviewRepository;
import com.tourism.repository.TourPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A local, deterministic, rule-based scoring engine. This is NOT machine
 * learning - every point awarded below is a simple, explainable business
 * rule, in line with the requirement not to call this "ML" unless it
 * actually is.
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final TourPackageRepository packageRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    public List<TourPackageResponseDTO> recommendForCustomer(Long customerId, int limit) {
        CustomerProfile profile = customerProfileRepository.findByUserId(customerId).orElse(null);
        List<Booking> pastBookings = bookingRepository.findByCustomerId(customerId, Pageable.unpaged()).getContent();

        Set<Long> pastDestinationIds = pastBookings.stream()
                .map(b -> b.getSchedule().getTourPackage().getDestination().getId())
                .collect(Collectors.toSet());
        Set<Long> alreadyBookedPackageIds = pastBookings.stream()
                .map(b -> b.getSchedule().getTourPackage().getId())
                .collect(Collectors.toSet());

        String preferredTravelType = profile != null ? profile.getPreferredTravelType() : null;
        BigDecimal preferredBudget = profile != null && profile.getPreferredBudget() != null
                ? BigDecimal.valueOf(profile.getPreferredBudget()) : null;

        List<TourPackage> candidates = packageRepository.findAll().stream()
                .filter(p -> p.getStatus() == PackageStatus.ACTIVE)
                .filter(p -> !alreadyBookedPackageIds.contains(p.getId()))
                .toList();

        return candidates.stream()
                .sorted(Comparator.comparingInt((TourPackage p) ->
                        score(p, pastDestinationIds, preferredTravelType, preferredBudget, null, null)).reversed())
                .limit(limit)
                .map(p -> DtoMapper.toPackageDTO(p, averageRating(p.getId()), reviewCount(p.getId())))
                .toList();
    }

    public List<TourPackageResponseDTO> planTrip(TripPlannerRequest request) {
        List<TourPackage> candidates = packageRepository.findAll().stream()
                .filter(p -> p.getStatus() == PackageStatus.ACTIVE)
                .filter(p -> request.getDestinationId() == null || p.getDestination().getId().equals(request.getDestinationId()))
                .toList();

        return candidates.stream()
                .sorted(Comparator.comparingInt((TourPackage p) ->
                        score(p, Set.of(), request.getTravelType(), request.getBudget(), request.getDays(), request.getPreferredActivities())).reversed())
                .limit(10)
                .map(p -> DtoMapper.toPackageDTO(p, averageRating(p.getId()), reviewCount(p.getId())))
                .toList();
    }

    private int score(TourPackage p, Set<Long> pastDestinationIds, String preferredTravelType,
                       BigDecimal budget, Integer days, String preferredActivities) {
        int score = 0;

        if (preferredTravelType != null && preferredTravelType.equalsIgnoreCase(p.getTravelType())) {
            score += 3;
        }
        if (pastDestinationIds.contains(p.getDestination().getId())) {
            score += 2;
        }
        if (budget != null) {
            if (p.getBasePrice().compareTo(budget) <= 0) {
                score += 3;
            } else if (p.getBasePrice().compareTo(budget.multiply(BigDecimal.valueOf(1.15))) <= 0) {
                score += 1; // close enough to budget to still surface it
            }
        }
        if (days != null) {
            int diff = Math.abs(p.getDurationDays() - days);
            if (diff == 0) score += 3;
            else if (diff <= 1) score += 2;
            else if (diff <= 2) score += 1;
        }
        if (preferredActivities != null && !preferredActivities.isBlank() && p.getDescription() != null
                && p.getDescription().toLowerCase().contains(preferredActivities.toLowerCase())) {
            score += 2;
        }
        // Small tie-breaker so well-reviewed packages edge out otherwise-equal ones.
        Double avg = averageRating(p.getId());
        if (avg != null) {
            score += (int) Math.round(avg);
        }
        return score;
    }

    private Double averageRating(Long packageId) {
        var reviews = reviewRepository.findByTourPackageIdAndStatus(packageId,
                com.tourism.entity.enums.ReviewStatus.APPROVED, Pageable.unpaged());
        return reviews.isEmpty() ? null : reviews.getContent().stream()
                .mapToInt(com.tourism.entity.Review::getRating).average().orElse(0);
    }

    private long reviewCount(Long packageId) {
        return reviewRepository.findByTourPackageIdAndStatus(packageId,
                com.tourism.entity.enums.ReviewStatus.APPROVED, Pageable.unpaged()).getTotalElements();
    }
}
