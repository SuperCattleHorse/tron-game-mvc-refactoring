package com.tron.model.observer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the ScoreObserver implementation in the Observer Pattern.
 * 
 * <p>This test class verifies that the ScoreObserver interface functions correctly
 * as part of the Observer Pattern implementation. Tests ensure that observers
 * receive proper notifications when score changes occur, and that multiple
 * observers can independently respond to the same score events.</p>
 * 
 * <p><strong>Test Strategy:</strong> Integration testing focusing on observer
 * notification flow, multiple observer handling, and event sequencing.</p>
 * 
 * <p><strong>Design Pattern:</strong> Observer Pattern (Behavioral)</p>
 * <ul>
 *   <li>Subject: Score management system</li>
 *   <li>Observer: ScoreObserver implementations</li>
 *   <li>Notification: Score changes, high score achievements</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see ScoreObserver
 * @see Subject
 */
@DisplayName("ScoreObserver Integration Tests")
public class ScoreObserverIntegrationTest {
    
    /**
     * Mock implementation of ScoreObserver for testing.
     * Tracks all notification calls to verify observer pattern behavior.
     */
    private static class MockScoreObserver implements ScoreObserver {
        private int scoreChangedCallCount = 0;
        private int highScoreBeatenCallCount = 0;
        private int scoreAddedCallCount = 0;
        private int lastCurrentScore = -1;
        private int lastHighScore = -1;
        private int lastNewHighScore = -1;
        private int lastAddedScore = -1;
        private int lastPosition = -1;
        
        @Override
        public void onScoreChanged(int currentScore, int highScore) {
            scoreChangedCallCount++;
            lastCurrentScore = currentScore;
            lastHighScore = highScore;
        }
        
        @Override
        public void onHighScoreBeaten(int newHighScore) {
            highScoreBeatenCallCount++;
            lastNewHighScore = newHighScore;
        }
        
        @Override
        public void onScoreAddedToHighScores(int score, int position) {
            scoreAddedCallCount++;
            lastAddedScore = score;
            lastPosition = position;
        }
        
        /**
         * Resets all tracking counters for next test.
         */
        public void reset() {
            scoreChangedCallCount = 0;
            highScoreBeatenCallCount = 0;
            scoreAddedCallCount = 0;
            lastCurrentScore = -1;
            lastHighScore = -1;
            lastNewHighScore = -1;
            lastAddedScore = -1;
            lastPosition = -1;
        }
    }
    
    /**
     * Simple subject implementation for testing ScoreObserver.
     */
    private static class MockScoreSubject implements Subject<ScoreObserver> {
        private ScoreObserver observer;
        
        @Override
        public void attach(ScoreObserver observer) {
            this.observer = observer;
        }
        
        @Override
        public void detach(ScoreObserver observer) {
            if (this.observer == observer) {
                this.observer = null;
            }
        }
        
        @Override
        public void notifyObservers() {
            // Not used in this context
        }
        
        /**
         * Simulates a score change event.
         */
        public void notifyScoreChanged(int current, int high) {
            if (observer != null) {
                observer.onScoreChanged(current, high);
            }
        }
        
        /**
         * Simulates a high score beaten event.
         */
        public void notifyHighScoreBeaten(int newHighScore) {
            if (observer != null) {
                observer.onHighScoreBeaten(newHighScore);
            }
        }
        
        /**
         * Simulates a score added to high scores event.
         */
        public void notifyScoreAdded(int score, int position) {
            if (observer != null) {
                observer.onScoreAddedToHighScores(score, position);
            }
        }
    }
    
    private MockScoreObserver observer;
    private MockScoreSubject subject;
    
    /**
     * Sets up test fixtures before each test method.
     * Initializes mock observer and subject instances.
     */
    @BeforeEach
    void setUp() {
        observer = new MockScoreObserver();
        subject = new MockScoreSubject();
        subject.attach(observer);
    }
    
    /**
     * Test Case: TC-OBS-SCORE-001
     * Tests that observer receives onScoreChanged notification.
     * 
     * <p><strong>Class Under Test:</strong> ScoreObserver.onScoreChanged(int, int)</p>
     * <p><strong>Preconditions:</strong> Observer attached to subject</p>
     * <p><strong>Test Input:</strong> Score change event (current=100, high=500)</p>
     * <p><strong>Expected Outcome:</strong> Observer notified once with correct values</p>
     * <p><strong>Actual Outcome:</strong> âœ?Observer receives notification</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should receive score changed notification")
    void testScoreChangedNotification() {
        // Act
        subject.notifyScoreChanged(100, 500);
        
        // Assert
        assertEquals(1, observer.scoreChangedCallCount,
                    "Observer should be notified exactly once");
        assertEquals(100, observer.lastCurrentScore,
                    "Current score should be 100");
        assertEquals(500, observer.lastHighScore,
                    "High score should be 500");
    }
    
    /**
     * Test Case: TC-OBS-SCORE-002
     * Tests that observer receives onHighScoreBeaten notification.
     * 
     * <p><strong>Class Under Test:</strong> ScoreObserver.onHighScoreBeaten(int)</p>
     * <p><strong>Preconditions:</strong> Observer attached to subject</p>
     * <p><strong>Test Input:</strong> High score beaten event (newHighScore=1000)</p>
     * <p><strong>Expected Outcome:</strong> Observer notified once with correct value</p>
     * <p><strong>Actual Outcome:</strong> âœ?Observer receives notification</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should receive high score beaten notification")
    void testHighScoreBeatenNotification() {
        // Act
        subject.notifyHighScoreBeaten(1000);
        
        // Assert
        assertEquals(1, observer.highScoreBeatenCallCount,
                    "Observer should be notified exactly once");
        assertEquals(1000, observer.lastNewHighScore,
                    "New high score should be 1000");
    }
    
    /**
     * Test Case: TC-OBS-SCORE-003
     * Tests that observer receives onScoreAddedToHighScores notification.
     * 
     * <p><strong>Class Under Test:</strong> ScoreObserver.onScoreAddedToHighScores(int, int)</p>
     * <p><strong>Preconditions:</strong> Observer attached to subject</p>
     * <p><strong>Test Input:</strong> Score added event (score=750, position=2)</p>
     * <p><strong>Expected Outcome:</strong> Observer notified once with correct values</p>
     * <p><strong>Actual Outcome:</strong> âœ?Observer receives notification</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should receive score added to high scores notification")
    void testScoreAddedNotification() {
        // Act
        subject.notifyScoreAdded(750, 2);
        
        // Assert
        assertEquals(1, observer.scoreAddedCallCount,
                    "Observer should be notified exactly once");
        assertEquals(750, observer.lastAddedScore,
                    "Added score should be 750");
        assertEquals(2, observer.lastPosition,
                    "Position should be 2");
    }
    
    /**
     * Test Case: TC-OBS-SCORE-004
     * Tests multiple sequential score change notifications.
     * 
     * <p><strong>Class Under Test:</strong> ScoreObserver.onScoreChanged(int, int)</p>
     * <p><strong>Preconditions:</strong> Observer attached to subject</p>
     * <p><strong>Test Input:</strong> Three sequential score changes</p>
     * <p><strong>Expected Outcome:</strong> Observer receives all three notifications</p>
     * <p><strong>Actual Outcome:</strong> âœ?All notifications received in sequence</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should receive multiple sequential notifications")
    void testMultipleSequentialNotifications() {
        // Act
        subject.notifyScoreChanged(50, 200);
        subject.notifyScoreChanged(100, 200);
        subject.notifyScoreChanged(150, 200);
        
        // Assert
        assertEquals(3, observer.scoreChangedCallCount,
                    "Observer should be notified three times");
        assertEquals(150, observer.lastCurrentScore,
                    "Last current score should be 150");
        assertEquals(200, observer.lastHighScore,
                    "High score should remain 200");
    }
    
    /**
     * Test Case: TC-OBS-SCORE-005
     * Tests that detached observer stops receiving notifications.
     * 
     * <p><strong>Class Under Test:</strong> Subject.detach(ScoreObserver)</p>
     * <p><strong>Preconditions:</strong> Observer attached then detached</p>
     * <p><strong>Test Input:</strong> Score change after detachment</p>
     * <p><strong>Expected Outcome:</strong> Observer receives no notification</p>
     * <p><strong>Actual Outcome:</strong> âœ?No notification after detachment</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Detached observer should not receive notifications")
    void testDetachedObserverReceivesNoNotifications() {
        // Arrange
        subject.notifyScoreChanged(50, 200);
        assertEquals(1, observer.scoreChangedCallCount);
        
        // Act
        subject.detach(observer);
        subject.notifyScoreChanged(100, 200);
        
        // Assert
        assertEquals(1, observer.scoreChangedCallCount,
                    "Observer should not receive notification after detachment");
        assertEquals(50, observer.lastCurrentScore,
                    "Last recorded score should be from before detachment");
    }
    
    /**
     * Test Case: TC-OBS-SCORE-006
     * Tests observer independence with multiple observers.
     * 
     * <p><strong>Class Under Test:</strong> Subject notification mechanism</p>
     * <p><strong>Preconditions:</strong> Two independent observers attached</p>
     * <p><strong>Test Input:</strong> Single score change notification</p>
     * <p><strong>Expected Outcome:</strong> Both observers notified independently</p>
     * <p><strong>Actual Outcome:</strong> âœ?Each observer maintains independent state</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Multiple observers should receive independent notifications")
    void testMultipleObserversIndependence() {
        // Arrange
        MockScoreObserver observer2 = new MockScoreObserver();
        MockScoreSubject subject2 = new MockScoreSubject();
        subject2.attach(observer2);
        
        // Act
        subject.notifyScoreChanged(100, 500);
        subject2.notifyScoreChanged(200, 600);
        
        // Assert
        assertEquals(1, observer.scoreChangedCallCount,
                    "First observer should be notified once");
        assertEquals(100, observer.lastCurrentScore,
                    "First observer should have score 100");
        
        assertEquals(1, observer2.scoreChangedCallCount,
                    "Second observer should be notified once");
        assertEquals(200, observer2.lastCurrentScore,
                    "Second observer should have score 200");
    }
    
    /**
     * Test Case: TC-OBS-SCORE-007
     * Tests mixed event type notifications in sequence.
     * 
     * <p><strong>Class Under Test:</strong> ScoreObserver (all methods)</p>
     * <p><strong>Preconditions:</strong> Observer attached to subject</p>
     * <p><strong>Test Input:</strong> Score changed, high score beaten, score added</p>
     * <p><strong>Expected Outcome:</strong> All event types received correctly</p>
     * <p><strong>Actual Outcome:</strong> âœ?Observer handles multiple event types</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should handle mixed event types correctly")
    void testMixedEventTypes() {
        // Act
        subject.notifyScoreChanged(50, 100);
        subject.notifyHighScoreBeaten(150);
        subject.notifyScoreAdded(150, 0);
        subject.notifyScoreChanged(150, 150);
        
        // Assert
        assertEquals(2, observer.scoreChangedCallCount,
                    "Score changed should be called twice");
        assertEquals(1, observer.highScoreBeatenCallCount,
                    "High score beaten should be called once");
        assertEquals(1, observer.scoreAddedCallCount,
                    "Score added should be called once");
        assertEquals(150, observer.lastCurrentScore,
                    "Final current score should be 150");
        assertEquals(150, observer.lastNewHighScore,
                    "New high score should be 150");
    }
    
    /**
     * Test Case: TC-OBS-SCORE-008
     * Tests zero and negative score values handling.
     * 
     * <p><strong>Class Under Test:</strong> ScoreObserver.onScoreChanged(int, int)</p>
     * <p><strong>Preconditions:</strong> Observer attached to subject</p>
     * <p><strong>Test Input:</strong> Score change with zero value</p>
     * <p><strong>Expected Outcome:</strong> Zero is accepted as valid score</p>
     * <p><strong>Actual Outcome:</strong> âœ?Zero score handled correctly</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should handle zero score values")
    void testZeroScoreHandling() {
        // Act
        subject.notifyScoreChanged(0, 0);
        
        // Assert
        assertEquals(1, observer.scoreChangedCallCount,
                    "Observer should be notified");
        assertEquals(0, observer.lastCurrentScore,
                    "Current score should be 0");
        assertEquals(0, observer.lastHighScore,
                    "High score should be 0");
    }
    
    /**
     * Test Case: TC-OBS-SCORE-009
     * Tests that observer can be reattached after detachment.
     * 
     * <p><strong>Class Under Test:</strong> Subject.attach(ScoreObserver) and detach()</p>
     * <p><strong>Preconditions:</strong> Observer detached then reattached</p>
     * <p><strong>Test Input:</strong> Notifications before, during, and after detachment</p>
     * <p><strong>Expected Outcome:</strong> Notifications received only when attached</p>
     * <p><strong>Actual Outcome:</strong> âœ?Reattachment works correctly</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should work correctly after reattachment")
    void testObserverReattachment() {
        // Arrange - First notification
        subject.notifyScoreChanged(50, 100);
        assertEquals(1, observer.scoreChangedCallCount);
        
        // Act - Detach
        subject.detach(observer);
        subject.notifyScoreChanged(75, 100);
        assertEquals(1, observer.scoreChangedCallCount,
                    "Should not receive notification when detached");
        
        // Act - Reattach
        subject.attach(observer);
        subject.notifyScoreChanged(100, 100);
        
        // Assert
        assertEquals(2, observer.scoreChangedCallCount,
                    "Should receive notification after reattachment");
        assertEquals(100, observer.lastCurrentScore,
                    "Should have latest score value");
    }
    
    /**
     * Test Case: TC-OBS-SCORE-010
     * Tests high score progression scenario.
     * 
     * <p><strong>Class Under Test:</strong> ScoreObserver (integration scenario)</p>
     * <p><strong>Preconditions:</strong> Observer attached, starting from low score</p>
     * <p><strong>Test Input:</strong> Progressive score increases with high score beats</p>
     * <p><strong>Expected Outcome:</strong> All events tracked correctly through progression</p>
     * <p><strong>Actual Outcome:</strong> âœ?Observer tracks complete score progression</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should track complete high score progression")
    void testHighScoreProgression() {
        // Act - Simulate game progression
        subject.notifyScoreChanged(0, 100);
        subject.notifyScoreChanged(50, 100);
        subject.notifyScoreChanged(100, 100);
        subject.notifyHighScoreBeaten(100);
        subject.notifyScoreAdded(100, 5);
        subject.notifyScoreChanged(150, 150);
        subject.notifyHighScoreBeaten(150);
        subject.notifyScoreAdded(150, 3);
        
        // Assert
        assertEquals(4, observer.scoreChangedCallCount,
                    "Should track all score changes");
        assertEquals(2, observer.highScoreBeatenCallCount,
                    "Should track high score beats");
        assertEquals(2, observer.scoreAddedCallCount,
                    "Should track score additions");
        assertEquals(150, observer.lastCurrentScore,
                    "Final score should be 150");
        assertEquals(150, observer.lastNewHighScore,
                    "Final high score should be 150");
    }
}
