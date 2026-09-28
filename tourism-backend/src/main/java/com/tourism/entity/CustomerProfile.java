package com.tourism.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CUSTOMER_PROFILE")
@Getter
@Setter
@NoArgsConstructor
public class CustomerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 200)
    private String address;

    @Column(length = 60)
    private String city;

    @Column(length = 60)
    private String state;

    @Column(length = 60)
    private String country;

    @Column(name = "preferred_travel_type", length = 60)
    private String preferredTravelType;

    @Column(name = "preferred_budget")
    private Double preferredBudget;
}
