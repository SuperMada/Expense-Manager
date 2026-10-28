import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Services {
    public static boolean login(Connection conn, String username, String password){
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
            ResultSet rs = ps.executeQuery();
            try{
                if(rs.next()){
                    user = rs.getInt("id");
                    role = rs.getString("role");
                } else{
                    //For these checks these need to return an error code that is used in the front
                    //Because we want to return user id these should be negative codes that equate to different errors
                    //If the username or password does not exist/not a valid login
                    System.out.println("This username + Password combo does not exist");
                }
                if(!role.equals("manager")){
                    //If the user is not a manager
                    System.out.println("You are not a manager");
                }

            } catch (Exception e){
                e.printStackTrace();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
