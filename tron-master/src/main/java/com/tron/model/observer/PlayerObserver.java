package com.tron.model.observer;

import com.tron.model.game.GameObject;
import com.tron.model.game.Player;

/**
 * PlayerObserver - Observer Interface for Player State Changes
 * 
 * This interface defines the contract for observing individual player state changes.
 * It is part of a custom Observer Pattern implementation that decouples player state
 * management from the view and other game systems.
 * 
 * Design Pattern: Observer Pattern (Behavioral)
 * - Defines a one-to-many dependency between Player and its observers
 * - When a Player changes state, all dependent observers are notified
 * - Promotes loose coupling between player logic and responsive systems
 * 
 * Custom Implementation:
 * This is a custom Observer Pattern implementation. We do NOT use Java's built-in
 * EventListener, PropertyChangeListener, or JavaFX InvalidationListener as those
 * are pre-made implementations. This custom version provides:
 * - Greater control over notification mechanism
 * - Custom event types specific to Tron game players
 * - Separation of player concerns from view components
 * - Better testability and debugging
 * 
 * Why Custom Observer for Players:
 * - Enables multiple systems to respond to player events (view, audio, effects)
 * - Allows player state to be observed without tight coupling
 * - Facilitates implementation of death animations, collision effects
 * - Supports replay and spectator features
 * 
 * Use Cases:
 * - TronGameView implements this to update player rendering
 * - AudioSystem implements this to play player sounds
 * - ParticleSystem implements this to spawn collision effects
 * - GameController implements this to track player statistics
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public interface PlayerObserver {
	
	/**
	 * Called when a player's state changes (position, velocity, etc.).
	 * 
	 * This method is invoked after the player has moved or its state has been
	 * updated in any way. Observers can use this to update their representation
	 * of the player (e.g., redraw the player on screen).
	 * 
	 * @param player The player whose state has changed
	 */
	void onPlayerStateChanged(Player player);
	
	/**
	 * Called when a player dies (crashes into wall or another player).
	 * 
	 * This method is invoked immediately when the player's alive status changes
	 * to false. Observers can use this to:
	 * - Display death animation
	 * - Play crash sound effect
	 * - Update game state (e.g., check if game is over)
	 * - Record player statistics
	 * 
	 * @param player The player that died
	 */
	void onPlayerDied(Player player);
	
	/**
	 * Called when a player collides with another game object.
	 * 
	 * This method is invoked during collision detection, before the player
	 * potentially dies. Observers can use this to:
	 * - Display collision effects
	 * - Play collision sound
	 * - Implement collision-based mechanics (e.g., damage, bounce)
	 * 
	 * @param player The player that collided
	 * @param other The game object that the player collided with
	 */
	void onPlayerCollision(Player player, GameObject other);
	
	/**
	 * Called when a player changes direction (velocity changes).
	 * 
	 * This method is invoked when the player's velocity vector changes,
	 * typically due to player input or AI decision. Observers can use this to:
	 * - Add a new trail segment
	 * - Play direction change sound
	 * - Update movement indicators
	 * 
	 * Default implementation does nothing, allowing observers to implement
	 * only the methods they need.
	 * 
	 * @param player The player that changed direction
	 * @param newVelocityX The new X velocity
	 * @param newVelocityY The new Y velocity
	 */
	default void onPlayerDirectionChanged(Player player, int newVelocityX, int newVelocityY) {
		// Default implementation does nothing
	}
	
	/**
	 * Called when a player activates boost.
	 * 
	 * This method is invoked when the player uses one of their boost charges.
	 * Observers can use this to:
	 * - Display boost visual effect
	 * - Play boost sound
	 * - Update boost count display
	 * 
	 * Default implementation does nothing, allowing observers to implement
	 * only the methods they need.
	 * 
	 * @param player The player that activated boost
	 * @param boostCountRemaining The number of boosts remaining
	 */
	default void onPlayerBoostActivated(Player player, int boostCountRemaining) {
		// Default implementation does nothing
	}
}
