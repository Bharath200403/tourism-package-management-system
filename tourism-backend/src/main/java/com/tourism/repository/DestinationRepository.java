package com.tourism.repository;

import com.tourism.entity.Destination;
import com.tourism.entity.enums.PackageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
    Page<Destination> findByStatus(PackageStatus status, Pageable pageable);
}
