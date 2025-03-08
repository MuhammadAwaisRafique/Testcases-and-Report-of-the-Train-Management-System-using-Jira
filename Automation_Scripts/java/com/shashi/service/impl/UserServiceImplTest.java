package com.shashi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shashi.beans.TrainException;
import com.shashi.beans.UserBean;
import com.shashi.constant.ResponseCode;
import com.shashi.constant.UserRole;
import com.shashi.utility.DBUtil;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserServiceImpl userService;

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(UserRole.CUSTOMER);
    }

    @Nested
    @DisplayName("Get User By Email Tests")
    class GetUserByEmailTests {
        @Test
        @DisplayName("Successful User Retrieval")
        void testGetUserByEmailId_Success() throws SQLException, TrainException {
            // Arrange
            String email = "test@example.com";
            UserBean expectedUser = new UserBean();
            expectedUser.setFName("John");
            expectedUser.setLName("Doe");
            expectedUser.setMailId(email);
            expectedUser.setAddr("123 Main St");
            expectedUser.setPhNo(1234567890L);

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("fname")).thenReturn(expectedUser.getFName());
            when(mockResultSet.getString("lname")).thenReturn(expectedUser.getLName());
            when(mockResultSet.getString("mailid")).thenReturn(expectedUser.getMailId());
            when(mockResultSet.getString("addr")).thenReturn(expectedUser.getAddr());
            when(mockResultSet.getLong("phno")).thenReturn(expectedUser.getPhNo());

            // Act
            UserBean retrievedUser = userService.getUserByEmailId(email);

            // Assert
            assertEquals(expectedUser, retrievedUser);
        }

        @Test
        @DisplayName("User Not Found")
        void testGetUserByEmailId_UserNotFound() throws SQLException, TrainException {
            // Arrange
            String email = "nonexistent@example.com";

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            // Act & Assert
            assertThrows(TrainException.class, () -> userService.getUserByEmailId(email));
        }

        @Test
        @DisplayName("Database Error")
        void testGetUserByEmailId_DatabaseError() throws SQLException, TrainException {
            // Arrange
            String email = "test@example.com";
            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Act & Assert
            assertThrows(TrainException.class, () -> userService.getUserByEmailId(email));
        }
    }

    @Nested
    @DisplayName("Register User Tests")
    class RegisterUserTests {
        @Test
        @DisplayName("Successful User Registration")
        void testRegisterUser_Success() throws SQLException, TrainException {
            // Arrange
            UserBean user = new UserBean();
            user.setMailId("newuser@example.com");
            user.setPWord("password123");
            user.setFName("New");
            user.setLName("User");
            user.setAddr("New Address");
            user.setPhNo(9876543210L);

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // Act
            String result = userService.registerUser(user);

            // Assert
            assertEquals("SUCCESS", result);
        }

        @Test
        @DisplayName("Registration Fails - Duplicate Email")
        void testRegisterUser_DuplicateEmail() throws SQLException, TrainException {
            // Arrange
            UserBean user = new UserBean();
            user.setMailId("existinguser@example.com");
            user.setPWord("password123");
            user.setFName("Existing");
            user.setLName("User");
            user.setAddr("Existing Address");
            user.setPhNo(1234567890L);

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("ORA-00001: unique constraint violated"));

            // Act
            String result = userService.registerUser(user);

            // Assert
            assertTrue(result.contains("already registered"));
        }

        @ParameterizedTest
        @DisplayName("Invalid Registration Data")
        @CsvSource({
            ",password123,John,Doe,Address,1234567890",
            "invalid-email,password123,John,Doe,Address,1234567890",
            "test@example.com,,John,Doe,Address,1234567890",
            "test@example.com,password123,,Doe,Address,1234567890",
            "test@example.com,password123,John,Doe,,1234567890"
        })
        void testRegisterUser_InvalidData(String email, String password, String firstName, String lastName, String address, long phone) throws SQLException, TrainException {
            // Arrange
            UserBean user = new UserBean();
            user.setMailId(email);
            user.setPWord(password);
            user.setFName(firstName);
            user.setLName(lastName);
            user.setAddr(address);
            user.setPhNo(phone);

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

            // Act
            String result = userService.registerUser(user);

            // Assert
            assertFalse(result.contains("SUCCESS"));
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {
        @Test
        @DisplayName("Successful User Update")
        void testUpdateUser_Success() throws SQLException, TrainException {
            // Arrange
            UserBean user = new UserBean();
            user.setMailId("test@example.com");
            user.setFName("Updated");
            user.setLName("User");
            user.setAddr("Updated Address");
            user.setPhNo(9876543210L);

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // Act
            String result = userService.updateUser(user);

            // Assert
            assertEquals("SUCCESS", result);
        }

        @Test
        @DisplayName("Update Fails - User Not Found")
        void testUpdateUser_UserNotFound() throws SQLException, TrainException {
            // Arrange
            UserBean user = new UserBean();
            user.setMailId("nonexistent@example.com");
            user.setFName("Updated");
            user.setLName("User");
            user.setAddr("Updated Address");
            user.setPhNo(9876543210L);

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(0);

            // Act
            String result = userService.updateUser(user);

            // Assert
            assertNotEquals("SUCCESS", result);
        }

        @Test
        @DisplayName("Database Error During Update")
        void testUpdateUser_DatabaseError() throws SQLException, TrainException {
            // Arrange
            UserBean user = new UserBean();
            user.setMailId("test@example.com");
            user.setFName("Updated");
            user.setLName("User");
            user.setAddr("Updated Address");
            user.setPhNo(9876543210L);

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Act & Assert
            assertThrows(TrainException.class, () -> userService.updateUser(user));
        }
    }

    @Nested
    @DisplayName("Login User Tests")
    class LoginUserTests {
        @Test
        @DisplayName("Successful Login")
        void testLoginUser_Success() throws SQLException, TrainException {
            // Arrange
            String email = "test@example.com";
            String password = "password123";
            UserBean expectedUser = new UserBean();
            expectedUser.setFName("John");
            expectedUser.setLName("Doe");
            expectedUser.setMailId(email);
            expectedUser.setAddr("123 Main St");
            expectedUser.setPhNo(1234567890L);
            expectedUser.setPWord(password);

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("fname")).thenReturn(expectedUser.getFName());
            when(mockResultSet.getString("lname")).thenReturn(expectedUser.getLName());
            when(mockResultSet.getString("mailid")).thenReturn(expectedUser.getMailId());
            when(mockResultSet.getString("pword")).thenReturn(expectedUser.getPWord());
            when(mockResultSet.getString("addr")).thenReturn(expectedUser.getAddr());
            when(mockResultSet.getLong("phno")).thenReturn(expectedUser.getPhNo());

            // Act
            UserBean user = userService.loginUser(email, password);

            // Assert
            assertEquals(expectedUser, user);
        }

        @Test
        @DisplayName("Login Fails - Invalid Credentials")
        void testLoginUser_InvalidCredentials() throws SQLException, TrainException {
            // Arrange
            String email = "test@example.com";
            String password = "wrongpassword";

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            // Act & Assert
            assertThrows(TrainException.class, () -> userService.loginUser(email, password));
        }

        @Test
        @DisplayName("Database Error During Login")
        void testLoginUser_DatabaseError() throws SQLException, TrainException {
            // Arrange
            String email = "test@example.com";
            String password = "password123";

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Act & Assert
            assertThrows(TrainException.class, () -> userService.loginUser(email, password));
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {
        @Test
        @DisplayName("Successful User Deletion")
        void testDeleteUser_Success() throws SQLException, TrainException {
            // Arrange
            UserBean user = new UserBean();
            user.setMailId("test@example.com");

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // Act
            String result = userService.deleteUser(user);

            // Assert
            assertEquals("SUCCESS", result);
        }

        @Test
        @DisplayName("Delete Fails - User Not Found")
        void testDeleteUser_UserNotFound() throws SQLException, TrainException {
            // Arrange
            UserBean user = new UserBean();
            user.setMailId("nonexistent@example.com");

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(0);

            // Act
            String result = userService.deleteUser(user);

            // Assert
            assertNotEquals("SUCCESS", result);
        }

        @Test
        @DisplayName("Database Error During Deletion")
        void testDeleteUser_DatabaseError() throws SQLException, TrainException {
            // Arrange
            UserBean user = new UserBean();
            user.setMailId("test@example.com");

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Act & Assert
            assertThrows(TrainException.class, () -> userService.deleteUser(user));
        }
    }

    @Nested
    @DisplayName("Get All Users Tests")
    class GetAllUsersTests {
        @Test
        @DisplayName("Retrieve All Users Successfully")
        void testGetAllUsers_Success() throws SQLException, TrainException {
            // Arrange
            List<UserBean> expectedUsers = new ArrayList<>();
            expectedUsers.add(new UserBean());
            expectedUsers.add(new UserBean());

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

            // Act
            List<UserBean> users = userService.getAllUsers();

            // Assert
            assertEquals(2, users.size());
        }

        @Test
        @DisplayName("No Users Found")
        void testGetAllUsers_NoUsers() throws SQLException, TrainException {
            // Arrange
            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            // Act & Assert
            assertThrows(TrainException.class, () -> userService.getAllUsers());
        }

        @Test
        @DisplayName("Database Error")
        void testGetAllUsers_DatabaseError() throws SQLException, TrainException {
            // Arrange
            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Act & Assert
            assertThrows(TrainException.class, () -> userService.getAllUsers());
        }
    }
}
