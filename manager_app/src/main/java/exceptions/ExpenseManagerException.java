package exceptions;

public class ExpenseManagerException extends RuntimeException{
    public ExpenseManagerException(String message){
        super(message);
    }
}
