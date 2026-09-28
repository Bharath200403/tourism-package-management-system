package com.tourism.service;

import com.tourism.dto.request.ItineraryRequest;
import com.tourism.dto.request.TourPackageRequest;
import com.tourism.dto.response.PageResponseDTO;
import com.tourism.dto.response.TourPackageResponseDTO;
import com.tourism.entity.*;
import com.tourism.entity.enums.PackageStatus;
import com.tourism.exception.BadRequestException;
import com.tourism.exception.ConflictException;
import com.tourism.exception.ResourceNotFoundException;
import com.tourism.mapper.DtoMapper;
import com.tourism.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PackageService {

    private final TourPackageRepository packageRepository;
    private final DestinationRepository destinationRepository;
    private final ItineraryRepository itineraryRepository;
    private final PackageInclusionRepository inclusionRepository;
    private final PackageExclusionRepository exclusionRepository;
    private final ReviewRepository reviewRepository;
    private final AuditService auditService;

    public PageResponseDTO<TourPackageResponseDTO> search(String name, Long destinationId, Integer minDuration,
                                                           Integer maxDuration, BigDecimal minPrice, BigDecimal maxPrice,
                                                           PackageStatus status, Pageable pageable) {
        Page<TourPackage> page = packageRepository.search(
                (name == null || name.isBlank()) ? null : name,
                destinationId, minDuration, maxDuration, minPrice, maxPrice, status, pageable);
        Page<TourPackageResponseDTO> dtoPage = page.map(p -> toDtoWithRating(p));
        return PageResponseDTO.of(dtoPage);
    }

    public TourPackageResponseDTO getById(Long id) {
        return toDtoWithRating(getOrThrow(id));
    }

    public List<TourPackageResponseDTO> byOperator(Long operatorId) {
        return packageRepository.findByCreatedByOperatorId(operatorId).stream()
                .map(this::toDtoWithRating).toList();
    }

    @Transactional
    public TourPackageResponseDTO create(TourPackageRequest request, User operator) {
        if (packageRepository.existsByPackageCode(request.getPackageCode())) {
            throw new ConflictException("PACKAGE_CODE_TAKEN", "This package code is already in use.");
        }
        Destination destination = destinationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: " + request.getDestinationId()));

        TourPackage p = new TourPackage();
        p.setDestination(destination);
        p.setCreatedByOperator(operator);
        applyBasics(p, request);
        packageRepository.save(p);

        replaceInclusionsExclusions(p, request.getInclusions(), request.getExclusions());

        auditService.log("PACKAGE_CREATED", "TourPackage", String.valueOf(p.getId()), p.getName());
        return toDtoWithRating(p);
    }

    @Transactional
    public TourPackageResponseDTO update(Long id, TourPackageRequest request) {
        TourPackage p = getOrThrow(id);

        if (!p.getPackageCode().equals(request.getPackageCode()) && packageRepository.existsByPackageCode(request.getPackageCode())) {
            throw new ConflictException("PACKAGE_CODE_TAKEN", "This package code is already in use.");
        }
        Destination destination = destinationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: " + request.getDestinationId()));
        p.setDestination(destination);
        applyBasics(p, request);
        packageRepository.save(p);

        replaceInclusionsExclusions(p, request.getInclusions(), request.getExclusions());

        auditService.log("PACKAGE_UPDATED", "TourPackage", String.valueOf(p.getId()), p.getName());
        return toDtoWithRating(p);
    }

    @Transactional
    public void deactivate(Long id) {
        TourPackage p = getOrThrow(id);
        p.setStatus(PackageStatus.INACTIVE);
        packageRepository.save(p);
        auditService.log("PACKAGE_DEACTIVATED", "TourPackage", String.valueOf(id), p.getName());
    }

    @Transactional
    public void addItineraryDay(Long packageId, ItineraryRequest request) {
        TourPackage p = getOrThrow(packageId);
        if (request.getDayNumber() <= 0) {
            throw new BadRequestException("Day number must be positive.");
        }
        if (itineraryRepository.existsByTourPackageIdAndDayNumber(packageId, request.getDayNumber())) {
            throw new ConflictException("DUPLICATE_DAY", "Day " + request.getDayNumber() + " already exists for this package.");
        }
        Itinerary it = new Itinerary();
        it.setTourPackage(p);
        it.setDayNumber(request.getDayNumber());
        it.setTitle(request.getTitle());
        it.setDescription(request.getDescription());
        it.setActivities(request.getActivities());
        it.setDisplayOrder(request.getDisplayOrder());
        itineraryRepository.save(it);
    }

    @Transactional
    public void removeItineraryDay(Long packageId, Long itineraryId) {
        Itinerary it = itineraryRepository.findById(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary entry not found"));
        if (!it.getTourPackage().getId().equals(packageId)) {
            throw new BadRequestException("Itinerary entry does not belong to this package.");
        }
        itineraryRepository.delete(it);
    }

    private void replaceInclusionsExclusions(TourPackage p, List<String> inclusions, List<String> exclusions) {
        inclusionRepository.deleteAll(inclusionRepository.findByTourPackageId(p.getId()));
        exclusionRepository.deleteAll(exclusionRepository.findByTourPackageId(p.getId()));
        if (inclusions != null) {
            for (String desc : inclusions) {
                if (desc == null || desc.isBlank()) continue;
                PackageInclusion inc = new PackageInclusion();
                inc.setTourPackage(p);
                inc.setDescription(desc);
                inclusionRepository.save(inc);
            }
        }
        if (exclusions != null) {
            for (String desc : exclusions) {
                if (desc == null || desc.isBlank()) continue;
                PackageExclusion exc = new PackageExclusion();
                exc.setTourPackage(p);
                exc.setDescription(desc);
                exclusionRepository.save(exc);
            }
        }
    }

    private void applyBasics(TourPackage p, TourPackageRequest request) {
        if (request.getBasePrice() == null || request.getBasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Base price must be greater than zero.");
        }
        if (request.getDurationDays() <= 0) {
            throw new BadRequestException("Duration must be a positive number of days.");
        }
        p.setPackageCode(request.getPackageCode());
        p.setName(request.getName());
        p.setDescription(request.getDescription());
        p.setDurationDays(request.getDurationDays());
        p.setBasePrice(request.getBasePrice());
        p.setTravelType(request.getTravelType());
        p.setPackageType(request.getPackageType());
        p.setStatus(request.getStatus() != null ? PackageStatus.valueOf(request.getStatus().toUpperCase())
                : (p.getStatus() != null ? p.getStatus() : PackageStatus.ACTIVE));
    }

    private TourPackageResponseDTO toDtoWithRating(TourPackage p) {
        var reviews = reviewRepository.findByTourPackageIdAndStatus(p.getId(),
                com.tourism.entity.enums.ReviewStatus.APPROVED, org.springframework.data.domain.Pageable.unpaged());
        Double avg = reviews.isEmpty() ? null :
                reviews.getContent().stream().mapToInt(Review::getRating).average().orElse(0);
        return DtoMapper.toPackageDTO(p, avg, reviews.getTotalElements());
    }

    TourPackage getOrThrow(Long id) {
        return packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found: " + id));
    }
}
