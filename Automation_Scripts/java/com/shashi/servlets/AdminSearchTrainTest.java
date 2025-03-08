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

import com.shashi.beans.TrainBean;
import com.shashi.beans.UserBean;
import com.shashi.constant.UserRole;
import com.shashi.service.TrainService;
import com.shashi.service.impl.TrainServiceImpl;

@ExtendWith(MockitoExtension.class)
class AdminSearchTrainTest {

    private AdminSearchTrain servlet;

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
        servlet = new AdminSearchTrain();
        when(mockRequest.getServletContext()).thenReturn(mockServletContext);
        when(mockResponse.getWriter()).thenReturn(mockPrintWriter);
        when(mockServletContext.getRequestDispatcher(anyString())).thenReturn(mockRequestDispatcher);
    }

    @Test
    @DisplayName("Successful Train Search")
    void testDoPost_Success() throws ServletException, IOException {
        // Arrange
        String trainNo = "10001";
        TrainBean mockTrain = new TrainBean();
        mockTrain.setTr_no(10001L);
        mockTrain.setTr_name("Test Express");
        mockTrain.setFrom_stn("HOWRAH");
        mockTrain.setTo_stn("DELHI");
        mockTrain.setSeats(100);
        mockTrain.setFare(500.50);

        UserBean adminUser = new UserBean();
        
        when(mockRequest.getParameter("trainnumber")).thenReturn(trainNo);
        when(mockServletContext.getAttribute(UserRole.ADMIN.toString())).thenReturn(adminUser);
        
        TrainService trainService = mock(TrainServiceImpl.class);
        when(trainService.getTrainById(trainNo)).thenReturn(mockTrain);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("Searched Train Detail"));
    }

    @Test
    @DisplayName("Train Search Fails - Train Not Found")
    void testDoPost_TrainNotFound() throws ServletException, IOException {
        // Arrange
        String trainNo = "99999";
        UserBean adminUser = new UserBean();
        
        when(mockRequest.getParameter("trainnumber")).thenReturn(trainNo);
        when(mockServletContext.getAttribute(UserRole.ADMIN.toString())).thenReturn(adminUser);
        
        TrainService trainService = mock(TrainServiceImpl.class);
        when(trainService.getTrainById(trainNo)).thenReturn(null);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("is Not Available"));
    }
}
