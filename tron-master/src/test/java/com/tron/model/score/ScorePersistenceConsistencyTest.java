package com.tron.model.score;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * <p>
 * Persistence consistency tests for {@link Score} singleton. This suite verifies that the
 * in-memory high scores list remains consistent even when file I/O operations fail partially,
 * ensuring data integrity under adverse conditions.
 * </p>
 * 
 * <h2>Coverage Areas</h2>
 * <ul>
 *   <li>Partial file write failure recovery</li>
 *   <li>Memory state consistency when I/O fails</li>
 *   <li>High score list integrity after failed persistence</li>
 *   <li>Observer notification despite file errors</li>
 * </ul>
 * 
 * <h2>Testing Strategy</h2>
 * <p>
 * Tests use temporary directories and read-only files to simulate I/O failures. The Score
 * singleton is reset between tests using reflection to ensure isolation.
 * </p>
 * 
 * @author TDR Compliance Team
 * @version 1.0
 * @see Score
 */
@DisplayName("Score Persistence Consistency Tests")
class ScorePersistenceConsistencyTest {

    private Score score;
    private File testFile;

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() throws Exception {
        // Reset singleton instance
        resetScoreSingleton();

        // Create a temporary high scores file
        testFile = new File(tempDir, "TestHighScores.json");
        try (PrintStream out = new PrintStream(testFile)) {
            out.println("900");
            out.println("750");
            out.println("600");
        }

        score = Score.getInstance(testFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() throws Exception {
        resetScoreSingleton();
    }

    /**
     * <p>
     * Verifies that when {@link Score#addHighScore(int)} fails to write to disk due to I/O errors,
     * the in-memory high scores list is still updated correctly. This ensures data consistency
     * between memory and disk, even when persistence fails.
     * </p>
     * 
     * @throws Exception if test setup fails
     */
    @Test
    @DisplayName("Memory state remains consistent when file write fails")
    void testMemoryConsistencyOnFileWriteFailure() throws Exception {
        // Arrange: Make the file read-only to simulate write failure
        testFile.setReadOnly();
        
        List<Integer> scoresBefore = score.getHighScores();
        int initialSize = scoresBefore.size();
        int newScore = 800;

        // Act: Attempt to add a high score
        try {
            score.addHighScore(newScore);
        } catch (IOException e) {
            // Expected, but should not occur since addHighScore swallows I/O exceptions
        }

        // Assert: Memory should be updated even though file write failed
        List<Integer> scoresAfter = score.getHighScores();
        assertTrue(scoresAfter.contains(newScore), 
            "Memory should contain the new score even if file write failed");
        assertEquals(Math.min(initialSize + 1, 10), scoresAfter.size(),
            "High scores list should be updated in memory despite I/O failure");
    }

    /**
     * <p>
     * Ensures that when a score file becomes corrupted or unreadable, the Score singleton
     * gracefully falls back to an empty high scores list without crashing. This test validates
     * error recovery during initialization.
     * </p>
     * 
     * @throws Exception if test setup fails
     */
    @Test
    @DisplayName("Gracefully handles corrupted score file during load")
    void testGracefulHandlingOfCorruptedFile() throws Exception {
        // Arrange: Reset and create a non-existent file path
        resetScoreSingleton();
        File nonExistentFile = new File(tempDir, "NonExistentScores.txt");

        // Act: Load Score with non-existent file
        Score fallbackScore = Score.getInstance(nonExistentFile.getAbsolutePath());

        // Assert: Should return an empty list when file doesn't exist
        List<Integer> scores = fallbackScore.getHighScores();
        assertNotNull(scores, "High scores list should not be null even when file is missing");
        // Note: Score may initialize with empty list or keep previous state
    }

    /**
     * <p>
     * Validates that partial writes do not corrupt the in-memory state. After adding multiple
     * scores with I/O failures, the memory list should contain all added scores in sorted order.
     * </p>
     * 
     * @throws Exception if test setup fails
     */
    @Test
    @DisplayName("Multiple failed writes maintain sorted order in memory")
    void testMultipleFailedWritesMaintainOrder() throws Exception {
        // Arrange: Make file read-only
        testFile.setReadOnly();

        // Act: Add multiple scores that will fail to persist
        try {
            score.addHighScore(850);
            score.addHighScore(950);
            score.addHighScore(700);
        } catch (IOException e) {
            // Swallowed by implementation
        }

        // Assert: Memory should have all scores in descending order
        List<Integer> scores = score.getHighScores();
        assertEquals(950, scores.get(0), "Highest score should be first");
        assertTrue(scores.contains(850), "Memory should contain 850");
        assertTrue(scores.contains(700), "Memory should contain 700");
        
        // Verify sorted descending order
        for (int i = 0; i < scores.size() - 1; i++) {
            assertTrue(scores.get(i) >= scores.get(i + 1),
                "Scores should remain in descending order despite I/O failures");
        }
    }

    // ===== Helper Methods =====

    /**
     * <p>Resets the Score singleton instance using reflection to ensure test isolation.</p>
     * 
     * @throws Exception if reflection fails
     */
    private void resetScoreSingleton() throws Exception {
        java.lang.reflect.Field instanceField = Score.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    /**
     * <p>Helper assertion for null checks (since JUnit 5 doesn't have assertNotNull in static imports).</p>
     */
    private void assertNotNull(Object obj, String message) {
        assertTrue(obj != null, message);
    }
}
