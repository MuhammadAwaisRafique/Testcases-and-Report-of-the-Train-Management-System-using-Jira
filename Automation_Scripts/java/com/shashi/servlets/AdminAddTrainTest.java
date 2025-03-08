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

import com.shashi.beans.TrainBean;
import com.shashi.beans.UserBean;
import com.shashi.constant.UserRole;
import com.shashi.service.TrainService;
import com.shashi.service.impl.TrainServiceImpl;

@ExtendWith(MockitoExtension.class)
class AdminAddTrainTest {

    private AdminAddTrain servlet;

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private ServletContext mockServletContext;
    @Mock
    private TrainService mockTrainService;
    @Mock
    private RequestDispatcher mockRequestDispatcher;
    @Mock
    private PrintWriter mockPrintWriter;

    @BeforeEach
    void setUp() throws IOException {
        servlet = new AdminAddTrain();
        when(mockRequest.getServletContext()).thenReturn(mockServletContext);
        when(mockResponse.getWriter()).thenReturn(mockPrintWriter);
        when(mockServletContext.getRequestDispatcher(anyString())).thenReturn(mockRequestDispatcher);
    }

    @Test
    @DisplayName("Successful Train Addition")
    void testDoPost_Success() throws ServletException, IOException {
        // Arrange
        when(mockRequest.getParameter("trainno")).thenReturn("10008");
        when(mockRequest.getParameter("trainname")).thenReturn("Test Express");
        when(mockRequest.getParameter("fromstation")).thenReturn("HOWRAH");
        when(mockRequest.getParameter("tostation")).thenReturn("DELHI");
        when(mockRequest.getParameter("available")).thenReturn("150");
        when(mockRequest.getParameter("fare")).thenReturn("500.50");
        
        UserBean adminUser = new UserBean();
        when(mockServletContext.getAttribute(UserRole.ADMIN.toString())).thenReturn(adminUser);
        
        // Mock TrainService
        TrainService trainService = mock(TrainServiceImpl.class);
        when(trainService.addTrain(any(TrainBean.class))).thenReturn("SUCCESS");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("Train Added Successfully"));
    }

    @Test
    @DisplayName("Train Addition Fails - Invalid Input")
    void testDoPost_InvalidInput() throws ServletException, IOException {
        // Arrange
        when(mockRequest.getParameter("trainno")).thenReturn(""); // Invalid train number
        when(mockRequest.getParameter("trainname")).thenReturn("");
        
        UserBean adminUser = new UserBean();
        when(mockServletContext.getAttribute(UserRole.ADMIN.toString())).thenReturn(adminUser);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("Error in filling the train Detail"));
    }
}
