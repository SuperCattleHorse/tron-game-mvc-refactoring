package com.tron.model.observer;

/**
 * ScoreObserver - Observer Interface for Score Changes
 * 
 * This interface defines the contract for observing score system changes.
 * It is part of a custom Observer Pattern implementation that decouples score
 * management from the view and other game systems.
 * 
 * Design Pattern: Observer Pattern (Behavioral)
 * - Defines a one-to-many dependency between Score and its observers
 * - When Score changes, all dependent observers are notified
 * - Promotes loose coupling between score logic and UI/effects
 * 
 * Custom Implementation:
 * This is a custom Observer Pattern implementation. We do NOT use Java's built-in
 * EventListener, PropertyChangeListener, or JavaFX InvalidationListener as those
 * are pre-made implementations. This custom version provides:
 * - Greater control over notification mechanism
 * - Custom event types specific to Tron game scoring
 * - Separation of score concerns from view components
 * - Better testability and debugging
 * 
 * Why Custom Observer for Scores:
 * - Enables multiple systems to respond to score events (UI, audio, achievements)
 * - Allows score updates without tight coupling to score manager
 * - Facilitates implementation of score animations and effects
 * - Supports score tracking and statistics features
 * 
 * Use Cases:
 * - TronGameView implements this to update score display
 * - AudioSystem implements this to play score milestone sounds
 * - AchievementSystem implements this to award achievements
 * - GameController implements this to save high scores
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public interface ScoreObserver {
	
	/**
	 * Called when the current score changes.
	 * 
	 * This method is invoked whenever the player's current score is updated
	 * or when viewing different score information. Observers can use this to:
	 * - Update score display on screen
	 * - Animate score changes
	 * - Update score history graphs
	 * 
	 * @param currentScore The current score value
	 * @param highScore The current high score value
	 */
	void onScoreChanged(int currentScore, int highScore);
	
	/**
	 * Called when a new high score is achieved.
	 * 
	 * This method is invoked when the current score exceeds the previous
	 * high score. Observers can use this to:
	 * - Display congratulations message
	 * - Play celebration sound/music
	 * - Trigger visual effects (fireworks, particles)
	 * - Save high score to persistent storage
	 * - Award achievements
	 * 
	 * @param newHighScore The new high score value
	 */
	void onHighScoreBeaten(int newHighScore);
	
	/**
	 * Called when a new score is added to the high scores list.
	 * 
	 * This method is invoked when a game ends and the score qualifies for
	 * the top 10 high scores list. Observers can use this to:
	 * - Update high scores display
	 * - Highlight the new entry in the list
	 * - Play achievement sound
	 * 
	 * Default implementation does nothing, allowing observers to implement
	 * only the methods they need.
	 * 
	 * @param score The score that was added to high scores
	 * @param position The position in the high scores list (0 = highest)
	 */
	default void onScoreAddedToHighScores(int score, int position) {
		// Default implementation does nothing
	}
}
