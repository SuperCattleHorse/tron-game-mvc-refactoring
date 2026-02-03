package com.tron.model.game.decorator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tron.model.game.AIBehaviorStrategy;
import com.tron.model.game.GameObject;
import com.tron.model.game.Player;
import com.tron.model.game.PlayerAI;
import com.tron.model.observer.PlayerObserver;
import com.tron.model.util.PlayerColor;

/**
 * Integration tests for the interaction between Observer Pattern and Decorator Pattern.
 * 
 * <p>This test class verifies that the Decorator Pattern (applied to PlayerBehaviorStrategy)
 * and Observer Pattern (applied to Player state changes) work correctly together without
 * interference. Tests ensure that decorating a player's behavior strategy does not affect
 * observer notifications, and that observers continue to receive proper notifications even
 * when behavior strategies are decorated.</p>
 * 
 * <p><strong>Test Strategy:</strong> Cross-pattern integration testing focusing on the
 * interaction between two distinct design patterns. Tests verify that pattern implementations
 * are properly decoupled and can coexist without conflicts.</p>
 * 
 * <p><strong>Design Patterns Tested:</strong></p>
 * <ul>
 *   <li><strong>Decorator Pattern (Structural):</strong> BehaviorStrategyDecorator enhances
 *       PlayerBehaviorStrategy without modifying core logic</li>
 *   <li><strong>Observer Pattern (Behavioral):</strong> Player notifies PlayerObservers of
 *       state changes</li>
 *   <li><strong>Integration:</strong> Decorated strategies trigger observer notifications
 *       correctly</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see BehaviorStrategyDecorator
 * @see LoggingBehaviorDecorator
 * @see PlayerObserver
 * @see Player
 */
@DisplayName("Observer-Decorator Pattern Integration Tests")
public class ObserverDecoratorIntegrationTest {
    
    /**
     * Mock implementation of PlayerObserver for testing.
     * Tracks all notification calls to verify observer pattern behavior.
     */
    private static class MockPlayerObserver implements PlayerObserver {
        private int stateChangedCallCount = 0;
        private int diedCallCount = 0;
        private int collisionCallCount = 0;
        private int directionChangedCallCount = 0;
        private int boostActivatedCallCount = 0;
        
        @Override
        public void onPlayerStateChanged(Player player) {
            stateChangedCallCount++;
        }
        
        @Override
        public void onPlayerDied(Player player) {
            diedCallCount++;
        }
        
        @Override
        public void onPlayerCollision(Player player, GameObject other) {
            collisionCallCount++;
        }
        
        @Override
        public void onPlayerDirectionChanged(Player player, int newVelocityX, int newVelocityY) {
            directionChangedCallCount++;
        }
        
        @Override
        public void onPlayerBoostActivated(Player player, int boostCountRemaining) {
            boostActivatedCallCount++;
        }
        
        /**
         * Resets all tracking counters for next test.
         */
        public void reset() {
            stateChangedCallCount = 0;
            diedCallCount = 0;
            collisionCallCount = 0;
            directionChangedCallCount = 0;
            boostActivatedCallCount = 0;
        }
    }
    
    /**
     * Testable subclass of PlayerAI that exposes protected notification methods.
     * This allows unit tests to trigger observer notifications directly.
     */
    private static class TestablePlayerAI extends PlayerAI {
        private int storedVelocityX;
        private int storedVelocityY;
        
        public TestablePlayerAI(int x, int y, int velocityX, int velocityY, PlayerColor color) {
            super(x, y, velocityX, velocityY, color);
            this.storedVelocityX = velocityX;
            this.storedVelocityY = velocityY;
        }
        
        public void testNotifyPlayerDied() {
            notifyPlayerDied();
        }
        
        public void testNotifyPlayerCollision(GameObject other) {
            notifyPlayerCollision(other);
        }
        
        public void testNotifyDirectionChanged(int velX, int velY) {
            notifyDirectionChanged(velX, velY);
            this.storedVelocityX = velX;
            this.storedVelocityY = velY;
        }
        
        public void testNotifyBoostActivated() {
            notifyBoostActivated();
        }
        
        public int getVelocityX() {
            return storedVelocityX;
        }
        
        public int getVelocityY() {
            return storedVelocityY;
        }
    }
    
    /**
     * Concrete implementation of GameObject for testing collision scenarios.
     */
    private static class TestGameObject extends GameObject {
        public TestGameObject(int x, int y, int width, int height) {
            super(x, y, 0, 0, width, height);
        }
        
        @Override
        public void accelerate() {
            // Empty implementation for testing
        }
        
        @Override
        public java.util.ArrayList<com.tron.model.util.Shape> getPath() {
            return new java.util.ArrayList<>();
        }
        
        @Override
        public boolean getAlive() {
            return true;
        }
        
        @Override
        public com.tron.model.data.DrawData getDrawData() {
            return null;
        }
    }
    
    private MockPlayerObserver observer;
    private TestablePlayerAI aiPlayer;
    private AIBehaviorStrategy baseStrategy;
    private LoggingBehaviorDecorator decoratedStrategy;
    
    /**
     * Sets up test fixtures before each test method.
     * Initializes observer, AI player, base strategy, and decorated strategy.
     */
    @BeforeEach
    void setUp() {
        observer = new MockPlayerObserver();
        aiPlayer = new TestablePlayerAI(250, 250, 3, 0, PlayerColor.RED);
        aiPlayer.setBounds(500, 500);
        aiPlayer.attach(observer);
        
        baseStrategy = new AIBehaviorStrategy(aiPlayer);
        Player[] players = {aiPlayer};
        baseStrategy.addPlayers(players);
        
        decoratedStrategy = new LoggingBehaviorDecorator(baseStrategy);
    }
    
    /**
     * Test Case: TC-CROSS-001
     * Tests that decorator creation does not affect observer attachment.
     * 
     * <p><strong>Class Under Test:</strong> LoggingBehaviorDecorator constructor</p>
     * <p><strong>Preconditions:</strong> Observer attached to player, strategy created</p>
     * <p><strong>Test Input:</strong> Create decorator wrapping base strategy</p>
     * <p><strong>Expected Outcome:</strong> Decorator created, observer still functional</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Patterns do not interfere with each other</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Decorator creation should not affect observer attachment")
    void testDecoratorCreationDoesNotAffectObserver() {
        // Arrange - decorator already created in setUp
        
        // Act
        aiPlayer.testNotifyPlayerDied();
        
        // Assert
        assertEquals(1, observer.diedCallCount,
                    "Observer should still receive notifications after decorator creation");
    }
    
    /**
     * Test Case: TC-CROSS-002
     * Tests that decorated strategy execution triggers observer notifications.
     * 
     * <p><strong>Class Under Test:</strong> Integration of decorator and observer</p>
     * <p><strong>Preconditions:</strong> Decorated strategy set on player, observer attached</p>
     * <p><strong>Test Input:</strong> Execute decorated strategy decideMoveDirection()</p>
     * <p><strong>Expected Outcome:</strong> Strategy executes, manual notifications work</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Decorated strategies compatible with observers</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Decorated strategy execution should allow observer notifications")
    void testDecoratedStrategyWithObserverNotifications() {
        // Act
        decoratedStrategy.decideMoveDirection();
        aiPlayer.testNotifyDirectionChanged(aiPlayer.getVelocityX(), aiPlayer.getVelocityY());
        
        // Assert
        assertEquals(1, observer.directionChangedCallCount,
                    "Observer should receive direction changed notification");
        assertTrue(decoratedStrategy.getMoveDecisionCount() > 0,
                   "Decorator should track move decisions");
    }
    
    /**
     * Test Case: TC-CROSS-003
     * Tests that multiple decorator layers do not break observer pattern.
     * 
     * <p><strong>Class Under Test:</strong> Multi-level decorator with observer</p>
     * <p><strong>Preconditions:</strong> Two decorators stacked, observer attached</p>
     * <p><strong>Test Input:</strong> Execute double-decorated strategy</p>
     * <p><strong>Expected Outcome:</strong> Strategy works, observer notifications work</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Multiple decorators compatible with observers</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Multiple decorator layers should not break observer pattern")
    void testMultipleDecoratorLayersWithObserver() {
        // Arrange - Add second decorator layer
        LoggingBehaviorDecorator doubleDecorated = new LoggingBehaviorDecorator(decoratedStrategy);
        
        // Act
        doubleDecorated.decideMoveDirection();
        aiPlayer.testNotifyPlayerDied();
        
        // Assert
        assertEquals(1, observer.diedCallCount,
                    "Observer should still work with multiple decorator layers");
        assertTrue(doubleDecorated.getMoveDecisionCount() > 0,
                   "Outer decorator should track decisions");
        assertTrue(decoratedStrategy.getMoveDecisionCount() > 0,
                   "Inner decorator should track decisions");
    }
    
    /**
     * Test Case: TC-CROSS-004
     * Tests that observer notifications work independently of decorator state.
     * 
     * <p><strong>Class Under Test:</strong> Observer independence from decorator</p>
     * <p><strong>Preconditions:</strong> Decorated strategy active, observer attached</p>
     * <p><strong>Test Input:</strong> Multiple notifications with decorator operations</p>
     * <p><strong>Expected Outcome:</strong> All observer notifications received correctly</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Observer pattern independent of decorator state</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Observer notifications should be independent of decorator state")
    void testObserverIndependenceFromDecorator() {
        // Act
        decoratedStrategy.decideMoveDirection();
        aiPlayer.testNotifyPlayerDied();
        decoratedStrategy.shouldBoost();
        aiPlayer.testNotifyBoostActivated();
        decoratedStrategy.reset();
        aiPlayer.testNotifyPlayerDied();
        
        // Assert
        assertEquals(2, observer.diedCallCount,
                    "Observer should track death notifications independently");
        assertEquals(1, observer.boostActivatedCallCount,
                    "Observer should track boost independently");
        assertTrue(decoratedStrategy.getMoveDecisionCount() > 0,
                   "Decorator should track independently");
        assertTrue(decoratedStrategy.getBoostDecisionCount() > 0,
                   "Decorator should track independently");
    }
    
    /**
     * Test Case: TC-CROSS-005
     * Tests that decorator reset does not affect observer attachment.
     * 
     * <p><strong>Class Under Test:</strong> Decorator.reset() with active observer</p>
     * <p><strong>Preconditions:</strong> Decorated strategy and observer both active</p>
     * <p><strong>Test Input:</strong> Reset decorated strategy, then notify</p>
     * <p><strong>Expected Outcome:</strong> Observer still receives notifications</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Decorator reset does not affect observers</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Decorator reset should not affect observer attachment")
    void testDecoratorResetDoesNotAffectObserver() {
        // Arrange
        decoratedStrategy.decideMoveDirection();
        aiPlayer.testNotifyPlayerDied();
        assertEquals(1, observer.diedCallCount);
        
        // Act
        decoratedStrategy.reset();
        aiPlayer.testNotifyPlayerDied();
        
        // Assert
        assertEquals(2, observer.diedCallCount,
                    "Observer should still work after decorator reset");
        assertEquals(1, decoratedStrategy.getResetCount(),
                    "Decorator should track reset");
    }
    
    /**
     * Test Case: TC-CROSS-006
     * Tests that attaching observer does not affect decorator functionality.
     * 
     * <p><strong>Class Under Test:</strong> Player.attach() with active decorator</p>
     * <p><strong>Preconditions:</strong> Decorated strategy active</p>
     * <p><strong>Test Input:</strong> Attach new observer, execute strategy</p>
     * <p><strong>Expected Outcome:</strong> Decorator continues working normally</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Observer attachment does not affect decorator</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Attaching observer should not affect decorator functionality")
    void testObserverAttachmentDoesNotAffectDecorator() {
        // Act
        MockPlayerObserver observer2 = new MockPlayerObserver();
        aiPlayer.attach(observer2);
        decoratedStrategy.decideMoveDirection();
        int[] velocity = decoratedStrategy.getVelocity();
        
        // Assert
        assertNotNull(velocity, "Decorator should return velocity");
        assertEquals(2, velocity.length, "Velocity should have 2 components");
        assertTrue(decoratedStrategy.getMoveDecisionCount() > 0,
                   "Decorator should continue tracking after observer attachment");
    }
    
    /**
     * Test Case: TC-CROSS-007
     * Tests that detaching observer does not affect decorator functionality.
     * 
     * <p><strong>Class Under Test:</strong> Player.detach() with active decorator</p>
     * <p><strong>Preconditions:</strong> Observer attached, decorator active</p>
     * <p><strong>Test Input:</strong> Detach observer, execute decorated strategy</p>
     * <p><strong>Expected Outcome:</strong> Decorator continues working normally</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Observer detachment does not affect decorator</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Detaching observer should not affect decorator functionality")
    void testObserverDetachmentDoesNotAffectDecorator() {
        // Arrange
        decoratedStrategy.decideMoveDirection();
        int initialCount = decoratedStrategy.getMoveDecisionCount();
        
        // Act
        aiPlayer.detach(observer);
        decoratedStrategy.decideMoveDirection();
        
        // Assert
        assertEquals(initialCount + 1, decoratedStrategy.getMoveDecisionCount(),
                    "Decorator should continue functioning after observer detachment");
    }
    
    /**
     * Test Case: TC-CROSS-008
     * Tests that decorator method calls can trigger manual observer notifications.
     * 
     * <p><strong>Class Under Test:</strong> Integration workflow</p>
     * <p><strong>Preconditions:</strong> Both patterns active</p>
     * <p><strong>Test Input:</strong> Decorator methods followed by manual notifications</p>
     * <p><strong>Expected Outcome:</strong> Both patterns work in coordination</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Patterns work together seamlessly</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Decorator methods should support manual observer notifications")
    void testDecoratorMethodsWithManualNotifications() {
        // Act - Simulate game loop with both patterns
        decoratedStrategy.decideMoveDirection();
        aiPlayer.testNotifyDirectionChanged(aiPlayer.getVelocityX(), aiPlayer.getVelocityY());
        
        boolean shouldBoost = decoratedStrategy.shouldBoost();
        if (shouldBoost) {
            aiPlayer.testNotifyBoostActivated();
        }
        
        aiPlayer.testNotifyPlayerDied();
        
        // Assert
        assertTrue(decoratedStrategy.getMoveDecisionCount() > 0,
                   "Decorator should track move decisions");
        assertTrue(decoratedStrategy.getBoostDecisionCount() > 0,
                   "Decorator should track boost decisions");
        assertEquals(1, observer.directionChangedCallCount,
                    "Observer should receive direction change");
        assertEquals(1, observer.diedCallCount,
                    "Observer should receive death notification");
    }
    
    /**
     * Test Case: TC-CROSS-009
     * Tests complete game scenario with both patterns active.
     * 
     * <p><strong>Class Under Test:</strong> Full integration scenario</p>
     * <p><strong>Preconditions:</strong> AI player with decorated strategy and observer</p>
     * <p><strong>Test Input:</strong> Complete game lifecycle with both patterns</p>
     * <p><strong>Expected Outcome:</strong> Both patterns function correctly throughout</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Patterns coexist in real-world scenario</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Complete game scenario should work with both patterns")
    void testCompleteGameScenarioWithBothPatterns() {
        // Arrange
        GameObject wall = new TestGameObject(200, 200, 10, 10);
        
        // Act - Simulate game progression
        decoratedStrategy.decideMoveDirection();           // AI decision
        aiPlayer.testNotifyDirectionChanged(3, 0);            // Direction change
        decoratedStrategy.shouldBoost();                   // Boost decision
        aiPlayer.testNotifyBoostActivated();                 // Boost activated
        decoratedStrategy.decideMoveDirection();           // Next move
        aiPlayer.testNotifyPlayerCollision(wall);                   // Collision
        aiPlayer.testNotifyPlayerDied();                            // Player dies
        decoratedStrategy.reset();                         // Strategy reset
        
        // Assert - Verify both patterns tracked everything
        assertEquals(2, decoratedStrategy.getMoveDecisionCount(),
                    "Decorator should track move decisions");
        assertTrue(decoratedStrategy.getBoostDecisionCount() > 0,
                   "Decorator should track boost decisions");
        assertEquals(1, decoratedStrategy.getResetCount(),
                    "Decorator should track reset");
        
        assertEquals(1, observer.directionChangedCallCount,
                    "Observer should track direction changes");
        assertEquals(1, observer.boostActivatedCallCount,
                    "Observer should track boost activations");
        assertEquals(1, observer.collisionCallCount,
                    "Observer should track collisions");
        assertEquals(1, observer.diedCallCount,
                    "Observer should track deaths");
    }
    
    /**
     * Test Case: TC-CROSS-010
     * Tests that decorator counters and observer counters remain independent.
     * 
     * <p><strong>Class Under Test:</strong> Pattern state independence</p>
     * <p><strong>Preconditions:</strong> Both patterns initialized</p>
     * <p><strong>Test Input:</strong> Operations that affect only one pattern</p>
     * <p><strong>Expected Outcome:</strong> Each pattern maintains independent state</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Pattern states are fully independent</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Decorator and observer counters should remain independent")
    void testPatternStateIndependence() {
        // Act - Decorator operations without notifications
        decoratedStrategy.decideMoveDirection();
        decoratedStrategy.shouldBoost();
        decoratedStrategy.getVelocity();
        
        // Assert - Decorator tracked, observer did not
        assertTrue(decoratedStrategy.getMoveDecisionCount() > 0,
                   "Decorator should track operations");
        assertTrue(decoratedStrategy.getBoostDecisionCount() > 0,
                   "Decorator should track operations");
        assertEquals(0, observer.diedCallCount,
                    "Observer should not receive notifications without explicit calls");
        
        // Act - Observer notifications without decorator operations
        aiPlayer.testNotifyPlayerDied();
        aiPlayer.testNotifyPlayerDied();
        
        // Assert - Observer tracked, decorator state unchanged
        assertEquals(2, observer.diedCallCount,
                    "Observer should track death notifications");
        // Decorator counts remain the same as before
    }
    
    /**
     * Test Case: TC-CROSS-011
     * Tests that null checks in one pattern do not affect the other.
     * 
     * <p><strong>Class Under Test:</strong> Pattern error handling independence</p>
     * <p><strong>Preconditions:</strong> Both patterns active</p>
     * <p><strong>Test Input:</strong> Null collision object (observer) vs normal decorator ops</p>
     * <p><strong>Expected Outcome:</strong> Both patterns handle their operations correctly</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Error handling is independent</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Null handling in one pattern should not affect the other")
    void testNullHandlingIndependence() {
        // Act - Null collision in observer pattern
        aiPlayer.testNotifyPlayerCollision(null);
        
        // Decorator should still work normally
        decoratedStrategy.decideMoveDirection();
        int[] velocity = decoratedStrategy.getVelocity();
        
        // Assert
        assertEquals(1, observer.collisionCallCount,
                    "Observer should handle null collision");
        assertNotNull(velocity, "Decorator should return velocity normally");
        assertTrue(decoratedStrategy.getMoveDecisionCount() > 0,
                   "Decorator should continue working");
    }
    
    /**
     * Test Case: TC-CROSS-012
     * Tests that both patterns can be reset independently.
     * 
     * <p><strong>Class Under Test:</strong> Independent reset mechanisms</p>
     * <p><strong>Preconditions:</strong> Both patterns active with tracked state</p>
     * <p><strong>Test Input:</strong> Reset decorator, verify observer still works</p>
     * <p><strong>Expected Outcome:</strong> Decorator reset, observer unaffected</p>
     * <p><strong>Actual Outcome:</strong> ï¿?Reset operations are independent</p>
     * <p><strong>Framework:</strong> JUnit 5</p>
     */
    @Test
    @DisplayName("Pattern reset operations should be independent")
    void testIndependentResetOperations() {
        // Arrange - Establish state in both patterns
        decoratedStrategy.decideMoveDirection();
        aiPlayer.testNotifyPlayerDied();
        assertTrue(decoratedStrategy.getMoveDecisionCount() > 0);
        assertEquals(1, observer.diedCallCount);
        
        // Act - Reset decorator only
        decoratedStrategy.resetCounters();
        
        // Assert
        assertEquals(0, decoratedStrategy.getMoveDecisionCount(),
                    "Decorator counters should be reset");
        assertEquals(1, observer.diedCallCount,
                    "Observer state should be unaffected by decorator reset");
        
        // Act - Observer still works
        aiPlayer.testNotifyPlayerDied();
        
        // Assert
        assertEquals(2, observer.diedCallCount,
                    "Observer should continue functioning after decorator reset");
    }
}
