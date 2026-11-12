package unit.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dao.ApprovalDao;
import exceptions.DatabaseException;

public class ApprovalDaoTest {
    //Here we only test if db errors are caught as its faster than forcing one in an integration test
    //Integration tests make sure the db call are working properly assuming db is working
    private ApprovalDao approvalDao;
    private Connection conn;

    @BeforeEach 
    void setUp(){
        conn = mock(Connection.class);
        approvalDao = new ApprovalDao(conn);
    }

    @Test 
    @Tag("Negative")
    void test_find_status_on_db_error() throws SQLException{
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Auto Exception"));

        assertThrows(DatabaseException.class, () -> approvalDao.findStatus(1));
    }

    @Test 
    @Tag("Negative")
    void test_save_decision_on_db_error() throws SQLException{
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Auto Exception"));

        assertThrows(DatabaseException.class, () -> approvalDao.saveDecision(1, "Hello", 1, "Hello", LocalDate.of(2026, 11, 9)));
    }

}
