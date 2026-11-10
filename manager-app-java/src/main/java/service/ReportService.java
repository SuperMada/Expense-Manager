package service;

import java.util.List;

import dao.ReportDao;
import model.ReportRow;
import model.User;

public class ReportService {
    private final ReportDao reportDao;

    public ReportService(ReportDao reportDao) { this.reportDao = reportDao; }

    /** FR-M3: one row per employee. */
    public List<ReportRow> byEmployee(User actor) {
        AuthorizationService.authorized(actor.role(), "manager");
        return reportDao.getReportEmployee();
    }

    /** FR-M3: one row per category. */
    public List<ReportRow> byCategory(User actor) {
        AuthorizationService.authorized(actor.role(), "manager");
        return reportDao.getReportCategory();
    }
}