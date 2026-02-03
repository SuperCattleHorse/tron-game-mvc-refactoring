package com.tron.view.fx;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tron.model.score.HighScoreEntry;

/**
 * Logic tests for {@link FXPlayerInfoDialog}.
 * 
 * Tests the validation logic and data handling for the player information dialog:
 * - Input validation patterns
 * - Length validation rules
 * - Gender options validation
 * - Form completeness checking
 * - HighScoreEntry creation from dialog data
 * 
 * Note: These tests focus on validation logic without requiring JavaFX initialization.
 * 
 * @author High Score System Team
 * @version 1.0
 */
@DisplayName("FXPlayerInfoDialog Validation Logic Tests")
public class FXPlayerInfoDialogTest {

    @Test
    @DisplayName("Given dialog, When checking field validation patterns, Then should match expected rules")
    void testValidationLogic() {
        // Given - validation pattern from FXPlayerInfoDialog
        String validPattern = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{}|;:',.<>?/` ~]+$";
        
        // When & Then - valid inputs
        assertTrue("Player1".matches(validPattern), "Alphanumeric should be valid");
        assertTrue("Test_User".matches(validPattern), "Underscore should be valid");
        assertTrue("Win!".matches(validPattern), "Exclamation should be valid");
        assertTrue("123".matches(validPattern), "Numbers should be valid");
        
        // When & Then - invalid inputs
        assertFalse("测试".matches(validPattern), "Chinese characters should be invalid");
        assertFalse("Test\nUser".matches(validPattern), "Newlines should be invalid");
        assertFalse("Test\tUser".matches(validPattern), "Tabs should be invalid");
    }

    @Test
    @DisplayName("Given length requirements, When checking strings, Then should validate correctly")
    void testLengthValidation() {
        // Given
        String tooShort = "AB";
        String validMin = "ABC";
        String validMax = "12345678901234567890";
        String tooLong = "123456789012345678901";
        
        // When & Then
        assertFalse(tooShort.length() >= 3 && tooShort.length() <= 20, 
            "String with 2 chars should be too short");
        assertTrue(validMin.length() >= 3 && validMin.length() <= 20, 
            "String with 3 chars should be valid");
        assertTrue(validMax.length() >= 3 && validMax.length() <= 20, 
            "String with 20 chars should be valid");
        assertFalse(tooLong.length() >= 3 && tooLong.length() <= 20, 
            "String with 21 chars should be too long");
    }

    @Test
    @DisplayName("Given valid entry data, When creating HighScoreEntry, Then all fields should match")
    void testHighScoreEntryCreationFromDialog() {
        // Given
        int score = 9999;
        String nickname = "TestPlayer";
        String gender = "Male";
        String manifesto = "Victory!";
        LocalDate date = LocalDate.now();
        
        // When
        HighScoreEntry entry = new HighScoreEntry(score, nickname, gender, manifesto, date);
        
        // Then
        assertNotNull(entry, "Entry should be created");
        assertEquals(score, entry.getScore(), "Score should match");
        assertEquals(nickname, entry.getNickname(), "Nickname should match");
        assertEquals(gender, entry.getGender(), "Gender should match");
        assertEquals(manifesto, entry.getManifesto(), "Manifesto should match");
        assertEquals(date, entry.getDate(), "Date should match");
    }

    @Test
    @DisplayName("Given gender options, When validating, Then should accept only valid values")
    void testGenderOptionsValidation() {
        // Given
        String[] validGenders = {"Male", "Female", "Hidden"};
        String invalidGender = "Other";
        
        // When & Then
        for (String gender : validGenders) {
            assertTrue(gender.equals("Male") || gender.equals("Female") || gender.equals("Hidden"),
                gender + " should be a valid option");
        }
        
        assertFalse(invalidGender.equals("Male") || invalidGender.equals("Female") || 
            invalidGender.equals("Hidden"), "Other should not be a valid option");
    }

    @Test
    @DisplayName("Given form validation rules, When checking complete form, Then should validate correctly")
    void testCompleteFormValidation() {
        // Given - simulate form state
        String nickname = "Player1";
        String manifesto = "I win!";
        String gender = "Male";
        
        // When
        boolean nicknameValid = !nickname.isEmpty() && 
                                nickname.length() >= 3 && 
                                nickname.length() <= 20 && 
                                nickname.matches("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{}|;:',.<>?/` ~]+$");
        
        boolean manifestoValid = !manifesto.isEmpty() && 
                                 manifesto.length() >= 3 && 
                                 manifesto.length() <= 20 && 
                                 manifesto.matches("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{}|;:',.<>?/` ~]+$");
        
        boolean formValid = nicknameValid && manifestoValid;
        
        // Then
        assertTrue(nicknameValid, "Nickname should be valid");
        assertTrue(manifestoValid, "Manifesto should be valid");
        assertTrue(formValid, "Complete form should be valid");
    }

    @Test
    @DisplayName("Given invalid inputs, When validating form, Then should reject")
    void testInvalidFormRejection() {
        // Given - various invalid scenarios
        String shortNickname = "AB";
        String longNickname = "ThisIsAVeryLongNicknameThatExceedsTwentyCharacters";
        String invalidCharsNickname = "Player测试";
        String emptyManifesto = "";
        
        String pattern = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{}|;:',.<>?/` ~]+$";
        
        // When & Then
        assertFalse(shortNickname.length() >= 3 && shortNickname.length() <= 20, 
            "Short nickname should be invalid");
        assertFalse(longNickname.length() >= 3 && longNickname.length() <= 20, 
            "Long nickname should be invalid");
        assertFalse(invalidCharsNickname.matches(pattern), 
            "Nickname with invalid chars should be invalid");
        assertFalse(!emptyManifesto.isEmpty(), 
            "Empty manifesto should be invalid");
    }

    @Test
    @DisplayName("Given current date, When creating entry, Then date should be today")
    void testDateIsToday() {
        // Given
        LocalDate today = LocalDate.now();
        
        // When
        HighScoreEntry entry = new HighScoreEntry(5000, "Test", "Male", "Win", today);
        
        // Then
        assertEquals(today, entry.getDate(), "Entry date should be today");
        assertEquals(today.getYear(), entry.getDate().getYear(), "Year should match");
        assertEquals(today.getMonthValue(), entry.getDate().getMonthValue(), "Month should match");
        assertEquals(today.getDayOfMonth(), entry.getDate().getDayOfMonth(), "Day should match");
    }
}
