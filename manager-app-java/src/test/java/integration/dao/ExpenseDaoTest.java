package integration.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dao.ExpenseDao;
import integration.TestDatabase;
import model.PendingExpense;

public class ExpenseDaoTest {
    private TestDatabase db;
    private Connection conn;
    private ExpenseDao expenseDao;

    @BeforeEach 
    void setUp() throws Exception{
        db = TestDatabase.create();
        conn = db.connection();
        expenseDao = new ExpenseDao(conn);
    }

    @AfterEach 
    void tearDown() throws Exception{
        db.close();
    }

    @Test
    @Tag("Integration")
    @DisplayName("Covers: FR-M1")
    void test_pending_expenses(){
        List<PendingExpense> output = expenseDao.getExpenseSortDate();
        assertEquals(output.size(), 10);
        assertEquals(output.get(0).id(), 24);
        assertEquals(output.get(0).fullName(), "Bob Lee");
        // Seed data sets with an offset so just check if the string returned can be cast to a date
        assertDoesNotThrow(() -> LocalDate.parse(output.get(0).date(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        assertEquals(output.get(0).category(), "Training");
        assertEquals(output.get(0).amount(), 100.00);
        assertEquals(output.get(0).description(), "Workshop registration fee");
    }
}
