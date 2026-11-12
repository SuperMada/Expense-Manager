package service;

import java.util.List;

import dao.ReportDao;
import model.ReportRow;
import model.User;

//This service is fairly simple, we just need output no input so just call the sql after checking user is manager
public class ReportService {
    private final ReportDao reportDao;

    public ReportService(ReportDao reportDao) { this.reportDao = reportDao; }

    public List<ReportRow> byEmployee(User actor) {
        AuthorizationService.authorized(actor.role(), "manager");
        return reportDao.getReportEmployee();
    }

    public List<ReportRow> byCategory(User actor) {
        AuthorizationService.authorized(actor.role(), "manager");
        return reportDao.getReportCategory();
    }
}