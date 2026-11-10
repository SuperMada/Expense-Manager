import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.util.Scanner;
import java.util.logging.Logger;

import dao.*;
import exceptions.ExpenseManagerException;
import logging.AppLogger;
import service.*;

public class Main {
    public static void main(String[] args) {
        int exitCode = run();
        System.exit(exitCode);
    }

    private static int run() {
        try {
            AppLogger.init(Path.of("logs", "manager_app.log"));
        } catch (IOException e) {
            System.out.println("Could not start logging.");
        }
        Logger log = AppLogger.get(Main.class);
        log.info("Manager app starting");

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