package com.shashi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shashi.beans.TrainBean;

@ExtendWith(MockitoExtension.class)
public class TrainServiceConcurrencyTest {

    private TrainServiceImpl trainService = new TrainServiceImpl();

    @Test
    @DisplayName("Concurrent Train Updates Test")
    void testConcurrentTrainUpdates() throws Exception {
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        TrainBean train = new TrainBean();
        train.setTr_no(10001L);
        train.setTr_name("TEST EXPRESS");
        train.setFrom_stn("HOWRAH");
        train.setTo_stn("JODHPUR");
        train.setSeats(100);
        train.setFare(500.50);

        // Simulate multiple concurrent updates
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadNum = i;
            executorService.submit(() -> {
                try {
                    train.setSeats(train.getSeats() - 1);
                    trainService.updateTrain(train);
                } catch (Exception e) {
                    fail("Concurrent update failed for thread " + threadNum);
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to complete
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executorService.shutdown();

        // Verify final state
        TrainBean updatedTrain = trainService.getTrainById("10001");
        assertEquals(90, updatedTrain.getSeats());
    }

    @Test
    @DisplayName("Concurrent Train Searches Test")
    void testConcurrentTrainSearches() throws Exception {
        int numberOfThreads = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // Simulate multiple concurrent searches
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    trainService.getTrainsBetweenStations("HOWRAH", "JODHPUR");
                    trainService.getTrainsBetweenStations("GAYA", "DELHI");
                } catch (Exception e) {
                    fail("Concurrent search failed");
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to complete
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executorService.shutdown();
    }
}
