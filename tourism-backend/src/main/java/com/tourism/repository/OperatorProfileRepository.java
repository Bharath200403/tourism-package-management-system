package com.tourism.repository;

import com.tourism.entity.OperatorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperatorProfileRepository extends JpaRepository<OperatorProfile, Long> {
    Optional<OperatorProfile> findByUserId(Long userId);
}
