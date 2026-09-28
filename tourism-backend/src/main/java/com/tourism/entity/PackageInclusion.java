package com.tourism.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PACKAGE_INCLUSION")
@Getter
@Setter
@NoArgsConstructor
public class PackageInclusion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private TourPackage tourPackage;

    @Column(nullable = false, length = 300)
    private String description;
}
