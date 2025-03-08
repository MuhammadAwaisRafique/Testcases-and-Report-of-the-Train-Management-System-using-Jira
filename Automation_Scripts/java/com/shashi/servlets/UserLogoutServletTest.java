package com.shashi.servlets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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
class UserLogoutServletTest {

    private UserLogoutServlet servlet;

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
    @Mock
    private PrintWriter mockPrintWriter;

    @BeforeEach
    void setUp() throws IOException {
        servlet = new UserLogoutServlet();
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockRequest.getServletContext()).thenReturn(mockServletContext);
        when(mockResponse.getWriter()).thenReturn(mockPrintWriter);
        when(mockServletContext.getRequestDispatcher(anyString())).thenReturn(mockRequestDispatcher);
    }

    @Test
    @DisplayName("Successful User Logout")
    void testDoGet_Success() throws ServletException, IOException {
        // Arrange
        Cookie[] cookies = new Cookie[] {
            new Cookie("sessionIdForCUSTOMER", "someSessionId")
        };
        when(mockRequest.getCookies()).thenReturn(cookies);

        // Act
        servlet.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("successfully logged out"));
    }

    @Test
    @DisplayName("Logout When Already Logged Out")
    void testDoGet_AlreadyLoggedOut() throws ServletException, IOException {
        // Arrange
        when(mockRequest.getCookies()).thenReturn(null);

        // Act
        servlet.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("Already Logged Out"));
    }
}
