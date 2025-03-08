package com.shashi.servlets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
import com.shashi.constant.UserRole;
import com.shashi.service.BookingService;
import com.shashi.service.impl.BookingServiceImpl;
import com.shashi.utility.TrainUtil;

@ExtendWith(MockitoExtension.class)
class TicketBookingHistoryTest {

    private TicketBookingHistory servlet;

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private ServletContext mockServletContext;
    @Mock
    private HttpSession mockSession;
    @Mock
    private BookingService mockBookingService;
    @Mock
    private RequestDispatcher mockRequestDispatcher;
    @Mock
    private PrintWriter mockPrintWriter;

    @BeforeEach
    void setUp() throws IOException {
        servlet = new TicketBookingHistory();
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockRequest.getServletContext()).thenReturn(mockServletContext);
        when(mockResponse.getWriter()).thenReturn(mockPrintWriter);
        when(mockServletContext.getRequestDispatcher(anyString())).thenReturn(mockRequestDispatcher);
    }

    @Test
    @DisplayName("Successful Booking History Retrieval")
    void testDoGet_Success() throws ServletException, IOException {
        // Arrange
        String customerId = "test@example.com";
        List<HistoryBean> bookings = new ArrayList<>();
        HistoryBean booking1 = new HistoryBean();
        booking1.setTransId("TRANS1");
        booking1.setTr_no("10001");
        bookings.add(booking1);

        when(mockSession.getAttribute("mailid")).thenReturn(customerId);
        
        BookingService bookingService = mock(BookingServiceImpl.class);
        when(bookingService.getAllBookingsByCustomerId(customerId)).thenReturn(bookings);

        // Act
        servlet.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("Booked Ticket History"));
    }

    @Test
    @DisplayName("No Booking History Found")
    void testDoGet_NoBookings() throws ServletException, IOException {
        // Arrange
        String customerId = "empty@example.com";
        List<HistoryBean> bookings = new ArrayList<>();

        when(mockSession.getAttribute("mailid")).thenReturn(customerId);
        
        BookingService bookingService = mock(BookingServiceImpl.class);
        when(bookingService.getAllBookingsByCustomerId(customerId)).thenReturn(bookings);

        // Act
        servlet.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("No any ticket booked"));
    }
}
