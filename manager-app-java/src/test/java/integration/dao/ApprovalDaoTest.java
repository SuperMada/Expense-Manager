package integration.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import dao.ApprovalDao;
import dao.ExpenseDao;
import integration.TestDatabase;

public class ApprovalDaoTest {
    //The exception handling is tested in the unit tests
    //Here we just need to test that when the database works we get the right things back

    private TestDatabase db;
    private Connection conn;
    private ApprovalDao approvalDao;
    private ExpenseDao expenseDao;

    @BeforeEach
    void setUp() throws Exception {
        db = TestDatabase.create();
        conn = db.connection();
        approvalDao = new ApprovalDao(conn);
        expenseDao = new ExpenseDao(conn);
    }

    @AfterEach
    void tearDown() throws Exception {
        db.close();
    }

    @ParameterizedTest 
    @Tag("Smoke")
    @Tag("Integration")
    @CsvSource({"1,pending", "2,approved", "3,denied"})
    void test_find_status_gets_status(int id, String expected){
        assertEquals(approvalDao.findStatus(id), expected);
    }

    @Test 
    @Tag("Negative")
    @Tag("Integration")
    void test_find_status_no_status(){
        assertNull(approvalDao.findStatus(100));
    }

    @Test 
    @Tag("Integration")
    void test_save_decision_write() throws SQLException{
        int id = expenseDao.getExpenseSortDate().get(0).id();
        String sql = """
            SELECT status, reviewer_id, comment, review_date
            FROM approvals
            WHERE id = ?
            """;

        approvalDao.saveDecision(id, "approved", 1, "This looks good", LocalDate.of(2026, 11, 9));

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                assertTrue(rs.next());
                assertEquals(rs.getString("status"), "approved");
                assertEquals(rs.getInt("reviewer_id"), 1);
                assertEquals(rs.getString("comment"), "This looks good");
                assertEquals(rs.getString("review_date"), LocalDate.of(2026, 11, 9).toString());
            }
        }
    }
}
