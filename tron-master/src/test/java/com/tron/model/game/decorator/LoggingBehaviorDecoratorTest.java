package com.tron.model.game.decorator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.tron.model.game.AIBehaviorStrategy;
import com.tron.model.game.HumanBehaviorStrategy;
import com.tron.model.game.PlayerAI;
import com.tron.model.game.PlayerBehaviorStrategy;
import com.tron.model.game.PlayerHuman;
import com.tron.model.util.PlayerColor;

/**
 * Unit Tests for LoggingBehaviorDecorator
 * 
 * This test class verifies the logging and monitoring functionality of the
 * LoggingBehaviorDecorator implementation. It tests:
 * - Proper delegation to decorated strategy
 * - Correct logging of method calls
 * - Accurate statistics collection (call counts)
 * - Counter reset functionality
 * - Logging behavior with both AI and Human strategies
 * - Return value propagation
 * 
 * Testing Philosophy:
 * The LoggingBehaviorDecorator should:
 * 1. Maintain transparency - not modify wrapped strategy behavior
 * 2. Accurately track all method invocations
 * 3. Provide useful debugging information
 * 4. Not impact performance significantly
 * 
 * @author Test Development Team
 * @see LoggingBehaviorDecorator
 * @see BehaviorStrategyDecorator
 * @see PlayerBehaviorStrategy
 */
@DisplayName("LoggingBehaviorDecorator Tests")
public class LoggingBehaviorDecoratorTest {
	
	/**
	 * Mock strategy instance for testing delegation.
	 */
	private PlayerBehaviorStrategy mockStrategy;
	
	/**
	 * Logging decorator instance under test.
	 */
	private LoggingBehaviorDecorator loggingDecorator;
	
	/**
	 * Set up test fixtures before each test case.
	 * Creates a mock strategy and wraps it with a logging decorator.
	 */
	@BeforeEach
	void setUp() {
		mockStrategy = Mockito.mock(PlayerBehaviorStrategy.class);
		loggingDecorator = new LoggingBehaviorDecorator(mockStrategy);
	}
	
	/**
	 * Test Case: TC-LOGGING-001
	 * Verifies that constructor properly initializes decorator with zero counts.
	 * 
	 * Scenario:
	 * - Create LoggingBehaviorDecorator
	 * - Check initial counter values
	 * 
	 * Expected Result:
	 * - All counters start at zero
	 * - Decorator is ready for use
	 */
	@Test
	@DisplayName("Constructor should initialize with zero counters")
	void testConstructorInitializesCounters() {
		// Assert
		assertEquals(0, loggingDecorator.getMoveDecisionCount(),
		            "Move decision count should start at 0");
		assertEquals(0, loggingDecorator.getBoostDecisionCount(),
		            "Boost decision count should start at 0");
		assertEquals(0, loggingDecorator.getResetCount(),
		            "Reset count should start at 0");
	}
	
	/**
	 * Test Case: TC-LOGGING-002
	 * Verifies that decideMoveDirection increments counter and delegates.
	 * 
	 * Scenario:
	 * - Call decideMoveDirection() once
	 * - Verify counter incremented to 1
	 * - Verify delegation to wrapped strategy
	 * 
	 * Expected Result:
	 * - Move decision count is 1
	 * - Wrapped strategy's method is called once
	 */
	@Test
	@DisplayName("decideMoveDirection should increment counter and delegate")
	void testDecideMoveDirectionIncrementsCounter() {
		// Act
		loggingDecorator.decideMoveDirection();
		
		// Assert
		assertEquals(1, loggingDecorator.getMoveDecisionCount(),
		            "Move decision count should be 1 after one call");
		Mockito.verify(mockStrategy, Mockito.times(1)).decideMoveDirection();
	}
	
	/**
	 * Test Case: TC-LOGGING-003
	 * Verifies that multiple calls to decideMoveDirection increment counter correctly.
	 * 
	 * Scenario:
	 * - Call decideMoveDirection() 5 times
	 * - Verify counter reaches 5
	 * - Verify 5 delegations occurred
	 * 
	 * Expected Result:
	 * - Move decision count is 5
	 * - Wrapped strategy called 5 times
	 */
	@Test
	@DisplayName("Multiple decideMoveDirection calls should accumulate count")
	void testMultipleDecideMoveDirectionCalls() {
		// Act
		for (int i = 0; i < 5; i++) {
			loggingDecorator.decideMoveDirection();
		}
		
		// Assert
		assertEquals(5, loggingDecorator.getMoveDecisionCount(),
		            "Move decision count should be 5");
		Mockito.verify(mockStrategy, Mockito.times(5)).decideMoveDirection();
	}
	
	/**
	 * Test Case: TC-LOGGING-004
	 * Verifies that shouldBoost increments counter and delegates.
	 * 
	 * Scenario:
	 * - Mock strategy returns true
	 * - Call shouldBoost() once
	 * - Verify counter incremented and return value propagated
	 * 
	 * Expected Result:
	 * - Boost decision count is 1
	 * - Returns true (from wrapped strategy)
	 * - Delegation occurred once
	 */
	@Test
	@DisplayName("shouldBoost should increment counter and delegate")
	void testShouldBoostIncrementsCounter() {
		// Arrange
		Mockito.when(mockStrategy.shouldBoost()).thenReturn(true);
		
		// Act
		boolean result = loggingDecorator.shouldBoost();
		
		// Assert
		assertEquals(1, loggingDecorator.getBoostDecisionCount(),
		            "Boost decision count should be 1");
		assertTrue(result, "Should return true from wrapped strategy");
		Mockito.verify(mockStrategy, Mockito.times(1)).shouldBoost();
	}
	
	/**
	 * Test Case: TC-LOGGING-005
	 * Verifies that shouldBoost correctly propagates false value.
	 * 
	 * Scenario:
	 * - Mock strategy returns false
	 * - Call shouldBoost()
	 * - Verify false is returned and counter increments
	 * 
	 * Expected Result:
	 * - Returns false
	 * - Boost decision count increments
	 */
	@Test
	@DisplayName("shouldBoost should propagate false value")
	void testShouldBoostPropagatesFalse() {
		// Arrange
		Mockito.when(mockStrategy.shouldBoost()).thenReturn(false);
		
		// Act
		boolean result = loggingDecorator.shouldBoost();
		
		// Assert
		assertEquals(false, result, "Should return false");
		assertEquals(1, loggingDecorator.getBoostDecisionCount(),
		            "Boost decision count should be 1");
	}
	
	/**
	 * Test Case: TC-LOGGING-006
	 * Verifies that multiple shouldBoost calls accumulate count.
	 * 
	 * Scenario:
	 * - Call shouldBoost() 10 times
	 * - Verify counter reaches 10
	 * 
	 * Expected Result:
	 * - Boost decision count is 10
	 * - All calls delegated
	 */
	@Test
	@DisplayName("Multiple shouldBoost calls should accumulate count")
	void testMultipleShouldBoostCalls() {
		// Arrange
		Mockito.when(mockStrategy.shouldBoost()).thenReturn(false);
		
		// Act
		for (int i = 0; i < 10; i++) {
			loggingDecorator.shouldBoost();
		}
		
		// Assert
		assertEquals(10, loggingDecorator.getBoostDecisionCount(),
		            "Boost decision count should be 10");
		Mockito.verify(mockStrategy, Mockito.times(10)).shouldBoost();
	}
	
	/**
	 * Test Case: TC-LOGGING-007
	 * Verifies that reset increments counter and delegates.
	 * 
	 * Scenario:
	 * - Call reset() once
	 * - Verify counter incremented
	 * - Verify delegation occurred
	 * 
	 * Expected Result:
	 * - Reset count is 1
	 * - Wrapped strategy's reset() called once
	 */
	@Test
	@DisplayName("reset should increment counter and delegate")
	void testResetIncrementsCounter() {
		// Act
		loggingDecorator.reset();
		
		// Assert
		assertEquals(1, loggingDecorator.getResetCount(),
		            "Reset count should be 1");
		Mockito.verify(mockStrategy, Mockito.times(1)).reset();
	}
	
	/**
	 * Test Case: TC-LOGGING-008
	 * Verifies that multiple reset calls accumulate count.
	 * 
	 * Scenario:
	 * - Call reset() 3 times
	 * - Verify counter reaches 3
	 * 
	 * Expected Result:
	 * - Reset count is 3
	 * - Delegation occurred 3 times
	 */
	@Test
	@DisplayName("Multiple reset calls should accumulate count")
	void testMultipleResetCalls() {
		// Act
		for (int i = 0; i < 3; i++) {
			loggingDecorator.reset();
		}
		
		// Assert
		assertEquals(3, loggingDecorator.getResetCount(),
		            "Reset count should be 3");
		Mockito.verify(mockStrategy, Mockito.times(3)).reset();
	}
	
	/**
	 * Test Case: TC-LOGGING-009
	 * Verifies that getVelocity delegates without counting.
	 * 
	 * Scenario:
	 * - Mock strategy returns specific velocity
	 * - Call getVelocity()
	 * - Verify velocity is returned correctly
	 * - Note: getVelocity typically doesn't increment a counter
	 * 
	 * Expected Result:
	 * - Returns correct velocity array
	 * - Delegation occurred
	 */
	@Test
	@DisplayName("getVelocity should delegate and return velocity")
	void testGetVelocityDelegates() {
		// Arrange
		int[] expectedVelocity = {3, -2};
		Mockito.when(mockStrategy.getVelocity()).thenReturn(expectedVelocity);
		
		// Act
		int[] result = loggingDecorator.getVelocity();
		
		// Assert
		assertSame(expectedVelocity, result,
		          "Should return velocity from wrapped strategy");
		Mockito.verify(mockStrategy, Mockito.times(1)).getVelocity();
	}
	
	/**
	 * Test Case: TC-LOGGING-010
	 * Verifies that resetCounters clears all statistics.
	 * 
	 * Scenario:
	 * - Call several methods to increment counters
	 * - Call resetCounters()
	 * - Verify all counters are zero
	 * 
	 * Expected Result:
	 * - All counters return to 0 after resetCounters()
	 * - Decorator is ready for new statistics collection
	 */
	@Test
	@DisplayName("resetCounters should clear all statistics")
	void testResetCounters() {
		// Arrange - Build up some counts
		loggingDecorator.decideMoveDirection();
		loggingDecorator.decideMoveDirection();
		loggingDecorator.shouldBoost();
		loggingDecorator.reset();
		
		// Verify counts are non-zero
		assertTrue(loggingDecorator.getMoveDecisionCount() > 0,
		          "Should have move count before reset");
		
		// Act
		loggingDecorator.resetCounters();
		
		// Assert - All should be zero
		assertEquals(0, loggingDecorator.getMoveDecisionCount(),
		            "Move decision count should be 0 after resetCounters");
		assertEquals(0, loggingDecorator.getBoostDecisionCount(),
		            "Boost decision count should be 0 after resetCounters");
		assertEquals(0, loggingDecorator.getResetCount(),
		            "Reset count should be 0 after resetCounters");
	}
	
	/**
	 * Test Case: TC-LOGGING-011
	 * Verifies that decorator works with real AIBehaviorStrategy.
	 * 
	 * Scenario:
	 * - Create real AI player and strategy
	 * - Wrap with logging decorator
	 * - Execute several operations
	 * - Verify counters track correctly
	 * 
	 * Expected Result:
	 * - Works with real strategy without errors
	 * - Counters accurately reflect method calls
	 * - Strategy behavior is preserved
	 */
	@Test
	@DisplayName("Decorator should work with real AIBehaviorStrategy")
	void testWithRealAIStrategy() {
		// Arrange
		PlayerAI aiPlayer = new PlayerAI(250, 250, 3, 0, PlayerColor.RED);
		AIBehaviorStrategy realStrategy = new AIBehaviorStrategy(aiPlayer);
		LoggingBehaviorDecorator decorator = new LoggingBehaviorDecorator(realStrategy);
		
		// Act
		decorator.decideMoveDirection();
		decorator.decideMoveDirection();
		decorator.shouldBoost();
		decorator.reset();
		
		// Assert
		assertEquals(2, decorator.getMoveDecisionCount(),
		            "Should track 2 move decisions");
		assertEquals(1, decorator.getBoostDecisionCount(),
		            "Should track 1 boost decision");
		assertEquals(1, decorator.getResetCount(),
		            "Should track 1 reset");
	}
	
	/**
	 * Test Case: TC-LOGGING-012
	 * Verifies that decorator works with real HumanBehaviorStrategy.
	 * 
	 * Scenario:
	 * - Create real human player and strategy
	 * - Wrap with logging decorator
	 * - Execute several operations
	 * - Verify counters track correctly
	 * 
	 * Expected Result:
	 * - Works with human strategy without errors
	 * - Counters track all method calls
	 * - shouldBoost returns false (human behavior)
	 */
	@Test
	@DisplayName("Decorator should work with real HumanBehaviorStrategy")
	void testWithRealHumanStrategy() {
		// Arrange
		PlayerHuman humanPlayer = new PlayerHuman(100, 100, 0, 0, PlayerColor.BLUE);
		HumanBehaviorStrategy realStrategy = new HumanBehaviorStrategy(humanPlayer);
		LoggingBehaviorDecorator decorator = new LoggingBehaviorDecorator(realStrategy);
		
		// Act
		decorator.decideMoveDirection();
		boolean boost = decorator.shouldBoost();
		decorator.reset();
		
		// Assert
		assertEquals(1, decorator.getMoveDecisionCount(),
		            "Should track 1 move decision");
		assertEquals(1, decorator.getBoostDecisionCount(),
		            "Should track 1 boost decision");
		assertEquals(1, decorator.getResetCount(),
		            "Should track 1 reset");
		assertEquals(false, boost,
		            "Human strategy should return false for boost");
	}
	
	/**
	 * Test Case: TC-LOGGING-013
	 * Verifies that counters are independent per instance.
	 * 
	 * Scenario:
	 * - Create two logging decorators
	 * - Call methods on each independently
	 * - Verify counters are separate
	 * 
	 * Expected Result:
	 * - Each decorator maintains its own counters
	 * - No interference between instances
	 */
	@Test
	@DisplayName("Counters should be independent per decorator instance")
	void testCountersIndependent() {
		// Arrange
		PlayerBehaviorStrategy mockStrategy2 = Mockito.mock(PlayerBehaviorStrategy.class);
		LoggingBehaviorDecorator decorator1 = loggingDecorator;
		LoggingBehaviorDecorator decorator2 = new LoggingBehaviorDecorator(mockStrategy2);
		
		// Act
		decorator1.decideMoveDirection();
		decorator1.decideMoveDirection();
		decorator2.decideMoveDirection();
		
		// Assert
		assertEquals(2, decorator1.getMoveDecisionCount(),
		            "Decorator 1 should have count 2");
		assertEquals(1, decorator2.getMoveDecisionCount(),
		            "Decorator 2 should have count 1");
	}
	
	/**
	 * Test Case: TC-LOGGING-014
	 * Verifies that getDecoratedStrategy returns wrapped strategy.
	 * 
	 * Scenario:
	 * - Call getDecoratedStrategy()
	 * - Verify it returns the original wrapped strategy
	 * 
	 * Expected Result:
	 * - Returns exact reference to wrapped strategy
	 * - Supports decorator chain traversal
	 */
	@Test
	@DisplayName("getDecoratedStrategy should return wrapped strategy")
	void testGetDecoratedStrategy() {
		// Act
		PlayerBehaviorStrategy retrieved = loggingDecorator.getDecoratedStrategy();
		
		// Assert
		assertSame(mockStrategy, retrieved,
		          "Should return wrapped strategy");
	}
	
	/**
	 * Test Case: TC-LOGGING-015
	 * Verifies that all methods complete without exceptions.
	 * 
	 * Scenario:
	 * - Call all decorator methods
	 * - Verify no exceptions are thrown
	 * 
	 * Expected Result:
	 * - All operations complete successfully
	 * - No runtime errors
	 */
	@Test
	@DisplayName("All methods should complete without exceptions")
	void testAllMethodsComplete() {
		// Arrange
		Mockito.when(mockStrategy.shouldBoost()).thenReturn(false);
		Mockito.when(mockStrategy.getVelocity()).thenReturn(new int[]{0, 0});
		
		// Act & Assert
		assertDoesNotThrow(() -> {
			loggingDecorator.decideMoveDirection();
			loggingDecorator.shouldBoost();
			loggingDecorator.reset();
			loggingDecorator.getVelocity();
			loggingDecorator.resetCounters();
		}, "All methods should complete without exception");
	}
	
	/**
	 * Test Case: TC-LOGGING-016
	 * Verifies high-frequency calls are handled correctly.
	 * 
	 * Scenario:
	 * - Call decideMoveDirection() 100 times
	 * - Verify counter accuracy
	 * 
	 * Expected Result:
	 * - Counter reaches 100
	 * - No overflow or accuracy issues
	 * - Performance remains acceptable
	 */
	@Test
	@DisplayName("High-frequency calls should be tracked accurately")
	void testHighFrequencyCalls() {
		// Act
		for (int i = 0; i < 100; i++) {
			loggingDecorator.decideMoveDirection();
		}
		
		// Assert
		assertEquals(100, loggingDecorator.getMoveDecisionCount(),
		            "Should accurately track 100 calls");
		Mockito.verify(mockStrategy, Mockito.times(100)).decideMoveDirection();
	}
	
	/**
	 * Test Case: TC-LOGGING-017
	 * Verifies that mixed method calls are tracked independently.
	 * 
	 * Scenario:
	 * - Call various methods in mixed order
	 * - Verify each counter tracks its respective method
	 * 
	 * Expected Result:
	 * - Each counter only tracks its own method
	 * - No counter cross-contamination
	 */
	@Test
	@DisplayName("Mixed method calls should be tracked independently")
	void testMixedMethodCalls() {
		// Act
		loggingDecorator.decideMoveDirection();  // 1
		loggingDecorator.shouldBoost();          // 1
		loggingDecorator.decideMoveDirection();  // 2
		loggingDecorator.reset();                // 1
		loggingDecorator.decideMoveDirection();  // 3
		loggingDecorator.shouldBoost();          // 2
		
		// Assert
		assertEquals(3, loggingDecorator.getMoveDecisionCount(),
		            "Move decision count should be 3");
		assertEquals(2, loggingDecorator.getBoostDecisionCount(),
		            "Boost decision count should be 2");
		assertEquals(1, loggingDecorator.getResetCount(),
		            "Reset count should be 1");
	}
	
	/**
	 * Test Case: TC-LOGGING-018
	 * Verifies behavior after resetCounters is called.
	 * 
	 * Scenario:
	 * - Build up counters
	 * - Reset counters
	 * - Make new calls
	 * - Verify counters restart from zero
	 * 
	 * Expected Result:
	 * - Counters restart counting from 0
	 * - New calls increment correctly
	 */
	@Test
	@DisplayName("Counters should restart from zero after resetCounters")
	void testCountersRestartAfterReset() {
		// Arrange - Build up counts
		loggingDecorator.decideMoveDirection();
		loggingDecorator.decideMoveDirection();
		loggingDecorator.decideMoveDirection();
		
		// Act - Reset
		loggingDecorator.resetCounters();
		
		// Act - Make new calls
		loggingDecorator.decideMoveDirection();
		loggingDecorator.decideMoveDirection();
		
		// Assert
		assertEquals(2, loggingDecorator.getMoveDecisionCount(),
		            "Should have count 2 after reset and 2 new calls");
	}
}

