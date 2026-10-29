package exceptions;

public class NotFoundException extends ExpenseManagerException {
    public NotFoundException(String message){
        super(message);
    }
}
