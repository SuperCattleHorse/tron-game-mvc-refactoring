package com.tron.model.score;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Score} class.
 * 
 * <p>This test class validates the high score management functionality,
 * including score persistence, sorting behavior, and file I/O operations.
 * Tests ensure that scores are properly stored, retrieved, and maintained
 * in descending order. Uses reflection to reset singleton state between tests.</p>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see Score
 */
@DisplayName("Score Class Unit Tests")
public class ScoreTest {

    private Score score;

    /**
     * Sets up test fixtures before each test method.
     * Resets the singleton Score instance and clears its high scores list
     * to ensure test isolation.
     */
    @BeforeEach
    void setUp() throws Exception {
        // Reset singleton instance using reflection for test isolation
        Field instanceField = Score.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        
        // Get fresh instance with test file
        score = Score.getInstance("test_scores_temp.txt");
        
        // Clear high scores list using reflection
        Field highScoreEntriesField = Score.class.getDeclaredField("highScoreEntries");
        highScoreEntriesField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<HighScoreEntry> highScoreEntries = (List<HighScoreEntry>) highScoreEntriesField.get(score);
        highScoreEntries.clear();
    }

    /**
     * Tests successful creation of Score instance.
     * Verifies that the Score object is properly instantiated.
     */
    @Test
    @DisplayName("Should create Score instance successfully")
    void testScoreCreation() {
        assertNotNull(score, "Score instance should not be null");
    }

    /**
     * Tests adding a high score to the list.
     * Ensures that scores can be added and are retrievable.
     */
    @Test
    @DisplayName("Should add high score to the list")
    void testAddHighScore() throws Exception {
        score.addHighScore(100);
        List<Integer> highScores = score.getHighScores();
        assertTrue(highScores.contains(100), "High scores list should contain 100");
    }

    /**
     * Tests retrieval of high scores list.
     * Verifies that getHighScores returns a non-null list.
     */
    @Test
    @DisplayName("Should return non-null high scores list")
    void testGetHighScores() {
        List<Integer> highScores = score.getHighScores();
        assertNotNull(highScores, "High scores list should not be null");
    }

    /**
     * Tests that high scores are maintained in descending order.
     * Verifies the sorting behavior after adding multiple scores.
     */
    @Test
    @DisplayName("Should maintain high scores in descending order")
    void testHighScoresOrder() throws Exception {
        score.addHighScore(100);
        score.addHighScore(200);
        score.addHighScore(150);
        
        List<Integer> highScores = score.getHighScores();
        assertEquals(200, (int) highScores.get(0), "Highest score should be 200");
        assertEquals(150, (int) highScores.get(1), "Second highest score should be 150");
        assertEquals(100, (int) highScores.get(2), "Third highest score should be 100");
    }
    
    /**
     * Tests that the high scores list maintains a maximum of 10 entries.
     * Ensures older lower scores are removed when limit is exceeded.
     */
    @Test
    @DisplayName("Should limit high scores list to maximum 10 entries")
    void testMaximumScoreLimit() throws Exception {
        // Add 12 scores to exceed the limit
        for (int i = 1; i <= 12; i++) {
            score.addHighScore(i * 10);
        }
        
        List<Integer> highScores = score.getHighScores();
        assertEquals(10, highScores.size(), "High scores list should contain exactly 10 entries");
        assertEquals(120, (int) highScores.get(0), "Highest score should be 120");
        assertEquals(30, (int) highScores.get(9), "Lowest kept score should be 30");
    }
    
    /**
     * Tests adding duplicate scores.
     * Verifies that duplicate scores are allowed and properly stored.
     */
    @Test
    @DisplayName("Should allow duplicate scores")
    void testDuplicateScores() throws Exception {
        score.addHighScore(100);
        score.addHighScore(100);
        score.addHighScore(100);
        
        List<Integer> highScores = score.getHighScores();
        assertEquals(3, highScores.size(), "Should contain 3 scores");
        long count100 = highScores.stream().filter(s -> s == 100).count();
        assertEquals(3, count100, "All 3 scores should be 100");
    }
    
    /**
     * Tests adding a score of zero.
     * Ensures zero is a valid score value.
     */
    @Test
    @DisplayName("Should accept zero as a valid score")
    void testZeroScore() throws Exception {
        score.addHighScore(0);
        List<Integer> highScores = score.getHighScores();
        assertTrue(highScores.contains(0), "Should accept zero as a valid score");
    }
    
    /**
     * Tests initial state of empty score list.
     * Verifies that after clearing, the Score instance has an empty list.
     */
    @Test
    @DisplayName("Should have empty score list after setUp")
    void testInitialEmptyList() {
        List<Integer> highScores = score.getHighScores();
        assertNotNull(highScores, "High scores list should not be null");
        assertEquals(0, highScores.size(), "High scores list should be empty after setUp");
    }
    
    /**
     * Tests multiple sequential score additions.
     * Ensures scores are correctly accumulated and sorted.
     */
    @Test
    @DisplayName("Should handle multiple sequential score additions")
    void testMultipleAdditions() throws Exception {
        score.addHighScore(50);
        assertEquals(1, score.getHighScores().size());
        
        score.addHighScore(75);
        assertEquals(2, score.getHighScores().size());
        
        score.addHighScore(25);
        assertEquals(3, score.getHighScores().size());
        
        List<Integer> highScores = score.getHighScores();
        assertEquals(75, (int) highScores.get(0), "First score should be 75");
        assertEquals(50, (int) highScores.get(1), "Second score should be 50");
        assertEquals(25, (int) highScores.get(2), "Third score should be 25");
    }
}
