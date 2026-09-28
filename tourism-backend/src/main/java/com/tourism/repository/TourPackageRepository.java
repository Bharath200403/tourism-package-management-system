package com.tourism.repository;

import com.tourism.entity.TourPackage;
import com.tourism.entity.enums.PackageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TourPackageRepository extends JpaRepository<TourPackage, Long> {

    boolean existsByPackageCode(String packageCode);

    @Query("select p from TourPackage p where " +
            "(:name is null or lower(p.name) like lower(concat('%', :name, '%'))) and " +
            "(:destinationId is null or p.destination.id = :destinationId) and " +
            "(:minDuration is null or p.durationDays >= :minDuration) and " +
            "(:maxDuration is null or p.durationDays <= :maxDuration) and " +
            "(:minPrice is null or p.basePrice >= :minPrice) and " +
            "(:maxPrice is null or p.basePrice <= :maxPrice) and " +
            "(:status is null or p.status = :status)")
    Page<TourPackage> search(@Param("name") String name,
                              @Param("destinationId") Long destinationId,
                              @Param("minDuration") Integer minDuration,
                              @Param("maxDuration") Integer maxDuration,
                              @Param("minPrice") BigDecimal minPrice,
                              @Param("maxPrice") BigDecimal maxPrice,
                              @Param("status") PackageStatus status,
                              Pageable pageable);

    List<TourPackage> findByCreatedByOperatorId(Long operatorId);
}
