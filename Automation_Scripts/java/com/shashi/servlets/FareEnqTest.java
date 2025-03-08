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

import com.shashi.beans.TrainBean;
import com.shashi.constant.UserRole;
import com.shashi.service.TrainService;
import com.shashi.service.impl.TrainServiceImpl;
import com.shashi.utility.TrainUtil;

@ExtendWith(MockitoExtension.class)
class FareEnqTest {

    private FareEnq servlet;

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
    private RequestDispatcher mockRequestDispatcher;
    @Mock
    private PrintWriter mockPrintWriter;

    @BeforeEach
    void setUp() throws IOException {
        servlet = new FareEnq();
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockRequest.getServletContext()).thenReturn(mockServletContext);
        when(mockResponse.getWriter()).thenReturn(mockPrintWriter);
        when(mockServletContext.getRequestDispatcher(anyString())).thenReturn(mockRequestDispatcher);
    }

    @Test
    @DisplayName("Successful Fare Enquiry")
    void testDoPost_Success() throws ServletException, IOException {
        // Arrange
        List<TrainBean> mockTrains = new ArrayList<>();
        TrainBean train = new TrainBean();
        train.setTr_no(10001L);
        train.setTr_name("Test Express");
        train.setFrom_stn("HOWRAH");
        train.setTo_stn("DELHI");
        train.setFare(500.50);
        mockTrains.add(train);

        when(mockRequest.getParameter("fromstation")).thenReturn("HOWRAH");
        when(mockRequest.getParameter("tostation")).thenReturn("DELHI");
        
        TrainService trainService = mock(TrainServiceImpl.class);
        when(trainService.getTrainsBetweenStations("HOWRAH", "DELHI")).thenReturn(mockTrains);

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("Fare for Trains BetWeen Station"));
    }

    @Test
    @DisplayName("Fare Enquiry - No Trains Found")
    void testDoPost_NoTrainsFound() throws ServletException, IOException {
        // Arrange
        when(mockRequest.getParameter("fromstation")).thenReturn("UNKNOWN");
        when(mockRequest.getParameter("tostation")).thenReturn("NOWHERE");
        
        TrainService trainService = mock(TrainServiceImpl.class);
        when(trainService.getTrainsBetweenStations("UNKNOWN", "NOWHERE")).thenReturn(new ArrayList<>());

        // Act
        servlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockRequestDispatcher).include(any(), any());
        verify(mockPrintWriter).println(contains("There are no trains Between"));
    }
}
