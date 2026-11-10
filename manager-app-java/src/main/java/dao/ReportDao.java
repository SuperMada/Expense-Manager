package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import exceptions.DatabaseException;
import logging.AppLogger;
import model.ReportRow;

public class ReportDao {
    private static final Logger log = AppLogger.get(ReportDao.class);   
    private final Connection conn;

    public ReportDao(Connection conn){
        this.conn = conn;
    }

    public List<ReportRow> getReportEmployee(){
        String sql = """
            SELECT u.full_name as name, COUNT(*) as totalReports, SUM(ex.amount) as totalAmount, SUM(CASE WHEN a.status = 'approved' THEN 1 ELSE 0 END) as totalApproved
            FROM users u
            JOIN expenses ex ON u.id = ex.user_id 
            JOIN approvals a ON ex.id = a.expense_id
            GROUP BY u.id;
            """;

        List<ReportRow> rows = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()){
                rows.add(new ReportRow(
                    rs.getString("name"),
                    rs.getInt("totalReports"),
                    rs.getDouble("totalAmount"),
                    rs.getInt("totalApproved")));
            }
            log.info("Report on employees successful");
            return rows;
        } catch(SQLException e){
            log.severe("DB error while attempting to create report on employees");
            throw new DatabaseException("DB Error attempting to get report");
        }
    }

    public List<ReportRow> getReportCategory(){
        String sql = """
            SELECT c.name as name, COUNT(*) as totalReports, SUM(ex.amount) as totalAmount, SUM(CASE WHEN a.status = 'approved' THEN 1 ELSE 0 END) as totalApproved
            FROM categories c 
            JOIN expenses ex ON c.id = ex.category_id 
            JOIN approvals a ON ex.id = a.expense_id
            GROUP BY c.id; 
            """;

        List<ReportRow> rows = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                rows.add(new ReportRow(
                    rs.getString("name"),
                    rs.getInt("totalReports"),
                    rs.getDouble("totalAmount"),
                    rs.getInt("totalApproved")));
            }
            log.info("Report on category successful");
            return rows;
        } catch(SQLException e){
            log.severe("DB error while attempting to create report on category");
            throw new DatabaseException("DB Error attempting to get report");
        }
    }
}
