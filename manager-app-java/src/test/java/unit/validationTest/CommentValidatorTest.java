package unit.validationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import validation.CommentValidator;

public class CommentValidatorTest { 

    @ParameterizedTest 
    @Tag("Boundary")
    @DisplayName("Covers: VR-05, FR-M2")
    @CsvSource({"4,false", "5,true", "6,true", "199,true", "200,true", "201,false"})
    void test_is_valid_denial_comment(int length, boolean expected){
        assertEquals(CommentValidator.isValidDenial("a".repeat(length)), expected);
    }

    @ParameterizedTest 
    @Tag("Boundary")
    @DisplayName("Covers: VR-06, FR-M2")
    @CsvSource({"0,true", "199,true", "200,true", "201,false"})
    void test_is_valid_approval_comment(int length, boolean expected){
        assertEquals(CommentValidator.isValidApproval("a".repeat(length)), expected);
    }

    @Test 
    @Tag("Negative")
    @DisplayName("Covers: VR-05, FR-M2")
    void test_valid_denial_with_null(){
        assertFalse(CommentValidator.isValidDenial(null));  
    }

    @Test 
    @Tag("Negative")
    @DisplayName("Covers: VR-06, FR-M2")
    void test_valid_approval_with_null(){
        assertTrue(CommentValidator.isValidApproval(null));
    }

    @Test 
    @Tag("Negative")
    @Tag("AI")
    @DisplayName("Covers: VR-05, FR-M2")
    void test_valid_denial_empty_string(){
        assertFalse(CommentValidator.isValidDenial("               "));
    }
}
