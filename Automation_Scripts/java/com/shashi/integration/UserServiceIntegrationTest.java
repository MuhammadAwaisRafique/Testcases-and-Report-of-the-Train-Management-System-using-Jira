package com.shashi.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.shashi.beans.TrainException;
import com.shashi.beans.UserBean;
import com.shashi.constant.UserRole;
import com.shashi.service.UserService;
import com.shashi.service.impl.UserServiceImpl;
import com.shashi.utility.DBUtil;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceIntegrationTest {

    private static UserService userService;
    private static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException {
        userService = new UserServiceImpl(UserRole.CUSTOMER);
        connection = DBUtil.getConnection();
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testRegisterUser() throws SQLException, TrainException {
        UserBean newUser = new UserBean();
        newUser.setMailId("integrationtest@example.com");
        newUser.setPWord("securepass");
        newUser.setFName("Integration");
        newUser.setLName("Test");
        newUser.setAddr("Test Address");
        newUser.setPhNo(1234567890L);

        String result = userService.registerUser(newUser);
        assertTrue(result.contains("SUCCESS"));

        UserBean retrievedUser = userService.getUserByEmailId("integrationtest@example.com");
        assertEquals(newUser, retrievedUser);
    }

    @Test
    void testGetAllUsers() throws SQLException, TrainException {
        List<UserBean> users = userService.getAllUsers();
        assertFalse(users.isEmpty());
    }

    @Test
    void testGetUserByEmailId() throws SQLException, TrainException {
        UserBean user = userService.getUserByEmailId("shashi@demo.com");
        assertNotNull(user);
    }

    @Test
    void testUpdateUser() throws SQLException, TrainException {
        UserBean user = userService.getUserByEmailId("shashi@demo.com");
        String initialName = user.getFName();
        user.setFName("UpdatedName");
        String result = userService.updateUser(user);
        assertTrue(result.contains("SUCCESS"));

        UserBean updatedUser = userService.getUserByEmailId("shashi@demo.com");
        assertEquals("UpdatedName", updatedUser.getFName());
    }

    @Test
    void testDeleteUser() throws SQLException, TrainException {
        String emailToDelete = "integrationtest@example.com"; // Replace with a user ID that exists in your test DB
        UserBean user = userService.getUserByEmailId(emailToDelete);
        assertNotNull(user);

        String result = userService.deleteUser(user);
        assertTrue(result.contains("SUCCESS"));

        assertNull(userService.getUserByEmailId(emailToDelete));
    }

    @Test
    void testLoginUser() throws SQLException, TrainException {
        UserBean user = userService.loginUser("shashi@demo.com", "shashi");
        assertNotNull(user);
    }
}
