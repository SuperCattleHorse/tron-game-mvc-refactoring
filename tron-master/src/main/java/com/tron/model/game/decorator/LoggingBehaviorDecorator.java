package com.tron.model.game.decorator;

import com.tron.model.game.PlayerBehaviorStrategy;

/**
 * Concrete decorator that adds logging and monitoring capabilities to player behavior strategies.
 * 
 * This decorator enhances player behavior by recording all behavior decisions and state changes,
 * enabling debugging and analysis without modifying the original strategy implementation.
 * It logs:
 * - Move direction decisions
 * - Boost activation events
 * - Velocity values
 * - Strategy reset events
 * 
 * Benefits of using this decorator:
 * - Original strategy logic remains unchanged and unaware of logging
 * - Logging can be dynamically added or removed at runtime
 * - Multiple decorators can be stacked for combined effects
 * - Easy to extend with additional monitoring capabilities
 * - Separates debugging concerns from core behavior logic
 * - Helps identify behavior issues and AI decision patterns
 * 
 * Usage:
 * {@code
 * PlayerBehaviorStrategy baseStrategy = new AIBehaviorStrategy(aiPlayer);
 * PlayerBehaviorStrategy monitoredStrategy = new LoggingBehaviorDecorator(baseStrategy);
 * }
 * 
 * Thread Safety:
 * This decorator is not thread-safe. If used in multi-threaded contexts,
 * synchronization should be added to the logging methods.
 * 
 * @author MattBrown
 * @author MattBrown
 * @see BehaviorStrategyDecorator
 * @see PlayerBehaviorStrategy
 */
public class LoggingBehaviorDecorator extends BehaviorStrategyDecorator {
	
	private static final boolean LOGGING_ENABLED = false;
	
	private int moveDecisionCount = 0;
	private int boostDecisionCount = 0;
	private int resetCount = 0;
	
	/**
	 * Constructs a LoggingBehaviorDecorator for the given behavior strategy.
	 * 
	 * @param decoratedStrategy the PlayerBehaviorStrategy to wrap with logging
	 * @throws IllegalArgumentException if decoratedStrategy is null
	 */
	public LoggingBehaviorDecorator(PlayerBehaviorStrategy decoratedStrategy) {
		super(decoratedStrategy);
	}
	
	/**
	 * Logs the move direction decision and delegates to the wrapped strategy.
	 * 
	 * Records the decision event and logs relevant information if logging is enabled,
	 * then calls the wrapped strategy's decideMoveDirection method.
	 */
	@Override
	public void decideMoveDirection() {
		moveDecisionCount++;
		
		if (LOGGING_ENABLED) {
			log("Move Decision #" + moveDecisionCount + " - Delegating to: " + 
				decoratedStrategy.getClass().getSimpleName());
		}
		
		// Delegate to wrapped strategy
		decoratedStrategy.decideMoveDirection();
		
		if (LOGGING_ENABLED) {
			int[] velocity = decoratedStrategy.getVelocity();
			log("Move Decision #" + moveDecisionCount + " - Result velocity: [" + 
				velocity[0] + ", " + velocity[1] + "]");
		}
	}
	
	/**
	 * Logs the boost decision and delegates to the wrapped strategy.
	 * 
	 * Records whether the strategy decides to boost and logs the decision,
	 * then returns the wrapped strategy's boost decision.
	 * 
	 * @return true if the wrapped strategy decides to boost, false otherwise
	 */
	@Override
	public boolean shouldBoost() {
		boolean shouldBoost = decoratedStrategy.shouldBoost();
		boostDecisionCount++;
		
		if (LOGGING_ENABLED) {
			log("Boost Decision #" + boostDecisionCount + " - From: " + 
				decoratedStrategy.getClass().getSimpleName() + 
				" - Result: " + (shouldBoost ? "BOOST" : "NO BOOST"));
		}
		
		return shouldBoost;
	}
	
	/**
	 * Logs the reset operation and delegates to the wrapped strategy.
	 * 
	 * Records the reset event and logs the reset call to the wrapped strategy.
	 */
	@Override
	public void reset() {
		resetCount++;
		
		if (LOGGING_ENABLED) {
			log("Reset #" + resetCount + " - Resetting: " + 
				decoratedStrategy.getClass().getSimpleName());
		}
		
		// Delegate to wrapped strategy
		decoratedStrategy.reset();
	}
	
	/**
	 * Returns the current velocity and logs the call if logging is enabled.
	 * 
	 * @return velocity array [velocityX, velocityY] from the wrapped strategy
	 */
	@Override
	public int[] getVelocity() {
		int[] velocity = decoratedStrategy.getVelocity();
		
		if (LOGGING_ENABLED) {
			log("Getting velocity from " + decoratedStrategy.getClass().getSimpleName() + 
				": [" + velocity[0] + ", " + velocity[1] + "]");
		}
		
		return velocity;
	}
	
	/**
	 * Logs a message with timestamp and strategy information.
	 * This is a utility method for internal logging.
	 * 
	 * @param message the message to log
	 */
	@SuppressWarnings("all")
	private void log(String message) {
		String timestamp = String.format("[%d]", System.currentTimeMillis());
		System.out.println(timestamp + " " + this.getClass().getSimpleName() + ": " + message);
	}
	
	/**
	 * Returns the total number of move decisions recorded.
	 * Useful for testing and statistics.
	 * 
	 * @return total move decision count
	 */
	public int getMoveDecisionCount() {
		return moveDecisionCount;
	}
	
	/**
	 * Returns the total number of boost decisions recorded.
	 * Useful for testing and statistics.
	 * 
	 * @return total boost decision count
	 */
	public int getBoostDecisionCount() {
		return boostDecisionCount;
	}
	
	/**
	 * Returns the total number of reset operations recorded.
	 * Useful for testing and statistics.
	 * 
	 * @return total reset count
	 */
	public int getResetCount() {
		return resetCount;
	}
	
	/**
	 * Resets all logging counters.
	 * Useful for resetting statistics between game sessions.
	 */
	public void resetCounters() {
		moveDecisionCount = 0;
		boostDecisionCount = 0;
		resetCount = 0;
	}
}
