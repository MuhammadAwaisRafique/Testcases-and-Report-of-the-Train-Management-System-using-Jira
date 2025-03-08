package com.shashi.servlets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.shashi.service.impl.BookingServiceImpl;
import com.shashi.service.impl.TrainServiceImpl;
import com.shashi.utility.TrainUtil;

@ExtendWith(MockitoExtension.class)
class BookTrainsTest {

    private BookTrains servlet;

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private ServletContext mockServletContext;
    @Mock
    private HttpSession mockSession;
    @Mock
    private TrainService mockTrainService;
    @Mock
    private BookingService mockBookingService;
    @Mock
    private RequestDispatcher mockRequestDispatcher;
    @Mock
    private PrintWriter mockPrintWriter;

    @BeforeEach
    void setUp() throws IOException {
        servlet = new BookTrains();
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockRequest.getServletContext()).thenReturn(mockServletContext);
        when(mockResponse.getWriter()).thenReturn(mockPrintWriter);
        when(mockServletContext.getRequestDispatcher(anyString())).thenReturn(mockRequestDispatcher);
    }

    @Test
    @DisplayName("Successful Train Booking")
    void testDoPost_Success() throws ServletException, IOException {
        // Arrange
        TrainBean mockTrain = new TrainBean();
        mockTrain.setTr_no(10001L);
        mockTrain.setSeats(100);
        mockTrain.setFare(500.50);

        UserBean mockUser = new UserBean();
        mockUser.setMailId("test@example.com");

        when(mockServletContext.getAttribute("seats")).thenReturn(2);
        when(mockServletContext.getAttribute("trainnumber")).thenReturn("10001");
        when(mockServletContext.getAttribute("journeydate")).thenReturn("2024-03-15");
        when(mockServletContext.getAttribute("class")).thenReturn("Sleeper");

        when(mockTrainService.getTrainById("10001")).thenReturn(mockTrain);
        when(mockSession.getAttribute("mailid")).thenReturn("test@example.com");

        HistoryBean mockBooking = new HistoryBean();
        mockBooking.setTransId("TEST-BOOKING-ID");
        when(mockBookingService.createHistory(any(HistoryBean.class))).thenReturn(mockBooking);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockBookingService).createHistory(any(HistoryBean.class));
        verify(mockPrintWriter).println(contains("Seats Booked Successfully"));
    }

    @Test
    @DisplayName("Booking Fails - Insufficient Seats")
    void testDoPost_InsufficientSeats() throws ServletException, IOException {
        // Arrange
        TrainBean mockTrain = new TrainBean();
        mockTrain.setTr_no(10001L);
        mockTrain.setSeats(1); // Only 1 seat available

        when(mockServletContext.getAttribute("seats")).thenReturn(2);
        when(mockServletContext.getAttribute("trainnumber")).thenReturn("10001");

        when(mockTrainService.getTrainById("10001")).thenReturn(mockTrain);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockPrintWriter).println(contains("Only 1 Seats are Available"));
    }
}
