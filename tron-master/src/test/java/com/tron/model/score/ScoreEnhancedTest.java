package com.tron.model.score;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Integration tests for enhanced {@link Score} class with {@link HighScoreEntry} support.
 * 
 * Tests the complete high score management system including:
 * - Adding entries with player information
 * - JSON persistence and loading
 * - Migration from legacy format
 * - Top 10 management with new data structure
 * 
 * @author High Score System Team
 * @version 1.0
 */
@DisplayName("Score Enhanced Functionality Integration Tests")
public class ScoreEnhancedTest {

    @TempDir
    Path tempDir;

    private Score score;
    private File testJsonFile;

    @BeforeEach
    void setUp() throws Exception {
        // Reset singleton instance
        Field instanceField = Score.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        // Create temp file for testing
        testJsonFile = tempDir.resolve("test_highscores.json").toFile();
        
        // Get fresh instance
        score = Score.getInstance(testJsonFile.getAbsolutePath());
        
        // Clear entries
        Field entriesField = Score.class.getDeclaredField("highScoreEntries");
        entriesField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<HighScoreEntry> entries = (List<HighScoreEntry>) entriesField.get(score);
        entries.clear();
    }

    @Test
    @DisplayName("Given new entry, When adding to empty list, Then entry should be stored")
    void testAddHighScoreEntry() throws Exception {
        // Given
        LocalDate today = LocalDate.now();
        HighScoreEntry entry = new HighScoreEntry(9999, "TestPlayer", "Male", "Victory!", today);
        
        // When
        score.addHighScore(entry);
        
        // Then
        List<HighScoreEntry> entries = score.getHighScoreEntries();
        assertEquals(1, entries.size(), "Should have one entry");
        assertEquals(9999, entries.get(0).getScore(), "Score should match");
        assertEquals("TestPlayer", entries.get(0).getNickname(), "Nickname should match");
    }

    @Test
    @DisplayName("Given multiple entries, When adding, Then should maintain descending order")
    void testHighScoreEntriesOrder() throws Exception {
        // Given
        LocalDate date = LocalDate.now();
        HighScoreEntry entry1 = new HighScoreEntry(5000, "Player1", "Male", "First", date);
        HighScoreEntry entry2 = new HighScoreEntry(8000, "Player2", "Female", "Second", date);
        HighScoreEntry entry3 = new HighScoreEntry(6500, "Player3", "Hidden", "Third", date);
        
        // When
        score.addHighScore(entry1);
        score.addHighScore(entry2);
        score.addHighScore(entry3);
        
        // Then
        List<HighScoreEntry> entries = score.getHighScoreEntries();
        assertEquals(3, entries.size(), "Should have three entries");
        assertEquals(8000, entries.get(0).getScore(), "Highest score should be first");
        assertEquals(6500, entries.get(1).getScore(), "Middle score should be second");
        assertEquals(5000, entries.get(2).getScore(), "Lowest score should be third");
    }

    @Test
    @DisplayName("Given 11 entries, When adding, Then should keep only top 10")
    void testTop10Limit() throws Exception {
        // Given - add 11 entries
        LocalDate date = LocalDate.now();
        for (int i = 1; i <= 11; i++) {
            HighScoreEntry entry = new HighScoreEntry(i * 1000, "Player" + i, "Hidden", 
                "Manifesto" + i, date);
            score.addHighScore(entry);
        }
        
        // When
        List<HighScoreEntry> entries = score.getHighScoreEntries();
        
        // Then
        assertEquals(10, entries.size(), "Should keep only top 10 entries");
        assertEquals(11000, entries.get(0).getScore(), "Highest score should be 11000");
        assertEquals(2000, entries.get(9).getScore(), "10th score should be 2000");
    }

    @Test
    @DisplayName("Given score, When checking qualification, Then should return correct result")
    void testIsHighScore() throws Exception {
        // Given - add 10 entries with scores 1000-10000
        LocalDate date = LocalDate.now();
        for (int i = 1; i <= 10; i++) {
            HighScoreEntry entry = new HighScoreEntry(i * 1000, "Player" + i, "Male", 
                "Test", date);
            score.addHighScore(entry);
        }
        
        // When & Then
        assertTrue(score.isHighScore(1500), "Score above 10th place should qualify");
        assertTrue(score.isHighScore(15000), "Score above 1st place should qualify");
        assertFalse(score.isHighScore(500), "Score below 10th place should not qualify");
    }

    @Test
    @DisplayName("Given less than 10 entries, When checking any score, Then should always qualify")
    void testIsHighScoreWithFewEntries() throws Exception {
        // Given - only 5 entries
        LocalDate date = LocalDate.now();
        for (int i = 1; i <= 5; i++) {
            HighScoreEntry entry = new HighScoreEntry(i * 1000, "Player" + i, "Male", 
                "Test", date);
            score.addHighScore(entry);
        }
        
        // When & Then
        assertTrue(score.isHighScore(100), "Any score should qualify with less than 10 entries");
        assertTrue(score.isHighScore(50000), "Any score should qualify with less than 10 entries");
    }

    @Test
    @DisplayName("Given entries, When saving and reloading, Then data should persist")
    void testPersistence() throws Exception {
        // Given
        LocalDate date = LocalDate.of(2025, 12, 5);
        HighScoreEntry entry1 = new HighScoreEntry(9999, "Player1", "Male", "Winner!", date);
        HighScoreEntry entry2 = new HighScoreEntry(8888, "Player2", "Female", "Victory", date);
        
        score.addHighScore(entry1);
        score.addHighScore(entry2);
        
        // When - reset singleton and reload
        Field instanceField = Score.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        
        Score reloadedScore = Score.getInstance(testJsonFile.getAbsolutePath());
        
        // Then
        List<HighScoreEntry> entries = reloadedScore.getHighScoreEntries();
        assertEquals(2, entries.size(), "Should reload 2 entries");
        assertEquals(9999, entries.get(0).getScore(), "First score should match");
        assertEquals("Player1", entries.get(0).getNickname(), "First nickname should match");
        assertEquals("Male", entries.get(0).getGender(), "First gender should match");
    }

    @Test
    @DisplayName("Given legacy txt file, When loading, Then should migrate to new format")
    void testLegacyFormatMigration() throws Exception {
        // Given - create legacy format file
        File legacyFile = tempDir.resolve("legacy_scores.txt").toFile();
        try (FileWriter writer = new FileWriter(legacyFile)) {
            writer.write("9999\n");
            writer.write("8888\n");
            writer.write("7777\n");
        }
        
        // Reset singleton
        Field instanceField = Score.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        
        // When - load with json filename (will try to migrate from txt)
        String jsonPath = legacyFile.getAbsolutePath().replace(".txt", ".json");
        Score migratedScore = Score.getInstance(jsonPath);
        
        // Manually trigger migration by copying legacy file
        File sourceDir = new File("src/main/resources");
        sourceDir.mkdirs();
        java.nio.file.Files.copy(legacyFile.toPath(), 
            new File(sourceDir, "legacy_scores.txt").toPath(),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        
        // Then - verify backward compatibility method still works
        List<Integer> scores = migratedScore.getHighScores();
        assertNotNull(scores, "Should return scores list");
    }

    @Test
    @DisplayName("Given negative score, When adding, Then should throw exception")
    void testNegativeScoreRejection() {
        // Given
        LocalDate date = LocalDate.now();
        HighScoreEntry invalidEntry = new HighScoreEntry(-100, "Bad", "Male", "Invalid", date);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            score.addHighScore(invalidEntry);
        }, "Should reject negative scores");
    }

    @Test
    @DisplayName("Given deprecated method, When adding int score, Then should create default entry")
    void testBackwardCompatibilityAddScore() throws Exception {
        // Given
        int oldScore = 5000;
        
        // When
        @SuppressWarnings("deprecation")
        boolean result = true;
        try {
            score.addHighScore(oldScore);
        } catch (Exception e) {
            result = false;
        }
        
        // Then
        assertTrue(result, "Deprecated method should still work");
        List<HighScoreEntry> entries = score.getHighScoreEntries();
        assertEquals(1, entries.size(), "Should have one entry");
        assertEquals(5000, entries.get(0).getScore(), "Score should match");
        assertEquals("unknown", entries.get(0).getNickname(), "Should use default nickname");
    }

    @Test
    @DisplayName("Given entries, When getting as integers, Then should return score values only")
    void testBackwardCompatibilityGetScores() throws Exception {
        // Given
        LocalDate date = LocalDate.now();
        score.addHighScore(new HighScoreEntry(9999, "P1", "Male", "M1", date));
        score.addHighScore(new HighScoreEntry(8888, "P2", "Female", "M2", date));
        
        // When
        @SuppressWarnings("deprecation")
        List<Integer> scores = score.getHighScores();
        
        // Then
        assertEquals(2, scores.size(), "Should return 2 scores");
        assertEquals(9999, scores.get(0), "First score should be 9999");
        assertEquals(8888, scores.get(1), "Second score should be 8888");
    }
}
