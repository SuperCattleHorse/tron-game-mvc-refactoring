package com.tron.model.game;

/**
 * Human Player Behavior Strategy Implementation
 * 
 * This strategy encapsulates the behavior of human-controlled players.
 * Human players rely on external input (keyboard) handled by the InputController,
 * so this strategy is primarily a pass-through that:
 * - Does not modify velocity (external input controls velocity)
 * - Provides manual boost control
 * - Resets state when a new game starts
 * 
 * This implementation achieves separation of concerns by extracting all
 * behavioral logic into a dedicated strategy class, making the Player class
 * a pure entity and enabling polymorphic behavior switching at runtime.
 * 
 * @author MattBrown
 * @author MattBrown
 * @see PlayerBehaviorStrategy
 */
public class HumanBehaviorStrategy implements PlayerBehaviorStrategy {
	
	private final Player player;
	
	/**
	 * Constructs a HumanBehaviorStrategy for the given player.
	 * @param player the human player this strategy controls
	 */
	public HumanBehaviorStrategy(Player player) {
		this.player = player;
	}
	
	/**
	 * For human players, move direction is controlled by external input (keyboard).
	 * This method does nothing as the velocity is already set by InputController.
	 * The actual velocity changes are handled in GameInputController.
	 */
	@Override
	public void decideMoveDirection() {
		// Human players get velocity from keyboard input
		// No automatic decision-making needed
	}
	
	/**
	 * Determines whether the human player should boost.
	 * For human players, this is handled by external input.
	 * @return false since boosting is handled externally
	 */
	@Override
	public boolean shouldBoost() {
		return false;  // Boost is controlled by external input
	}
	
	/**
	 * Resets the strategy state (no state to reset for human strategy).
	 */
	@Override
	public void reset() {
		// No internal state to reset for human players
	}
	
	/**
	 * Returns the current velocity of the player.
	 * For human players, velocity is controlled by external input.
	 * @return array containing [velocityX, velocityY]
	 */
	@Override
	public int[] getVelocity() {
		return new int[] { player.velocityX, player.velocityY };
	}
	
}
