package com.tourism.service;

import com.tourism.entity.Booking;
import com.tourism.entity.enums.BookingStatus;
import com.tourism.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Two small housekeeping jobs that keep statuses accurate without any
 * external scheduler or service: schedules past their start date are closed
 * to new bookings, and confirmed bookings whose trip has finished move to
 * COMPLETED (which then unlocks reviews for that booking).
 */
@Component
@RequiredArgsConstructor
public class BookingLifecycleScheduler {

    private final BookingRepository bookingRepository;
    private final ScheduleService scheduleService;

    @Scheduled(fixedRate = 6 * 60 * 60 * 1000, initialDelay = 30 * 1000)
    @Transactional
    public void runHousekeeping() {
        scheduleService.closeExpiredSchedules();

        List<Booking> confirmed = bookingRepository.findByBookingStatus(BookingStatus.CONFIRMED);
        for (Booking b : confirmed) {
            if (b.getSchedule().getEndDate().isBefore(LocalDate.now())) {
                b.setBookingStatus(BookingStatus.COMPLETED);
            }
        }
        bookingRepository.saveAll(confirmed);
    }
}
