package com.tourism.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates short, human-readable, unique-enough reference codes for bookings,
 * invoices and payment transactions. Uniqueness is still verified against the
 * database by the calling service before being persisted.
 */
public final class ReferenceGenerator {

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final SecureRandom RANDOM = new SecureRandom();

    private ReferenceGenerator() {
    }

    public static String bookingReference() {
        return "BK-" + LocalDateTime.now().format(STAMP) + "-" + randomSuffix();
    }

    public static String invoiceNumber() {
        return "INV-" + LocalDateTime.now().format(STAMP) + "-" + randomSuffix();
    }

    public static String transactionReference(String mode) {
        return "TXN-" + mode + "-" + LocalDateTime.now().format(STAMP) + "-" + randomSuffix();
    }

    private static String randomSuffix() {
        int n = RANDOM.nextInt(9000) + 1000;
        return String.valueOf(n);
    }
}
