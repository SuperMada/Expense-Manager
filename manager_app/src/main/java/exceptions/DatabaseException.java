package exceptions;

public class DatabaseException extends ExpenseManagerException {
    public DatabaseException(String message){
        super(message);
    }
}
