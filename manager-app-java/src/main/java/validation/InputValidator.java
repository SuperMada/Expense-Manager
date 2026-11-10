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
        try { return Integer.parseInt(s) > 0; }
        catch (NumberFormatException e) { return false; }  // too large for an int
    }

    /** VR-09: a whole number from 0 to maxOption. */
    public static boolean isValidMenuChoice(String input, int maxOption) {
        if (input == null || !input.strip().matches("\\d+")) return false;
        try { return Integer.parseInt(input.strip()) <= maxOption; }
        catch (NumberFormatException e) { return false; }
    }

    /** FR-A1: login fields must not be empty or spaces only. */
    public static boolean isNonEmpty(String input) {
        return input != null && !input.strip().isEmpty();
    }

    /** VR-07: both dates are real YYYY-MM-DD dates and start <= end. */
    public static boolean isValidDateRange(String start, String end) {
        try {
            return !LocalDate.parse(start).isAfter(LocalDate.parse(end));
        } catch (DateTimeParseException | NullPointerException e) {
            return false;   // LocalDate.parse is strict, so 2026-02-30 is rejected
        }
    }
}