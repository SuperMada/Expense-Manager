import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import exceptions.AuthenticationException;
import exceptions.AuthorizationException;


public class Menu {
    static int TOTAL_LOGIN_ATTEMPTS = 3;

    public static String loginUsername(Scanner in){
        String username = "";
        while(username.equals("")){
            System.out.print("Please Enter Your Username: ");
            username = in.nextLine();
            System.out.println();
        }
        return username;
    }

    public static String loginPassword(Scanner in){
        String password = "";
        while(password.equals("")){
            System.out.print("Please Enter Your Password: ");
            password = in.nextLine();
            System.out.println();
        }
        return password;
    }

    public static int loginMenu(Connection conn, Scanner in){
        String username = "";
        String password = "";
        int login_attempts = 0;
        int user = 0;
        
        while(login_attempts < TOTAL_LOGIN_ATTEMPTS){
            username = "";
            password = "";
            //When the input is blank we don't want to use a login attempt
            //Question: Does an empty input include just spaces i.e. do we need to strip the input
            //I believe we need to strip afterwords but do we do it before
            username = loginUsername(in);
            password = loginPassword(in);
            try{
                user = Services.login(conn, username, password);
                break;
            } catch(AuthenticationException e){
                //Bad username + password
                login_attempts++;
                System.out.println(e.getMessage());
                continue;
            } catch(AuthorizationException e){
                //User not manager
                System.out.println(e.getMessage());
                System.exit(0);
            } catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }
        if(login_attempts == TOTAL_LOGIN_ATTEMPTS){
            System.out.println("Too many failed attempts.");
            System.exit(1);
        }
        return user;
    }

    public static void managerMenu(Connection conn, Scanner in, int user)
        throws SQLException{
        String name = Services.getNameFromID(conn, user);
        System.out.printf("Welcome, %s\n", name);

    }
}
