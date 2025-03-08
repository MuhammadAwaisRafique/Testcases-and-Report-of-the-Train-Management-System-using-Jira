package com.shashi.servlets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

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

import com.shashi.constant.UserRole;
import com.shashi.utility.TrainUtil;

@ExtendWith(MockitoExtension.class)
class BookTrainPaymentTest {

    private BookTrainPayment servlet;

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private ServletContext mockServletContext;
    @Mock
    private HttpSession mockSession;
    @Mock
    private RequestDispatcher mockRequestDispatcher;

    @BeforeEach
    void setUp() {
        servlet = new BookTrainPayment();
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockRequest.getServletContext()).thenReturn(mockServletContext);
    }

    @Test
    @DisplayName("Successful Payment Page Forward")
    void testDoPost_Success() throws ServletException, IOException {
        // Arrange
        int seats = 2;
        String trainNo = "10001";
        String journeyDate = "2024-03-15";
        String seatClass = "Sleeper";

        when(mockRequest.getParameter("seats")).thenReturn(String.valueOf(seats));
        when(mockRequest.getParameter("trainnumber")).thenReturn(trainNo);
        when(mockRequest.getParameter("journeydate")).thenReturn(journeyDate);
        when(mockRequest.getParameter("class")).thenReturn(seatClass);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockServletContext).setAttribute("seats", seats);
        verify(mockServletContext).setAttribute("trainnumber", trainNo);
        verify(mockServletContext).setAttribute("journeydate", journeyDate);
        verify(mockServletContext).setAttribute("class", seatClass);
        verify(mockRequest).getRequestDispatcher("Payment.html");
    }

    @Test
    @DisplayName("Payment Page Forward with Minimal Data")
    void testDoPost_MinimalData() throws ServletException, IOException {
        // Arrange
        when(mockRequest.getParameter("seats")).thenReturn("1");
        when(mockRequest.getParameter("trainnumber")).thenReturn("10001");
        when(mockRequest.getParameter("journeydate")).thenReturn("");
        when(mockRequest.getParameter("class")).thenReturn("");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockServletContext).setAttribute(eq("seats"), anyInt());
        verify(mockServletContext).setAttribute(eq("trainnumber"), anyString());
        verify(mockRequest).getRequestDispatcher("Payment.html");
    }
}
