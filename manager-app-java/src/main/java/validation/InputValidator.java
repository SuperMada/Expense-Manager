package validation;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class InputValidator {
    private InputValidator() {}

    /** VR-08: positive whole number that fits in an int. */
    public static boolean isValidExpenseId(String input) {
        if (input == null) return false;
        String s = input.strip();
        if (!s.matches("\\d+")) return false;          // rejects -1, 1.5, abc, empty
        return Integer.parseInt(s) > 0;
    }

    /** VR-09: a whole number from 0 to maxOption. */
    public static boolean isValidMenuChoice(String input, int maxOption) {
        if (input == null || !input.strip().matches("\\d+")) return false;
        return Integer.parseInt(input.strip()) <= maxOption;
    }

    /** FR-A1: login fields must not be empty or spaces only. */
    public static boolean isNonEmpty(String input) {
        return input != null && !input.strip().isEmpty();
    }
}