import java.sql.Connection;
import java.sql.ResultSet;
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
        int option = -1;
        String input;
        while(true){
            option = -1;
            System.out.printf("Welcome, %s\n", name);
            System.out.println("Please Kindly Select an Option...\n");
            System.out.println("1: List Pending Expenses");
            System.out.println("2: Review An Expense");
            System.out.println("3: Reports");
            System.out.println("0: Exit");
            input = in.nextLine();
            System.out.println();
            try{
                option = Integer.parseInt(input);
            } catch(NumberFormatException e){
                System.out.println("Please enter a valid option.\n");
                continue;
            }
            if(option == 0){
                System.out.println("Shutting Down...");
                System.exit(0);
            } else if(option == 1){
                //Call pending expenses
                listExpenses(conn);
            } else if(option == 2){
                //Call Rewview expenses
            } else if(option == 3){
                //Call reports
            } else{
                System.out.println("Please enter a valid option.\n");
                continue;
            }
        }
    }

    public static void listExpenses(Connection conn)
        throws SQLException{
        ResultSet data = Services.getExpensesFromID(conn);
        if(!data.next()){
            System.out.println("No pending expenses.\n");
        } else{
            System.out.printf("Expense ID: %d, Employee Name: %s, Date: %s, Category: %s, Amount: $%d, Description: %s", data.getInt("id"), data.getString("name"), data.getString("expense_date"), data.getString("category"), data.getInt("amount"), data.getString("description"));
        }

    }
}
