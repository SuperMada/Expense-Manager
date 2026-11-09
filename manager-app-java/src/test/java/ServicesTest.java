import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.zip.DataFormatException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import exceptions.AuthenticationException;
import exceptions.AuthorizationException;
import exceptions.DatabaseException;

//@ExtendWith(MockitoExtension.class)
public class ServicesTest {

    private AutoCloseable mocks;

    @BeforeEach
    void initMocks() {
        mocks = MockitoAnnotations.openMocks(this);
    }
 
    @AfterEach
    void closeMocks() throws Exception {
        mocks.close();
    }

    void db_default_mock() throws SQLException{
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
    }

    @Mock Connection conn;
    @Mock PreparedStatement ps;
    @Mock ResultSet rs;


    @Test 
    void test_manager_login_works() throws Exception{
        db_default_mock();

        when(rs.next()).thenReturn(true);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("role")).thenReturn("manager");

        int id = Services.login(conn, "mgr_john", "Passw0rd!");

        assertEquals(1, id);
        verify(ps).setString(1, "mgr_john");
        verify(ps).setString(2, "Passw0rd!");
    }

    @ParameterizedTest 
    @CsvSource({"mgr_john, bad_password",
                "bad_username, Passw0rd!",
                "bad_username, bad_password"})
    void test_manager_login_stops_invalid_username_password_input(String username, String password) throws SQLException {
        db_default_mock();
        
        when(rs.next()).thenReturn(false);
        assertThrows(AuthenticationException.class, () -> Services.login(conn, username, password));
    }

    @Test
    void test_manager_login_on_db_error() throws Exception{
        Connection conn = mock(Connection.class);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("boom"));

        assertThrows(DatabaseException.class, () -> Services.login(conn, "db", "Blowup"));
    }

    @Test 
    void test_get_name_from_id_works() throws Exception{
        db_default_mock();
        when(rs.next()).thenReturn(true);
        when(rs.getString("full_name")).thenReturn("John Doe");

        String name = Services.getNameFromID(conn, 1);

        assertEquals(name, "John Doe");
        verify(ps).setInt(1, 1);
    }

    @Test
    void test_manager_get_name_on_db_error() throws Exception{
        Connection conn = mock(Connection.class);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("boom"));

        assertThrows(DatabaseException.class, () -> Services.getNameFromID(conn, 1));
    }

    @Test
    void test_get_expenses_option_1_orders_by_date_works() throws Exception {
        db_default_mock();
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getObject(1)).thenReturn(24, 6);
        when(rs.getObject(2)).thenReturn("Bob Lee", "Jane Doe");
        when(rs.getObject(3)).thenReturn("2026-10-27", "2026-11-07");
        when(rs.getObject(4)).thenReturn("Training", "Other");
        when(rs.getObject(5)).thenReturn(100, 12.99);
        when(rs.getObject(6)).thenReturn("Workshop registration fee", "Misc reimbursement");
 
        List<List<Object>> results = Services.getExpensesFromID(conn, 1, 1);
 
        assertEquals(2, results.size());
        assertEquals(List.of(24, "Bob Lee", "2026-10-27", "Training", 100, "Workshop registration fee"), results.get(0));
        assertEquals(List.of(6, "Jane Doe", "2026-11-07", "Other", 12.99, "Misc reimbursement"), results.get(1));

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        verify(conn).prepareStatement(sql.capture());
        assertTrue(sql.getValue().contains("ORDER BY ex.expense_date"));

    }

    //The earlier test tests that the function returns the correct stuff so this 
    //tests that it can return a bunch of values, we don't need to check they
    //are the correct ones
    @Test
    void test_get_expenses_option_2_orders_by_amount_works() throws Exception {
        db_default_mock();
        when(rs.next()).thenReturn(true, true, true, true, false);
        when(rs.getObject(anyInt())).thenReturn("x");
 
        List<List<Object>> results = Services.getExpensesFromID(conn, 2, 1);
 
        assertEquals(4, results.size());
        assertEquals(6, results.get(0).size());

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        verify(conn).prepareStatement(sql.capture());
        assertTrue(sql.getValue().contains("ORDER BY ex.amount"));
    }

    @Test
    void test_manager_get_expenses_on_db_error() throws Exception{
        Connection conn = mock(Connection.class);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("boom"));

        assertThrows(DatabaseException.class, () -> Services.getExpensesFromID(conn, 1, 1));
    }

    @Test
    void test_get_category_works() throws Exception {
        db_default_mock();
        when(rs.next()).thenReturn(true);
        when(rs.getString("name")).thenReturn("Travel");
 
        String category = Services.getCategory(conn, 2);
 
        assertEquals("Travel", category);
        verify(ps).setInt(1, 2);
    }
 
    @Test
    void test_is_pending_report_pending_works() throws Exception {
        db_default_mock();
        when(rs.next()).thenReturn(true);
        when(rs.getString("status")).thenReturn("pending");
 
        String message = Services.isPendingReport(conn, 5);
 
        assertEquals("Report Pending Approval Found\n", message);
        verify(ps).setInt(1, 5);
    }
 
    @Test
    void test_is_pending_report_already_approved_works() throws Exception {
        db_default_mock();
        when(rs.next()).thenReturn(true);
        when(rs.getString("status")).thenReturn("approved");
 
        String message = Services.isPendingReport(conn, 5);
 
        assertEquals("Given Report ID has already been approved\n", message);
    }

    @Test
    void test_is_pending_report_already_denied_works() throws Exception {
        db_default_mock();
        when(rs.next()).thenReturn(true);
        when(rs.getString("status")).thenReturn("denied");
 
        String message = Services.isPendingReport(conn, 5);
 
        assertEquals("Given Report ID has already been denied\n", message);
    }
 
    @Test
    void test_is_pending_report_not_found_works() throws Exception {
        db_default_mock();
        when(rs.next()).thenReturn(false);
 
        String message = Services.isPendingReport(conn, 5);
 
        assertEquals("No report found under given ID\n", message);
    }
 
    @Test
    void test_update_approval_with_comment_works() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
 
        int rows = Services.updateApproval(conn, "approved", 3, "Looks good", "2026-11-08", 12);
 
        assertEquals(1, rows);
        verify(ps).setString(1, "approved");
        verify(ps).setInt(2, 3);
        verify(ps).setString(3, "Looks good");
        verify(ps).setString(4, "2026-11-08");
        verify(ps).setInt(5, 12);
    }
 
    @Test
    void test_update_approval_without_comment_works() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
 
        int rows = Services.updateApproval(conn, "approved", 3, "2026-11-08", 12);
 
        assertEquals(1, rows);
        verify(ps).setString(1, "approved");
        verify(ps).setInt(2, 3);
        verify(ps).setString(3, "2026-11-08");
        verify(ps).setInt(4, 12);
    }
 
    @Test
    void test_get_employee_report_works() throws Exception {
        db_default_mock();
        when(rs.next()).thenReturn(true, false);
        when(rs.getObject(1)).thenReturn("John Doe");
        when(rs.getObject(2)).thenReturn(3);
        when(rs.getObject(3)).thenReturn(250.75);
        when(rs.getObject(4)).thenReturn(2);
 
        List<List<Object>> report = Services.getEmployeeReport(conn, 1);
 
        assertEquals(1, report.size());
        assertEquals(List.of("John Doe", 3, 250.75, 2), report.get(0));
    }
 
    @Test
    void test_get_category_report_works() throws Exception {
        db_default_mock();
        when(rs.next()).thenReturn(true, false);
        when(rs.getObject(1)).thenReturn("Travel");
        when(rs.getObject(2)).thenReturn(4);
        when(rs.getObject(3)).thenReturn(500.00);
        when(rs.getObject(4)).thenReturn(3);
 
        List<List<Object>> report = Services.getCategoryReport(conn, 1);
 
        assertEquals(1, report.size());
        assertEquals(List.of("Travel", 4, 500.00, 3), report.get(0));
    }
}
