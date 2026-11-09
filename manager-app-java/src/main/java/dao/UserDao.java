package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import exceptions.DatabaseException;

public class UserDao {
    private final Connection conn;

    public UserDao(Connection conn){
        this.conn = conn;
    }

    public User findByLogin(String username, String password){
        String sql = "SELECT id, username, role, full_name FROM users WHERE username = ? and password = ?;";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                } else {
                    return null;
                }
            } catch(SQLException e) {
                throw new DatabaseException("DB Error attempting to login");
            }
        }
    }

    public User findById(int id){
        String sql = "SELECT id, username, role, full_name FROM users WHERE id = ?;";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                } else {
                    return null;
                }
            } catch(SQLException e) {
                throw new DatabaseException("DB Error attempting to get user with id");
            }
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id"), rs.getString("username"),
                        rs.getString("role"), rs.getString("full_name"));
    }


}
