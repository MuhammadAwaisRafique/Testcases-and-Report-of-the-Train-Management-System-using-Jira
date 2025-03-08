package com.shashi.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    void testUserRoleValues() {
        assertEquals("ADMIN", UserRole.ADMIN.name());
        assertEquals("CUSTOMER", UserRole.CUSTOMER.name());
    }
}
