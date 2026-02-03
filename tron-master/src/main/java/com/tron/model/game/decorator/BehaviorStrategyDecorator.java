package com.tron.model.game.decorator;

import com.tron.model.game.PlayerBehaviorStrategy;

/**
 * Abstract base decorator for PlayerBehaviorStrategy that implements the Decorator Pattern.
 * 
 * This abstract class allows dynamic enhancement of player behavior functionality at runtime
 * without modifying the original PlayerBehaviorStrategy implementations (HumanBehaviorStrategy,
 * AIBehaviorStrategy). Each concrete decorator can add specific behavioral features such as:
 * - Logging and debugging information
 * - Performance monitoring
 * - Behavior modification and enhancement
 * - Input validation and filtering
 * 
 * Key benefits:
 * - Open/Closed Principle: strategies can be extended without modification
 * - Composition over Inheritance: complex behavior is built through composition
 * - Single Responsibility: each decorator handles one specific enhancement
 * - Runtime Flexibility: decorators can be applied and removed dynamically
 * - Separation of Concerns: behavior logic remains organized and modular
 * 
 * Architecture:
 * The decorator wraps a PlayerBehaviorStrategy and delegates all method calls to it,
 * allowing each decorator to add behavior before or after the wrapped strategy's execution.
 * Multiple decorators can be stacked for cumulative effects.
 * 
 * Usage Example:
 * {@code
 * PlayerBehaviorStrategy baseStrategy = new AIBehaviorStrategy(aiPlayer);
 * PlayerBehaviorStrategy enhancedStrategy = new LoggingBehaviorDecorator(baseStrategy);
 * }
 * 
 * @author MattBrown
 * @author MattBrown
 * @see PlayerBehaviorStrategy
 * @see com.tron.model.game.AIBehaviorStrategy
 * @see com.tron.model.game.HumanBehaviorStrategy
 */
public abstract class BehaviorStrategyDecorator implements PlayerBehaviorStrategy {
	
	/**
	 * The wrapped PlayerBehaviorStrategy that this decorator enhances.
	 */
	protected PlayerBehaviorStrategy decoratedStrategy;
	
	/**
	 * Constructs a BehaviorStrategyDecorator with the specified strategy to wrap.
	 * 
	 * @param decoratedStrategy the PlayerBehaviorStrategy to wrap with additional behavior
	 * @throws IllegalArgumentException if decoratedStrategy is null
	 */
	public BehaviorStrategyDecorator(PlayerBehaviorStrategy decoratedStrategy) {
		if (decoratedStrategy == null) {
			throw new IllegalArgumentException("Decorated strategy cannot be null");
		}
		this.decoratedStrategy = decoratedStrategy;
	}
	
	/**
	 * Delegates the move direction decision to the wrapped strategy.
	 * Subclasses override this method to add additional behavior
	 * before or after the wrapped strategy's decideMoveDirection.
	 */
	@Override
	public void decideMoveDirection() {
		decoratedStrategy.decideMoveDirection();
	}
	
	/**
	 * Delegates the boost decision to the wrapped strategy.
	 * Subclasses override this method to add additional behavior
	 * before or after the wrapped strategy's shouldBoost.
	 * 
	 * @return the boost decision from the wrapped strategy
	 */
	@Override
	public boolean shouldBoost() {
		return decoratedStrategy.shouldBoost();
	}
	
	/**
	 * Delegates the reset operation to the wrapped strategy.
	 * Subclasses override this method to add additional behavior
	 * before or after the wrapped strategy's reset.
	 */
	@Override
	public void reset() {
		decoratedStrategy.reset();
	}
	
	/**
	 * Delegates the velocity retrieval to the wrapped strategy.
	 * Subclasses override this method to add additional behavior
	 * before or after the wrapped strategy's getVelocity.
	 * 
	 * @return velocity array from the wrapped strategy
	 */
	@Override
	public int[] getVelocity() {
		return decoratedStrategy.getVelocity();
	}
	
	/**
	 * Returns the wrapped strategy.
	 * Useful for introspection and debugging of decorator chains.
	 * 
	 * @return the decorated PlayerBehaviorStrategy
	 */
	public PlayerBehaviorStrategy getDecoratedStrategy() {
		return decoratedStrategy;
	}
}
