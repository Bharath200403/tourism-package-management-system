package com.tourism.entity;

import com.tourism.entity.enums.ScheduleStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "PACKAGE_SCHEDULE")
@Getter
@Setter
@NoArgsConstructor
public class PackageSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private TourPackage tourPackage;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ScheduleStatus status = ScheduleStatus.OPEN;

    /** Optimistic-lock guard, used together with a pessimistic write lock in the booking
     *  transaction, so two concurrent bookings can never both succeed against the same
     *  remaining seats. */
    @jakarta.persistence.Version
    @Column(name = "version")
    private Long version;
}
