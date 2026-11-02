import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
                role_required(role, "manager");
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

    public static List<List<Object>> getExpensesFromID(Connection conn, int option)
        throws SQLException{
        List<List<Object>> results = new ArrayList<>();
        String sql = "";
        if(option == 1){
            sql = """
                    SELECT ex.id, u.full_name, ex.expense_date, c.name, ex.amount, ex.description
                    FROM expenses ex
                    JOIN users u ON ex.user_id = u.id 
                    JOIN categories c ON ex.category_id = c.id
                    JOIN approvals a ON ex.id = a.expense_id
                    WHERE a.status like 'pending'
                    ORDER BY ex.expense_date;
                    """;
        } else{
            sql = """
                    SELECT ex.id, u.full_name, ex.expense_date, c.name, ex.amount, ex.description
                    FROM expenses ex
                    JOIN users u ON ex.user_id = u.id 
                    JOIN categories c ON ex.category_id = c.id
                    JOIN approvals a ON ex.id = a.expense_id
                    WHERE a.status like 'pending'
                    ORDER BY ex.amount;
                    """;
        }
        try(Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)){
            
            while(rs.next()){
                List<Object> result = new ArrayList<>();
                for(int i = 1; i<=6; i++){
                    result.add(rs.getObject(i));
                }
                results.add(result);
            }
            return results;
        }
    }


    public static void role_required(String role, String expected_role){
        if(!role.equals(expected_role)){
            //If the user is not a manager
            throw new AuthorizationException("This account does not have access to this app.");
        }
    }

    public static String getRole(Connection conn, int id)
        throws SQLException{
        String role = "";
        String sql = """
                    SELECT role
                    FROM users
                    WHERE id = ?;
                    """;
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    role = rs.getString("role");
                }
            }
        }
        return role;
    }

    public static String getCategory(Connection conn, int id)
        throws SQLException{
        String role = "";
        String sql = """
                    SELECT name
                    FROM categories
                    WHERE id = ?;
                    """;
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    role = rs.getString("name");
                }
            }
        }
        return role;
    }

    public static String isPendingReport(Connection conn, int id)
        throws SQLException{
        String sql = """
                    SELECT status
                    FROM approvals
                    WHERE expense_id = ?;    
                    """;
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    String status = rs.getString("status");
                    if(status.equals("pending")){
                        return "Report Pending Approval Found\n";
                    } else{
                        return String.format("Given Report ID has already been %s\n", status);
                    }
                } else{
                    return "No report found under given ID\n";
                }
            }
        }
    }

    //Adding approval with comment
    public static int updateApproval(Connection conn, String status, int reviewer_id, String comment, String review_date, int expense_id)
        throws SQLException{
        String sql = """
                    UPDATE approvals
                    SET status = ?, reviewer_id = ?, comment = ?, review_date = ?
                    WHERE expense_id = ?;
                    """;
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, status);
            ps.setInt(2, reviewer_id);
            ps.setString(3, comment);
            ps.setString(4, review_date);
            ps.setInt(5, expense_id);
            return ps.executeUpdate();
        }
    }
    //Adding approval without comment
    public static int updateApproval(Connection conn, String status, int reviewer_id, String review_date, int expense_id)
        throws SQLException{
        String sql = """
                    UPDATE approvals
                    SET status = ?, reviewer_id = ?, review_date = ?
                    WHERE expense_id = ?;
                    """;
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, status);
            ps.setInt(2, reviewer_id);
            ps.setString(3, review_date);
            ps.setInt(4, expense_id);
            return ps.executeUpdate();
        }
    }

    public static List<List<Object>> getEmployeeReport(Connection conn, int user_id)
        throws SQLException{
        List<List<Object>> results = new ArrayList<>();
        String sql = """
            SELECT u.full_name as name, COUNT(*) as totalReports, SUM(ex.amount) as totalAmount, SUM(CASE WHEN a.status = 'approved' THEN 1 ELSE 0 END) as totalApproved
            FROM users u
            JOIN expenses ex ON u.id = ex.user_id 
            JOIN approvals a ON ex.id = a.expense_id
            WHERE u.id = ?
            GROUP BY u.id;
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, user_id);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    List<Object> result = new ArrayList<>();
                    for(int i = 1; i<=4; i++){
                        result.add(rs.getObject(i));
                    }
                    results.add(result);
                }
                return results;
            }
        }
    }
    public static List<List<Object>> getCategoryReport(Connection conn, int category_id)
        throws SQLException{
        List<List<Object>> results = new ArrayList<>();
            String sql = """
            SELECT c.name as name, COUNT(*) as totalReports, SUM(ex.amount) as totalAmount, SUM(CASE WHEN a.status = 'approved' THEN 1 ELSE 0 END) as totalApproved
            FROM categories c 
            JOIN expenses ex ON c.id = ex.category_id 
            JOIN approvals a ON ex.id = a.expense_id
            WHERE c.id = ?
            GROUP BY c.id; 
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, category_id);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    List<Object> result = new ArrayList<>();
                    for(int i = 1; i<=4; i++){
                        result.add(rs.getObject(i));
                    }
                    results.add(result);
                }
                return results;
            }
        }
    }
}
