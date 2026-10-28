package exceptions;

public class ValidationException extends ExpenseManagerException{
    public ValidationException(String message){
        super(message);
    }
}
