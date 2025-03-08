package com.shashi.beans;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HistoryBeanTest {

    @Test
    void testHistoryBeanGettersAndSetters() {
        HistoryBean history = new HistoryBean();
        history.setTransId("12345");
        history.setMailId("user@example.com");
        history.setTr_no("1001");
        history.setDate("2024-03-15");
        history.setFrom_stn("Station A");
        history.setTo_stn("Station B");
        history.setSeats(2);
        history.setAmount(500.0);

        assertEquals("12345", history.getTransId());
        assertEquals("user@example.com", history.getMailId());
        assertEquals("1001", history.getTr_no());
        assertEquals("2024-03-15", history.getDate());
        assertEquals("Station A", history.getFrom_stn());
        assertEquals("Station B", history.getTo_stn());
        assertEquals(2, history.getSeats());
        assertEquals(500.0, history.getAmount());
    }
}
