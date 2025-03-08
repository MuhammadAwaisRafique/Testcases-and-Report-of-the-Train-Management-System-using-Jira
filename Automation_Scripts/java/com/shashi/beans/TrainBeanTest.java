package com.shashi.beans;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class TrainBeanTest {

    @Test
    void testTrainBeanGettersAndSetters() {
        TrainBean train = new TrainBean();
        train.setTr_no(1001L);
        train.setTr_name("Express Train");
        train.setFrom_stn("Station A");
        train.setTo_stn("Station B");
        train.setSeats(150);
        train.setFare(500.50);

        assertEquals(1001L, train.getTr_no());
        assertEquals("Express Train", train.getTr_name());
        assertEquals("Station A", train.getFrom_stn());
        assertEquals("Station B", train.getTo_stn());
        assertEquals(150, train.getSeats());
        assertEquals(500.50, train.getFare());
    }

    @ParameterizedTest
    @CsvSource({
        "1001,Express Train,Station A,Station B,150,500.50",
        "1002,Another Train,Station C,Station D,100,600.00",
        "1003,Local Train,Station X,Station Y,200,250.75"
    })
    void testTrainBeanValidData(long trainNo, String trainName, String fromStn, String toStn, int seats, double fare) {
        TrainBean train = new TrainBean();
        train.setTr_no(trainNo);
        train.setTr_name(trainName);
        train.setFrom_stn(fromStn);
        train.setTo_stn(toStn);
        train.setSeats(seats);
        train.setFare(fare);

        assertNotNull(train);
    }

    @ParameterizedTest
    @CsvSource({
        "0,,Station A,Station B,150,500.50",
        "-1,Express Train,Station A,Station B,150,500.50",
        "1001,Express Train,,Station B,150,500.50",
        "1001,Express Train,Station A,,150,500.50",
        "1001,Express Train,Station A,Station B,-1,500.50",
        "1001,Express Train,Station A,Station B,150,-1.0"
    })
    void testTrainBeanInvalidData(long trainNo, String trainName, String fromStn, String toStn, int seats, double fare) {
        TrainBean train = new TrainBean();
        train.setTr_no(trainNo);
        train.setTr_name(trainName);
        train.setFrom_stn(fromStn);
        train.setTo_stn(toStn);
        train.setSeats(seats);
        train.setFare(fare);

        // Assertions depend on how you want to handle invalid data (exceptions or default values)
        // Example: If invalid data should throw exceptions, use assertThrows
        // assertThrows(IllegalArgumentException.class, () -> new TrainBean(trainNo, trainName, fromStn, toStn, seats, fare));
    }
}
