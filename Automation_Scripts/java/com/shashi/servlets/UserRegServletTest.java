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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shashi.beans.UserBean;
import com.shashi.service.UserService;
import com.shashi.service.impl.UserServiceImpl;
import com.shashi.constant.UserRole;

@ExtendWith(MockitoExtension.class)
class UserRegServletTest {

    private UserRegServlet servlet;

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private ServletContext mockServletContext;
    @Mock
    private UserService mockUserService;
    @Mock
    private RequestDispatcher mockRequestDispatcher;
    @Mock
    private PrintWriter mockPrintWriter;

    @BeforeEach
    void setUp() throws IOException {
        servlet = new UserRegServlet();
        when(mockResponse.getWriter()).thenReturn(mockPrintWriter);
        when(mockRequest.getServletContext()).thenReturn(mockServletContext);
        when(mockServletContext.getRequestDispatcher(anyString())).thenReturn(mockRequestDispatcher);
    }

    @Test
    @DisplayName("Successful User Registration")
    void testDoPost_Success() throws ServletException, IOException {
        // Arrange
        when(mockRequest.getParameter("mailid")).thenReturn("test@example.com");
        when(mockRequest.getParameter("pword")).thenReturn("password123");
        when(mockRequest.getParameter("firstname")).thenReturn("John");
        when(mockRequest.getParameter("lastname")).thenReturn("Doe");
        when(mockRequest.getParameter("address")).thenReturn("123 Test St");
        when(mockRequest.getParameter("phoneno")).thenReturn("1234567890");

        UserService userService = mock(UserServiceImpl.class);
        when(userService.registerUser(any(UserBean.class))).thenReturn("SUCCESS");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("User Registered Successfully"));
    }

    @Test
    @DisplayName("User Registration Fails - Duplicate Email")
    void testDoPost_DuplicateEmail() throws ServletException, IOException {
        // Arrange
        when(mockRequest.getParameter("mailid")).thenReturn("existing@example.com");
        when(mockRequest.getParameter("pword")).thenReturn("password123");
        when(mockRequest.getParameter("firstname")).thenReturn("John");
        when(mockRequest.getParameter("lastname")).thenReturn("Doe");
        when(mockRequest.getParameter("address")).thenReturn("123 Test St");
        when(mockRequest.getParameter("phoneno")).thenReturn("1234567890");

        UserService userService = mock(UserServiceImpl.class);
        when(userService.registerUser(any(UserBean.class))).thenReturn("FAILURE: User already exists");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("already registered"));
    }

    @Test
    @DisplayName("User Registration Fails - Invalid Input")
    void testDoPost_InvalidInput() throws ServletException, IOException {
        // Arrange
        when(mockRequest.getParameter("mailid")).thenReturn("");
        when(mockRequest.getParameter("pword")).thenReturn("");
        when(mockRequest.getParameter("firstname")).thenReturn("");
        when(mockRequest.getParameter("lastname")).thenReturn("");
        when(mockRequest.getParameter("address")).thenReturn("");
        when(mockRequest.getParameter("phoneno")).thenReturn("");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("Please Enter the valid Information"));
    }
}
