package exceptions;

public class AuthorizationException extends ExpenseManagerException {
    public AuthorizationException(String message){
        super(message);
    }
}
