package com.tron.model.score;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.tron.model.observer.ScoreObserver;

/**
 * <p>
 * Observer-focused regression tests for {@link Score}. These tests exercise the
 * real singleton and its notification hooks so that future refactors cannot
 * silently drop calls to {@link ScoreObserver} methods.
 * </p>
 */
@DisplayName("Score Observer Notification Tests")
class ScoreNotificationIntegrationTest {

    @TempDir
    Path tempDir;

    private Score score;
    private RecordingObserver observer;
    private Path scoreFile;

    @BeforeEach
    void setUp() throws Exception {
        resetSingleton();
        scoreFile = tempDir.resolve("high_scores.txt");
        Files.createFile(scoreFile);
        score = Score.getInstance(scoreFile.toString());
        observer = new RecordingObserver();
        score.attach(observer);
    }

    /**
     * <p>Verifies the push-style updates emitted by {@link Score#updateCurrentScore(int)}.</p>
     */
    @Test
    void testUpdateCurrentScoreNotifiesObservers() throws IOException {
        score.addHighScore(400);
        observer.reset();

        score.updateCurrentScore(250);
        score.updateCurrentScore(500);

        assertEquals(2, observer.scoreChangedEvents,
            "Observers should receive each score change notification");
        assertEquals(500, observer.lastCurrentScore,
            "Observers must see the latest current score");
        assertEquals(400, observer.lastHighScore,
            "High score snapshot should reflect the persisted leaderboard");
        assertEquals(1, observer.highScoreBeatenEvents,
            "Beating the high score should emit a dedicated notification");
        assertEquals(500, observer.lastNewHighScore,
            "High-score notification should echo the new record");
    }

    /**
     * <p>Ensures {@link Score#addHighScore(int)} notifies observers when a value enters the leaderboard.</p>
     */
    @Test
    void testAddHighScoreNotifiesWhenQualifying() throws IOException {
        score.addHighScore(750);

        assertEquals(1, observer.scoreAddedEvents,
            "Adding a top-10 score should trigger observer callbacks");
        assertEquals(750, observer.lastAddedScore,
            "Observer must receive the score value that was persisted");
        assertEquals(0, observer.lastPosition,
            "First score written should occupy the top slot");
        assertTrue(score.getHighScores().contains(750),
            "High scores list should reflect the newly added score");
    }

    /**
     * <p>Resets the singleton between tests to guarantee isolation.</p>
     */
    private static void resetSingleton() throws Exception {
        Field instanceField = Score.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    /**
     * <p>Simple in-memory observer that records every callback.</p>
     */
    private static final class RecordingObserver implements ScoreObserver {
        private int scoreChangedEvents;
        private int highScoreBeatenEvents;
        private int scoreAddedEvents;
        private int lastCurrentScore;
        private int lastHighScore;
        private int lastNewHighScore;
        private int lastAddedScore;
        private int lastPosition;

        @Override
        public void onScoreChanged(int currentScore, int highScore) {
            scoreChangedEvents++;
            lastCurrentScore = currentScore;
            lastHighScore = highScore;
        }

        @Override
        public void onHighScoreBeaten(int newHighScore) {
            highScoreBeatenEvents++;
            lastNewHighScore = newHighScore;
        }

        @Override
        public void onScoreAddedToHighScores(int score, int position) {
            scoreAddedEvents++;
            lastAddedScore = score;
            lastPosition = position;
        }

        void reset() {
            scoreChangedEvents = 0;
            highScoreBeatenEvents = 0;
            scoreAddedEvents = 0;
            lastCurrentScore = 0;
            lastHighScore = 0;
            lastNewHighScore = 0;
            lastAddedScore = 0;
            lastPosition = -1;
        }
    }

    /**
     * <p>Nested tests that demonstrate detaching observers halts updates.</p>
     */
    @Nested
    @DisplayName("Observer Lifecycle Tests")
    class ObserverLifecycleTests {

        @Test
        void testDetachStopsNotifications() {
            ScoreNotificationIntegrationTest.this.observer.reset();
            score.detach(ScoreNotificationIntegrationTest.this.observer);
            score.updateCurrentScore(999);
            assertEquals(0, ScoreNotificationIntegrationTest.this.observer.scoreChangedEvents,
                "Detached observers must not receive further updates");
        }
    }
}
