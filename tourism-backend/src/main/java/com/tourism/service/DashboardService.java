package com.tourism.service;

import com.tourism.dto.response.*;
import com.tourism.entity.Booking;
import com.tourism.entity.TourPackage;
import com.tourism.entity.User;
import com.tourism.entity.enums.BookingStatus;
import com.tourism.entity.enums.PackageStatus;
import com.tourism.entity.enums.Role;
import com.tourism.mapper.DtoMapper;
import com.tourism.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final TourPackageRepository packageRepository;
    private final PackageScheduleRepository scheduleRepository;
    private final RecommendationService recommendationService;

    public CustomerDashboardDTO customerDashboard(Long customerId) {
        List<Booking> bookings = bookingRepository.findByCustomerId(customerId, PageRequest.of(0, 1000)).getContent();

        long upcoming = bookings.stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.CONFIRMED && b.getSchedule().getStartDate().isAfter(LocalDate.now()))
                .count();
        long active = bookings.stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.PENDING || b.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();
        long completed = bookings.stream().filter(b -> b.getBookingStatus() == BookingStatus.COMPLETED).count();
        long cancelled = bookings.stream().filter(b -> b.getBookingStatus() == BookingStatus.CANCELLED).count();

        List<BookingResponseDTO> recent = bookings.stream()
                .sorted(Comparator.comparing(Booking::getBookingDate).reversed())
                .limit(5)
                .map(b -> {
                    var payment = paymentRepository.findByBookingId(b.getId()).map(DtoMapper::toPaymentDTO).orElse(null);
                    return DtoMapper.toBookingDTO(b, payment);
                }).toList();

        List<TourPackageResponseDTO> recommended = recommendationService.recommendForCustomer(customerId, 4);

        return CustomerDashboardDTO.builder()
                .upcomingTrips(upcoming)
                .activeBookings(active)
                .completedTrips(completed)
                .cancelledBookings(cancelled)
                .recentBookings(recent)
                .recommendedPackages(recommended)
                .build();
    }

    public OperatorDashboardDTO operatorDashboard(Long operatorId) {
        List<TourPackage> myPackages = packageRepository.findByCreatedByOperatorId(operatorId);
        long active = myPackages.stream().filter(p -> p.getStatus() == PackageStatus.ACTIVE).count();

        long totalBookings = myPackages.stream()
                .mapToLong(p -> bookingRepository.findBySchedule_TourPackage_Id(p.getId()).size())
                .sum();
        long pendingBookings = myPackages.stream()
                .flatMap(p -> bookingRepository.findBySchedule_TourPackage_Id(p.getId()).stream())
                .filter(b -> b.getBookingStatus() == BookingStatus.PENDING)
                .count();

        List<TourPackageResponseDTO> packageDtos = myPackages.stream()
                .map(p -> DtoMapper.toPackageDTO(p, null, 0L)).toList();

        return OperatorDashboardDTO.builder()
                .totalPackages(myPackages.size())
                .activePackages(active)
                .totalBookings(totalBookings)
                .pendingBookings(pendingBookings)
                .myPackages(packageDtos)
                .build();
    }

    public AdminDashboardDTO adminDashboard() {
        long totalCustomers = userRepository.findByRole(Role.CUSTOMER).size();
        long totalOperators = userRepository.findByRole(Role.TOUR_OPERATOR).size();
        long totalPackages = packageRepository.count();
        long activePackages = packageRepository.findAll().stream().filter(p -> p.getStatus() == PackageStatus.ACTIVE).count();
        long upcomingTours = scheduleRepository.findAll().stream()
                .filter(s -> s.getStartDate().isAfter(LocalDate.now())).count();

        long totalBookings = bookingRepository.count();
        long pending = bookingRepository.countByBookingStatus(BookingStatus.PENDING);
        long confirmed = bookingRepository.countByBookingStatus(BookingStatus.CONFIRMED);
        long cancelled = bookingRepository.countByBookingStatus(BookingStatus.CANCELLED);
        long completed = bookingRepository.countByBookingStatus(BookingStatus.COMPLETED);

        BigDecimal revenue = paymentRepository.findAll().stream()
                .filter(p -> p.getPaymentStatus() == com.tourism.entity.enums.PaymentStatus.SUCCESS)
                .map(com.tourism.entity.Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalCapacity = scheduleRepository.findAll().stream().mapToInt(com.tourism.entity.PackageSchedule::getCapacity).sum();
        int totalBooked = scheduleRepository.findAll().stream()
                .mapToInt(s -> s.getCapacity() - s.getAvailableSeats()).sum();
        double occupancy = totalCapacity == 0 ? 0.0 : (totalBooked * 100.0) / totalCapacity;

        Map<Long, Long> bookingCountByPackage = bookingRepository.findAll().stream()
                .collect(Collectors.groupingBy(b -> b.getSchedule().getTourPackage().getId(), Collectors.counting()));

        List<PopularPackageDTO> popular = bookingCountByPackage.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new PopularPackageDTO(e.getKey(),
                        packageRepository.findById(e.getKey()).map(TourPackage::getName).orElse("Unknown"),
                        e.getValue()))
                .toList();

        return AdminDashboardDTO.builder()
                .totalCustomers(totalCustomers)
                .totalOperators(totalOperators)
                .totalPackages(totalPackages)
                .activePackages(activePackages)
                .upcomingTours(upcomingTours)
                .totalBookings(totalBookings)
                .pendingBookings(pending)
                .confirmedBookings(confirmed)
                .cancelledBookings(cancelled)
                .completedBookings(completed)
                .revenue(revenue)
                .occupancyPercentage(occupancy)
                .popularPackages(popular)
                .build();
    }
}
