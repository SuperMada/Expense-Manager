package serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import dao.ApprovalDao;
import dao.ExpenseDao;
import exceptions.AuthorizationException;
import exceptions.InvalidStateException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import model.User;
import service.ApprovalService;

public class ApprovalServiceTest {
    private final User manager  = new User(1, "mgr_asha", "manager", "Asha Rao");
    private final User employee = new User(3, "emp_li", "employee", "Li Wei");
    //Fixed clock for rerunning tests
    private final Clock clock = Clock.fixed(Instant.parse("2026-11-09T12:00:00Z"), ZoneOffset.UTC);
    private final LocalDate today = LocalDate.of(2026, 11, 9);

    private ApprovalDao approvalDao;
    private ExpenseDao expenseDao;
    private ApprovalService service;

    @BeforeEach
    void setUp(){
        approvalDao = mock(ApprovalDao.class);
        expenseDao = mock(ExpenseDao.class);
        service = new ApprovalService(approvalDao, expenseDao, clock);
    }

    @Test 
    @Tag("Smoke")
    @Tag("Partitioning")
    void test_approve_works(){
        when(approvalDao.findStatus(1)).thenReturn("pending");

        service.approve(manager, 1, "This report is good");

        verify(approvalDao).saveDecision(1, "approved", manager.id(), "This report is good", today);
    }

    @Test 
    @Tag("Smoke")
    @Tag("Partitioning")
    void test_deny_works(){
        when(approvalDao.findStatus(1)).thenReturn("pending");

        service.deny(manager, 1, "This is a valid comment");

        verify(approvalDao).saveDecision(1, "denied", manager.id(), "This is a valid comment", today);
    }

    @Test 
    @Tag("Negative")
    @Tag("Partitioning")
    void test_approval_checks_valid_comment(){
        when(approvalDao.findStatus(1)).thenReturn("pending");

        assertThrows(ValidationException.class, () -> service.approve(manager, 1, "a".repeat(201)));
    }
 
    @ParameterizedTest 
    @Tag("Negative")
    @Tag("Partitioning")
    @CsvSource({"201", "4"})
    void test_denial_checks_valid_comment(int length){
        when(approvalDao.findStatus(1)).thenReturn("pending");

        assertThrows(ValidationException.class, () -> service.deny(manager, 1, "a".repeat(length)));
    }

    @Test 
    @Tag("Negative")
    @Tag("Partitioning")
    void test_approval_fails_on_employee_attempt(){
        assertThrows(AuthorizationException.class, () -> service.approve(employee, 1, "Comment"));
    }

    @Test 
    @Tag("Negative")
    @Tag("Partitioning")
    void test_deny_fails_on_employee_attempt(){
        assertThrows(AuthorizationException.class, () -> service.deny(employee, 1, "Comment"));
    }

    @Test 
    @Tag("Negative")
    @Tag("Partitioning")
    void test_pending_check_throws_on_approved(){
        when(approvalDao.findStatus(1)).thenReturn("approved");

        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> service.requirePending(manager, 1));
        assertEquals("Expense has already been approved.", exception.getMessage());
    }

    @Test 
    @Tag("Negative")
    @Tag("Partitioning")
    void test_pending_check_throws_on_denied(){
        when(approvalDao.findStatus(1)).thenReturn("denied");

        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> service.requirePending(manager, 1));
        assertEquals("Expense has already been denied.", exception.getMessage());
    }

    @Test 
    @Tag("Negative")
    @Tag("Partitioning")
    void test_pending_check_throws_on_null_status(){
        when(approvalDao.findStatus(1)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> service.requirePending(manager, 1));
    }
}
