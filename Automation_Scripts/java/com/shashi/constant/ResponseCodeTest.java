package com.shashi.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponseCodeTest {

    @Test
    void testResponseCodeValues() {
        assertEquals(200, ResponseCode.SUCCESS.getCode());
        assertEquals("OK", ResponseCode.SUCCESS.getMessage());
        // Add assertions for other ResponseCode values as needed
    }

    @Test
    void testGetMessageByStatusCode() {
        assertEquals("OK", ResponseCode.getMessageByStatusCode(200).get().getMessage());
        // Add assertions for other status codes as needed
    }
}
