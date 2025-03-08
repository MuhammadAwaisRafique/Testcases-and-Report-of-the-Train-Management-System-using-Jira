package com.shashi.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.shashi.beans.TrainBean;
import com.shashi.beans.TrainException;
import com.shashi.service.TrainService;
import com.shashi.service.impl.TrainServiceImpl;
import com.shashi.utility.DBUtil;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TrainServiceIntegrationTest {

    private static TrainService trainService;
    private static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException {
        trainService = new TrainServiceImpl();
        connection = DBUtil.getConnection();
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testAddTrain() throws SQLException, TrainException {
        TrainBean newTrain = new TrainBean();
        newTrain.setTr_no(10008L);
        newTrain.setTr_name("Test Train");
        newTrain.setFrom_stn("A");
        newTrain.setTo_stn("B");
        newTrain.setSeats(100);
        newTrain.setFare(100.0);

        String result = trainService.addTrain(newTrain);
        assertTrue(result.contains("SUCCESS"));

        TrainBean retrievedTrain = trainService.getTrainById("10008");
        assertEquals(newTrain, retrievedTrain);
    }

    @Test
    void testGetAllTrains() throws SQLException, TrainException {
        List<TrainBean> trains = trainService.getAllTrains();
        assertFalse(trains.isEmpty());
    }

    @Test
    void testGetTrainById() throws SQLException, TrainException {
        TrainBean train = trainService.getTrainById("10001");
        assertNotNull(train);
    }

    @Test
    void testGetTrainsBetweenStations() throws SQLException, TrainException {
        List<TrainBean> trains = trainService.getTrainsBetweenStations("GAYA", "HOWRAH");
        assertFalse(trains.isEmpty());
    }

    @Test
    void testUpdateTrain() throws SQLException, TrainException {
        TrainBean train = trainService.getTrainById("10001");
        int initialSeats = train.getSeats();
        train.setSeats(initialSeats + 10);
        String result = trainService.updateTrain(train);
        assertTrue(result.contains("SUCCESS"));

        TrainBean updatedTrain = trainService.getTrainById("10001");
        assertEquals(initialSeats + 10, updatedTrain.getSeats());
    }

    @Test
    void testDeleteTrainById() throws SQLException, TrainException {
        String trainNoToDelete = "10007"; // Replace with a train ID that exists in your test DB
        TrainBean train = trainService.getTrainById(trainNoToDelete);
        assertNotNull(train);

        String result = trainService.deleteTrainById(trainNoToDelete);
        assertTrue(result.contains("SUCCESS"));

        assertNull(trainService.getTrainById(trainNoToDelete));
    }
}
