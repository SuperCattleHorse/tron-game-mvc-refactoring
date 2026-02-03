package com.tron.model.game;

/**
 * Strategy Pattern Interface for Player Behavior
 * 
 * This interface defines the contract for different player behavior strategies
 * (AI vs Human). By using this pattern, we achieve:
 * 1. Separation of concerns - behavior logic is decoupled from the Player class
 * 2. Flexibility - different behaviors can be switched at runtime
 * 3. Testability - each strategy can be tested independently
 * 4. Extensibility - new behaviors can be added without modifying existing code
 * 
 * @author MattBrown
 * @author MattBrown
 */
public interface PlayerBehaviorStrategy {
	
	/**
	 * Decides the move direction and velocity for the player based on strategy.
	 * For human players, this is typically no-op (handled by external input).
	 * For AI players, this contains the decision-making logic.
	 */
	void decideMoveDirection();
	
	/**
	 * Determines whether the player should boost in the current state.
	 * @return true if player should boost, false otherwise
	 */
	boolean shouldBoost();
	
	/**
	 * Resets the strategy state if needed (e.g., for AI timers).
	 */
	void reset();
	
	/**
	 * Sets the velocity components for the player's current frame.
	 * This method is called after decideMoveDirection() to get the velocity values.
	 * @return array containing [velocityX, velocityY]
	 */
	int[] getVelocity();
}
