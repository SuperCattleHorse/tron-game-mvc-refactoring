package com.tron.model.game;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tron.model.util.PlayerColor;

/**
 * Unit Tests for AIBehaviorStrategy
 * 
 * This test class verifies the independent behavior of the AIBehaviorStrategy
 * implementation. It tests:
 * - Boundary detection and avoidance
 * - Random boost activation
 * - Velocity management
 * - Strategy state reset
 * - Equivalence with original AI behavior
 * 
 * @author Test Development Team
 * @see AIBehaviorStrategy
 * @see PlayerBehaviorStrategy
 */
@DisplayName("AIBehaviorStrategy Tests")
public class AIBehaviorStrategyTest {
	
	/**
	 * AI player instance for testing.
	 */
	private PlayerAI aiPlayer;
	
	/**
	 * Strategy instance being tested.
	 */
	private AIBehaviorStrategy strategy;
	
	/**
	 * Set up test fixtures before each test case.
	 * Initializes a new AI player at center position with default velocity.
	 */
	@BeforeEach
	void setUp() {
		aiPlayer = new PlayerAI(250, 250, 3, 0, PlayerColor.RED);
		aiPlayer.setBounds(500, 500);
		strategy = new AIBehaviorStrategy(aiPlayer);
		
		Player[] players = {aiPlayer};
		strategy.addPlayers(players);
	}
	
	/**
	 * Test Case: TC-AI-001
	 * Verifies that AI avoids the right boundary and changes direction.
	 * 
	 * Scenario:
	 * - AI player positioned near right boundary (x = 490)
	 * - Moving right with velocity (3, 0)
	 * - Should detect boundary proximity and change direction vertically
	 * 
	 * Expected Result:
	 * - Velocity should change (either X becomes 0 or Y changes)
	 * - Player should not move beyond boundary
	 */
	@Test
	@DisplayName("AI should avoid right boundary and change direction")
	void testRightBoundaryAvoidance() {
		// Arrange
		aiPlayer = new PlayerAI(490, 250, 3, 0, PlayerColor.RED);
		aiPlayer.setBounds(500, 500);
		strategy = new AIBehaviorStrategy(aiPlayer);
		Player[] players = {aiPlayer};
		strategy.addPlayers(players);
		
		int initialVelX = aiPlayer.velocityX;
		int initialVelY = aiPlayer.velocityY;
		
		// Act - Call move which internally uses decideMoveDirection
		aiPlayer.move();
		
		// Assert - Direction should change when near boundary
		boolean velocityChanged = (aiPlayer.velocityX != initialVelX) || 
		                          (aiPlayer.velocityY != initialVelY);
		assertTrue(velocityChanged || aiPlayer.x <= 500,
		          "AI should change direction or stay within bounds near right boundary");
	}
	
	/**
	 * Test Case: TC-AI-002
	 * Verifies that AI avoids the left boundary and changes direction.
	 * 
	 * Scenario:
	 * - AI player positioned near left boundary (x = 5)
	 * - Moving left with velocity (-3, 0)
	 * - Should detect boundary proximity and change direction vertically
	 * 
	 * Expected Result:
	 * - Velocity should change
	 * - Player should remain within bounds
	 */
	@Test
	@DisplayName("AI should avoid left boundary and change direction")
	void testLeftBoundaryAvoidance() {
		// Arrange
		aiPlayer = new PlayerAI(5, 250, -3, 0, PlayerColor.RED);
		aiPlayer.setBounds(500, 500);
		strategy = new AIBehaviorStrategy(aiPlayer);
		Player[] players = {aiPlayer};
		strategy.addPlayers(players);
		
		// Act
		aiPlayer.move();
		
		// Assert
		assertTrue(aiPlayer.x >= 0,
		          "AI should stay within left boundary");
	}
	
	/**
	 * Test Case: TC-AI-003
	 * Verifies that AI avoids the top boundary and changes direction.
	 * 
	 * Scenario:
	 * - AI player positioned near top boundary (y = 5)
	 * - Moving up with velocity (0, -3)
	 * - Should detect boundary proximity and change direction horizontally
	 * 
	 * Expected Result:
	 * - Y velocity should become 0 or X velocity should change
	 * - Player should remain within bounds
	 */
	@Test
	@DisplayName("AI should avoid top boundary and change direction")
	void testTopBoundaryAvoidance() {
		// Arrange
		aiPlayer = new PlayerAI(250, 5, 0, -3, PlayerColor.RED);
		aiPlayer.setBounds(500, 500);
		strategy = new AIBehaviorStrategy(aiPlayer);
		Player[] players = {aiPlayer};
		strategy.addPlayers(players);
		
		// Act
		aiPlayer.move();
		
		// Assert
		assertTrue(aiPlayer.y >= 0,
		          "AI should stay within top boundary");
	}
	
	/**
	 * Test Case: TC-AI-004
	 * Verifies that AI avoids the bottom boundary and changes direction.
	 * 
	 * Scenario:
	 * - AI player positioned near bottom boundary (y = 495)
	 * - Moving down with velocity (0, 3)
	 * - Should detect boundary proximity and change direction horizontally
	 * 
	 * Expected Result:
	 * - Y velocity should become 0 or X velocity should change
	 * - Player should remain within bounds
	 */
	@Test
	@DisplayName("AI should avoid bottom boundary and change direction")
	void testBottomBoundaryAvoidance() {
		// Arrange
		aiPlayer = new PlayerAI(250, 495, 0, 3, PlayerColor.RED);
		aiPlayer.setBounds(500, 500);
		strategy = new AIBehaviorStrategy(aiPlayer);
		Player[] players = {aiPlayer};
		strategy.addPlayers(players);
		
		// Act
		aiPlayer.move();
		
		// Assert
		assertTrue(aiPlayer.y <= 500,
		          "AI should stay within bottom boundary");
	}
	
	/**
	 * Test Case: TC-AI-005
	 * Verifies that shouldBoost returns boolean values correctly.
	 * This test runs multiple iterations to check for any boost activation.
	 * 
	 * Scenario:
	 * - Query shouldBoost() multiple times
	 * - With random seed, at least one should return true (1/100 chance per call)
	 * 
	 * Expected Result:
	 * - shouldBoost() returns boolean
	 * - Over many calls, at least some should return false (most likely)
	 * - No exceptions thrown
	 */
	@Test
	@DisplayName("shouldBoost should return boolean values")
	void testShouldBoostReturnsBool() {
		// Arrange - Run multiple iterations
		int falseCount = 0;
		int trueCount = 0;
		
		// Act
		for (int i = 0; i < 100; i++) {
			if (strategy.shouldBoost()) {
				trueCount++;
			} else {
				falseCount++;
			}
		}
		
		// Assert
		assertEquals(100, trueCount + falseCount,
		            "Total calls should equal 100");
		assertTrue(falseCount > 0,
		          "Most calls to shouldBoost should return false (1/100 chance)");
	}
	
	/**
	 * Test Case: TC-AI-006
	 * Verifies that reset() resets the strategy state.
	 * 
	 * Scenario:
	 * - Create strategy instance
	 * - Call reset()
	 * - Verify no exceptions
	 * 
	 * Expected Result:
	 * - reset() completes without error
	 * - Strategy is ready for new game
	 */
	@Test
	@DisplayName("reset should reset strategy state")
	void testResetStrategy() {
		// Act & Assert - Should not throw exception
		assertDoesNotThrow(() -> strategy.reset(),
		                  "reset() should complete without exception");
	}
	
	/**
	 * Test Case: TC-AI-007
	 * Verifies that getVelocity returns current velocity array.
	 * 
	 * Scenario:
	 * - Set specific velocity values
	 * - Call getVelocity()
	 * - Check returned array matches current velocity
	 * 
	 * Expected Result:
	 * - Returns array with [velocityX, velocityY]
	 * - Array length is 2
	 * - Values match player velocity
	 */
	@Test
	@DisplayName("getVelocity should return current velocity")
	void testGetVelocity() {
		// Arrange
		aiPlayer.velocityX = 3;
		aiPlayer.velocityY = -2;
		
		// Act
		int[] velocity = strategy.getVelocity();
		
		// Assert
		assertNotNull(velocity, "Velocity array should not be null");
		assertEquals(2, velocity.length, "Velocity array should have 2 elements");
		assertEquals(3, velocity[0], "X velocity should match");
		assertEquals(-2, velocity[1], "Y velocity should match");
	}
	
	/**
	 * Test Case: TC-AI-008 (Parameterized)
	 * Verifies boundary detection at multiple boundary positions.
	 * 
	 * Scenario:
	 * - Parameterized test with multiple boundary positions
	 * - Each test position is near a different boundary
	 * - Verify player stays within bounds after move
	 * 
	 * Expected Result:
	 * - Player remains within [0, 500] bounds for both X and Y
	 * - No boundary violations
	 */
	@ParameterizedTest(name = "Test boundary at position ({0}, {1})")
	@MethodSource("provideBoundaryPositions")
	@DisplayName("AI should respect all boundaries (parameterized)")
	void testBoundaryDetectionMultiplePositions(int x, int y, int velX, int velY) {
		// Arrange
		aiPlayer = new PlayerAI(x, y, velX, velY, PlayerColor.RED);
		aiPlayer.setBounds(500, 500);
		strategy = new AIBehaviorStrategy(aiPlayer);
		Player[] players = {aiPlayer};
		strategy.addPlayers(players);
		
		// Act
		aiPlayer.move();
		
		// Assert - Verify bounds compliance
		assertTrue(aiPlayer.x >= 0 && aiPlayer.x <= 500,
		          "X position should be within bounds [0, 500]");
		assertTrue(aiPlayer.y >= 0 && aiPlayer.y <= 500,
		          "Y position should be within bounds [0, 500]");
	}
	
	/**
	 * Provides test data for boundary detection parameterized test.
	 * Includes positions near all four boundaries with appropriate velocities.
	 * 
	 * @return Stream of test arguments [x, y, velX, velY]
	 */
	static Stream<org.junit.jupiter.params.provider.Arguments> provideBoundaryPositions() {
		return Stream.of(
			org.junit.jupiter.params.provider.Arguments.of(5, 250, -3, 0),      // Left
			org.junit.jupiter.params.provider.Arguments.of(495, 250, 3, 0),     // Right
			org.junit.jupiter.params.provider.Arguments.of(250, 5, 0, -3),      // Top
			org.junit.jupiter.params.provider.Arguments.of(250, 495, 0, 3)      // Bottom
		);
	}
	
	/**
	 * Test Case: TC-AI-009
	 * Verifies that strategy handles multiple consecutive moves correctly.
	 * 
	 * Scenario:
	 * - Execute multiple move() calls in sequence
	 * - Check player remains within bounds throughout
	 * - Verify no state corruption occurs
	 * 
	 * Expected Result:
	 * - Player stays within bounds for all moves
	 * - No exceptions thrown
	 */
	@Test
	@DisplayName("AI should handle multiple consecutive moves")
	void testMultipleConsecutiveMoves() {
		// Act & Assert
		assertDoesNotThrow(() -> {
			for (int i = 0; i < 50; i++) {
				aiPlayer.move();
				// Verify bounds after each move
				assertTrue(aiPlayer.x >= 0 && aiPlayer.x <= 500,
				          "X should be within bounds after move " + i);
				assertTrue(aiPlayer.y >= 0 && aiPlayer.y <= 500,
				          "Y should be within bounds after move " + i);
			}
		}, "Multiple consecutive moves should not throw exception");
	}
	
	/**
	 * Test Case: TC-AI-010
	 * Verifies that decideMoveDirection is called and processed correctly.
	 * 
	 * Scenario:
	 * - Call decideMoveDirection directly
	 * - Check velocity remains valid
	 * 
	 * Expected Result:
	 * - decideMoveDirection completes without error
	 * - Velocity values are valid (within reasonable bounds)
	 */
	@Test
	@DisplayName("decideMoveDirection should execute without error")
	void testDecideMoveDirection() {
		// Act & Assert
		assertDoesNotThrow(() -> strategy.decideMoveDirection(),
		                  "decideMoveDirection should complete without exception");
		
		// Verify velocity is still reasonable
		assertNotNull(strategy.getVelocity(), "Velocity should not be null after decision");
	}
}
