

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import exceptions.AuthenticationException;
import exceptions.ValidationException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import model.PendingExpense;
import model.ReportRow;
import model.User;
import service.ApprovalService;
import service.AuthorizationService;
import service.ReportService;

public class MenuFlowTest {
    private AuthorizationService auth;
    private ApprovalService approvals;
    private ReportService reports;
    private ByteArrayOutputStream out;

    @BeforeEach 
    void setUp(){
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        auth = mock(AuthorizationService.class);
        approvals = mock(ApprovalService.class);
        reports = mock(ReportService.class);
        //We don't make a menu here like we would with the other classes because the scanner needs to hold the specific input for each test
    }

    @AfterEach 
    void tearDown(){
        System.setOut(System.out);
    }

    @Test 
    @Tag("Smoke")
    void test_login_screen_works(){
        when(auth.login(anyString(), anyString())).thenReturn(new User(1, "mgr_john", "manager", "John Smith"));


        Menu menu = new Menu(auth, approvals, reports, new Scanner("mgr_john\nPassw0rd!\n0\n"));
        menu.run();

        assertTrue(out.toString().contains("Welcome, John Smith"));
    }

    @Test 
    @Tag("Negative")
    void test_login_fails_on_3_attempts(){
        when(auth.login(anyString(), anyString())).thenThrow(new AuthenticationException("Invalid username or password"));

        Menu menu = new Menu(auth, approvals, reports, new Scanner("mgr_john\nbadpass\nbaduser\nPassw0rd!\nbaduser\nbadpass"));
        int exit_code = menu.run();

        assertEquals(exit_code, 1);
    }

    @Test 
    void test_login_on_empty_input(){
        when(auth.login(anyString(), anyString())).thenReturn(new User(1, "mgr_john", "manager", "John Smith"));

        Menu menu = new Menu(auth, approvals, reports, new Scanner("\n\n\n\n\n\n\n\n\n\nmgr_john\nPassw0rd!\n0"));
        menu.run();

        assertTrue(out.toString().contains("Welcome, John Smith"));
    }

    @Test 
    void test_menu_option_1_not_empty(){
        when(auth.login(anyString(), anyString())).thenReturn(new User(1, "mgr_adam", "manager", "Adam Fischer"));
        when(approvals.listPendingByDate(any())).thenReturn(List.of(new PendingExpense(1, "Adam Fischer", "2026-08-28", "Lodging", 100.00, "That date is my birthday")));

        Menu menu = new Menu(auth, approvals, reports, new Scanner("mgr_john\nPassw0rd!\n1\n\n0"));
        menu.run();

        assertTrue(out.toString().contains("Welcome, Adam Fischer"));
        assertTrue(out.toString().contains("1"));
        assertTrue(out.toString().contains("Adam Fischer"));
        assertTrue(out.toString().contains("2026-08-28"));
        assertTrue(out.toString().contains("Lodging"));
        assertTrue(out.toString().contains("100.00"));
        assertTrue(out.toString().contains("That date is my birthday"));
    }

    @Test 
    void test_menu_option_1_empty(){
        when(auth.login(anyString(), anyString())).thenReturn(new User(1, "mgr_adam", "manager", "Adam Fischer"));
        when(approvals.listPendingByDate(any())).thenReturn(List.of());

        Menu menu = new Menu(auth, approvals, reports, new Scanner("mgr_john\nPassw0rd!\n1\n\n0"));
        menu.run();

        //Checks logged in
        assertTrue(out.toString().contains("Welcome, Adam Fischer"));

        assertTrue(out.toString().contains("No pending expenses."));
    }

    //approval, deny, bad id input, yes comment, no comment

    @Test 
    void test_menu_option_2_approve_with_comment(){
        when(auth.login(anyString(), anyString())).thenReturn(new User(1, "mgr_adam", "manager", "Adam Fischer"));
        Menu menu = new Menu(auth, approvals, reports, new Scanner("mgr_adam\nPassw0rd!\n2\n1\na\ny\nComment\n0"));

        menu.run();

        assertTrue(out.toString().contains("Successfully updated the expense report"));
    }

    @Test 
    void test_menu_option_2_approve_without_comment(){
        when(auth.login(anyString(), anyString())).thenReturn(new User(1, "mgr_adam", "manager", "Adam Fischer"));
        Menu menu = new Menu(auth, approvals, reports, new Scanner("mgr_adam\nPassw0rd!\n2\n1\na\nn\n0"));

        menu.run();

        assertTrue(out.toString().contains("Successfully updated the expense report"));
    }

    @Test 
    void test_menu_option_2_deny(){
        when(auth.login(anyString(), anyString())).thenReturn(new User(1, "mgr_adam", "manager", "Adam Fischer"));
        Menu menu = new Menu(auth, approvals, reports, new Scanner("mgr_adam\nPassw0rd!\n2\n1\nd\nComment\n0"));

        menu.run();

        assertTrue(out.toString().contains("Successfully updated the expense report"));
    }

    @Test 
    void test_menu_option_2_bad_input_id(){
        when(auth.login(anyString(), anyString())).thenReturn(new User(1, "mgr_adam", "manager", "Adam Fischer"));
        Menu menu = new Menu(auth, approvals, reports, new Scanner("mgr_adam\nPassw0rd!\n2\n-1\n0"));

        menu.run();

        assertTrue(out.toString().contains("Please enter a valid expense ID (a positive whole number)."));
    }

    @Test 
    void test_menu_option_3_employee(){
        when(auth.login(anyString(), anyString())).thenReturn(new User(1, "mgr_adam", "manager", "Adam Fischer"));
        when(reports.byEmployee(any())).thenReturn(List.of(new ReportRow("MD Tanjeem", 3, 1000.00, 2)));

        Menu menu = new Menu(auth, approvals, reports, new Scanner("mgr_adam\nPassw0rd!\n3\ne\n\n0"));
        menu.run();

        assertTrue(out.toString().contains("Employee Name"));
        assertTrue(out.toString().contains("MD Tanjeem"));
        assertTrue(out.toString().contains("3"));
        assertTrue(out.toString().contains("1000.00"));
        assertTrue(out.toString().contains("2"));
    }

    @Test 
    void test_menu_option_3_category(){
        when(auth.login(anyString(), anyString())).thenReturn(new User(1, "mgr_adam", "manager", "Adam Fischer"));
        when(reports.byCategory(any())).thenReturn(List.of(new ReportRow("Excursions", 3, 1000.00, 2)));

        Menu menu = new Menu(auth, approvals, reports, new Scanner("mgr_adam\nPassw0rd!\n3\nc\n\n0"));
        menu.run();

        assertTrue(out.toString().contains("Category Name"));
        assertTrue(out.toString().contains("Excursions"));
        assertTrue(out.toString().contains("3"));
        assertTrue(out.toString().contains("1000.00"));
        assertTrue(out.toString().contains("2"));
    }

    @Test 
    void test_menu_option_3_empty(){
        when(auth.login(anyString(), anyString())).thenReturn(new User(1, "mgr_adam", "manager", "Adam Fischer"));
        when(reports.byCategory(any())).thenReturn(List.of());

        Menu menu = new Menu(auth, approvals, reports, new Scanner("mgr_adam\nPassw0rd!\n3\nc\n\n0"));
        menu.run();

        assertTrue(out.toString().contains("No data for the selected criteria."));
    }
    

}
