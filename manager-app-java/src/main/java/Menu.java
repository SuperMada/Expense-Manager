import java.io.Console;
import java.util.List;
import java.util.Scanner;

import exceptions.AuthenticationException;
import exceptions.AuthorizationException;
import exceptions.ExpenseManagerException;
import exceptions.ValidationException;
import model.PendingExpense;
import model.ReportRow;
import model.User;
import service.ApprovalService;
import service.ReportService;
import service.AuthorizationService;
import validation.InputValidator;

public class Menu {
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private final AuthorizationService auth;
    private final ApprovalService approvals;
    private final ReportService reports;
    private final Scanner in;

    public Menu(AuthorizationService auth, ApprovalService approvals,
                ReportService reports, Scanner in) {
        this.auth = auth;
        this.approvals = approvals;
        this.reports = reports;
        this.in = in;
    }

    /** Runs the whole session and returns the process exit code. */
    public int run() {
        User user;
        try {
            user = login();
        } catch (AuthorizationException e) {       // FR-A2: wrong role
            System.out.println(e.getMessage());
            return 0;
        }
        if (user == null) {                        // FR-A3
            System.out.println("Too many failed attempts.");
            return 1;
        }
        System.out.printf("Welcome, %s\n\n", user.fullName());
        mainLoop(user);
        return 0;
    }

    // ---------- login ----------

    /** Returns the user, or null after too many failed attempts. */
    private User login() {
        for (int attempt = 0; attempt < MAX_LOGIN_ATTEMPTS; attempt++) {
            String username = promptNonEmpty("Please Enter Your Username: ");
            String password = promptNonEmptyPassword("Please Enter Your Password: ");
            try {
                return auth.login(username, password);
            } catch (AuthenticationException e) {
                System.out.println(e.getMessage() + "\n");
            }
        }
        return null;
    }

    // ---------- main menu ----------

    private void mainLoop(User user) {
        while (true) {
            System.out.println("Please Kindly Select an Option...\n");
            System.out.println("1: List Pending Expenses");
            System.out.println("2: Review An Expense");
            System.out.println("3: Reports");
            System.out.println("0: Exit");
            String input = in.nextLine().strip();
            System.out.println();

            try {
                switch (input) {
                    case "0" -> { System.out.println("Shutting Down..."); return; }
                    case "1" -> listPending(user);
                    case "2" -> review(user);
                    case "3" -> reportsMenu(user);
                    default  -> System.out.println("Please enter a valid option.\n");
                }
            } catch (ExpenseManagerException e) {
                System.out.println(e.getMessage() + "\n");
            }
        }
    }

    // ---------- 1: list pending ----------

    private void listPending(User user) {
        List<PendingExpense> expenses = approvals.listPendingByDate(user);
        if (expenses.isEmpty()) {
            System.out.println("No pending expenses.\n");
            return;
        }
        for (PendingExpense e : expenses) {
            System.out.printf(
                "Expense ID: %d, Employee Name: %s, Date: %s, Category: %s, Amount: $%.2f, Description: %s\n",
                e.id(), e.fullName(), e.date(), e.category(), e.amount(), e.description());
        }
        waitForEnter();
    }

    // ---------- 2: review ----------

    private void review(User user) {
        String idText = prompt("Please enter the ID of the expense you wish to review: ");
        if (!InputValidator.isValidExpenseId(idText)) {            // VR-08
            System.out.println("Please enter a valid expense ID (a positive whole number).\n");
            return;
        }
        int id = Integer.parseInt(idText);

        approvals.requirePending(user, id);

        boolean approve = charInput("Please Select Approve('a') or Deny('d')", "a", "d").equals("a");
        boolean wantsComment = !approve || charInput("Would you like to add a comment? (y/n)", "y", "n").equals("y");

        while (true) {
            String comment = wantsComment ? prompt("Please enter your comment: ") : null;
            try {
                if (approve) approvals.approve(user, id, comment);
                else         approvals.deny(user, id, comment);
                System.out.println("Successfully updated the expense report\n");
                return;
            } catch (ValidationException e) {
                System.out.println(e.getMessage() + "\n");
            }
        }
    }

    // ---------- 3: reports ----------

    private void reportsMenu(User user) {
        boolean byEmployee = charInput(
            "Would you like to create report on Employee('e') or Category('c'): ", "e", "c").equals("e");

        List<ReportRow> rows = byEmployee ? reports.byEmployee(user) : reports.byCategory(user);
        if (rows.isEmpty()) {
            System.out.println("No data for the selected criteria.\n");
            return;
        }
        String label = byEmployee ? "Employee Name" : "Category Name";
        for (ReportRow r : rows) {
            System.out.printf("%s: %s, Total Reports: %d, Total Amount: $%.2f, Total Approved: %d\n",
                label, r.name(), r.count(), r.total(), r.approvedCount());
        }
        waitForEnter();
    }

    // ---------- input helpers (the only place Scanner is read) ----------

    private String prompt(String question) {
        System.out.print(question);
        String input = in.nextLine().strip();
        System.out.println();
        return input;
    }

    private String promptNonEmpty(String question) {       // empty input re-prompts, no attempt used
        String input = "";
        while (input.isEmpty()) input = prompt(question);
        return input;
    }

    private String promptPassword(String question){
        Console console = System.console();
        //For tests the console is always null and I can't take in password
        if (console == null) {
            return prompt(question);
        }
        char[] password = console.readPassword(question);
        System.out.println();
        return new String(password);
    }

    private String promptNonEmptyPassword(String question){
        String input = "";
        while (input.isEmpty()) input = promptPassword(question);
        return input;
    }

    private String charInput(String question, String opt1, String opt2) {
        while (true) {
            String input = prompt(question + " ");
            if (input.equals(opt1) || input.equals(opt2)) return input;
            System.out.printf("Please enter a valid selection %s/%s\n", opt1, opt2);
        }
    }

    private void waitForEnter() {
        System.out.println("\nPress ENTER to return to main menu");
        in.nextLine();
    }
}