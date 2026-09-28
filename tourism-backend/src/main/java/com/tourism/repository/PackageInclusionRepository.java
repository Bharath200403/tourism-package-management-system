package com.tourism.repository;

import com.tourism.entity.PackageInclusion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackageInclusionRepository extends JpaRepository<PackageInclusion, Long> {
    List<PackageInclusion> findByTourPackageId(Long packageId);
}
