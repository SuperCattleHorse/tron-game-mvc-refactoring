package com.tron.model.score;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link HighScoreEntry} class.
 * 
 * Tests the data model for high score entries including:
 * - Entry creation with complete player information
 * - Legacy entry creation (migration from old format)
 * - Comparison and sorting behavior
 * - Equality and hash code operations
 * 
 * @author High Score System Team
 * @version 1.0
 */
@DisplayName("HighScoreEntry Class Unit Tests")
public class HighScoreEntryTest {

    private HighScoreEntry entry1;
    private HighScoreEntry entry2;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2025, 12, 5);
        entry1 = new HighScoreEntry(9999, "Player1", "Male", "I am the best!", testDate);
        entry2 = new HighScoreEntry(8888, "Player2", "Female", "Victory is mine", testDate);
    }

    @Test
    @DisplayName("Given valid parameters, When creating entry, Then all fields should be set correctly")
    void testEntryCreation() {
        // Given - setUp provides entry1
        
        // When - entry is created in setUp
        
        // Then
        assertEquals(9999, entry1.getScore(), "Score should be 9999");
        assertEquals("Player1", entry1.getNickname(), "Nickname should be Player1");
        assertEquals("Male", entry1.getGender(), "Gender should be Male");
        assertEquals("I am the best!", entry1.getManifesto(), "Manifesto should match");
        assertEquals(testDate, entry1.getDate(), "Date should match");
    }

    @Test
    @DisplayName("Given only score, When creating legacy entry, Then default values should be used")
    void testLegacyEntryCreation() {
        // Given
        int score = 5000;
        
        // When
        HighScoreEntry legacyEntry = new HighScoreEntry(score);
        
        // Then
        assertEquals(5000, legacyEntry.getScore(), "Score should be 5000");
        assertEquals("unknown", legacyEntry.getNickname(), "Default nickname should be 'unknown'");
        assertEquals("Hidden", legacyEntry.getGender(), "Default gender should be 'Hidden'");
        assertEquals("No manifesto", legacyEntry.getManifesto(), "Default manifesto should be set");
        assertEquals(LocalDate.of(1970, 1, 1), legacyEntry.getDate(), 
            "Default date should be 1970-01-01");
    }

    @Test
    @DisplayName("Given two entries, When comparing, Then higher score should come first")
    void testCompareTo() {
        // Given - entry1 (9999) and entry2 (8888) from setUp
        
        // When
        int comparison = entry1.compareTo(entry2);
        
        // Then
        assertTrue(comparison < 0, "Entry with higher score should come before lower score");
        
        // When - reverse comparison
        int reverseComparison = entry2.compareTo(entry1);
        
        // Then
        assertTrue(reverseComparison > 0, "Entry with lower score should come after higher score");
    }

    @Test
    @DisplayName("Given entries with same score, When comparing, Then should return zero")
    void testCompareToEqualScores() {
        // Given
        HighScoreEntry entry3 = new HighScoreEntry(9999, "Player3", "Hidden", "Test", testDate);
        
        // When
        int comparison = entry1.compareTo(entry3);
        
        // Then
        assertEquals(0, comparison, "Entries with same score should be equal in comparison");
    }

    @Test
    @DisplayName("Given two identical entries, When checking equality, Then should be equal")
    void testEquals() {
        // Given
        HighScoreEntry identicalEntry = new HighScoreEntry(9999, "Player1", "Male", 
            "I am the best!", testDate);
        
        // When & Then
        assertEquals(entry1, identicalEntry, "Identical entries should be equal");
        assertEquals(entry1.hashCode(), identicalEntry.hashCode(), 
            "Equal entries should have same hash code");
    }

    @Test
    @DisplayName("Given two different entries, When checking equality, Then should not be equal")
    void testNotEquals() {
        // Given - entry1 and entry2 from setUp
        
        // When & Then
        assertNotEquals(entry1, entry2, "Different entries should not be equal");
    }

    @Test
    @DisplayName("Given entry, When calling toString, Then should return formatted string")
    void testToString() {
        // Given - entry1 from setUp
        
        // When
        String result = entry1.toString();
        
        // Then
        assertTrue(result.contains("9999"), "String should contain score");
        assertTrue(result.contains("Player1"), "String should contain nickname");
        assertTrue(result.contains("Male"), "String should contain gender");
        assertTrue(result.contains("I am the best!"), "String should contain manifesto");
    }

    @Test
    @DisplayName("Given entry, When modifying fields, Then getters should return new values")
    void testSetters() {
        // Given - entry1 from setUp
        
        // When
        entry1.setScore(10000);
        entry1.setNickname("NewName");
        entry1.setGender("Female");
        entry1.setManifesto("New manifesto");
        LocalDate newDate = LocalDate.of(2025, 12, 6);
        entry1.setDate(newDate);
        
        // Then
        assertEquals(10000, entry1.getScore(), "Score should be updated");
        assertEquals("NewName", entry1.getNickname(), "Nickname should be updated");
        assertEquals("Female", entry1.getGender(), "Gender should be updated");
        assertEquals("New manifesto", entry1.getManifesto(), "Manifesto should be updated");
        assertEquals(newDate, entry1.getDate(), "Date should be updated");
    }
}
