//The main rn is just gonna set up the database and then I'll use it for testing, once we have each service and validator working we can connect everything


import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import exceptions.AuthenticationException;
import exceptions.AuthorizationException;



public class Main {
    static int TOTAL_LOGIN_ATTEMPTS = 3;
    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            try (Connection conn = Database.init()) {
                String username = "";
                String password = "";
                System.out.println("Connected!");
                int login_attempts = 0;
                int user = 0;
                
                while(login_attempts < TOTAL_LOGIN_ATTEMPTS){
                    username = "";
                    password = "";
                    //When the input is blank we don't want to use a login attempt
                    //Question: Does an empty input include just spaces i.e. do we need to strip the input
                    //I believe we need to strip afterwords but do we do it before
                    while(username.equals("")){
                        System.out.print("Please Enter Your Username: ");
                        username = in.nextLine();
                        System.out.println();
                    }
                    while(password.equals("")){
                        System.out.print("Please Enter Your Password: ");
                        password = in.nextLine();
                        System.out.println();
                    }
                    try{
                        user = Services.login(conn, username, password);
                    } catch(AuthenticationException e){
                        //Bad username + password
                        login_attempts++;
                        System.out.println(e.getMessage());
                        System.out.println(login_attempts);
                        continue;
                    } catch(AuthorizationException e){
                        //User not manager
                        System.out.println(e.getMessage());
                        return;
                    }
                    
                    //This is where we start our regular code, we only get here if login attempt worked
                    //user is the id of the users table, the primary key of the account
                    String name = Services.getNameFromID(conn, user);
                    System.out.println(String.format("Welcome, %s", name));
                }

                if(login_attempts == TOTAL_LOGIN_ATTEMPTS){
                    System.out.println("Max login attempts hit you are blocked");
                }
            } catch (SQLException | IOException e){
                e.printStackTrace();
            }
            
        }
    }
}