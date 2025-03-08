package com.shashi.beans;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminBeanTest {

    @Test
    void testAdminBeanGettersAndSetters() {
        AdminBean admin = new AdminBean();
        admin.setMailId("admin@example.com");
        admin.setPWord("admin123");
        admin.setFName("Admin");
        admin.setLName("User");
        admin.setAddr("Admin Address");
        admin.setPhNo(1234567890L);

        assertEquals("admin@example.com", admin.getMailId());
        assertEquals("admin123", admin.getPWord());
        assertEquals("Admin", admin.getFName());
        assertEquals("User", admin.getLName());
        assertEquals("Admin Address", admin.getAddr());
        assertEquals(1234567890L, admin.getPhNo());
    }
}
