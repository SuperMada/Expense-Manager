import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import javax.xml.validation.Validator;

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
            System.out.printf("Welcome, %s,\n", name);
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
                listExpenses(conn, in);
            } else if(option == 2){
                //Call Rewview expenses
                reviewExpenses(conn, in, user);
            } else if(option == 3){
                //Call reports
            } else{
                System.out.println("Please enter a valid option.\n");
                continue;
            }
        }
    }

    public static void reviewExpenses(Connection conn, Scanner in, int user)
        throws SQLException{
        int expense_id = -1;
        String status = "";
        int reviewer_id = user;
        String comment = "";
        String date = LocalDate.now().toString();

        //Need to take an expense id input first and check that it exists and is still pending before moving forward
        while(expense_id < 0){
            int option = -1;
            System.out.println("Please enter the ID of the expense you wish to review: ");
            String input = in.nextLine();
            System.out.println();
            try{
                option = Integer.parseInt(input);
            } catch(NumberFormatException e){
                System.out.println("Please enter an integer ID.\n");
                continue;
            }
            if(option>=0){
                //See if the id exists and is pending
                String report = Services.isPendingReport(conn, option);
                System.out.println(report);
                if(report.equals("Report Pending Approval Found\n")){
                    //Ask inputs
                    //Add to db
                    expense_id = option;
                    //Update status on a or d
                    while(true){
                        System.out.println("Please Select Approve('a') or Deny('d')");
                        String status_input = in.nextLine();
                        System.out.println();
                        if(status_input.equals("a")){
                            status = "approved";
                            break;
                        } else if(status_input.equals("d")){
                            status = "denied";
                            break;
                        }
                        System.out.println("Please enter a valid option (a/d)\n");
                    }
                    if(status.equals("approved")){
                        String comment_status = "";
                        while(true){
                            System.out.println("Would you like to add a comment? (y/n)");
                            comment_status = in.nextLine();
                            System.out.println();
                            if(comment_status.equals("y")){
                                boolean comment_valid = false;
                                while(!comment_valid){
                                    System.out.println("Please enter your comment: ");
                                    String comment_input = in.nextLine();
                                    System.out.println();

                                    comment_valid = Validators.approvalComment(comment_input);
                                    //If the approval comment is empty should we place the empty string in the db of should be keep it null
                                    if(comment_valid){
                                        comment = comment_input;
                                    }
                                }   
                                int success = Services.updateApproval(conn, status, reviewer_id, comment, date, expense_id);
                                if(success == 1){
                                    System.out.println("Successfully updated the expense report");
                                } else{
                                    System.out.println("Unexpected error, please try again");
                                }
                                break;
                            } else if(comment_status.equals("n")){
                                int success = Services.updateApproval(conn, status, reviewer_id, date, expense_id);
                                if(success == 1){
                                    System.out.println("Successfully updated the expense report");
                                } else{
                                    System.out.println("Unexpected error, please try again");
                                }
                                break;
                            } else{
                                System.out.println("Please enter either y or n\n");
                            }
                        }
                    }
                }
            } else{
                System.out.println("Please enter a valid pending expense ID\n");
                continue;
            }
        }
    }

    public static void listExpenses(Connection conn, Scanner in)
        throws SQLException{
        int option = 0;
        while(true){
            System.out.println("1: Sort by Date\n2: Sort by Amount");
            String input = in.nextLine();
            try{
                option = Integer.parseInt(input);
            } catch(NumberFormatException e){
                System.out.println("Please enter a valid option.\n");
                continue;
            }
            if(option != 1 && option != 2){
                System.out.println("Please enter a valid option.\n");
                continue;
            }
            break;
        }
        List<List<Object>> data = Services.getExpensesFromID(conn, option);
        if(data.isEmpty()){
            System.out.println("No pending expenses.\n");
        } else{
            for(List<Object> row : data){
            System.out.printf("Expense ID: %d, Employee Name: %s, Date: %s, Category: %s, Amount: $%.2f, Description: %s\n", row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5));
            }
            System.out.println("\n\nPress ENTER to return to main menu");
            in.nextLine();
        }
    }
}
