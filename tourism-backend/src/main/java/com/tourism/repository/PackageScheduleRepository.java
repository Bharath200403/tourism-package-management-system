package com.tourism.repository;

import com.tourism.entity.PackageSchedule;
import com.tourism.entity.enums.ScheduleStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PackageScheduleRepository extends JpaRepository<PackageSchedule, Long> {

    List<PackageSchedule> findByTourPackageId(Long packageId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from PackageSchedule s where s.id = :id")
    Optional<PackageSchedule> findByIdForUpdate(Long id);

    List<PackageSchedule> findByStartDateBeforeAndStatus(LocalDate date, ScheduleStatus status);
}
