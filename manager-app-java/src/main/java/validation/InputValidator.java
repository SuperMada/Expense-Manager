package validation;

public final class InputValidator {
    private InputValidator() {}

    //Checks valid expense ID
    public static boolean isValidExpenseId(String input) {
        if (input == null) return false;
        String s = input.strip();
        if (!s.matches("\\d+")) return false;
        return Integer.parseInt(s) > 0;
    }

    //Checks valid menu choice
    public static boolean isValidMenuChoice(String input, int maxOption) {
        if (input == null || !input.strip().matches("\\d+")) return false;
        return Integer.parseInt(input.strip()) <= maxOption;
    }

    //Check string not empty
    public static boolean isNonEmpty(String input) {
        return input != null && !input.strip().isEmpty();
    }
}