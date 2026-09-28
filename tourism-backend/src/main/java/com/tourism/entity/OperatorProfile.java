package com.tourism.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "OPERATOR_PROFILE")
@Getter
@Setter
@NoArgsConstructor
public class OperatorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "company_name", length = 150)
    private String companyName;

    @Column(name = "license_number", length = 80)
    private String licenseNumber;

    @Column(length = 30)
    private String phone;
}
