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
import model.PendingExpense;

public class ExpenseDao {
    private static final Logger log = AppLogger.get(ExpenseDao.class);
    private final Connection conn;

    public ExpenseDao(Connection conn){
        this.conn = conn;
    }

    //SQL to get pending expenses
    //Name is kinda whack, its from when i though we needed to sort by date and amount
    public List<PendingExpense> getExpenseSortDate(){
        String sql = """
                    SELECT ex.id, u.full_name, ex.expense_date, c.name, ex.amount, ex.description
                    FROM expenses ex
                    JOIN users u ON ex.user_id = u.id 
                    JOIN categories c ON ex.category_id = c.id
                    JOIN approvals a ON ex.id = a.expense_id
                    WHERE a.status like 'pending'
                    ORDER BY ex.expense_date;
                    """;
        List<PendingExpense> results = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                results.add(new PendingExpense(
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("expense_date"),
                    rs.getString("name"),
                    rs.getDouble("amount"),
                    rs.getString("description")));
            }
            log.info("Pending expenses list successful");
            return results;
        } catch(SQLException e){
            log.severe("DB error while trying to get pending expenses");
            throw new DatabaseException("DB Error attempting to list pending expenses");
        }
    }
}
