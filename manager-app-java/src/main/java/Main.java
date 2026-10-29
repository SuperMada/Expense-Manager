//The main rn is just gonna set up the database and then I'll use it for testing, once we have each service and validator working we can connect everything


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;




public class Main {
    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            try (Connection conn = Database.init()) {
                System.out.println("Connected!");
                System.out.println();

                
                int user = Menu.loginMenu(conn, in);
                Menu.managerMenu(conn, in, user);

                
            } catch (SQLException | IOException e){
                e.printStackTrace();
            }
            
        }
    }
}