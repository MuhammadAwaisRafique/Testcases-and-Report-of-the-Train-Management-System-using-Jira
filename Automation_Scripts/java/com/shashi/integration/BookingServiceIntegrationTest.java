package com.shashi.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.shashi.beans.HistoryBean;
import com.shashi.beans.TrainException;
import com.shashi.service.BookingService;
import com.shashi.service.impl.BookingServiceImpl;
import com.shashi.utility.DBUtil;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookingServiceIntegrationTest {

    private static BookingService bookingService;
    private static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException {
        bookingService = new BookingServiceImpl();
        connection = DBUtil.getConnection();
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testCreateHistory() throws SQLException, TrainException {
        HistoryBean newBooking = new HistoryBean();
        newBooking.setMailId("test@example.com");
        newBooking.setTr_no("10001");
        newBooking.setDate(LocalDate.now().toString());
        newBooking.setFrom_stn("A");
        newBooking.setTo_stn("B");
        newBooking.setSeats(2);
        newBooking.setAmount(200.0);

        HistoryBean createdBooking = bookingService.createHistory(newBooking);
        assertNotNull(createdBooking);
        assertNotNull(createdBooking.getTransId());
    }

    @Test
    void testGetAllBookingsByCustomerId() throws SQLException, TrainException {
        List<HistoryBean> bookings = bookingService.getAllBookingsByCustomerId("shashi@demo.com");
        assertFalse(bookings.isEmpty());
    }
}
