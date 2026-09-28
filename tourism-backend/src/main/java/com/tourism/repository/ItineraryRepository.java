package com.tourism.repository;

import com.tourism.entity.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    List<Itinerary> findByTourPackageIdOrderByDisplayOrderAsc(Long packageId);
    boolean existsByTourPackageIdAndDayNumber(Long packageId, Integer dayNumber);
}
