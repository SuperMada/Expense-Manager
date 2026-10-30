public class Validators {
    private static int DENIAL_MIN = 5;
    private static int DENIAL_MAX = 200;

    private static int APPROVAL_MIN = 0;
    private static int APPROVAL_MAX = 200;

    public static boolean denialComment(String comment){
        int length = comment.length();
        if(length < DENIAL_MIN){
            return false;
        } else if(length > DENIAL_MAX){
            return false;
        }
        return true;
    }

    public static boolean approvalComment(String comment){
        int length = comment.length();
        if(length < APPROVAL_MIN){
            return false;
        } else if(length > APPROVAL_MAX){
            return false;
        }
        return true;
    }
}
