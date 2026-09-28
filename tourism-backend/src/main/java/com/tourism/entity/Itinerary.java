package com.tourism.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ITINERARY", uniqueConstraints = {
        @UniqueConstraint(name = "UK_ITINERARY_PACKAGE_DAY", columnNames = {"package_id", "day_number"})
})
@Getter
@Setter
@NoArgsConstructor
public class Itinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private TourPackage tourPackage;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(length = 1000)
    private String activities;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;
}
