import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.util.Scanner;

import dao.*;
import exceptions.ExpenseManagerException;
import service.*;

public class Main {
    public static void main(String[] args) {
        int exitCode = run();
        System.exit(exitCode);
    }

    private static int run() {
        try (Scanner in = new Scanner(System.in);
            Connection conn = db.Database.init()) {

            UserDao userDao = new UserDao(conn);
            ExpenseDao expenseDao = new ExpenseDao(conn);
            ApprovalDao approvalDao = new ApprovalDao(conn);
            ReportDao reportDao = new ReportDao(conn);

            AuthorizationService auth = new AuthorizationService(userDao);
            ApprovalService approvals = new ApprovalService(approvalDao, expenseDao, Clock.systemDefaultZone());
            ReportService reports = new ReportService(reportDao);

            return new Menu(auth, approvals, reports, in).run();

        } catch (ExpenseManagerException e) {          // expected errors: short message
            System.out.println(e.getMessage());
            return 1;
        } catch (SQLException | IOException e) {       // DB couldn't open / init
            System.out.println("Could not open the database.");
            // log e here
            return 1;
        } catch (Exception e) {                        // anything else (FR-S4)
            System.out.println("Unexpected error. See log.");
            // log e here
            return 1;
        }
    }
}