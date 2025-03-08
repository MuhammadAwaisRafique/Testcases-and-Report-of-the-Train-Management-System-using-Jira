package com.shashi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
class TrainServiceImplTest {

    private TrainServiceImpl trainService;

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        trainService = new TrainServiceImpl();
    }

    @Nested
    @DisplayName("Get Train By ID Tests")
    class GetTrainByIdTests {
        @Test
        @DisplayName("Successful Train Retrieval")
        void testGetTrainById_Success() throws SQLException, TrainException {
            // Arrange
            String trainNo = "10001";
            TrainBean expectedTrain = new TrainBean();
            expectedTrain.setTr_no(10001L);
            expectedTrain.setTr_name("JODHPUR EXP");
            expectedTrain.setFrom_stn("HOWRAH");
            expectedTrain.setTo_stn("JODHPUR");
            expectedTrain.setSeats(152);
            expectedTrain.setFare(490.50);

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong("tr_no")).thenReturn(expectedTrain.getTr_no());
            when(mockResultSet.getString("tr_name")).thenReturn(expectedTrain.getTr_name());
            when(mockResultSet.getString("from_stn")).thenReturn(expectedTrain.getFrom_stn());
            when(mockResultSet.getString("to_stn")).thenReturn(expectedTrain.getTo_stn());
            when(mockResultSet.getInt("seats")).thenReturn(expectedTrain.getSeats());
            when(mockResultSet.getDouble("fare")).thenReturn(expectedTrain.getFare());

            // Act
            TrainBean retrievedTrain = trainService.getTrainById(trainNo);

            // Assert
            assertEquals(expectedTrain, retrievedTrain);
        }

        @Test
        @DisplayName("Train Not Found")
        void testGetTrainById_TrainNotFound() throws SQLException, TrainException {
            // Arrange
            String trainNo = "99999";

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            // Act
            TrainBean retrievedTrain = trainService.getTrainById(trainNo);

            // Assert
            assertNull(retrievedTrain);
        }

        @Test
        @DisplayName("Database Error")
        void testGetTrainById_DatabaseError() throws SQLException, TrainException {
            // Arrange
            String trainNo = "10001";
            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Act & Assert
            assertThrows(TrainException.class, () -> trainService.getTrainById(trainNo));
        }
    }

    @Nested
    @DisplayName("Add Train Tests")
    class AddTrainTests {
        @Test
        @DisplayName("Successful Train Addition")
        void testAddTrain_Success() throws SQLException, TrainException {
            // Arrange
            TrainBean train = new TrainBean();
            train.setTr_no(10007L);
            train.setTr_name("TEST TRAIN");
            train.setFrom_stn("TEST FROM");
            train.setTo_stn("TEST TO");
            train.setSeats(100);
            train.setFare(500.0);

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // Act
            String result = trainService.addTrain(train);

            // Assert
            assertEquals("SUCCESS", result);
        }

        @Test
        @DisplayName("Add Train Fails - Duplicate Train Number")
        void testAddTrain_DuplicateTrainNumber() throws SQLException, TrainException {
            // Arrange
            TrainBean train = new TrainBean();
            train.setTr_no(10001L);
            train.setTr_name("Duplicate Train");
            train.setFrom_stn("From");
            train.setTo_stn("To");
            train.setSeats(100);
            train.setFare(500.0);

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("ORA-00001: unique constraint violated"));

            // Act
            String result = trainService.addTrain(train);

            // Assert
            assertFalse(result.contains("SUCCESS"));
        }

        @ParameterizedTest
        @DisplayName("Invalid Train Data")
        @CsvSource({
            "0, , , , -1, -1.0",
            "-100, ValidName, FromStation, ToStation, 0, 100.0",
            "10001, , FromStation, ToStation, 100, 100.0"
        })
        void testAddTrain_InvalidData(long trainNo, String trainName, String fromStation, String toStation, int seats, double fare) throws SQLException, TrainException {
            // Arrange
            TrainBean train = new TrainBean();
            train.setTr_no(trainNo);
            train.setTr_name(trainName);
            train.setFrom_stn(fromStation);
            train.setTo_stn(toStation);
            train.setSeats(seats);
            train.setFare(fare);

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

            // Act
            String result = trainService.addTrain(train);

            // Assert
            assertFalse(result.contains("SUCCESS"));
        }
    }

    @Nested
    @DisplayName("Update Train Tests")
    class UpdateTrainTests {
        @Test
        @DisplayName("Successful Train Update")
        void testUpdateTrain_Success() throws SQLException, TrainException {
            // Arrange
            TrainBean train = new TrainBean();
            train.setTr_no(10001L);
            train.setTr_name("Updated Train");
            train.setFrom_stn("Updated From");
            train.setTo_stn("Updated To");
            train.setSeats(120);
            train.setFare(550.0);

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // Act
            String result = trainService.updateTrain(train);

            // Assert
            assertEquals("SUCCESS", result);
        }

        @Test
        @DisplayName("Update Fails - Train Not Found")
        void testUpdateTrain_TrainNotFound() throws SQLException, TrainException {
            // Arrange
            TrainBean train = new TrainBean();
            train.setTr_no(99999L);
            train.setTr_name("Updated Train");
            train.setFrom_stn("Updated From");
            train.setTo_stn("Updated To");
            train.setSeats(120);
            train.setFare(550.0);

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(0);

            // Act
            String result = trainService.updateTrain(train);

            // Assert
            assertNotEquals("SUCCESS", result);
        }

        @Test
        @DisplayName("Database Error During Update")
        void testUpdateTrain_DatabaseError() throws SQLException, TrainException {
            // Arrange
            TrainBean train = new TrainBean();
            train.setTr_no(10001L);
            train.setTr_name("Updated Train");
            train.setFrom_stn("Updated From");
            train.setTo_stn("Updated To");
            train.setSeats(120);
            train.setFare(550.0);

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Act & Assert
            assertThrows(TrainException.class, () -> trainService.updateTrain(train));
        }
    }

    @Nested
    @DisplayName("Delete Train Tests")
    class DeleteTrainTests {
        @Test
        @DisplayName("Successful Train Deletion")
        void testDeleteTrainById_Success() throws SQLException, TrainException {
            // Arrange
            String trainNo = "10001";

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // Act
            String result = trainService.deleteTrainById(trainNo);

            // Assert
            assertEquals("SUCCESS", result);
        }

        @Test
        @DisplayName("Delete Fails - Train Not Found")
        void testDeleteTrainById_TrainNotFound() throws SQLException, TrainException {
            // Arrange
            String trainNo = "99999";

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(0);

            // Act
            String result = trainService.deleteTrainById(trainNo);

            // Assert
            assertNotEquals("SUCCESS", result);
        }

        @Test
        @DisplayName("Database Error During Deletion")
        void testDeleteTrainById_DatabaseError() throws SQLException, TrainException {
            // Arrange
            String trainNo = "10001";

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Act & Assert
            assertThrows(TrainException.class, () -> trainService.deleteTrainById(trainNo));
        }
    }

    @Nested
    @DisplayName("Get All Trains Tests")
    class GetAllTrainsTests {
        @Test
        @DisplayName("Retrieve All Trains Successfully")
        void testGetAllTrains_Success() throws SQLException, TrainException {
            // Arrange
            List<TrainBean> expectedTrains = new ArrayList<>();
            expectedTrains.add(new TrainBean());
            expectedTrains.add(new TrainBean());

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

            // Act
            List<TrainBean> trains = trainService.getAllTrains();

            // Assert
            assertEquals(2, trains.size());
        }

        @Test
        @DisplayName("No Trains Found")
        void testGetAllTrains_NoTrains() throws SQLException, TrainException {
            // Arrange
            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            // Act
            List<TrainBean> trains = trainService.getAllTrains();

            // Assert
            assertTrue(trains.isEmpty());
        }

        @Test
        @DisplayName("Database Error")
        void testGetAllTrains_DatabaseError() throws SQLException, TrainException {
            // Arrange
            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Act & Assert
            assertThrows(TrainException.class, () -> trainService.getAllTrains());
        }
    }

    @Nested
    @DisplayName("Get Trains Between Stations Tests")
    class GetTrainsBetweenStationsTests {
        @Test
        @DisplayName("Retrieve Trains Between Stations Successfully")
        void testGetTrainsBetweenStations_Success() throws SQLException, TrainException {
            // Arrange
            String fromStation = "HOWRAH";
            String toStation = "JODHPUR";
            List<TrainBean> expectedTrains = new ArrayList<>();
            expectedTrains.add(new TrainBean());

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true).thenReturn(false);

            // Act
            List<TrainBean> trains = trainService.getTrainsBetweenStations(fromStation, toStation);

            // Assert
            assertEquals(1, trains.size());
        }

        @Test
        @DisplayName("No Trains Found Between Stations")
        void testGetTrainsBetweenStations_NoTrains() throws SQLException, TrainException {
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

        @Test
        @DisplayName("Database Error")
        void testGetTrainsBetweenStations_DatabaseError() throws SQLException, TrainException {
            // Arrange
            String fromStation = "HOWRAH";
            String toStation = "JODHPUR";

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Act & Assert
            assertThrows(TrainException.class, () -> trainService.getTrainsBetweenStations(fromStation, toStation));
        }
    }
}
