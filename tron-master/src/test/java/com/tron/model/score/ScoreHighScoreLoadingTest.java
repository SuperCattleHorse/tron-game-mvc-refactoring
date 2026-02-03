package com.tron.model.score;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * <p>
 * Regression tests that ensure {@link Score} obeys its documented load order: resources first,
 * filesystem fallback second. The tests verify that custom score files populate the in-memory list
 * and that missing files yield the expected empty list.
 * </p>
 */
@DisplayName("Score High Score Loading Tests")
class ScoreHighScoreLoadingTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void resetSingleton() throws Exception {
        Field instanceField = Score.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    /**
     * <p>Validates that when an application-specific score file exists, {@link Score} loads its
     * contents even if no classpath resource matches.</p>
     */
    @Test
    void testLoadsFromFileSystemWhenResourceMissing() throws Exception {
        Path customScores = tempDir.resolve("custom_scores.txt");
        Files.writeString(customScores, "900\n750\n600");

        Score score = Score.getInstance(customScores.toString());

        List<Integer> highs = score.getHighScores();
        assertEquals(3, highs.size(), "The file-backed list should contain all entries");
        assertEquals(List.of(900, 750, 600), highs,
                "High scores should be read in the order stored on disk");
    }

    /**
     * <p>Ensures that missing files do not crash the loader and simply produce an empty list.</p>
     */
    @Test
    void testMissingFileYieldsEmptyScores() throws Exception {
        Score score = Score.getInstance(tempDir.resolve("missing_scores.txt").toString());
        assertTrue(score.getHighScores().isEmpty(),
                "Absent score files should result in an empty leaderboard");
    }
}
