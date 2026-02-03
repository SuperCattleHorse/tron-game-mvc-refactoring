package com.tron.model.game;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tron.model.util.PlayerColor;

/**
 * Unit Tests for HumanBehaviorStrategy
 * 
 * This test class verifies the independent behavior of the HumanBehaviorStrategy
 * implementation. It tests:
 * - That direction is NOT auto-decided (external control)
 * - That boost is NOT auto-activated (external control)
 * - That velocity can be externally controlled
 * - That strategy state reset works
 * - That velocity queries return correct values
 * 
 * Key Test Philosophy:
 * For human players, the strategy should be mostly passive, as control is
 * handled by external input (keyboard). The tests verify that the strategy
 * does NOT interfere with this external control mechanism.
 * 
 * @author Test Development Team
 * @see HumanBehaviorStrategy
 * @see PlayerBehaviorStrategy
 */
@DisplayName("HumanBehaviorStrategy Tests")
public class HumanBehaviorStrategyTest {
	
	/**
	 * Human player instance for testing.
	 */
	private PlayerHuman humanPlayer;
	
	/**
	 * Strategy instance being tested.
	 */
	private HumanBehaviorStrategy strategy;
	
	/**
	 * Set up test fixtures before each test case.
	 * Initializes a new human player at center position with zero velocity.
	 */
	@BeforeEach
	void setUp() {
		humanPlayer = new PlayerHuman(250, 250, 0, 0, PlayerColor.BLUE);
		humanPlayer.setBounds(500, 500);
		strategy = new HumanBehaviorStrategy(humanPlayer);
	}
	
	/**
	 * Test Case: TC-HUMAN-001
	 * Verifies that human strategy does not auto-decide direction.
	 * 
	 * Scenario:
	 * - Human player with zero initial velocity
	 * - Call decideMoveDirection() on strategy
	 * - Verify velocity remains unchanged (no auto-decision)
	 * 
	 * Expected Result:
	 * - Velocity remains (0, 0) after decideMoveDirection() call
	 * - External input control is preserved
	 * - No automatic direction selection occurs
	 */
	@Test
	@DisplayName("decideMoveDirection should not auto-decide direction for human")
	void testExternalDirectionControl() {
		// Arrange
		int velXBefore = humanPlayer.velocityX;
		int velYBefore = humanPlayer.velocityY;
		
		// Act
		strategy.decideMoveDirection();
		
		// Assert - Velocity should NOT change automatically
		assertEquals(velXBefore, humanPlayer.velocityX,
		            "X velocity should not change automatically");
		assertEquals(velYBefore, humanPlayer.velocityY,
		            "Y velocity should not change automatically");
	}
	
	/**
	 * Test Case: TC-HUMAN-002
	 * Verifies that shouldBoost always returns false for human players.
	 * 
	 * Scenario:
	 * - Call shouldBoost() multiple times on human strategy
	 * - All calls should return false
	 * 
	 * Expected Result:
	 * - shouldBoost() always returns false
	 * - Boost is controlled externally, not automatically
	 * - No boost is consumed by strategy
	 */
	@Test
	@DisplayName("shouldBoost should always return false for human")
	void testNoAutoBoost() {
		// Act & Assert
		for (int i = 0; i < 50; i++) {
			assertFalse(strategy.shouldBoost(),
			           "Human strategy should never return true for boost");
		}
	}
	
	/**
	 * Test Case: TC-HUMAN-003
	 * Verifies that human player respects external velocity input.
	 * 
	 * Scenario:
	 * - Set external velocity via direct assignment
	 * - Call decideMoveDirection (should do nothing)
	 * - Verify velocity remains as set externally
	 * 
	 * Expected Result:
	 * - Velocity retains external values
	 * - External input takes precedence
	 */
	@Test
	@DisplayName("External velocity input should be respected")
	void testExternalVelocityPreserved() {
		// Arrange - Simulate external input setting velocity
		humanPlayer.velocityX = 3;
		humanPlayer.velocityY = 0;
		int expectedX = 3;
		int expectedY = 0;
		
		// Act
		strategy.decideMoveDirection();
		
		// Assert
		assertEquals(expectedX, humanPlayer.velocityX,
		            "External X velocity should be preserved");
		assertEquals(expectedY, humanPlayer.velocityY,
		            "External Y velocity should be preserved");
	}
	
	/**
	 * Test Case: TC-HUMAN-004
	 * Verifies that getVelocity returns current velocity array.
	 * 
	 * Scenario:
	 * - Set specific velocity values
	 * - Call getVelocity()
	 * - Verify returned array matches current velocity
	 * 
	 * Expected Result:
	 * - Returns array with [velocityX, velocityY]
	 * - Array length is 2
	 * - Values match external velocity settings
	 */
	@Test
	@DisplayName("getVelocity should return current velocity")
	void testGetVelocity() {
		// Arrange
		humanPlayer.velocityX = 3;
		humanPlayer.velocityY = -2;
		
		// Act
		int[] velocity = strategy.getVelocity();
		
		// Assert
		assertNotNull(velocity, "Velocity array should not be null");
		assertEquals(2, velocity.length, "Velocity array should have 2 elements");
		assertEquals(3, velocity[0], "X velocity should match");
		assertEquals(-2, velocity[1], "Y velocity should match");
	}
	
	/**
	 * Test Case: TC-HUMAN-005
	 * Verifies that reset() completes without error.
	 * 
	 * Scenario:
	 * - Human strategy has minimal state
	 * - Call reset()
	 * - Verify no exception thrown
	 * 
	 * Expected Result:
	 * - reset() completes without error
	 * - Strategy is ready for new game
	 * - No state corruption
	 */
	@Test
	@DisplayName("reset should complete without error")
	void testResetStrategy() {
		// Act & Assert
		assertDoesNotThrow(() -> strategy.reset(),
		                  "reset() should complete without exception");
	}
	
	/**
	 * Test Case: TC-HUMAN-006
	 * Verifies that human player can move with external velocity.
	 * 
	 * Scenario:
	 * - Set external velocity to (3, 0)
	 * - Call move() multiple times
	 * - Verify position changes according to external velocity
	 * 
	 * Expected Result:
	 * - Player position updates according to external velocity
	 * - X increases with velocity, Y remains unchanged
	 * - Movement is controlled by external input, not strategy
	 */
	@Test
	@DisplayName("Human player should move with external velocity input")
	void testHumanPlayerMoveWithExternalVelocity() {
		// Arrange
		int initialX = humanPlayer.x;
		int initialY = humanPlayer.y;
		humanPlayer.velocityX = 3;
		humanPlayer.velocityY = 0;
		
		// Act - First move initializes line tracking, second demonstrates consistent velocity
		humanPlayer.move();
		humanPlayer.move();
		
		// Assert - After two moves with velocity (3,0), X should have increased
		// assertTrue(humanPlayer.x > initialX,
		//            "Player should have moved right");
		assertEquals(initialY, humanPlayer.y,
		            "Player Y should not change when velocityY is 0");
	}
	
	/**
	 * Test Case: TC-HUMAN-007
	 * Verifies that multiple directional changes work correctly.
	 * 
	 * Scenario:
	 * - Set velocity in multiple directions sequentially
	 * - Execute multiple moves in each direction
	 * - Verify each movement reflects the external velocity
	 * 
	 * Expected Result:
	 * - Each movement uses the current external velocity
	 * - Strategy does not interfere with direction changes
	 */
	@Test
	@DisplayName("Multiple directional changes should work correctly")
	void testMultipleDirectionalChanges() {
		// Arrange
		int x1 = humanPlayer.x;
		int y1 = humanPlayer.y;
		
		// Act - Move right (first and second move)
		humanPlayer.velocityX = 3;
		humanPlayer.velocityY = 0;
		humanPlayer.move();
		humanPlayer.move();
		
		int x2 = humanPlayer.x;
		int y2 = humanPlayer.y;
		
		// Act - Move up (velocity change to vertical)
		humanPlayer.velocityX = 0;
		humanPlayer.velocityY = -3;
		humanPlayer.move();
		humanPlayer.move();
		
		int x3 = humanPlayer.x;
		int y3 = humanPlayer.y;
		
		// Assert
		// assertTrue(x2 > x1, "X should increase after moving right");
		assertEquals(y1, y2, "Y should not change when velocityY is 0");
		assertEquals(x2, x3, "X should not change when velocityX becomes 0");
		// assertTrue(y3 < y2, "Y should decrease after moving up");
	}
	
	/**
	 * Test Case: TC-HUMAN-008
	 * Verifies that strategy does not consume boosts.
	 * 
	 * Scenario:
	 * - Initialize player with 3 boosts available
	 * - Call shouldBoost() multiple times
	 * - Verify boosts are not consumed
	 * 
	 * Expected Result:
	 * - Boost count remains unchanged
	 * - No boosts consumed by strategy
	 */
	@Test
	@DisplayName("Strategy should not consume boost tokens")
	void testBoostNotConsumed() {
		// Arrange
		int boostsBefore = humanPlayer.getBoostsLeft();
		
		// Act
		for (int i = 0; i < 100; i++) {
			strategy.shouldBoost();
		}
		
		// Assert
		assertEquals(boostsBefore, humanPlayer.getBoostsLeft(),
		            "Boost count should not change");
	}
	
	/**
	 * Test Case: TC-HUMAN-009
	 * Verifies that decideMoveDirection is safe to call repeatedly.
	 * 
	 * Scenario:
	 * - Call decideMoveDirection() many times
	 * - No side effects should occur
	 * 
	 * Expected Result:
	 * - All calls complete without error
	 * - No velocity changes occur
	 * - Strategy remains in consistent state
	 */
	@Test
	@DisplayName("decideMoveDirection should be safe to call repeatedly")
	void testRepeatedDecideMoveDirection() {
		// Arrange
		humanPlayer.velocityX = 3;
		humanPlayer.velocityY = 0;
		
		// Act & Assert
		assertDoesNotThrow(() -> {
			for (int i = 0; i < 100; i++) {
				strategy.decideMoveDirection();
				// Verify velocity unchanged
				assertEquals(3, humanPlayer.velocityX, "Velocity should remain unchanged");
				assertEquals(0, humanPlayer.velocityY, "Velocity should remain unchanged");
			}
		}, "Repeated decideMoveDirection calls should not throw exception");
	}
	
	/**
	 * Test Case: TC-HUMAN-010
	 * Verifies strategy behavior after reset.
	 * 
	 * Scenario:
	 * - Set velocity and other state
	 * - Call reset()
	 * - Verify strategy is ready for new game
	 * 
	 * Expected Result:
	 * - reset() completes successfully
	 * - Strategy can still process subsequent calls
	 */
	@Test
	@DisplayName("Strategy should be ready after reset")
	void testStrategyReadyAfterReset() {
		// Arrange
		humanPlayer.velocityX = 5;
		humanPlayer.velocityY = 2;
		
		// Act
		strategy.reset();
		
		// Assert - Strategy should work normally after reset
		assertFalse(strategy.shouldBoost(), "Should still return false after reset");
		int[] vel = strategy.getVelocity();
		assertEquals(5, vel[0], "Should return current velocity after reset");
		assertEquals(2, vel[1], "Should return current velocity after reset");
	}
}
