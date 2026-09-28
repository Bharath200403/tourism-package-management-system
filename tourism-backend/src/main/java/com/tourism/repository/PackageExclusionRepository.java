package com.tourism.repository;

import com.tourism.entity.PackageExclusion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackageExclusionRepository extends JpaRepository<PackageExclusion, Long> {
    List<PackageExclusion> findByTourPackageId(Long packageId);
}
