package com.tron.model.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Input Subject - Observer Pattern with Singleton Implementation
 * 
 * This class acts as the Subject in the Observer Pattern. It maintains a list
 * of observers (InputObserver instances) and notifies them when keyboard input
 * events occur.
 * 
 * Design Patterns:
 * - Observer Pattern (Behavioral): Subject-Observer relationship for input events
 * - Singleton Pattern (Creational): Ensures only one global input dispatcher
 * 
 * Singleton Benefits:
 * - Guarantees single centralized input event dispatcher
 * - Prevents conflicting input handling from multiple instances
 * - Simplifies observer registration/unregistration globally
 * - Ensures consistent event propagation throughout the application
 * 
 * Observer Pattern Benefits:
 * - Decouples input source from input handlers
 * - Allows multiple handlers to respond to same input
 * - Easy to add new input handlers without modifying existing code
 * - Testable: observers can be easily mocked
 * 
 * Responsibilities:
 * - Register and unregister observers
 * - Notify all observers when input events occur
 * - Maintain the list of interested observers
 * - Provide global access via singleton getInstance() method
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 2.0 (Singleton + Observer)
 */
public class InputSubject {
	
	private static InputSubject instance;
	private List<InputObserver> observers;
	
	/**
	 * Private constructor to prevent external instantiation.
	 * This enforces the Singleton pattern.
	 * 
	 * Initializes the observer list.
	 */
	private InputSubject() {
		this.observers = new ArrayList<>();
	}
	
	/**
	 * Gets the singleton instance of InputSubject.
	 * If no instance exists, a new one is created (lazy initialization).
	 * 
	 * Thread-safe synchronized method ensures only one instance is created
	 * even in multi-threaded environments.
	 * 
	 * @return The unique InputSubject instance
	 */
	public static synchronized InputSubject getInstance() {
		if (instance == null) {
			instance = new InputSubject();
		}
		return instance;
	}
	
	/**
	 * Registers an observer to receive input events.
	 * 
	 * When a keyboard input occurs, this observer will be notified
	 * via onKeyPressed() or onKeyReleased() methods.
	 * 
	 * @param observer The InputObserver to register
	 */
	public void addObserver(InputObserver observer) {
		if (observer != null && !observers.contains(observer)) {
			observers.add(observer);
		}
	}
	
	/**
	 * Unregisters an observer from receiving input events.
	 * 
	 * The observer will no longer be notified of keyboard input.
	 * 
	 * @param observer The InputObserver to unregister
	 */
	public void removeObserver(InputObserver observer) {
		observers.remove(observer);
	}
	
	/**
	 * Removes all observers from the subject.
	 * 
	 * Called when cleanup is needed or when changing game modes.
	 */
	public void clearObservers() {
		observers.clear();
	}
	
	/**
	 * Notifies all observers that a key has been pressed.
	 * 
	 * This method is called by the input event source (typically a KeyListener)
	 * to propagate key press events to all registered observers.
	 * 
	 * @param keyCode The virtual key code of the pressed key
	 * @param keyChar The character associated with the key
	 * @param isPlayer1 Flag indicating which player's input this is
	 */
	public void notifyKeyPressed(int keyCode, char keyChar, boolean isPlayer1) {
		for (InputObserver observer : observers) {
			observer.onKeyPressed(keyCode, keyChar, isPlayer1);
		}
	}
	
	/**
	 * Notifies all observers that a key has been released.
	 * 
	 * This method is called by the input event source to propagate
	 * key release events to all registered observers.
	 * 
	 * @param keyCode The virtual key code of the released key
	 * @param isPlayer1 Flag indicating which player's input this is
	 */
	public void notifyKeyReleased(int keyCode, boolean isPlayer1) {
		for (InputObserver observer : observers) {
			observer.onKeyReleased(keyCode, isPlayer1);
		}
	}
	
	/**
	 * Gets the count of registered observers.
	 * 
	 * Useful for debugging and testing.
	 * 
	 * @return Number of currently registered observers
	 */
	public int getObserverCount() {
		return observers.size();
	}
}
