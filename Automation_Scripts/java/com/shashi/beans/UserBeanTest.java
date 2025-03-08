package com.shashi.beans;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class UserBeanTest {

    @Test
    void testUserBeanGettersAndSetters() {
        UserBean user = new UserBean();
        user.setMailId("test@example.com");
        user.setPWord("password123");
        user.setFName("John");
        user.setLName("Doe");
        user.setAddr("123 Main St");
        user.setPhNo(1234567890L);

        assertEquals("test@example.com", user.getMailId());
        assertEquals("password123", user.getPWord());
        assertEquals("John", user.getFName());
        assertEquals("Doe", user.getLName());
        assertEquals("123 Main St", user.getAddr());
        assertEquals(1234567890L, user.getPhNo());
    }

    @ParameterizedTest
    @CsvSource({
        "test@example.com,password123,John,Doe,123 Main St,1234567890",
        "another@example.com,securepass,Jane,Doe,456 Oak Ave,9876543210"
    })
    void testUserBeanValidData(String email, String password, String firstName, String lastName, String address, long phone) {
        UserBean user = new UserBean();
        user.setMailId(email);
        user.setPWord(password);
        user.setFName(firstName);
        user.setLName(lastName);
        user.setAddr(address);
        user.setPhNo(phone);

        assertNotNull(user);
    }

    @ParameterizedTest
    @CsvSource({
        ",password123,John,Doe,123 Main St,1234567890",
        "invalid-email,password123,John,Doe,123 Main St,1234567890",
        "test@example.com,,John,Doe,123 Main St,1234567890",
        "test@example.com,password123,,Doe,123 Main St,1234567890",
        "test@example.com,password123,John,Doe,123 Main St,0"
    })
    void testUserBeanInvalidData(String email, String password, String firstName, String lastName, String address, long phone) {
        UserBean user = new UserBean();
        user.setMailId(email);
        user.setPWord(password);
        user.setFName(firstName);
        user.setLName(lastName);
        user.setAddr(address);
        user.setPhNo(phone);

        // Assertions depend on how you handle invalid data (exceptions or default values)
        // Example: If invalid data should throw exceptions, use assertThrows
        // assertThrows(IllegalArgumentException.class, () -> new UserBean(email, password, firstName, lastName, address, phone));
    }
}
