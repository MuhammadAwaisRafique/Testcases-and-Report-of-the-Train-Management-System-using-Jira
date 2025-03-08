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
import com.shashi.service.TrainService;
import com.shashi.service.impl.TrainServiceImpl;

@ExtendWith(MockitoExtension.class)
class AdminCancleTrainTest {

    private AdminCancleTrain servlet;

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
        servlet = new AdminCancleTrain();
        when(mockRequest.getServletContext()).thenReturn(mockServletContext);
        when(mockResponse.getWriter()).thenReturn(mockPrintWriter);
        when(mockServletContext.getRequestDispatcher(anyString())).thenReturn(mockRequestDispatcher);
    }

    @Test
    @DisplayName("Successful Train Cancellation")
    void testDoPost_Success() throws ServletException, IOException {
        // Arrange
        String trainNo = "10001";
        UserBean adminUser = new UserBean();
        
        when(mockRequest.getParameter("trainno")).thenReturn(trainNo);
        when(mockServletContext.getAttribute(UserRole.ADMIN.toString())).thenReturn(adminUser);
        
        TrainService trainService = mock(TrainServiceImpl.class);
        when(trainService.deleteTrainById(trainNo)).thenReturn("SUCCESS");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("Deleted Successfully"));
    }

    @Test
    @DisplayName("Train Cancellation Fails - Train Not Found")
    void testDoPost_TrainNotFound() throws ServletException, IOException {
        // Arrange
        String trainNo = "99999";
        UserBean adminUser = new UserBean();
        
        when(mockRequest.getParameter("trainno")).thenReturn(trainNo);
        when(mockServletContext.getAttribute(UserRole.ADMIN.toString())).thenReturn(adminUser);
        
        TrainService trainService = mock(TrainServiceImpl.class);
        when(trainService.deleteTrainById(trainNo)).thenReturn("FAILURE");

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("is Not Available"));
    }
}
