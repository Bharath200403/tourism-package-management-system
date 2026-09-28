package com.tourism.entity;

import com.tourism.entity.enums.PackageStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TOUR_PACKAGE", uniqueConstraints = {
        @UniqueConstraint(name = "UK_PACKAGE_CODE", columnNames = "package_code")
})
@Getter
@Setter
@NoArgsConstructor
public class TourPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_code", nullable = false, length = 40)
    private String packageCode;

    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;

    @Column(length = 3000)
    private String description;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "travel_type", length = 60)
    private String travelType;

    @Column(name = "package_type", length = 60)
    private String packageType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PackageStatus status = PackageStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_operator_id")
    private User createdByOperator;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "tourPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackageSchedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "tourPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Itinerary> itineraries = new ArrayList<>();

    @OneToMany(mappedBy = "tourPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackageInclusion> inclusions = new ArrayList<>();

    @OneToMany(mappedBy = "tourPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackageExclusion> exclusions = new ArrayList<>();

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
