package com.shashi.beans;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingDetailsTest {

    @Test
    void testBookingDetailsGettersAndSetters() {
        BookingDetails booking = new BookingDetails();
        booking.setMailId("user@example.com");
        booking.setTr_no("1001");
        booking.setDate("2024-03-15");
        booking.setFrom_stn("Station A");
        booking.setTo_stn("Station B");
        booking.setSeats(2);
        booking.setAmount(500.0);

        assertEquals("user@example.com", booking.getMailId());
        assertEquals("1001", booking.getTr_no());
        assertEquals("2024-03-15", booking.getDate());
        assertEquals("Station A", booking.getFrom_stn());
        assertEquals("Station B", booking.getTo_stn());
        assertEquals(2, booking.getSeats());
        assertEquals(500.0, booking.getAmount());
    }
}
