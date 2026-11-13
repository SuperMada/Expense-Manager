package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Logger;

import exceptions.DatabaseException;
import logging.AppLogger;

public class ApprovalDao {
    private static final Logger log = AppLogger.get(ApprovalDao.class);
    private final Connection conn;

    public ApprovalDao(Connection conn){ 
        this.conn = conn;
    }

    // Returns "pending", "approved", "denied", or null if no such expense.
    public String findStatus(int expenseId) {
        String sql = "SELECT status FROM approvals WHERE expense_id = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, expenseId);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    log.info("Got status: " + rs.getString("status") + " from id: " + expenseId);
                    return rs.getString("status");
                }
                else{
                    log.warning("Failed to find expense with id: " + expenseId);
                    return null;
                }
            }
        } catch(SQLException e) {
            log.severe("DB error attempting to find status for id: " + expenseId);
            throw new DatabaseException("DB Error attempting to read approval status");
        }
    }

    //SQL to write to db
    public void saveDecision(int expenseId, String status, int reviewerId,
                             String comment, LocalDate reviewDate) {
        String sql = """
            UPDATE approvals
            SET status = ?, reviewer_id = ?, comment = ?, review_date = ?
            WHERE expense_id = ?
            """;
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reviewerId);
            ps.setString(3, comment);
            ps.setString(4, reviewDate.toString());
            ps.setInt(5, expenseId);
            ps.executeUpdate();
        } catch(SQLException e) {
            log.severe("DB error attempting to update expense: " + expenseId);
            throw new DatabaseException("DB Error attempting to update approval");
        }
        log.info("Expense with id: " + expenseId + " updated with status: " + status);
    }
}