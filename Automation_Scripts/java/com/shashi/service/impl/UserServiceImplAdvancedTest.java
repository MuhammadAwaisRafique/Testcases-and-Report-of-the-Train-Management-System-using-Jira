package com.shashi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
import com.shashi.constant.UserRole;
import com.shashi.utility.DBUtil;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplAdvancedTest {

    private UserServiceImpl userService;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(UserRole.CUSTOMER);
    }

    @Nested
    @DisplayName("User Registration Tests")
    class UserRegistrationTests {
        @Test
        @DisplayName("Successful User Registration")
        public void testRegisterUser_Success() throws Exception {
            // Arrange
            UserBean user = createValidUserBean();

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);

            // Act
            String result = userService.registerUser(user);

            // Assert
            assertTrue(result.contains("SUCCESS"));
            verify(mockPreparedStatement).setString(1, user.getMailId());
        }

        @Test
        @DisplayName("Registration Fails - Duplicate Email")
        public void testRegisterUser_DuplicateEmail() throws Exception {
            // Arrange
            UserBean user = createValidUserBean();

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("ORA-00001: unique constraint violated"));

            // Act & Assert
            String result = userService.registerUser(user);
            assertTrue(result.contains("already registered"));
        }

        @ParameterizedTest
        @DisplayName("Invalid User Registration Data")
        @CsvSource({
            "'', password, John, Doe, Address, 1234567890",
            "invalid-email, password, John, Doe, Address, 1234567890",
            "test@example.com, '', John, Doe, Address, 1234567890",
            "test@example.com, password, '', Doe, Address, 1234567890"
        })
        public void testRegisterUser_InvalidData(
            String email, String password, String firstName, 
            String lastName, String address, long phoneNo
        ) throws Exception {
            // Arrange
            UserBean user = new UserBean();
            user.setMailId(email);
            user.setPWord(password);
            user.setFName(firstName);
            user.setLName(lastName);
            user.setAddr(address);
            user.setPhNo(phoneNo);

            // Act & Assert
            String result = userService.registerUser(user);
            assertFalse(result.contains("SUCCESS"));
        }
    }

    @Nested
    @DisplayName("User Login Tests")
    class UserLoginTests {
        @Test
        @DisplayName("Successful Login")
        public void testLoginUser_ValidCredentials() throws Exception {
            // Arrange
            String email = "test@example.com";
            String password = "correctpassword";

            setupMockLoginScenario(email, password, true);

            // Act
            UserBean user = userService.loginUser(email, password);

            // Assert
            assertNotNull(user);
            assertEquals(email, user.getMailId());
        }

        @Test
        @DisplayName("Login Fails - Invalid Credentials")
        public void testLoginUser_InvalidCredentials() throws Exception {
            // Arrange
            String email = "test@example.com";
            String password = "wrongpassword";

            setupMockLoginScenario(email, password, false);

            // Act & Assert
            assertThrows(TrainException.class, () -> {
                userService.loginUser(email, password);
            });
        }

        @ParameterizedTest
        @DisplayName("Login with Invalid Email Formats")
        @CsvSource({
            "'', password",
            "invalid-email, password",
            "test@example, password"
        })
        public void testLoginUser_InvalidEmailFormat(String email, String password) throws Exception {
            // Act & Assert
            assertThrows(TrainException.class, () -> {
                userService.loginUser(email, password);
            });
        }
    }

    @Nested
    @DisplayName("User Profile Management Tests")
    class UserProfileTests {
        @Test
        @DisplayName("Successful User Profile Update")
        public void testUpdateUser_Success() throws Exception {
            // Arrange
            UserBean user = createValidUserBean();

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // Act
            String result = userService.updateUser(user);

            // Assert
            assertEquals("SUCCESS", result);
        }

        @Test
        @DisplayName("User Profile Update Fails")
        public void testUpdateUser_Failure() throws Exception {
            // Arrange
            UserBean user = createValidUserBean();

            when(DBUtil.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(0);

            // Act
            String result = userService.updateUser(user);

            // Assert
            assertNotEquals("SUCCESS", result);
        }
    }

    // Helper Methods
    private UserBean createValidUserBean() {
        UserBean user = new UserBean();
        user.setMailId("test@example.com");
        user.setPWord("password123");
        user.setFName("John");
        user.setLName("Doe");
        user.setAddr("123 Test Street");
        user.setPhNo(1234567890L);
        return user;
    }

    private void setupMockLoginScenario(String email, String password, boolean validLogin) throws SQLException {
        when(DBUtil.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        
        when(mockResultSet.next()).thenReturn(validLogin);
        
        if (validLogin) {
            when(mockResultSet.getString("fname")).thenReturn("John");
            when(mockResultSet.getString("lname")).thenReturn("Doe");
            when(mockResultSet.getString("addr")).thenReturn("Test Address");
            when(mockResultSet.getString("mailid")).thenReturn(email);
            when(mockResultSet.getLong("phno")).thenReturn(1234567890L);
            when(mockResultSet.getString("pword")).thenReturn(password);
        }
    }
}
