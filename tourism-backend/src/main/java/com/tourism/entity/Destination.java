package com.tourism.entity;

import com.tourism.entity.enums.PackageStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "DESTINATION")
@Getter
@Setter
@NoArgsConstructor
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 80)
    private String state;

    @Column(length = 80)
    private String country;

    @Column(length = 2000)
    private String description;

    @Column(name = "best_season", length = 100)
    private String bestSeason;

    @Column(name = "estimated_duration", length = 60)
    private String estimatedDuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PackageStatus status = PackageStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
