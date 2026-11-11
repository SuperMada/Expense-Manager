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

import dao.ReportDao;
import exceptions.DatabaseException;

public class ReportDaoTest {
    private ReportDao reportDao;
    private Connection conn;

    @BeforeEach 
    void setUp(){
        conn = mock(Connection.class);
        reportDao = new ReportDao(conn);
    }

    @Test 
    @Tag("Negative")
    void test_get_report_employee_on_db_error() throws SQLException{
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Error, Error, Error"));
        
        assertThrows(DatabaseException.class, () -> reportDao.getReportEmployee());
    }

    @Test 
    @Tag("Negative")
    void test_get_report_category_on_db_error() throws SQLException{
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Error, Error, Error"));
        
        assertThrows(DatabaseException.class, () -> reportDao.getReportCategory());
    }
}
