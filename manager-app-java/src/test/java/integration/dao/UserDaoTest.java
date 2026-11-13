package integration.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Connection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import dao.UserDao;
import integration.TestDatabase;
import model.User;

public class UserDaoTest {
    private TestDatabase db;
    private Connection conn;
    private UserDao userDao;

    @BeforeEach 
    void setUp() throws Exception{
        db = TestDatabase.create();
        conn = db.connection();
        userDao = new UserDao(conn);
    }

    @AfterEach 
    void tearDown() throws Exception{
        db.close();
    }

    @Test 
    @Tag("Integration")
    @DisplayName("Covers: FR-A1, FR-A2")
    void test_find_by_login_works(){
        User user = userDao.findByLogin("mgr_john", "Passw0rd!");
        
        assertEquals(user.id(), 1);
        assertEquals(user.role(), "manager");
        assertEquals(user.fullName(), "John Smith");
    }

    @ParameterizedTest 
    @Tag("Negative")
    @Tag("Integration")
    @DisplayName("Covers: FR-A1, FR-A3")
    @CsvSource({"badusername,badpassword", "mgr_john,badpassword", "badusername,Passw0rd!", ",", ",Passw0rd!", "mgr_john,"})
    void test_find_by_login_fails_to_find(String username, String password){
        assertNull(userDao.findByLogin(username, password));
    }
}
