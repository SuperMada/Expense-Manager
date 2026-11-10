package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import exceptions.DatabaseException;
import model.PendingExpense;

public class ExpenseDao {
    private final Connection conn;

    public ExpenseDao(Connection conn){
        this.conn = conn;
    }


    public List<PendingExpense> getExpenseSortAmount(){
        String sql = """
                    SELECT ex.id, u.full_name, ex.expense_date, c.name, ex.amount, ex.description
                    FROM expenses ex
                    JOIN users u ON ex.user_id = u.id 
                    JOIN categories c ON ex.category_id = c.id
                    JOIN approvals a ON ex.id = a.expense_id
                    WHERE a.status like 'pending'
                    ORDER BY ex.amount;
                    """;
        List<PendingExpense> results = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            while(rs.next()) {
                results.add(new PendingExpense(
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("expense_date"),
                    rs.getString("name"),
                    rs.getDouble("amount"),
                    rs.getString("description")));
            }
            return results;
        } catch(SQLException e){
            throw new DatabaseException("DB Error attempting to list pending expenses");
        }
    }


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
            return results;
        } catch(SQLException e){
            throw new DatabaseException("DB Error attempting to list pending expenses");
        }
    }
}
