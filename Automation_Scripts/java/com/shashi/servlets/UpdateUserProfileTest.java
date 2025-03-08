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
import com.shashi.constant.UserRole;
import com.shashi.service.UserService;
import com.shashi.service.impl.UserServiceImpl;
import com.shashi.utility.TrainUtil;

@ExtendWith(MockitoExtension.class)
class UpdateUserProfileTest {

    private UpdateUserProfile servlet;

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
        servlet = new UpdateUserProfile();
        when(mockResponse.getWriter()).thenReturn(mockPrintWriter);
        when(mockRequest.getServletContext()).thenReturn(mockServletContext);
        when(mockServletContext.getRequestDispatcher(anyString())).thenReturn(mockRequestDispatcher);
    }

    @Test
    @DisplayName("Successful User Profile Update")
    void testDoPost_Success() throws ServletException, IOException {
        // Arrange
        UserBean existingUser = new UserBean();
        existingUser.setMailId("test@example.com");
        
        when(mockRequest.getParameter("firstname")).thenReturn("UpdatedFirstName");
        when(mockRequest.getParameter("lastname")).thenReturn("UpdatedLastName");
        when(mockRequest.getParameter("address")).thenReturn("Updated Address");
        when(mockRequest.getParameter("phone")).thenReturn("9876543210");
        when(mockRequest.getParameter("mail")).thenReturn("test@example.com");
        
        when(mockServletContext.getAttribute(UserRole.CUSTOMER.toString())).thenReturn(existingUser);

        UserService userService = mock(UserServiceImpl.class);
        when(userService.updateUser(any(UserBean.class))).thenReturn("SUCCESS");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("Profile has Been Successfully Updated"));
    }

    @Test
    @DisplayName("User Profile Update Fails - Invalid Information")
    void testDoPost_InvalidInformation() throws ServletException, IOException {
        // Arrange
        UserBean existingUser = new UserBean();
        existingUser.setMailId("test@example.com");
        
        when(mockRequest.getParameter("firstname")).thenReturn("");
        when(mockRequest.getParameter("lastname")).thenReturn("");
        when(mockRequest.getParameter("address")).thenReturn("");
        when(mockRequest.getParameter("phone")).thenReturn("");
        
        when(mockServletContext.getAttribute(UserRole.CUSTOMER.toString())).thenReturn(existingUser);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("Please Enter the valid Information"));
    }
}
