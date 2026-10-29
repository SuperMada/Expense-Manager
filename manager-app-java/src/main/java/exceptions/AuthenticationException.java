package exceptions;

public class AuthenticationException extends ExpenseManagerException {
    public AuthenticationException(String message){
        super(message);
    }
}
