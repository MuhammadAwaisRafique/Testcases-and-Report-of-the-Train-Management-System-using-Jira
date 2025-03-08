package com.shashi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shashi.beans.HistoryBean;
import com.shashi.beans.TrainException;
import com.shashi.utility.DBUtil;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplAdvancedTest {

    private BookingServiceImpl bookingService;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl();
    }

    @Nested
    @DisplayName("Booking History Retrieval Tests")
    class BookingHistoryRetrievalTests {
        @Test
        @DisplayName("Retrieve Booking History for Customer with Multiple Bookings")
        public void testGetAllBookingsByCustomerId_MultipleBookings() throws Exception {
            // Arrange
            String customerEmail = "test@example.com";
            
            setupMockBookingHistoryResultSet(customerEmail, 3);

            // Act
            List<HistoryBean> bookings = bookingService.getAllBookingsByCustomerId(customerEmail);

            // Assert
            assertNotNull(bookings);
            assertEquals(3, bookings.size());
            
            // Verify details of first booking
            assertEquals("BBC374-NSDF-4673", bookings.get(0).getTransId());
            assertEquals(customerEmail, bookings.get(0).getMailId());
        }

        @Test
        @DisplayName("Retrieve Booking History for Customer with No Bookings")
        public void testGetAllBookingsByCustomerId_NoBookings() throws Exception {
            // Arrange
            String customerEmail = "empty@example.com";
            
            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            
            when(mockResultSet.next()).thenReturn(false);

            // Act & Assert
            assertThrows(TrainException.class, () -> {
                bookingService.getAllBookingsByCustomerId(customerEmail);
            });
        }

        @ParameterizedTest
        @DisplayName("Retrieve Booking History with Different Date Formats")
        @CsvSource({
            "test@example.com, 02-FEB-2024",
            "test@example.com, 12-JAN-2024",
            "test@example.com, 22-JULY-2024"
        })
        public void testGetAllBookingsByCustomerId_DateVariations(String customerEmail, String bookingDate) throws Exception {
            // Arrange
            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            
            when(mockResultSet.next())
                .thenReturn(true)   // First booking
                .thenReturn(false); // End of results

            when(mockResultSet.getString("transid")).thenReturn("BBC374-NSDF-4673");
            when(mockResultSet.getString("mailid")).thenReturn(customerEmail);
            when(mockResultSet.getString("tr_no")).thenReturn("10001");
            when(mockResultSet.getString("date")).thenReturn(bookingDate);
            when(mockResultSet.getString("from_stn")).thenReturn("HOWRAH");
            when(mockResultSet.getString("to_stn")).thenReturn("JODHPUR");
            when(mockResultSet.getInt("seats")).thenReturn(2);
            when(mockResultSet.getDouble("amount")).thenReturn(981.0);

            // Act
            List<HistoryBean> bookings = bookingService.getAllBookingsByCustomerId(customerEmail);

            // Assert
            assertNotNull(bookings);
            assertEquals(1, bookings.size());
            assertEquals(bookingDate, bookings.get(0).getDate());
        }
    }

    @Nested
    @DisplayName("Booking Creation Tests")
    class BookingCreationTests {
        @Test
        @DisplayName("Successful Booking Creation")
        public void testCreateHistory_Success() throws Exception {
            // Arrange
            HistoryBean details = createValidBookingDetails();

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);

            // Act
            HistoryBean createdHistory = bookingService.createHistory(details);

            // Assert
            assertNotNull(createdHistory);
            assertNotNull(createdHistory.getTransId());
            assertEquals(details.getMailId(), createdHistory.getMailId());
        }

        @Test
        @DisplayName("Booking Creation Fails - Database Error")
        public void testCreateHistory_DatabaseError() throws Exception {
            // Arrange
            HistoryBean details = createValidBookingDetails();

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database connection error"));

            // Act & Assert
            assertThrows(TrainException.class, () -> {
                bookingService.createHistory(details);
            });
        }

        @ParameterizedTest
        @DisplayName("Booking Creation with Invalid Data")
        @CsvSource({
            "'', 10001, 02-FEB-2024, HOWRAH, JODHPUR, 0, 0.0",
            "test@example.com, 0, 02-FEB-2024, HOWRAH, JODHPUR, -1, 100.0",
            "test@example.com, 10001, '', HOWRAH, JODHPUR, 2, 981.0"
        })
        public void testCreateHistory_InvalidData(
            String mailId, long trainNo, String date, 
            String fromStn, String toStn, int seats, double amount
        ) throws Exception {
            // Arrange
            HistoryBean details = new HistoryBean();
            details.setMailId(mailId);
            details.setTr_no(String.valueOf(trainNo));
            details.setDate(date);
            details.setFrom_stn(fromStn);
            details.setTo_stn(toStn);
            details.setSeats(seats);
            details.setAmount(amount);

            // Act & Assert
            assertThrows(TrainException.class, () -> {
                bookingService.createHistory(details);
            });
        }
    }

    // Helper Methods
    private HistoryBean createValidBookingDetails() {
        HistoryBean details = new HistoryBean();
        details.setMailId("test@example.com");
        details.setTr_no("10001");
        details.setDate(LocalDate.now().toString());
        details.setFrom_stn("HOWRAH");
        details.setTo_stn("JODHPUR");
        details.setSeats(2);
        details.setAmount(981.0);
        return details;
    }

    private void setupMockBookingHistoryResultSet(String customerEmail, int numberOfBookings) throws SQLException {
        when(DBUtil.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        
        // Setup result set to return multiple rows
        when(mockResultSet.next())
            .thenReturn(true)   // First booking
            .thenReturn(true)   // Second booking
            .thenReturn(true)   // Third booking
            .thenReturn(false); // End of results

        // Setup mock data for multiple bookings
        when(mockResultSet.getString("transid"))
            .thenReturn("BBC374-NSDF-4673")
            .thenReturn("BBC375-NSDF-4675")
            .thenReturn("BBC373-NSDF-4674");
        
        when(mockResultSet.getString("mailid"))
            .thenReturn(customerEmail)
            .thenReturn(customerEmail)
            .thenReturn(customerEmail);
        
        when(mockResultSet.getString("tr_no"))
            .thenReturn("10001")
            .thenReturn("10004")
            .thenReturn("10006");
        
        when(mockResultSet.getString("date"))
            .thenReturn("02-FEB-2024")
            .thenReturn("12-JAN-2024")
            .thenReturn("22-JULY-2024");
        
        when(mockResultSet.getString("from_stn"))
            .thenReturn("HOWRAH")
            .thenReturn("RANCHI")
            .thenReturn("PATNA");
        
        when(mockResultSet.getString("to_stn"))
            .thenReturn("JODHPUR")
            .thenReturn("PATNA")
            .thenReturn("DELHI");
        
        when(mockResultSet.getInt("seats"))
            .thenReturn(2)
            .thenReturn(1)
            .thenReturn(3);
        
        when(mockResultSet.getDouble("amount"))
            .thenReturn(981.0)
            .thenReturn(550.0)
            .thenReturn(4352.25);
    }
}
