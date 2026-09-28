package com.tourism.repository;

import com.tourism.entity.Traveler;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelerRepository extends JpaRepository<Traveler, Long> {
    List<Traveler> findByBookingId(Long bookingId);
}
