package validation;

public final class CommentValidator {
    private CommentValidator() {}

    //Check if the denial comment is valid
    public static boolean isValidDenial(String comment) {
        if (comment == null) return false;
        int len = comment.strip().length();
        return len >= 5 && len <= 200;
    }

    //Check if the approval comment is valid
    public static boolean isValidApproval(String comment) {
        return comment == null || comment.strip().length() <= 200;
    }
}