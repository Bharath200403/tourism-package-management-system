package com.tourism.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TRAVELER")
@Getter
@Setter
@NoArgsConstructor
public class Traveler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false)
    private Integer age;

    @Column(length = 20)
    private String gender;

    @Column(length = 60)
    private String contact;

    @Column(name = "special_requirement", length = 500)
    private String specialRequirement;
}
