package unit.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dao.ExpenseDao;
import exceptions.DatabaseException;

public class ExpenseDaoTest {
    private ExpenseDao expenseDao;
    private Connection conn;

    @BeforeEach 
    void setUp(){
        conn = mock(Connection.class);
        expenseDao = new ExpenseDao(conn);
    }

    @Test 
    @Tag("Negative")
    @DisplayName("Covers: FR-M1")
    void test_get_expense_on_db_error() throws SQLException{
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("This is an error"));

        assertThrows(DatabaseException.class, () -> expenseDao.getExpenseSortDate());
    }
}
