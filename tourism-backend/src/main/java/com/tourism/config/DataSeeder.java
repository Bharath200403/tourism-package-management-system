package com.tourism.config;

import com.tourism.entity.*;
import com.tourism.entity.enums.*;
import com.tourism.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Populates local demo data on first run (when the database is empty), so
 * the whole customer/operator/admin journey can be explored immediately.
 * These are DEMO credentials only - never treat them as production
 * credentials. See README-DEMO-CREDENTIALS for the login list.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final OperatorProfileRepository operatorProfileRepository;
    private final DestinationRepository destinationRepository;
    private final TourPackageRepository packageRepository;
    private final PackageScheduleRepository scheduleRepository;
    private final ItineraryRepository itineraryRepository;
    private final PackageInclusionRepository inclusionRepository;
    private final PackageExclusionRepository exclusionRepository;
    private final BookingRepository bookingRepository;
    private final TravelerRepository travelerRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled || userRepository.count() > 0) {
            return;
        }

        // ---- Users ----
        User admin = user("admin", "admin@tourism.local", "Admin@123", "System Administrator", Role.ADMIN);
        User operator = user("operator1", "operator1@tourism.local", "Operator@123", "Priya Sharma", Role.TOUR_OPERATOR);
        OperatorProfile op = new OperatorProfile();
        op.setUser(operator);
        op.setCompanyName("Incredible Trails Tours");
        op.setLicenseNumber("TOP-2024-001");
        op.setPhone("+91-9800000001");
        operatorProfileRepository.save(op);

        User customer1 = user("customer1", "customer1@tourism.local", "Customer@123", "Arjun Mehta", Role.CUSTOMER);
        customerProfile(customer1, "Bengaluru", "Karnataka", "India", "Nature", 30000.0);

        User customer2 = user("customer2", "customer2@tourism.local", "Customer@123", "Sneha Rao", Role.CUSTOMER);
        customerProfile(customer2, "Mumbai", "Maharashtra", "India", "Adventure", 45000.0);

        // ---- Destinations ----
        Destination kerala = destination("Kerala Backwaters", "Kerala", "India",
                "Serene backwaters, houseboats and lush greenery.", "Sep - Mar", "4-6 days");
        Destination goa = destination("Goa Beaches", "Goa", "India",
                "Sun, sand and vibrant nightlife on India's favourite coastline.", "Nov - Feb", "3-5 days");
        Destination manali = destination("Manali Hills", "Himachal Pradesh", "India",
                "Snow-capped peaks, adventure sports and cool mountain air.", "Mar - Jun", "5-7 days");
        Destination rajasthan = destination("Rajasthan Heritage", "Rajasthan", "India",
                "Majestic forts, palaces and the golden desert.", "Oct - Mar", "6-8 days");

        // ---- Packages ----
        TourPackage keralaPkg = tourPackage("KER-EXP-01", "Kerala Explorer", kerala,
                "A relaxing journey through Alleppey backwaters, Munnar tea gardens and Kochi's old town.",
                5, new BigDecimal("24999"), "Nature", "Leisure", operator,
                new String[]{"Houseboat stay", "Daily breakfast", "AC transport", "Local guide"},
                new String[]{"Airfare", "Personal expenses", "Travel insurance"});

        TourPackage goaPkg = tourPackage("GOA-BCH-01", "Goa Beach Getaway", goa,
                "North and South Goa beach hopping with water sports and sunset cruises.",
                4, new BigDecimal("15999"), "Beach", "Leisure", operator,
                new String[]{"Hotel stay", "Breakfast", "Airport pickup"},
                new String[]{"Water sports charges", "Alcohol", "Lunch/Dinner"});

        TourPackage manaliPkg = tourPackage("MAN-ADV-01", "Manali Adventure Trek", manali,
                "Trekking, paragliding and river rafting in the heart of the Himalayas.",
                6, new BigDecimal("28999"), "Adventure", "Trekking", operator,
                new String[]{"Camping equipment", "All meals during trek", "Certified trek guide"},
                new String[]{"Personal trekking gear", "Emergency evacuation cost"});

        TourPackage rajasthanPkg = tourPackage("RAJ-HER-01", "Royal Rajasthan Heritage Tour", rajasthan,
                "Jaipur, Udaipur and Jodhpur - forts, palaces and desert camping under the stars.",
                7, new BigDecimal("34999"), "Heritage", "Cultural", operator,
                new String[]{"Heritage hotel stay", "Daily breakfast & dinner", "Desert safari"},
                new String[]{"Monument entry fees", "Camera fees", "Tips"});

        itinerary(keralaPkg, 1, "Arrival in Kochi", "Check-in and evening at leisure.", "Airport pickup, hotel check-in");
        itinerary(keralaPkg, 2, "Alleppey Houseboat", "Full-day houseboat cruise through the backwaters.", "Houseboat cruise, sunset viewing");
        itinerary(keralaPkg, 3, "Munnar Tea Gardens", "Drive to Munnar, visit tea estates.", "Tea museum, viewpoint visits");
        itinerary(keralaPkg, 4, "Munnar Sightseeing", "Explore Eravikulam National Park.", "Wildlife spotting, waterfalls");
        itinerary(keralaPkg, 5, "Departure", "Drive back to Kochi for departure.", "Local shopping, airport drop");

        itinerary(goaPkg, 1, "Arrival & North Goa", "Check-in, Baga and Calangute beach visit.", "Beach walk, local market");
        itinerary(goaPkg, 2, "Water Sports Day", "Parasailing, jet-ski and banana boat rides.", "Water sports session");
        itinerary(goaPkg, 3, "South Goa Exploration", "Quiet beaches and Portuguese architecture.", "Church visits, beach relaxation");
        itinerary(goaPkg, 4, "Departure", "Sunset cruise and departure.", "Sunset cruise, airport drop");

        // ---- Schedules ----
        schedule(keralaPkg, LocalDate.now().plusDays(14), LocalDate.now().plusDays(19), 30);
        schedule(keralaPkg, LocalDate.now().plusDays(28), LocalDate.now().plusDays(33), 25);
        schedule(goaPkg, LocalDate.now().plusDays(10), LocalDate.now().plusDays(14), 40);
        schedule(manaliPkg, LocalDate.now().plusDays(20), LocalDate.now().plusDays(26), 20);
        schedule(rajasthanPkg, LocalDate.now().plusDays(35), LocalDate.now().plusDays(42), 25);
        // A past, completed schedule - used to seed a COMPLETED booking + review below.
        PackageSchedule pastGoaSchedule = schedule(goaPkg, LocalDate.now().minusDays(30), LocalDate.now().minusDays(26), 40);
        pastGoaSchedule.setStatus(ScheduleStatus.COMPLETED);
        scheduleRepository.save(pastGoaSchedule);

        // ---- Sample completed booking + payment + invoice + review for customer1 ----
        Booking completedBooking = new Booking();
        completedBooking.setBookingReference("BK-DEMO-0001");
        completedBooking.setCustomer(customer1);
        completedBooking.setSchedule(pastGoaSchedule);
        completedBooking.setTravelerCount(2);
        completedBooking.setTotalAmount(goaPkg.getBasePrice().multiply(BigDecimal.valueOf(2)));
        completedBooking.setBookingStatus(BookingStatus.COMPLETED);
        completedBooking.setBookingDate(LocalDateTime.now().minusDays(40));
        bookingRepository.save(completedBooking);
        pastGoaSchedule.setAvailableSeats(pastGoaSchedule.getAvailableSeats() - 2);
        scheduleRepository.save(pastGoaSchedule);

        Traveler t1 = new Traveler();
        t1.setBooking(completedBooking);
        t1.setFullName("Arjun Mehta");
        t1.setAge(29);
        t1.setGender("Male");
        t1.setContact("+91-9800000010");
        travelerRepository.save(t1);
        Traveler t2 = new Traveler();
        t2.setBooking(completedBooking);
        t2.setFullName("Kavya Mehta");
        t2.setAge(27);
        t2.setGender("Female");
        t2.setContact("+91-9800000011");
        travelerRepository.save(t2);

        Payment demoPayment = new Payment();
        demoPayment.setBooking(completedBooking);
        demoPayment.setPaymentMode(PaymentMode.UPI_SIMULATION);
        demoPayment.setPaymentStatus(PaymentStatus.SUCCESS);
        demoPayment.setAmount(completedBooking.getTotalAmount());
        demoPayment.setTransactionReference("TXN-DEMO-0001");
        demoPayment.setPaidAt(LocalDateTime.now().minusDays(40));
        paymentRepository.save(demoPayment);

        Invoice demoInvoice = new Invoice();
        demoInvoice.setInvoiceNumber("INV-DEMO-0001");
        demoInvoice.setBooking(completedBooking);
        demoInvoice.setSubtotal(completedBooking.getTotalAmount());
        demoInvoice.setDiscount(BigDecimal.ZERO);
        demoInvoice.setCharges(BigDecimal.ZERO);
        demoInvoice.setTotal(completedBooking.getTotalAmount());
        invoiceRepository.save(demoInvoice);

        Review demoReview = new Review();
        demoReview.setBooking(completedBooking);
        demoReview.setCustomer(customer1);
        demoReview.setTourPackage(goaPkg);
        demoReview.setRating(5);
        demoReview.setReviewText("Fantastic trip! The beaches were beautiful and the itinerary was well planned.");
        demoReview.setStatus(ReviewStatus.APPROVED);
        reviewRepository.save(demoReview);

        // ---- A second, pending booking for customer2 to show the pre-payment state ----
        var keralaSchedules = scheduleRepository.findByTourPackageId(keralaPkg.getId());
        PackageSchedule firstKeralaSchedule = keralaSchedules.get(0);
        Booking pendingBooking = new Booking();
        pendingBooking.setBookingReference("BK-DEMO-0002");
        pendingBooking.setCustomer(customer2);
        pendingBooking.setSchedule(firstKeralaSchedule);
        pendingBooking.setTravelerCount(1);
        pendingBooking.setTotalAmount(keralaPkg.getBasePrice());
        pendingBooking.setBookingStatus(BookingStatus.PENDING);
        pendingBooking.setBookingDate(LocalDateTime.now().minusDays(1));
        bookingRepository.save(pendingBooking);
        firstKeralaSchedule.setAvailableSeats(firstKeralaSchedule.getAvailableSeats() - 1);
        scheduleRepository.save(firstKeralaSchedule);

        Traveler t3 = new Traveler();
        t3.setBooking(pendingBooking);
        t3.setFullName("Sneha Rao");
        t3.setAge(31);
        t3.setGender("Female");
        t3.setContact("+91-9800000020");
        travelerRepository.save(t3);
    }

    private User user(String username, String email, String rawPassword, String fullName, Role role) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setFullName(fullName);
        u.setRole(role);
        u.setStatus(UserStatus.ACTIVE);
        return userRepository.save(u);
    }

    private void customerProfile(User user, String city, String state, String country, String travelType, Double budget) {
        CustomerProfile p = new CustomerProfile();
        p.setUser(user);
        p.setCity(city);
        p.setState(state);
        p.setCountry(country);
        p.setPreferredTravelType(travelType);
        p.setPreferredBudget(budget);
        customerProfileRepository.save(p);
    }

    private Destination destination(String name, String state, String country, String description, String season, String duration) {
        Destination d = new Destination();
        d.setName(name);
        d.setState(state);
        d.setCountry(country);
        d.setDescription(description);
        d.setBestSeason(season);
        d.setEstimatedDuration(duration);
        d.setStatus(PackageStatus.ACTIVE);
        return destinationRepository.save(d);
    }

    private TourPackage tourPackage(String code, String name, Destination destination, String description, int days,
                                     BigDecimal price, String travelType, String packageType, User operator,
                                     String[] inclusions, String[] exclusions) {
        TourPackage p = new TourPackage();
        p.setPackageCode(code);
        p.setName(name);
        p.setDestination(destination);
        p.setDescription(description);
        p.setDurationDays(days);
        p.setBasePrice(price);
        p.setTravelType(travelType);
        p.setPackageType(packageType);
        p.setStatus(PackageStatus.ACTIVE);
        p.setCreatedByOperator(operator);
        packageRepository.save(p);

        for (String desc : inclusions) {
            PackageInclusion inc = new PackageInclusion();
            inc.setTourPackage(p);
            inc.setDescription(desc);
            inclusionRepository.save(inc);
        }
        for (String desc : exclusions) {
            PackageExclusion exc = new PackageExclusion();
            exc.setTourPackage(p);
            exc.setDescription(desc);
            exclusionRepository.save(exc);
        }
        return p;
    }

    private void itinerary(TourPackage p, int day, String title, String description, String activities) {
        Itinerary it = new Itinerary();
        it.setTourPackage(p);
        it.setDayNumber(day);
        it.setTitle(title);
        it.setDescription(description);
        it.setActivities(activities);
        it.setDisplayOrder(day);
        itineraryRepository.save(it);
    }

    private PackageSchedule schedule(TourPackage p, LocalDate start, LocalDate end, int capacity) {
        PackageSchedule s = new PackageSchedule();
        s.setTourPackage(p);
        s.setStartDate(start);
        s.setEndDate(end);
        s.setCapacity(capacity);
        s.setAvailableSeats(capacity);
        s.setStatus(start.isAfter(LocalDate.now()) ? ScheduleStatus.OPEN : ScheduleStatus.CLOSED);
        return scheduleRepository.save(s);
    }
}
