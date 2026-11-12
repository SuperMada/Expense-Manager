package unit.validationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import validation.InputValidator;

public class InputValidatorTest {
    
    //Test called through the shared resource
    void test_valid_expense_id(String input, boolean expected){
        assertEquals(InputValidator.isValidExpenseId(input), expected);
    }

    @Test 
    @Tag("Negative")
    void test_valid_expense_id_null(){
        assertFalse(InputValidator.isValidExpenseId(null));
    }

    //Test called through the shared resource
    void test_valid_menu_choice(String input, boolean expected){
        assertEquals(InputValidator.isValidMenuChoice(input, 3), expected);
    }

    @Test 
    @Tag("Negative")
    void test_valid_menu_choice_null(){
        assertFalse(InputValidator.isValidMenuChoice(null, 3));
    }

    //Test called through the shared resource
    void test_not_empty(String input, boolean expected){
        assertEquals(InputValidator.isNonEmpty(input), expected);
    }

    @ParameterizedTest 
    @CsvFileSource(resources="/validation_cases.csv", numLinesToSkip = 1)
    void test_shared_data(String field, String input, boolean expected){
        if(field.equals("expense_id")){
            test_valid_expense_id(input, expected);
        } else if(field.equals("menu_choice")){
            test_valid_menu_choice(input, expected);
        } else if(field.equals("login_input")){
            test_not_empty(input, expected);
        }

    }
}
