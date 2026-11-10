package validationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import validation.InputValidator;

public class InputValidatorTest {
    @ParameterizedTest 
    @CsvSource({"0,false", "-1,false", "1.5,false", "abc,false", "1,true", "500,true"})
    @Tag("Boundary")
    void test_valid_expense_id(String input, boolean expected){
        assertEquals(InputValidator.isValidExpenseId(input), expected);
    }

    @Test 
    @Tag("Negative")
    void test_valid_expense_id_null(){
        assertFalse(InputValidator.isValidExpenseId(null));
    }

    @ParameterizedTest 
    @CsvSource({"0,true", "1,true", "abc,false", "3,true", "4,false"})
    @Tag("Boundary")
    void test_valid_menu_choice(String input, boolean expected){
        assertEquals(InputValidator.isValidMenuChoice(input, 3), expected);
    }

    @Test 
    @Tag("Negative")
    void test_valid_menu_choice_null(){
        assertFalse(InputValidator.isValidMenuChoice(null, 3));
    }

    @ParameterizedTest 
    @CsvSource({",false", "   ,false", "TheresStuffHere,true"})
    @Tag("Boundary")
    void test_not_empty(String input, boolean expected){
        assertEquals(InputValidator.isNonEmpty(input), expected);
    }
}
