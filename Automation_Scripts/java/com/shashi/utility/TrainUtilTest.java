package com.shashi.utility;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shashi.beans.UserBean;
import com.shashi.constant.UserRole;

@ExtendWith(MockitoExtension.class)
public class TrainUtilTest {

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private HttpSession mockSession;

    @Mock
    private ServletContext mockServletContext;

    @Test
    public void testReadCookie_CookieExists() {
        // Arrange
        Cookie[] cookies = new Cookie[] {
            new Cookie("testCookie", "testValue")
        };
        when(mockRequest.getCookies()).thenReturn(cookies);

        // Act
        var result = TrainUtil.readCookie(mockRequest, "testCookie");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testValue", result.get());
    }

    @Test
    public void testReadCookie_CookieNotExists() {
        // Arrange
        Cookie[] cookies = new Cookie[] {
            new Cookie("otherCookie", "otherValue")
        };
        when(mockRequest.getCookies()).thenReturn(cookies);

        // Act
        var result = TrainUtil.readCookie(mockRequest, "testCookie");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetCurrentUserName() {
        // Arrange
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("uName")).thenReturn("TestUser");

        // Act
        String userName = TrainUtil.getCurrentUserName(mockRequest);

        // Assert
        assertEquals("TestUser", userName);
    }

    @Test
    public void testGetCurrentUserEmail() {
        // Arrange
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute("mailid")).thenReturn("test@example.com");

        // Act
        String userEmail = TrainUtil.getCurrentUserEmail(mockRequest);

        // Assert
        assertEquals("test@example.com", userEmail);
    }

    @Test
    public void testGetCurrentCustomer() {
        // Arrange
        UserBean mockUser = new UserBean();
        mockUser.setMailId("test@example.com");
        mockUser.setFName("Test");

        when(mockRequest.getServletContext()).thenReturn(mockServletContext);
        when(mockServletContext.getAttribute(UserRole.CUSTOMER.toString())).thenReturn(mockUser);

        // Act
        UserBean currentUser = TrainUtil.getCurrentCustomer(mockRequest);

        // Assert
        assertNotNull(currentUser);
        assertEquals("test@example.com", currentUser.getMailId());
    }

    @Test
    public void testIsLoggedIn_LoggedIn() {
        // Arrange
        Cookie[] cookies = new Cookie[] {
            new Cookie("sessionIdForCUSTOMER", "someSessionId")
        };
        when(mockRequest.getCookies()).thenReturn(cookies);

        // Act
        boolean isLoggedIn = TrainUtil.isLoggedIn(mockRequest, UserRole.CUSTOMER);

        // Assert
        assertTrue(isLoggedIn);
    }

    @Test
    public void testIsLoggedIn_NotLoggedIn() {
        // Arrange
        Cookie[] cookies = new Cookie[] {
            new Cookie("otherCookie", "someValue")
        };
        when(mockRequest.getCookies()).thenReturn(cookies);

        // Act
        boolean isLoggedIn = TrainUtil.isLoggedIn(mockRequest, UserRole.CUSTOMER);

        // Assert
        assertFalse(isLoggedIn);
    }
}
