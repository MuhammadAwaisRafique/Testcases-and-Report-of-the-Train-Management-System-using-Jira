package com.shashi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shashi.beans.HistoryBean;
import com.shashi.beans.TrainException;
import com.shashi.utility.DBUtil;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private BookingServiceImpl bookingService;

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl();
    }

    @Nested
    @DisplayName("Get All Bookings By Customer ID Tests")
    class GetAllBookingsByCustomerIdTests {
        @Test
        @DisplayName("Successful Booking History Retrieval")
        void testGetAllBookingsByCustomerId_Success() throws SQLException, TrainException {
            // Arrange
            String customerId = "test@example.com";
            List<HistoryBean> expectedBookings = createSampleBookingHistory(customerId, 2);

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true, true, false); // Two bookings, then no more

            // Mock ResultSet data (adjust as needed to match your HistoryBean structure)
            mockResultSetData(expectedBookings);

            // Act
            List<HistoryBean> bookings = bookingService.getAllBookingsByCustomerId(customerId);

            // Assert
            assertEquals(2, bookings.size());
            assertEquals(expectedBookings, bookings); // Assumes proper equals() in HistoryBean
        }

        @Test
        @DisplayName("No Bookings Found for Customer")
        void testGetAllBookingsByCustomerId_NoBookings() throws SQLException, TrainException {
            // Arrange
            String customerId = "nonexistent@example.com";

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            // Act
            List<HistoryBean> bookings = bookingService.getAllBookingsByCustomerId(customerId);

            // Assert
            assertTrue(bookings.isEmpty());
        }

        @Test
        @DisplayName("Database Error During Booking Retrieval")
        void testGetAllBookingsByCustomerId_DatabaseError() throws SQLException, TrainException {
            // Arrange
            String customerId = "test@example.com";
            when(DBUtil.getConnection()).thenThrow(new SQLException("Database error"));

            // Act & Assert
            assertThrows(TrainException.class, () -> bookingService.getAllBookingsByCustomerId(customerId));
        }
    }

    @Nested
    @DisplayName("Create Booking History Tests")
    class CreateHistoryTests {
        @Test
        @DisplayName("Successful Booking History Creation")
        void testCreateHistory_Success() throws SQLException, TrainException {
            // Arrange
            HistoryBean bookingDetails = createValidBookingDetails();

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // Act
            HistoryBean createdBooking = bookingService.createHistory(bookingDetails);

            // Assert
            assertNotNull(createdBooking);
            assertNotNull(createdBooking.getTransId());
            // Add more specific assertions based on your HistoryBean fields
            assertEquals(bookingDetails.getMailId(), createdBooking.getMailId());
            assertEquals(bookingDetails.getTr_no(), createdBooking.getTr_no());
        }

        @Test
        @DisplayName("Booking Creation Fails - Database Error")
        void testCreateHistory_DatabaseError() throws SQLException, TrainException {
            // Arrange
            HistoryBean bookingDetails = createValidBookingDetails();
            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(0); // Simulate failure

            // Act & Assert
            assertThrows(TrainException.class, () -> bookingService.createHistory(bookingDetails));
        }

        @Test
        @DisplayName("Booking Creation Fails - Invalid Data")
        void testCreateHistory_InvalidData() throws SQLException, TrainException {
            // Arrange
            HistoryBean bookingDetails = new HistoryBean(); // Missing required fields
            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

            // Act & Assert
            assertThrows(TrainException.class, () -> bookingService.createHistory(bookingDetails));
        }

        @Test
        @DisplayName("Booking Creation with Zero Seats")
        void testCreateHistory_ZeroSeats() throws SQLException, TrainException {
            // Arrange
            HistoryBean bookingDetails = createValidBookingDetails();
            bookingDetails.setSeats(0); // Invalid: zero seats
            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

            // Act & Assert
            assertThrows(TrainException.class, () -> bookingService.createHistory(bookingDetails));
        }

        @Test
        @DisplayName("Booking Creation with Negative Amount")
        void testCreateHistory_NegativeAmount() throws SQLException, TrainException {
            // Arrange
            HistoryBean bookingDetails = createValidBookingDetails();
            bookingDetails.setAmount(-100.0); // Invalid: negative amount
            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

            // Act & Assert
            assertThrows(TrainException.class, () -> bookingService.createHistory(bookingDetails));
        }
    }

    // Helper methods
    private HistoryBean createValidBookingDetails() {
        HistoryBean details = new HistoryBean();
        details.setMailId("test@example.com");
        details.setTr_no("10001");
        details.setDate("2024-03-15");
        details.setFrom_stn("Kolkata");
        details.setTo_stn("Delhi");
        details.setSeats(2);
        details.setAmount(1000.0);
        return details;
    }

    private List<HistoryBean> createSampleBookingHistory(String customerId, int numBookings) {
        List<HistoryBean> bookings = new ArrayList<>();
        for (int i = 0; i < numBookings; i++) {
            HistoryBean booking = new HistoryBean();
            booking.setTransId(UUID.randomUUID().toString());
            booking.setMailId(customerId);
            booking.setTr_no("100" + (i + 1));
            booking.setDate("2024-01-" + (i + 15));
            booking.setFrom_stn("StationA");
            booking.setTo_stn("StationB");
            booking.setSeats(i + 1);
            booking.setAmount(100.0 * (i + 1));
            bookings.add(booking);
        }
        return bookings;
    }

    private void mockResultSetData(List<HistoryBean> bookings) throws SQLException {
        for (HistoryBean booking : bookings) {
            when(mockResultSet.getString("transid")).thenReturn(booking.getTransId());
            when(mockResultSet.getString("mailid")).thenReturn(booking.getMailId());
            when(mockResultSet.getString("tr_no")).thenReturn(booking.getTr_no());
            when(mockResultSet.getString("date")).thenReturn(booking.getDate());
            when(mockResultSet.getString("from_stn")).thenReturn(booking.getFrom_stn());
            when(mockResultSet.getString("to_stn")).thenReturn(booking.getTo_stn());
            when(mockResultSet.getInt("seats")).thenReturn(booking.getSeats());
            when(mockResultSet.getDouble("amount")).thenReturn(booking.getAmount());
        }
    }
}
