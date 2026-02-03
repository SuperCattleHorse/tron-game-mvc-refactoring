package com.tron.model.observer;

/**
 * Subject - Generic Subject Interface for Observer Pattern
 * 
 * This interface defines the contract for the Subject role in the Observer Pattern.
 * Subjects maintain a list of observers and notify them of state changes.
 * 
 * Design Pattern: Observer Pattern (Behavioral)
 * - Defines one-to-many dependency between objects
 * - When subject changes state, all observers are automatically notified
 * - Promotes loose coupling between subject and observers
 * 
 * Custom Implementation:
 * This is a custom Observer Pattern implementation. We do NOT use Java's built-in
 * Observable class or PropertyChangeListener as those are pre-made implementations.
 * This custom version provides:
 * - Better control over notification mechanism
 * - Type-safe observer management
 * - Clear separation of concerns for the Tron game
 * - Better testability and debugging capabilities
 * 
 * Usage:
 * Classes that need to notify observers of state changes should implement this
 * interface. For example:
 * - TronGameModel notifies GameStateObservers
 * - Player notifies PlayerObservers
 * - Score notifies ScoreObservers
 * 
 * Type Parameter:
 * @param <T> The type of observer that this subject will notify.
 *            Must be an observer interface (e.g., GameStateObserver, PlayerObserver)
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public interface Subject<T> {
	
	/**
	 * Attaches an observer to this subject.
	 * 
	 * After attachment, the observer will receive notifications when
	 * this subject's state changes. Duplicate observers are typically
	 * not added (implementation-dependent).
	 * 
	 * @param observer The observer to attach. Should not be null.
	 */
	void attach(T observer);
	
	/**
	 * Detaches an observer from this subject.
	 * 
	 * After detachment, the observer will no longer receive notifications
	 * from this subject. If the observer was not attached, this method
	 * has no effect.
	 * 
	 * @param observer The observer to detach.
	 */
	void detach(T observer);
	
	/**
	 * Notifies all attached observers of a state change.
	 * 
	 * This method is typically called internally by the subject after
	 * its state has changed. The specific notification method called on
	 * each observer depends on the type of state change and the observer
	 * interface implementation.
	 * 
	 * Implementation Note:
	 * Subclasses may have multiple notification methods (e.g., notifyGameOver(),
	 * notifyScoreChanged()) that call different methods on the observers.
	 * This base method represents a general state change notification.
	 */
	void notifyObservers();
}
