import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import exceptions.AuthenticationException;
import exceptions.AuthorizationException;

public class Services {
    public static int login(Connection conn, String username, String password)
        throws AuthenticationException, AuthorizationException, SQLException{
            /*String sql = "INSERT INTO expenses (description, amount, category_id) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "Coffee");
            ps.setDouble(2, 4.75);
            ps.setInt(3, 1);
            ps.executeUpdate();
} */
        //Check that the username and password are ok - dont need that because we never create them, as long as they are strings we just need to check if empty or not on bad
        //attempt so we can use an attempt or not
        //If not return an error stating we need proper formatting
        //If ok then attempt to find the user in the db
        //If not found say user doesnt exist
        //If ok return the users id so we know what user is in
        //Log logins

        String sql = "SELECT id, role FROM users WHERE username = ? and password = ?;";
        int user = 0;
        String role = "";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, username);
            ps.setString(2, password);

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    user = rs.getInt("id");
                    role = rs.getString("role");
                } else{
                    //For these checks these need to return an error code that is used in the front
                    //Because we want to return user id these should be negative codes that equate to different errors
                    //If the username or password does not exist/not a valid login
                    throw new AuthenticationException("Invalid username or password.\n");
                }
                if(!role.equals("manager")){
                    //If the user is not a manager
                    throw new AuthorizationException("This account does not have access to this app.");
                }
            }
        }
        return user;
    }

    public static String getNameFromID(Connection conn, int id)
        throws SQLException{
        String sql = "SELECT full_name FROM users WHERE id = ?;";
        String name = "";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    name = rs.getString("full_name");
                }
            }
        }
        return name;
    }
}
