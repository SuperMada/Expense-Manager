package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import exceptions.DatabaseException;

public class ApprovalDao {
    private final Connection conn;

    public ApprovalDao(Connection conn){ 
        this.conn = conn;
    }

    /** Returns "pending", "approved", "denied", or null if no such expense. */
    public String findStatus(int expenseId) {
        String sql = "SELECT status FROM approvals WHERE expense_id = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, expenseId);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) 
                    return rs.getString("status");
                else{
                    return null;
                }
            }
        } catch(SQLException e) {
            throw new DatabaseException("DB Error attempting to read approval status");
        }
    }

    /** comment may be null for an approval with no comment. */
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
            ps.setString(4, reviewDate.toString());   // YYYY-MM-DD
            ps.setInt(5, expenseId);
            ps.executeUpdate();
        } catch(SQLException e) {
            throw new DatabaseException("DB Error attempting to update approval");
        }
    }
}