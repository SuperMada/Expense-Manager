package integration.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dao.ReportDao;
import integration.TestDatabase;
import model.ReportRow;

public class ReportDaoTest {
    private TestDatabase db;
    private Connection conn;
    private ReportDao reportDao;

    @BeforeEach 
    void setUp() throws Exception{
        db = TestDatabase.create();
        conn = db.connection();
        reportDao = new ReportDao(conn);
    }

    @AfterEach 
    void tearDown() throws Exception{
        db.close();
    }

    @Test 
    @Tag("Integration")
    @DisplayName("Covers: FR-M3")
    void test_get_report_employee(){
        List<ReportRow> output = reportDao.getReportEmployee();
        assertEquals(output.size(), 4);
        assertEquals(output.get(0).name(), "Jane Doe");
        assertEquals(output.get(0).count(), 7);
        assertEquals(output.get(0).total(), 10686.75);
        assertEquals(output.get(0).approvedCount(), 3);
    }

    @Test 
    @Tag("Integration")
    @DisplayName("Covers: FR-M3")
    void test_get_report_category(){
        List<ReportRow> output = reportDao.getReportCategory();
        assertEquals(output.size(), 6);
        assertEquals(output.get(0).name(), "Travel");
        assertEquals(output.get(0).count(), 4);
        assertEquals(output.get(0).total(), 1520.50);
        assertEquals(output.get(0).approvedCount(), 2);
    }

}
