package com.tron.model.observer;

/**
 * Custom Observer Pattern - Input Observer Interface
 * 
 * This interface defines the contract for observing keyboard input events.
 * It is part of a custom Observer Pattern implementation that decouples
 * input handling from the game model.
 * 
 * Design Pattern: Observer Pattern (Behavioral)
 * - Defines a one-to-many dependency between objects
 * - When one object changes state, all dependent objects are notified
 * - Promotes loose coupling between input source and input handlers
 * 
 * Note: This is a custom implementation. We do NOT use Java's built-in
 * EventListener, KeyListener, or other built-in event listeners as those
 * are pre-made implementations. This custom version provides better control
 * and separation of concerns for the Tron game.
 * 
 * Why Custom Observer Instead of Built-in Listeners:
 * - Greater control over notification mechanism
 * - Custom event types specific to Tron game
 * - Separation of input concerns from view components
 * - Enables input replay and recording features
 * - Better testability and debugging
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public interface InputObserver {
	
	/**
	 * Called when a key is pressed by the player.
	 * 
	 * Observers implementing this interface will be notified of each key press
	 * and can decide how to handle the input (e.g., updating player velocity,
	 * triggering boost, etc.).
	 * 
	 * @param keyCode The KeyEvent virtual key code (e.g., KeyEvent.VK_LEFT)
	 * @param keyChar The character associated with the key
	 * @param isPlayer1 Flag indicating if this is player 1 or player 2 input
	 *                  (true for player 1, false for player 2)
	 */
	void onKeyPressed(int keyCode, char keyChar, boolean isPlayer1);
	
	/**
	 * Called when a key is released by the player.
	 * 
	 * Observers can use this to detect when a directional key is released,
	 * allowing them to implement key-release-specific behaviors.
	 * 
	 * @param keyCode The KeyEvent virtual key code
	 * @param isPlayer1 Flag indicating if this is player 1 or player 2 input
	 */
	void onKeyReleased(int keyCode, boolean isPlayer1);
	
	/**
	 * Gets the name of this input observer for logging purposes.
	 * 
	 * @return String identifier for this observer
	 */
	String getObserverName();
}
