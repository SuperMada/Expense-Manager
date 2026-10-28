//The main rn is just gonna set up the database and then I'll use it for testing, once we have each service and validator working we can connect everything


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            try (Connection conn = Database.init()) {
                System.out.println("Connected!");
                System.out.println("Please Enter Your Username: ");
                String username = in.nextLine();
                System.out.println("Please Enter Your Password: ");
                String password = in.nextLine();
                Services.login(conn, username, password);

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}