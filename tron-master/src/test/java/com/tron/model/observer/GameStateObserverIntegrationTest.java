package com.tron.model.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tron.model.game.TronGameModel;
import com.tron.model.util.PlayerColor;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the GameStateObserver implementation in the Observer Pattern.
 * 
 * <p>This test class verifies that the GameStateObserver interface and TronGameModel
 * Subject implementation work correctly together. Tests ensure that observers receive
 * proper notifications for all game state changes including score updates, boost
 * changes, player crashes, and game resets.</p>
 * 
 * <p><strong>Test Strategy:</strong> Integration testing focusing on TronGameModel
 * as Subject and GameStateObserver implementations, testing notification flow for
 * all game events, multiple observer handling, and observer lifecycle management.</p>
 * 
 * <p><strong>Design Pattern:</strong> Observer Pattern (Behavioral) + MVC Architecture</p>
 * <ul>
 *   <li>Subject: TronGameModel</li>
 *   <li>Observer: GameStateObserver implementations (Views)</li>
 *   <li>Notification: Game state, score, boost, crash, reset events</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see GameStateObserver
 * @see TronGameModel
 * @see Subject
 */
@DisplayName("GameStateObserver Integration Tests")
public class GameStateObserverIntegrationTest {
    
    /**
     * Testable TronGameModel subclass that exposes protected notification methods.
     */
    private static class TestableTronGameModel extends TronGameModel {
        public TestableTronGameModel(int mapWidth, int mapHeight, int velocity, int playerCount) {
            super(mapWidth, mapHeight, velocity, playerCount);
        }
        
        public void testNotifyScoreChanged(int playerIndex, int newScore) {
            notifyScoreChanged(playerIndex, newScore);
        }
        
        public void testNotifyBoostChanged(int playerIndex, int boostCount) {
            notifyBoostChanged(playerIndex, boostCount);
        }
        
        public void testNotifyPlayerCrashed(int playerIndex) {
            notifyPlayerCrashed(playerIndex);
        }
        
        public void testNotifyGameReset() {
            notifyGameReset();
        }
    }
    
    /**
     * Mock implementation of GameStateObserver for testing.
     * Tracks all notification calls to verify observer pattern behavior.
     */
    private static class MockGameStateObserver implements GameStateObserver {
        private int gameStateChangedCallCount = 0;
        private int scoreChangedCallCount = 0;
        private int boostChangedCallCount = 0;
        private int playerCrashedCallCount = 0;
        private int gameResetCallCount = 0;
        private int lastPlayerIndex = -1;
        private int lastScore = -1;
        private int lastBoostCount = -1;
        
        @Override
        public void onGameStateChanged() {
            gameStateChangedCallCount++;
        }
        
        @Override
        public void onScoreChanged(int playerIndex, int newScore) {
            scoreChangedCallCount++;
            lastPlayerIndex = playerIndex;
            lastScore = newScore;
        }
        
        @Override
        public void onBoostChanged(int playerIndex, int boostCount) {
            boostChangedCallCount++;
            lastPlayerIndex = playerIndex;
            lastBoostCount = boostCount;
        }
        
        @Override
        public void onPlayerCrashed(int playerIndex) {
            playerCrashedCallCount++;
            lastPlayerIndex = playerIndex;
        }
        
        @Override
        public void onGameReset() {
            gameResetCallCount++;
        }
        
        /**
         * Resets all tracking counters for next test.
         */
        public void reset() {
            gameStateChangedCallCount = 0;
            scoreChangedCallCount = 0;
            boostChangedCallCount = 0;
            playerCrashedCallCount = 0;
            gameResetCallCount = 0;
            lastPlayerIndex = -1;
            lastScore = -1;
            lastBoostCount = -1;
        }
    }
    
    private MockGameStateObserver observer;
    private TestableTronGameModel gameModel;
    
    /**
     * Sets up test fixtures before each test method.
     * Initializes mock observer and game model instances.
     */
    @BeforeEach
    void setUp() {
        observer = new MockGameStateObserver();
        gameModel = new TestableTronGameModel(500, 500, 3, 2);
        gameModel.attach(observer);
    }
    
    /**
     * Test Case: TC-OBS-GAME-001
     * Tests that observer is successfully attached to game model.
     * 
     * <p><strong>Class Under Test:</strong> TronGameModel.attach(GameStateObserver)</p>
     * <p><strong>Preconditions:</strong> TronGameModel instance created</p>
     * <p><strong>Test Input:</strong> Attach observer to game model</p>
     * <p><strong>Expected Outcome:</strong> Observer attached without exceptions</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Observer successfully attached</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should be successfully attached to game model")
    void testObserverAttachment() {
        // Arrange
        MockGameStateObserver newObserver = new MockGameStateObserver();
        
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> gameModel.attach(newObserver),
                          "Attaching observer should not throw exception");
    }
    
    /**
     * Test Case: TC-OBS-GAME-002
     * Tests that observer receives onGameStateChanged notification.
     * 
     * <p><strong>Class Under Test:</strong> GameStateObserver.onGameStateChanged()</p>
     * <p><strong>Preconditions:</strong> Observer attached to game model</p>
     * <p><strong>Test Input:</strong> Call notifyObservers()</p>
     * <p><strong>Expected Outcome:</strong> Observer notified of state change</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Observer receives state change notification</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should receive game state changed notification")
    void testGameStateChangedNotification() {
        // Act
        gameModel.notifyObservers();
        
        // Assert
        assertEquals(1, observer.gameStateChangedCallCount,
                    "Observer should be notified exactly once");
    }
    
    /**
     * Test Case: TC-OBS-GAME-003
     * Tests that observer receives onScoreChanged notification.
     * 
     * <p><strong>Class Under Test:</strong> GameStateObserver.onScoreChanged(int, int)</p>
     * <p><strong>Preconditions:</strong> Observer attached to game model</p>
     * <p><strong>Test Input:</strong> Score change for player 0 to 100</p>
     * <p><strong>Expected Outcome:</strong> Observer notified with correct values</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Observer receives score change notification</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should receive score changed notification")
    void testScoreChangedNotification() {
        // Act
        gameModel.testNotifyScoreChanged(0, 100);
        
        // Assert
        assertEquals(1, observer.scoreChangedCallCount,
                    "Observer should be notified exactly once");
        assertEquals(0, observer.lastPlayerIndex,
                    "Player index should be 0");
        assertEquals(100, observer.lastScore,
                    "Score should be 100");
    }
    
    /**
     * Test Case: TC-OBS-GAME-004
     * Tests that observer receives onBoostChanged notification.
     * 
     * <p><strong>Class Under Test:</strong> GameStateObserver.onBoostChanged(int, int)</p>
     * <p><strong>Preconditions:</strong> Observer attached to game model</p>
     * <p><strong>Test Input:</strong> Boost change for player 0 to 2</p>
     * <p><strong>Expected Outcome:</strong> Observer notified with correct values</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Observer receives boost change notification</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should receive boost changed notification")
    void testBoostChangedNotification() {
        // Act
        gameModel.testNotifyBoostChanged(0, 2);
        
        // Assert
        assertEquals(1, observer.boostChangedCallCount,
                    "Observer should be notified exactly once");
        assertEquals(0, observer.lastPlayerIndex,
                    "Player index should be 0");
        assertEquals(2, observer.lastBoostCount,
                    "Boost count should be 2");
    }
    
    /**
     * Test Case: TC-OBS-GAME-005
     * Tests that observer receives onPlayerCrashed notification.
     * 
     * <p><strong>Class Under Test:</strong> GameStateObserver.onPlayerCrashed(int)</p>
     * <p><strong>Preconditions:</strong> Observer attached to game model</p>
     * <p><strong>Test Input:</strong> Player 1 crashes</p>
     * <p><strong>Expected Outcome:</strong> Observer notified with correct player</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Observer receives crash notification</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should receive player crashed notification")
    void testPlayerCrashedNotification() {
        // Act
        gameModel.testNotifyPlayerCrashed(1);
        
        // Assert
        assertEquals(1, observer.playerCrashedCallCount,
                    "Observer should be notified exactly once");
        assertEquals(1, observer.lastPlayerIndex,
                    "Player index should be 1");
    }
    
    /**
     * Test Case: TC-OBS-GAME-006
     * Tests that observer receives onGameReset notification.
     * 
     * <p><strong>Class Under Test:</strong> GameStateObserver.onGameReset()</p>
     * <p><strong>Preconditions:</strong> Observer attached to game model</p>
     * <p><strong>Test Input:</strong> Game reset triggered</p>
     * <p><strong>Expected Outcome:</strong> Observer notified of reset</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Observer receives reset notification</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should receive game reset notification")
    void testGameResetNotification() {
        // Act
        gameModel.testNotifyGameReset();
        
        // Assert
        assertEquals(1, observer.gameResetCallCount,
                    "Observer should be notified exactly once");
    }
    
    /**
     * Test Case: TC-OBS-GAME-007
     * Tests multiple observers attached to same game model.
     * 
     * <p><strong>Class Under Test:</strong> TronGameModel notification mechanism</p>
     * <p><strong>Preconditions:</strong> Two observers attached to game model</p>
     * <p><strong>Test Input:</strong> Single state change notification</p>
     * <p><strong>Expected Outcome:</strong> Both observers notified independently</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Multiple observers work correctly</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Multiple observers should receive independent notifications")
    void testMultipleObservers() {
        // Arrange
        MockGameStateObserver observer2 = new MockGameStateObserver();
        gameModel.attach(observer2);
        
        // Act
        gameModel.notifyObservers();
        
        // Assert
        assertEquals(1, observer.gameStateChangedCallCount,
                    "First observer should be notified once");
        assertEquals(1, observer2.gameStateChangedCallCount,
                    "Second observer should be notified once");
    }
    
    /**
     * Test Case: TC-OBS-GAME-008
     * Tests that detached observer stops receiving notifications.
     * 
     * <p><strong>Class Under Test:</strong> TronGameModel.detach(GameStateObserver)</p>
     * <p><strong>Preconditions:</strong> Observer attached then detached</p>
     * <p><strong>Test Input:</strong> State change after detachment</p>
     * <p><strong>Expected Outcome:</strong> Observer receives no notification</p>
     * <p><strong>Actual Outcome:</strong> ï¿?No notification after detachment</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Detached observer should not receive notifications")
    void testDetachedObserverReceivesNoNotifications() {
        // Arrange
        gameModel.notifyObservers();
        assertEquals(1, observer.gameStateChangedCallCount);
        
        // Act
        gameModel.detach(observer);
        gameModel.notifyObservers();
        
        // Assert
        assertEquals(1, observer.gameStateChangedCallCount,
                    "Observer should not receive notification after detachment");
    }
    
    /**
     * Test Case: TC-OBS-GAME-009
     * Tests mixed event type notifications in sequence.
     * 
     * <p><strong>Class Under Test:</strong> GameStateObserver (all methods)</p>
     * <p><strong>Preconditions:</strong> Observer attached to game model</p>
     * <p><strong>Test Input:</strong> State, score, boost, crash, reset events</p>
     * <p><strong>Expected Outcome:</strong> All event types received correctly</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Observer handles multiple event types</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should handle mixed event types correctly")
    void testMixedEventTypes() {
        // Act
        gameModel.notifyObservers();
        gameModel.testNotifyScoreChanged(0, 50);
        gameModel.testNotifyBoostChanged(0, 2);
        gameModel.testNotifyScoreChanged(1, 75);
        gameModel.testNotifyPlayerCrashed(1);
        gameModel.testNotifyGameReset();
        
        // Assert
        assertEquals(1, observer.gameStateChangedCallCount,
                    "State changed should be called once");
        assertEquals(2, observer.scoreChangedCallCount,
                    "Score changed should be called twice");
        assertEquals(1, observer.boostChangedCallCount,
                    "Boost changed should be called once");
        assertEquals(1, observer.playerCrashedCallCount,
                    "Player crashed should be called once");
        assertEquals(1, observer.gameResetCallCount,
                    "Game reset should be called once");
    }
    
    /**
     * Test Case: TC-OBS-GAME-010
     * Tests multiple score changes for different players.
     * 
     * <p><strong>Class Under Test:</strong> GameStateObserver.onScoreChanged(int, int)</p>
     * <p><strong>Preconditions:</strong> Observer attached, 2-player game</p>
     * <p><strong>Test Input:</strong> Score changes for both players</p>
     * <p><strong>Expected Outcome:</strong> All score changes tracked correctly</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Multiple player scores tracked</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should track score changes for multiple players")
    void testMultiplePlayerScores() {
        // Act
        gameModel.testNotifyScoreChanged(0, 50);
        gameModel.testNotifyScoreChanged(1, 75);
        gameModel.testNotifyScoreChanged(0, 100);
        gameModel.testNotifyScoreChanged(1, 150);
        
        // Assert
        assertEquals(4, observer.scoreChangedCallCount,
                    "Observer should be notified four times");
        assertEquals(1, observer.lastPlayerIndex,
                    "Last player index should be 1");
        assertEquals(150, observer.lastScore,
                    "Last score should be 150");
    }
    
    /**
     * Test Case: TC-OBS-GAME-011
     * Tests boost depletion tracking across multiple activations.
     * 
     * <p><strong>Class Under Test:</strong> GameStateObserver.onBoostChanged(int, int)</p>
     * <p><strong>Preconditions:</strong> Observer attached, player starts with 3 boosts</p>
     * <p><strong>Test Input:</strong> Sequential boost activations (3 ï¿?2 ï¿?1 ï¿?0)</p>
     * <p><strong>Expected Outcome:</strong> All boost changes tracked correctly</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Boost depletion tracked</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should track boost depletion")
    void testBoostDepletion() {
        // Act
        gameModel.testNotifyBoostChanged(0, 3);
        gameModel.testNotifyBoostChanged(0, 2);
        gameModel.testNotifyBoostChanged(0, 1);
        gameModel.testNotifyBoostChanged(0, 0);
        
        // Assert
        assertEquals(4, observer.boostChangedCallCount,
                    "Observer should be notified four times");
        assertEquals(0, observer.lastBoostCount,
                    "Final boost count should be 0");
    }
    
    /**
     * Test Case: TC-OBS-GAME-012
     * Tests all players crashing in sequence.
     * 
     * <p><strong>Class Under Test:</strong> GameStateObserver.onPlayerCrashed(int)</p>
     * <p><strong>Preconditions:</strong> Observer attached, 2-player game</p>
     * <p><strong>Test Input:</strong> Both players crash</p>
     * <p><strong>Expected Outcome:</strong> Both crashes tracked correctly</p>
     * <p><strong>Actual Outcome:</strong> ï¿?All crashes tracked</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should track multiple player crashes")
    void testMultiplePlayerCrashes() {
        // Act
        gameModel.testNotifyPlayerCrashed(0);
        gameModel.testNotifyPlayerCrashed(1);
        
        // Assert
        assertEquals(2, observer.playerCrashedCallCount,
                    "Observer should be notified twice");
        assertEquals(1, observer.lastPlayerIndex,
                    "Last crashed player should be 1");
    }
    
    /**
     * Test Case: TC-OBS-GAME-013
     * Tests game reset resets observation state correctly.
     * 
     * <p><strong>Class Under Test:</strong> GameStateObserver.onGameReset()</p>
     * <p><strong>Preconditions:</strong> Observer attached, game in progress</p>
     * <p><strong>Test Input:</strong> Game activity followed by reset</p>
     * <p><strong>Expected Outcome:</strong> Reset notification received</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Reset tracked correctly</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should receive reset after game activity")
    void testGameResetAfterActivity() {
        // Act
        gameModel.notifyObservers();
        gameModel.testNotifyScoreChanged(0, 100);
        gameModel.testNotifyPlayerCrashed(0);
        gameModel.testNotifyGameReset();
        
        // Assert
        assertEquals(1, observer.gameResetCallCount,
                    "Reset should be called once");
        assertEquals(1, observer.gameStateChangedCallCount,
                    "State changed should be called once");
        assertEquals(1, observer.scoreChangedCallCount,
                    "Score changed should be called once");
        assertEquals(1, observer.playerCrashedCallCount,
                    "Player crashed should be called once");
    }
    
    /**
     * Test Case: TC-OBS-GAME-014
     * Tests that null observer is handled safely.
     * 
     * <p><strong>Class Under Test:</strong> TronGameModel.attach(GameStateObserver)</p>
     * <p><strong>Preconditions:</strong> Game model instance created</p>
     * <p><strong>Test Input:</strong> Attempt to attach null observer</p>
     * <p><strong>Expected Outcome:</strong> Null handled gracefully (no exception or ignored)</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Null observer handled safely</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Game model should handle null observer safely")
    void testNullObserverHandling() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> gameModel.attach(null),
                          "Attaching null observer should not throw exception");
        
        // Act - Notify should still work for valid observers
        gameModel.notifyObservers();
        
        // Assert
        assertEquals(1, observer.gameStateChangedCallCount,
                    "Valid observer should still receive notifications");
    }
    
    /**
     * Test Case: TC-OBS-GAME-015
     * Tests complete game lifecycle with all event types.
     * 
     * <p><strong>Class Under Test:</strong> GameStateObserver (integration scenario)</p>
     * <p><strong>Preconditions:</strong> Observer attached, 2-player game starting</p>
     * <p><strong>Test Input:</strong> Complete game progression from start to end</p>
     * <p><strong>Expected Outcome:</strong> All events tracked correctly through lifecycle</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Observer tracks complete game lifecycle</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer should track complete game lifecycle")
    void testCompleteGameLifecycle() {
        // Act - Simulate complete game
        gameModel.testNotifyGameReset();              // Game starts
        gameModel.notifyObservers();              // Initial state
        gameModel.testNotifyScoreChanged(0, 10);      // Player 0 scores
        gameModel.testNotifyBoostChanged(0, 2);       // Player 0 uses boost
        gameModel.notifyObservers();              // State update
        gameModel.testNotifyScoreChanged(1, 20);      // Player 1 scores
        gameModel.testNotifyBoostChanged(1, 2);       // Player 1 uses boost
        gameModel.notifyObservers();              // State update
        gameModel.testNotifyScoreChanged(0, 30);      // Player 0 scores more
        gameModel.testNotifyPlayerCrashed(1);         // Player 1 crashes
        gameModel.notifyObservers();              // Final state
        gameModel.testNotifyGameReset();              // Game resets
        
        // Assert
        assertEquals(4, observer.gameStateChangedCallCount,
                    "Should track all state updates");
        assertEquals(3, observer.scoreChangedCallCount,
                    "Should track all score changes");
        assertEquals(2, observer.boostChangedCallCount,
                    "Should track all boost changes");
        assertEquals(1, observer.playerCrashedCallCount,
                    "Should track player crash");
        assertEquals(2, observer.gameResetCallCount,
                    "Should track game resets");
    }
}
