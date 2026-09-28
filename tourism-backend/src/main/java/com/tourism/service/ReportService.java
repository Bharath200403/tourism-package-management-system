package com.tourism.service;

import com.tourism.dto.response.*;
import com.tourism.entity.Booking;
import com.tourism.entity.Cancellation;
import com.tourism.entity.PackageSchedule;
import com.tourism.entity.Payment;
import com.tourism.entity.enums.BookingStatus;
import com.tourism.entity.enums.PaymentStatus;
import com.tourism.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * All reports read straight from Derby via the JPA repositories - there is
 * no external analytics service. Aggregation is done in memory, which is
 * appropriate at this application's local, single-machine data scale.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final CancellationRepository cancellationRepository;
    private final PackageScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public BookingReportDTO bookingReport() {
        return BookingReportDTO.builder()
                .totalBookings(bookingRepository.count())
                .pending(bookingRepository.countByBookingStatus(BookingStatus.PENDING))
                .confirmed(bookingRepository.countByBookingStatus(BookingStatus.CONFIRMED))
                .completed(bookingRepository.countByBookingStatus(BookingStatus.COMPLETED))
                .cancelled(bookingRepository.countByBookingStatus(BookingStatus.CANCELLED))
                .build();
    }

    public RevenueReportDTO revenueReport() {
        BigDecimal totalRevenue = paymentRepository.findAll().stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.SUCCESS || p.getPaymentStatus() == PaymentStatus.REFUNDED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal refunded = cancellationRepository.findAll().stream()
                .map(Cancellation::getRefundAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return RevenueReportDTO.builder()
                .totalRevenue(totalRevenue)
                .refundedAmount(refunded)
                .netRevenue(totalRevenue.subtract(refunded))
                .build();
    }

    public List<PackagePerformanceDTO> packagePerformanceReport() {
        List<Booking> bookings = bookingRepository.findAll();
        Map<Long, List<Booking>> byPackage = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getSchedule().getTourPackage().getId()));

        return byPackage.entrySet().stream().map(entry -> {
            List<Booking> pkgBookings = entry.getValue();
            String name = pkgBookings.get(0).getSchedule().getTourPackage().getName();
            BigDecimal revenue = pkgBookings.stream()
                    .filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
                    .map(Booking::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            List<PackageSchedule> schedules = scheduleRepository.findByTourPackageId(entry.getKey());
            int capacity = schedules.stream().mapToInt(PackageSchedule::getCapacity).sum();
            int booked = schedules.stream().mapToInt(s -> s.getCapacity() - s.getAvailableSeats()).sum();
            double occupancy = capacity == 0 ? 0.0 : (booked * 100.0) / capacity;

            return new PackagePerformanceDTO(entry.getKey(), name, pkgBookings.size(), revenue, occupancy);
        }).sorted(Comparator.comparing(PackagePerformanceDTO::getRevenue).reversed()).toList();
    }

    public List<CustomerReportDTO> customerReport() {
        List<Booking> bookings = bookingRepository.findAll();
        Map<Long, List<Booking>> byCustomer = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getCustomer().getId()));

        return byCustomer.entrySet().stream().map(entry -> {
            List<Booking> custBookings = entry.getValue();
            String name = custBookings.get(0).getCustomer().getFullName();
            BigDecimal spend = custBookings.stream()
                    .filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
                    .map(Booking::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            return new CustomerReportDTO(entry.getKey(), name, custBookings.size(), spend);
        }).sorted(Comparator.comparing(CustomerReportDTO::getTotalSpend).reversed()).toList();
    }

    public CancellationReportDTO cancellationReport() {
        List<Cancellation> cancellations = cancellationRepository.findAll();
        BigDecimal totalRefund = cancellations.stream().map(Cancellation::getRefundAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return CancellationReportDTO.builder()
                .totalCancellations(cancellations.size())
                .totalRefundAmount(totalRefund)
                .build();
    }

    public List<OccupancyReportDTO> occupancyReport() {
        return scheduleRepository.findAll().stream().map(s -> {
            int booked = s.getCapacity() - s.getAvailableSeats();
            double occupancy = s.getCapacity() == 0 ? 0.0 : (booked * 100.0) / s.getCapacity();
            return new OccupancyReportDTO(s.getId(), s.getTourPackage().getName(), s.getStartDate(), s.getCapacity(), booked, occupancy);
        }).toList();
    }
}
