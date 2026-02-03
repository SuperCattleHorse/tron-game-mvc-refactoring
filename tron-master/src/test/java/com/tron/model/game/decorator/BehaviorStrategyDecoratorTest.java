package com.tron.model.game.decorator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.tron.model.game.AIBehaviorStrategy;
import com.tron.model.game.PlayerAI;
import com.tron.model.game.PlayerBehaviorStrategy;
import com.tron.model.util.PlayerColor;

/**
 * Unit Tests for BehaviorStrategyDecorator
 * 
 * This test class verifies the base decorator functionality for the Decorator Pattern
 * implementation on PlayerBehaviorStrategy. It tests:
 * - Proper delegation to the decorated strategy
 * - Constructor validation and null safety
 * - Accessor methods for decorator chain traversal
 * - Correct implementation of all interface methods
 * - Decorator wrapping behavior
 * 
 * Testing Philosophy:
 * The decorator should be transparent - it should delegate all calls to the wrapped
 * strategy without modifying behavior. Subclasses add enhancement, but the base
 * decorator only provides delegation infrastructure.
 * 
 * @author Test Development Team
 * @see BehaviorStrategyDecorator
 * @see PlayerBehaviorStrategy
 */
@DisplayName("BehaviorStrategyDecorator Base Class Tests")
public class BehaviorStrategyDecoratorTest {
	
	/**
	 * Concrete test implementation of abstract decorator.
	 * Used for testing base decorator functionality.
	 */
	private static class TestDecorator extends BehaviorStrategyDecorator {
		public TestDecorator(PlayerBehaviorStrategy decoratedStrategy) {
			super(decoratedStrategy);
		}
	}
	
	/**
	 * Mock strategy instance for testing delegation.
	 */
	private PlayerBehaviorStrategy mockStrategy;
	
	/**
	 * Test decorator instance.
	 */
	private BehaviorStrategyDecorator decorator;
	
	/**
	 * Set up test fixtures before each test case.
	 * Creates a mock strategy and wraps it with a test decorator.
	 */
	@BeforeEach
	void setUp() {
		mockStrategy = Mockito.mock(PlayerBehaviorStrategy.class);
		decorator = new TestDecorator(mockStrategy);
	}
	
	/**
	 * Test Case: TC-DECORATOR-001
	 * Verifies that constructor properly wraps a strategy.
	 * 
	 * Scenario:
	 * - Create decorator with a mock strategy
	 * - Verify decorator is initialized
	 * - Verify wrapped strategy is accessible
	 * 
	 * Expected Result:
	 * - Decorator instance is created successfully
	 * - getDecoratedStrategy() returns the wrapped strategy
	 */
	@Test
	@DisplayName("Constructor should properly wrap strategy")
	void testConstructorWrapsStrategy() {
		// Assert
		assertNotNull(decorator, "Decorator should be created successfully");
		assertSame(mockStrategy, decorator.getDecoratedStrategy(),
		          "Decorator should wrap the provided strategy");
	}
	
	/**
	 * Test Case: TC-DECORATOR-002
	 * Verifies that constructor rejects null strategy.
	 * 
	 * Scenario:
	 * - Attempt to create decorator with null strategy
	 * 
	 * Expected Result:
	 * - IllegalArgumentException is thrown
	 * - Error message indicates null strategy
	 */
	@Test
	@DisplayName("Constructor should reject null strategy")
	void testConstructorRejectsNull() {
		// Act & Assert
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new TestDecorator(null),
			"Constructor should throw IllegalArgumentException for null strategy"
		);
		
		assertEquals("Decorated strategy cannot be null", exception.getMessage(),
		            "Exception message should indicate null strategy");
	}
	
	/**
	 * Test Case: TC-DECORATOR-003
	 * Verifies that decideMoveDirection delegates to wrapped strategy.
	 * 
	 * Scenario:
	 * - Call decideMoveDirection() on decorator
	 * - Verify mock strategy's method is called exactly once
	 * 
	 * Expected Result:
	 * - Mock strategy's decideMoveDirection() is invoked once
	 * - No additional behavior occurs
	 */
	@Test
	@DisplayName("decideMoveDirection should delegate to wrapped strategy")
	void testDecideMoveDirectionDelegates() {
		// Act
		decorator.decideMoveDirection();
		
		// Assert
		Mockito.verify(mockStrategy, Mockito.times(1)).decideMoveDirection();
	}
	
	/**
	 * Test Case: TC-DECORATOR-004
	 * Verifies that shouldBoost delegates to wrapped strategy.
	 * 
	 * Scenario:
	 * - Mock strategy returns true for shouldBoost
	 * - Call shouldBoost() on decorator
	 * - Verify result is propagated from wrapped strategy
	 * 
	 * Expected Result:
	 * - Mock strategy's shouldBoost() is invoked once
	 * - Return value is correctly propagated (true)
	 */
	@Test
	@DisplayName("shouldBoost should delegate and return wrapped strategy result")
	void testShouldBoostDelegates() {
		// Arrange
		Mockito.when(mockStrategy.shouldBoost()).thenReturn(true);
		
		// Act
		boolean result = decorator.shouldBoost();
		
		// Assert
		Mockito.verify(mockStrategy, Mockito.times(1)).shouldBoost();
		assertEquals(true, result, "Decorator should return wrapped strategy's result");
	}
	
	/**
	 * Test Case: TC-DECORATOR-005
	 * Verifies that shouldBoost correctly propagates false value.
	 * 
	 * Scenario:
	 * - Mock strategy returns false for shouldBoost
	 * - Call shouldBoost() on decorator
	 * - Verify false is returned
	 * 
	 * Expected Result:
	 * - Returns false as per wrapped strategy
	 */
	@Test
	@DisplayName("shouldBoost should propagate false value")
	void testShouldBoostPropagatesFalse() {
		// Arrange
		Mockito.when(mockStrategy.shouldBoost()).thenReturn(false);
		
		// Act
		boolean result = decorator.shouldBoost();
		
		// Assert
		assertEquals(false, result, "Decorator should propagate false");
	}
	
	/**
	 * Test Case: TC-DECORATOR-006
	 * Verifies that reset delegates to wrapped strategy.
	 * 
	 * Scenario:
	 * - Call reset() on decorator
	 * - Verify mock strategy's reset() is called once
	 * 
	 * Expected Result:
	 * - Mock strategy's reset() is invoked once
	 * - No exceptions thrown
	 */
	@Test
	@DisplayName("reset should delegate to wrapped strategy")
	void testResetDelegates() {
		// Act
		assertDoesNotThrow(() -> decorator.reset(),
		                  "reset() should complete without exception");
		
		// Assert
		Mockito.verify(mockStrategy, Mockito.times(1)).reset();
	}
	
	/**
	 * Test Case: TC-DECORATOR-007
	 * Verifies that getVelocity delegates to wrapped strategy.
	 * 
	 * Scenario:
	 * - Mock strategy returns specific velocity array
	 * - Call getVelocity() on decorator
	 * - Verify array is propagated correctly
	 * 
	 * Expected Result:
	 * - Mock strategy's getVelocity() is invoked once
	 * - Returned array matches wrapped strategy's return value
	 */
	@Test
	@DisplayName("getVelocity should delegate and return wrapped strategy result")
	void testGetVelocityDelegates() {
		// Arrange
		int[] expectedVelocity = {3, -2};
		Mockito.when(mockStrategy.getVelocity()).thenReturn(expectedVelocity);
		
		// Act
		int[] result = decorator.getVelocity();
		
		// Assert
		Mockito.verify(mockStrategy, Mockito.times(1)).getVelocity();
		assertSame(expectedVelocity, result,
		          "Decorator should return wrapped strategy's velocity array");
	}
	
	/**
	 * Test Case: TC-DECORATOR-008
	 * Verifies that getDecoratedStrategy returns the wrapped strategy.
	 * 
	 * Scenario:
	 * - Call getDecoratedStrategy() on decorator
	 * - Verify it returns the original wrapped strategy
	 * 
	 * Expected Result:
	 * - Returns exact reference to wrapped strategy
	 * - Useful for decorator chain traversal
	 */
	@Test
	@DisplayName("getDecoratedStrategy should return wrapped strategy")
	void testGetDecoratedStrategy() {
		// Act
		PlayerBehaviorStrategy retrieved = decorator.getDecoratedStrategy();
		
		// Assert
		assertSame(mockStrategy, retrieved,
		          "Should return exact reference to wrapped strategy");
	}
	
	/**
	 * Test Case: TC-DECORATOR-009
	 * Verifies that decorator can wrap a real strategy.
	 * 
	 * Scenario:
	 * - Create real AIBehaviorStrategy
	 * - Wrap it with decorator
	 * - Call methods and verify they work
	 * 
	 * Expected Result:
	 * - Decorator wraps real strategy successfully
	 * - All methods can be called without error
	 * - Real strategy behavior is preserved
	 */
	@Test
	@DisplayName("Decorator should work with real strategy")
	void testDecoratorWithRealStrategy() {
		// Arrange
		PlayerAI player = new PlayerAI(250, 250, 3, 0, PlayerColor.BLUE);
		AIBehaviorStrategy realStrategy = new AIBehaviorStrategy(player);
		BehaviorStrategyDecorator realDecorator = new TestDecorator(realStrategy);
		
		// Act & Assert
		assertDoesNotThrow(() -> {
			realDecorator.decideMoveDirection();
			realDecorator.shouldBoost();
			realDecorator.reset();
			int[] vel = realDecorator.getVelocity();
			assertNotNull(vel, "Velocity should not be null");
		}, "Decorator should work with real strategy without exception");
	}
	
	/**
	 * Test Case: TC-DECORATOR-010
	 * Verifies multiple consecutive calls are properly delegated.
	 * 
	 * Scenario:
	 * - Call decideMoveDirection() multiple times
	 * - Verify each call is delegated
	 * 
	 * Expected Result:
	 * - All calls are delegated to wrapped strategy
	 * - Call count matches invocation count
	 */
	@Test
	@DisplayName("Multiple calls should be properly delegated")
	void testMultipleCallsDelegation() {
		// Act
		for (int i = 0; i < 5; i++) {
			decorator.decideMoveDirection();
		}
		
		// Assert
		Mockito.verify(mockStrategy, Mockito.times(5)).decideMoveDirection();
	}
	
	/**
	 * Test Case: TC-DECORATOR-011
	 * Verifies that all interface methods are implemented.
	 * 
	 * Scenario:
	 * - Call all PlayerBehaviorStrategy interface methods
	 * - Verify no method throws UnsupportedOperationException
	 * 
	 * Expected Result:
	 * - All methods can be called successfully
	 * - Interface contract is fully satisfied
	 */
	@Test
	@DisplayName("All interface methods should be implemented")
	void testAllInterfaceMethodsImplemented() {
		// Arrange
		Mockito.when(mockStrategy.shouldBoost()).thenReturn(false);
		Mockito.when(mockStrategy.getVelocity()).thenReturn(new int[]{0, 0});
		
		// Act & Assert
		assertDoesNotThrow(() -> {
			decorator.decideMoveDirection();
			decorator.shouldBoost();
			decorator.reset();
			decorator.getVelocity();
		}, "All interface methods should be callable without exception");
	}
	
	/**
	 * Test Case: TC-DECORATOR-012
	 * Verifies decorator can be used as PlayerBehaviorStrategy.
	 * 
	 * Scenario:
	 * - Assign decorator to PlayerBehaviorStrategy reference
	 * - Call interface methods through that reference
	 * 
	 * Expected Result:
	 * - Decorator is compatible with interface type
	 * - Polymorphism works correctly
	 */
	@Test
	@DisplayName("Decorator should be usable as PlayerBehaviorStrategy")
	void testDecoratorAsInterface() {
		// Arrange
		PlayerBehaviorStrategy strategy = decorator;
		Mockito.when(mockStrategy.getVelocity()).thenReturn(new int[]{1, 1});
		
		// Act
		strategy.decideMoveDirection();
		int[] vel = strategy.getVelocity();
		
		// Assert
		Mockito.verify(mockStrategy, Mockito.times(1)).decideMoveDirection();
		assertNotNull(vel, "Should be able to call through interface reference");
	}
}
