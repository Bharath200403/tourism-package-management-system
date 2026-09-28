package com.tourism.service;

import com.tourism.entity.enums.BookingStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingStatusRulesTest {

    @Test
    void pendingCanMoveToConfirmedOrCancelled() {
        assertTrue(BookingStatusRules.isAllowed(BookingStatus.PENDING, BookingStatus.CONFIRMED));
        assertTrue(BookingStatusRules.isAllowed(BookingStatus.PENDING, BookingStatus.CANCELLED));
    }

    @Test
    void confirmedCanMoveToCompletedOrCancelled() {
        assertTrue(BookingStatusRules.isAllowed(BookingStatus.CONFIRMED, BookingStatus.COMPLETED));
        assertTrue(BookingStatusRules.isAllowed(BookingStatus.CONFIRMED, BookingStatus.CANCELLED));
    }

    @Test
    void completedCannotMoveBackToPending() {
        assertFalse(BookingStatusRules.isAllowed(BookingStatus.COMPLETED, BookingStatus.PENDING));
    }

    @Test
    void cancelledIsTerminal() {
        assertFalse(BookingStatusRules.isAllowed(BookingStatus.CANCELLED, BookingStatus.CONFIRMED));
        assertFalse(BookingStatusRules.isAllowed(BookingStatus.CANCELLED, BookingStatus.PENDING));
    }

    @Test
    void pendingCannotJumpStraightToCompleted() {
        assertFalse(BookingStatusRules.isAllowed(BookingStatus.PENDING, BookingStatus.COMPLETED));
    }
}
