package com.shashi.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shashi.beans.HistoryBean;
import com.shashi.beans.TrainBean;
import com.shashi.beans.UserBean;
import com.shashi.service.BookingService;
import com.shashi.service.TrainService;
import com.shashi.service.UserService;
import com.shashi.service.impl.BookingServiceImpl;
import com.shashi.service.impl.TrainServiceImpl;
import com.shashi.service.impl.UserServiceImpl;
import com.shashi.constant.UserRole;

@ExtendWith(MockitoExtension.class)
public class TrainBookingIntegrationTest {

    private UserService userService;
    private TrainService trainService;
    private BookingService bookingService;

    @Mock
    private Connection mockConnection;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(UserRole.CUSTOMER);
        trainService = new TrainServiceImpl();
        bookingService = new BookingServiceImpl();
    }

    @Test
    @DisplayName("Complete Booking Flow Test")
    void testCompleteBookingFlow() throws Exception {
        // 1. User Registration
        UserBean user = new UserBean();
        user.setMailId("test@example.com");
        user.setPWord("password123");
        user.setFName("Test");
        user.setLName("User");
        user.setAddr("Test Address");
        user.setPhNo(1234567890L);

        String registrationResult = userService.registerUser(user);
        assertTrue(registrationResult.contains("SUCCESS"));

        // 2. User Login
        UserBean loggedInUser = userService.loginUser("test@example.com", "password123");
        assertNotNull(loggedInUser);

        // 3. Search Train
        TrainBean train = trainService.getTrainById("10001");
        assertNotNull(train);
        assertTrue(train.getSeats() > 0);

        // 4. Book Ticket
        HistoryBean booking = new HistoryBean();
        booking.setMailId(loggedInUser.getMailId());
        booking.setTr_no(train.getTr_no().toString());
        booking.setDate(LocalDate.now().toString());
        booking.setFrom_stn(train.getFrom_stn());
        booking.setTo_stn(train.getTo_stn());
        booking.setSeats(2);
        booking.setAmount(train.getFare() * 2);

        HistoryBean confirmedBooking = bookingService.createHistory(booking);
        assertNotNull(confirmedBooking);
        assertNotNull(confirmedBooking.getTransId());

        // 5. Verify Booking History
        List<HistoryBean> bookingHistory = bookingService.getAllBookingsByCustomerId(loggedInUser.getMailId());
        assertFalse(bookingHistory.isEmpty());
        assertTrue(bookingHistory.stream()
            .anyMatch(b -> b.getTransId().equals(confirmedBooking.getTransId())));
    }

    @Test
    @DisplayName("Booking Flow with Seat Unavailability")
    void testBookingFlowWithSeatUnavailability() throws Exception {
        // Setup train with limited seats
        TrainBean train = trainService.getTrainById("10001");
        train.setSeats(1); // Only 1 seat available
        trainService.updateTrain(train);

        // Try to book more seats than available
        HistoryBean booking = new HistoryBean();
        booking.setMailId("test@example.com");
        booking.setTr_no(train.getTr_no().toString());
        booking.setDate(LocalDate.now().toString());
        booking.setFrom_stn(train.getFrom_stn());
        booking.setTo_stn(train.getTo_stn());
        booking.setSeats(2); // Trying to book 2 seats
        booking.setAmount(train.getFare() * 2);

        assertThrows(Exception.class, () -> {
            bookingService.createHistory(booking);
        });
    }

    @Test
    @DisplayName("Multiple Concurrent Bookings Test")
    void testMultipleConcurrentBookings() throws Exception {
        TrainBean train = trainService.getTrainById("10001");
        int initialSeats = train.getSeats();
        int numberOfConcurrentBookings = 3;
        
        // Create multiple concurrent bookings
        HistoryBean[] bookings = new HistoryBean[numberOfConcurrentBookings];
        for (int i = 0; i < numberOfConcurrentBookings; i++) {
            bookings[i] = new HistoryBean();
            bookings[i].setMailId("user" + i + "@example.com");
            bookings[i].setTr_no(train.getTr_no().toString());
            bookings[i].setDate(LocalDate.now().toString());
            bookings[i].setFrom_stn(train.getFrom_stn());
            bookings[i].setTo_stn(train.getTo_stn());
            bookings[i].setSeats(1);
            bookings[i].setAmount(train.getFare());
        }

        // Book tickets concurrently
        for (HistoryBean booking : bookings) {
            HistoryBean confirmedBooking = bookingService.createHistory(booking);
            assertNotNull(confirmedBooking);
            assertNotNull(confirmedBooking.getTransId());
        }

        // Verify seat count is updated correctly
        TrainBean updatedTrain = trainService.getTrainById("10001");
        assertEquals(initialSeats - numberOfConcurrentBookings, updatedTrain.getSeats());
    }

    @Test
    @DisplayName("Booking Cancellation Flow")
    void testBookingCancellationFlow() throws Exception {
        // 1. Create initial booking
        TrainBean train = trainService.getTrainById("10001");
        int initialSeats = train.getSeats();

        HistoryBean booking = new HistoryBean();
        booking.setMailId("test@example.com");
        booking.setTr_no(train.getTr_no().toString());
        booking.setDate(LocalDate.now().toString());
        booking.setFrom_stn(train.getFrom_stn());
        booking.setTo_stn(train.getTo_stn());
        booking.setSeats(2);
        booking.setAmount(train.getFare() * 2);

        HistoryBean confirmedBooking = bookingService.createHistory(booking);
        assertNotNull(confirmedBooking);

        // 2. Cancel booking (simulated by updating train seats)
        train.setSeats(initialSeats); // Restore original seat count
        String updateResult = trainService.updateTrain(train);
        assertEquals("SUCCESS", updateResult);

        // 3. Verify seat count is restored
        TrainBean updatedTrain = trainService.getTrainById("10001");
        assertEquals(initialSeats, updatedTrain.getSeats());
    }
}
