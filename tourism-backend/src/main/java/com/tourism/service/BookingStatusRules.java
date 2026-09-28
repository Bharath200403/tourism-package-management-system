package com.tourism.service;

import com.tourism.entity.enums.BookingStatus;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * Single source of truth for which booking-status transitions are legal.
 * Centralized here (rather than scattered across controllers) per the
 * requirement that arbitrary state changes are never allowed.
 */
public final class BookingStatusRules {

    private static final Map<BookingStatus, EnumSet<BookingStatus>> ALLOWED = new EnumMap<>(BookingStatus.class);

    static {
        ALLOWED.put(BookingStatus.PENDING, EnumSet.of(BookingStatus.CONFIRMED, BookingStatus.CANCELLED));
        ALLOWED.put(BookingStatus.CONFIRMED, EnumSet.of(BookingStatus.COMPLETED, BookingStatus.CANCELLED));
        ALLOWED.put(BookingStatus.COMPLETED, EnumSet.noneOf(BookingStatus.class));
        ALLOWED.put(BookingStatus.CANCELLED, EnumSet.noneOf(BookingStatus.class));
    }

    private BookingStatusRules() {
    }

    public static boolean isAllowed(BookingStatus from, BookingStatus to) {
        return ALLOWED.getOrDefault(from, EnumSet.noneOf(BookingStatus.class)).contains(to);
    }
}
