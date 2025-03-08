package com.shashi.beans;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import com.shashi.constant.ResponseCode;

class TrainExceptionTest {

    @Test
    void testTrainExceptionWithResponseCode() {
        TrainException ex = new TrainException(ResponseCode.DATABASE_CONNECTION_FAILURE);
        assertEquals(ResponseCode.DATABASE_CONNECTION_FAILURE.getCode(), ex.getStatusCode());
        assertEquals(ResponseCode.DATABASE_CONNECTION_FAILURE.getMessage(), ex.getErrorMessage());
        assertEquals(ResponseCode.DATABASE_CONNECTION_FAILURE.name(), ex.getErrorCode());
    }

    @Test
    void testTrainExceptionWithStringMessage() {
        TrainException ex = new TrainException("Custom error message");
        assertEquals(400, ex.getStatusCode());
        assertEquals("Custom error message", ex.getErrorMessage());
        assertEquals("BAD_REQUEST", ex.getErrorCode());
    }

    @Test
    void testTrainExceptionWithCustomCodeAndMessage() {
        TrainException ex = new TrainException(409, "CONFLICT", "Resource conflict");
        assertEquals(409, ex.getStatusCode());
        assertEquals("Resource conflict", ex.getErrorMessage());
        assertEquals("CONFLICT", ex.getErrorCode());
    }
}
