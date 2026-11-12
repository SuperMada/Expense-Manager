package validation;

public final class CommentValidator {
    private CommentValidator() {}

    /** VR-05: required, 5-200 characters after trimming. */
    public static boolean isValidDenial(String comment) {
        if (comment == null) return false;
        int len = comment.strip().length();
        return len >= 5 && len <= 200;
    }

    /** VR-06: optional (null or empty is fine), at most 200 characters. */
    public static boolean isValidApproval(String comment) {
        return comment == null || comment.strip().length() <= 200;
    }
}