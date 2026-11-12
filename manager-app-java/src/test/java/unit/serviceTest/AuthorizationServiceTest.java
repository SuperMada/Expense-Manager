package unit.serviceTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dao.UserDao;
import exceptions.AuthenticationException;
import exceptions.AuthorizationException;
import service.AuthorizationService;
import model.User;

public class AuthorizationServiceTest {
    private UserDao userDao;
    private AuthorizationService service;

    @BeforeEach 
    void setUp(){
        userDao = mock(UserDao.class);
        service = new AuthorizationService(userDao);
    }

    @Test 
    @Tag("Smoke")
    @DisplayName("Covers: FR-A1")
    void test_login_works_with_valid_inputs(){
        when(userDao.findByLogin("mgr_john", "FakepasswordButWorks")).thenReturn(new User(1, "mgr_john", "manager", "John Malkovich"));
        
        assertDoesNotThrow(() -> service.login("mgr_john", "FakepasswordButWorks"));
    }

    @Test 
    @Tag("Negative")
    @DisplayName("Covers: FR-A1")
    void test_login_throws_on_no_user_found(){
        when(userDao.findByLogin("mgr_john", "Bad")).thenReturn(null);

        assertThrows(AuthenticationException.class, () -> service.login("mgr_john", "bad"));
    }

    @Test 
    @Tag("Negative")
    @DisplayName("Covers: FR-A1")
    void test_login_throws_on_employee(){
        when(userDao.findByLogin("emp_bob", "FakepasswordButWorks")).thenReturn(new User(3, "emp_bob", "employee", "Bob Odenkirk"));

        assertThrows(AuthorizationException.class, () -> service.login("emp_bob", "FakepasswordButWorks"));
    }
}
