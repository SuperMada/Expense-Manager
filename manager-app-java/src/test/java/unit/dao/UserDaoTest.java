package unit.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dao.UserDao;
import exceptions.DatabaseException;

public class UserDaoTest {
    private UserDao userDao;
    private Connection conn;

    @BeforeEach 
    void setUp(){
        conn = mock(Connection.class);
        userDao = new UserDao(conn);
    }

    @Test 
    @Tag("Negative")
    void test_find_by_login_on_db_error() throws SQLException{
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Oops"));
        
        assertThrows(DatabaseException.class, () -> userDao.findByLogin("Username", "Password"));
    }

    @Test 
    @Tag("Negative")
    void test_find_by_id_on_db_error() throws SQLException{
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Oops"));
        
        assertThrows(DatabaseException.class, () -> userDao.findById(1));
    }
}
