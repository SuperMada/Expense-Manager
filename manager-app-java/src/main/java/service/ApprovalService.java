package service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import dao.ApprovalDao;
import dao.ExpenseDao;
import exceptions.InvalidStateException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import model.PendingExpense;
import model.User;
import service.AuthorizationService;
import validation.CommentValidator;

public class ApprovalService {
    private final ApprovalDao approvalDao;
    private final ExpenseDao expenseDao;
    private final Clock clock;

    public ApprovalService(ApprovalDao approvalDao, ExpenseDao expenseDao, Clock clock) {
        this.approvalDao = approvalDao;
        this.expenseDao = expenseDao;
        this.clock = clock;
    }

    public List<PendingExpense> listPendingByDate(User actor){
        AuthorizationService.authorized(actor.role(), "manager");
        return expenseDao.getExpenseSortDate();
    }

    /** Used by the menu before it asks for a comment. Throws if the expense can't be decided. */
    public void requirePending(User actor, int expenseId) {
        AuthorizationService.authorized(actor.role(), "manager");
        checkPending(expenseId);
    }

    /** FR-M2, VR-06: comment is optional; a blank comment is stored as null. */
    public void approve(User actor, int expenseId, String comment) {
        AuthorizationService.authorized(actor.role(), "manager");
        if (!CommentValidator.isValidApproval(comment))
            throw new ValidationException("Approval comment must be at most 200 characters.");
        String cleaned = (comment == null || comment.isBlank()) ? null : comment.strip();
        checkPending(expenseId);
        approvalDao.saveDecision(expenseId, "approved", actor.id(), cleaned, today());
    }

    /** FR-M2, VR-05: comment is required, 5-200 characters. */
    public void deny(User actor, int expenseId, String comment) {
        AuthorizationService.authorized(actor.role(), "manager");
        if (!CommentValidator.isValidDenial(comment))
            throw new ValidationException("A denial comment is required (5-200 characters).");
        checkPending(expenseId);
        approvalDao.saveDecision(expenseId, "denied", actor.id(), comment.strip(), today());
    }

    private void checkPending(int expenseId) {
        String status = approvalDao.findStatus(expenseId);
        if (status == null)
            throw new NotFoundException("No expense found with ID " + expenseId + ".");
        if (!status.equals("pending"))
            throw new InvalidStateException("Expense has already been " + status + ".");
    }

    private LocalDate today() { return LocalDate.now(clock); }
}
