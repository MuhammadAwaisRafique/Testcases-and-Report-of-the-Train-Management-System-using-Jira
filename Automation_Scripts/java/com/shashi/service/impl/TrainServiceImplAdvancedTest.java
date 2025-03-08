package com.shashi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shashi.beans.TrainBean;
import com.shashi.beans.TrainException;
import com.shashi.utility.DBUtil;

@ExtendWith(MockitoExtension.class)
public class TrainServiceImplAdvancedTest {

    private TrainServiceImpl trainService;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() {
        trainService = new TrainServiceImpl();
    }

    @Nested
    @DisplayName("Train Addition Tests")
    class TrainAdditionTests {
        @Test
        @DisplayName("Successful Train Addition")
        public void testAddTrain_Success() throws Exception {
            // Arrange
            TrainBean train = createValidTrainBean();

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);

            // Act
            String result = trainService.addTrain(train);

            // Assert
            assertTrue(result.contains("SUCCESS"));
            verify(mockPreparedStatement).setLong(1, train.getTr_no());
        }

        @Test
        @DisplayName("Train Addition Fails - Duplicate Train Number")
        public void testAddTrain_DuplicateTrainNumber() throws Exception {
            // Arrange
            TrainBean train = createValidTrainBean();

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("ORA-00001: unique constraint violated"));

            // Act
            String result = trainService.addTrain(train);

            // Assert
            assertTrue(result.contains("FAILURE"));
        }

        @ParameterizedTest
        @DisplayName("Invalid Train Data")
        @CsvSource({
            "0, '', '', '', -1, -1.0",
            "-100, ValidName, FromStation, ToStation, 0, 100.0",
            "10001, '', FromStation, ToStation, 100, 100.0"
        })
        public void testAddTrain_InvalidData(
            long trainNo, String trainName, String fromStation, 
            String toStation, int seats, double fare
        ) throws Exception {
            // Arrange
            TrainBean train = new TrainBean();
            train.setTr_no(trainNo);
            train.setTr_name(trainName);
            train.setFrom_stn(fromStation);
            train.setTo_stn(toStation);
            train.setSeats(seats);
            train.setFare(fare);

            // Act
            String result = trainService.addTrain(train);

            // Assert
            assertFalse(result.contains("SUCCESS"));
        }
    }

    @Nested
    @DisplayName("Train Retrieval Tests")
    class TrainRetrievalTests {
        @Test
        @DisplayName("Get Train by Existing ID")
        public void testGetTrainById_Exists() throws Exception {
            // Arrange
            String trainNo = "10001";
            
            setupMockTrainResultSet(trainNo, true);

            // Act
            TrainBean train = trainService.getTrainById(trainNo);

            // Assert
            assertNotNull(train);
            assertEquals(10001L, train.getTr_no());
        }

        @Test
        @DisplayName("Get Train by Non-Existing ID")
        public void testGetTrainById_NotExists() throws Exception {
            // Arrange
            String trainNo = "99999";
            
            setupMockTrainResultSet(trainNo, false);

            // Act
            TrainBean train = trainService.getTrainById(trainNo);

            // Assert
            assertNull(train);
        }

        @Test
        @DisplayName("Get Trains Between Stations")
        public void testGetTrainsBetweenStations_MultipleTrains() throws Exception {
            // Arrange
            String fromStation = "HOWRAH";
            String toStation = "JODHPUR";
            
            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            
            when(mockResultSet.next())
                .thenReturn(true)   // First train
                .thenReturn(true)   // Second train
                .thenReturn(false); // End of results

            setupMultipleTrainResultSet();

            // Act
            List<TrainBean> trains = trainService.getTrainsBetweenStations(fromStation, toStation);

            // Assert
            assertNotNull(trains);
            assertEquals(2, trains.size());
        }

        @Test
        @DisplayName("No Trains Between Stations")
        public void testGetTrainsBetweenStations_NoTrains() throws Exception {
            // Arrange
            String fromStation = "UNKNOWN";
            String toStation = "NOWHERE";
            
            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            
            when(mockResultSet.next()).thenReturn(false);

            // Act
            List<TrainBean> trains = trainService.getTrainsBetweenStations(fromStation, toStation);

            // Assert
            assertTrue(trains.isEmpty());
        }
    }

    @Nested
    @DisplayName("Train Update and Deletion Tests")
    class TrainModificationTests {
        @Test
        @DisplayName("Successful Train Update")
        public void testUpdateTrain_Success() throws Exception {
            // Arrange
            TrainBean train = createValidTrainBean();

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);

            // Act
            String result = trainService.updateTrain(train);

            // Assert
            assertEquals("SUCCESS", result);
        }

        @Test
        @DisplayName("Train Update Fails")
        public void testUpdateTrain_Failure() throws Exception {
            // Arrange
            TrainBean train = createValidTrainBean();

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            // Act
            String result = trainService.updateTrain(train);

            // Assert
            assertNotEquals("SUCCESS", result);
        }

        @Test
        @DisplayName("Successful Train Deletion")
        public void testDeleteTrainById_Success() throws Exception {
            // Arrange
            String trainNo = "10001";

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // Act
            String result = trainService.deleteTrainById(trainNo);

            // Assert
            assertTrue(result.contains("SUCCESS"));
        }

        @Test
        @DisplayName("Train Deletion Fails - Train Not Found")
        public void testDeleteTrainById_NotFound() throws Exception {
            // Arrange
            String trainNo = "99999";

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(0);

            // Act
            String result = trainService.deleteTrainById(trainNo);

            // Assert
            assertFalse(result.contains("SUCCESS"));
        }
    }

    // Helper Methods
    private TrainBean createValidTrainBean() {
        TrainBean train = new TrainBean();
        train.setTr_no(10001L);
        train.setTr_name("TEST EXPRESS");
        train.setFrom_stn("HOWRAH");
        train.setTo_stn("JODHPUR");
        train.setSeats(150);
        train.setFare(500.50);
        return train;
    }

    private void setupMockTrainResultSet(String trainNo, boolean exists) throws SQLException {
        when(DBUtil.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        
        when(mockResultSet.next()).thenReturn(exists);
        
        if (exists) {
            when(mockResultSet.getLong("tr_no")).thenReturn(Long.parseLong(trainNo));
            when(mockResultSet.getString("tr_name")).thenReturn("TEST EXPRESS");
            when(mockResultSet.getString("from_stn")).thenReturn("HOWRAH");
            when(mockResultSet.getString("to_stn")).thenReturn("JODHPUR");
            when(mockResultSet.getInt("seats")).thenReturn(150);
            when(mockResultSet.getDouble("fare")).thenReturn(500.50);
        }
    }

    private void setupMultipleTrainResultSet() throws SQLException {
        when(mockResultSet.getLong("tr_no"))
            .thenReturn(10001L)
            .thenReturn(10002L);
        when(mockResultSet.getString("tr_name"))
            .thenReturn("JODHPUR EXP")
            .thenReturn("YAMUNA EXP");
        when(mockResultSet.getString("from_stn"))
            .thenReturn("HOWRAH")
            .thenReturn("GAYA");
        when(mockResultSet.getString("to_stn"))
            .thenReturn("JODHPUR")
            .thenReturn("DELHI");
        when(mockResultSet.getInt("seats"))
            .thenReturn(152)
            .thenReturn(52);
        when(mockResultSet.getDouble("fare"))
            .thenReturn(490.50)
            .thenReturn(550.50);
    }
}
