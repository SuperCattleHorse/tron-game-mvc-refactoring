package com.tron.model.score;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tron.model.observer.ScoreObserver;
import com.tron.model.observer.Subject;

/**
 * Score - Singleton Pattern + Observer Pattern Implementation
 * 
 * Manages application high scores with persistent storage and observer notifications.
 * Now supports rich high score entries with player information.
 * 
 * Design Patterns:
 * - Singleton Pattern (Creational): Ensures only one Score manager instance exists
 * - Observer Pattern (Behavioral): Notifies observers of score changes
 * 
 * Data Format:
 * - Uses JSON format to store high score entries
 * - Each entry contains: score, nickname, gender, manifesto, date
 * - Automatically migrates legacy plain-text score files
 * 
 * Custom Observer Implementation:
 * This is a custom Observer Pattern implementation. We do NOT use Java's built-in
 * Observable class, PropertyChangeSupport, or JavaFX InvalidationListener as those
 * are pre-made implementations. Benefits:
 * - Type-safe observer management through {@code Subject<ScoreObserver>} interface
 * - Explicit notification methods for different score events
 * - Better control over notification timing
 * - Clear separation between score logic and observer notification
 * - Easier testing with mock observers
 * 
 * Singleton Benefits:
 * - Prevents multiple Score instances from conflicting
 * - Ensures single source of truth for high scores
 * - Simplifies high score access and updates throughout application
 * - Guarantees consistent file I/O operations
 * 
 * Observer Pattern Benefits:
 * - Enables multiple systems to respond to score changes (UI, audio, achievements)
 * - Decouples score management from responsive systems
 * - Allows real-time score updates across multiple views
 * - Facilitates score animations and celebration effects
 * 
 * Responsibilities:
 * - Load high scores from persistent storage (JSON format)
 * - Maintain list of top 10 high scores in memory
 * - Add new scores with player information and maintain sorted order
 * - Persist updated scores to file system
 * - Notify observers of score changes and achievements
 * - Migrate legacy score files to new format
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 4.0 (Enhanced with Player Information)
 */
public class Score implements Subject<ScoreObserver> {
	
	private static Score instance;
	
	// List of high score entries (new format)
	private List<HighScoreEntry> highScoreEntries;
	private String file;
	
	// Gson instance for JSON serialization
	private final Gson gson;
	
	// Observer pattern - list of observers to notify of score changes
	private final List<ScoreObserver> observers = new ArrayList<>();
	
	// Current score tracking for notifications
	private int currentScore = 0;
	
	/**
	 * Private constructor to prevent external instantiation.
	 * This enforces the Singleton pattern.
	 * 
	 * Note: Score should be instantiated via getInstance() method,
	 * typically with a filename parameter set during singleton initialization.
	 * For now, this uses a default filename.
	 */
	private Score() {
		this("HighScores.json");
	}
	
	/**
	 * Private constructor with filename parameter.
	 * Called during singleton initialization to specify the scores file.
	 * 
	 * @param filename The name of the file to load/save scores from
	 */
	private Score(String filename) {
		this.file = filename;
		this.gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
				.create();
		loadHighScores();
	}
	
	/**
	 * Gets the singleton instance of Score.
	 * If no instance exists, a new one is created with default filename (lazy initialization).
	 * 
	 * Thread-safe synchronized method ensures only one instance is created
	 * even in multi-threaded environments.
	 * 
	 * @return The unique Score instance
	 */
	public static synchronized Score getInstance() {
		if (instance == null) {
			instance = new Score();
		}
		return instance;
	}
	
	/**
	 * Gets the singleton instance of Score with a specific filename.
	 * 
	 * Note: This method only applies the filename on first instantiation.
	 * Subsequent calls will return the existing instance with its original filename.
	 * 
	 * @param filename The filename to use for high scores storage
	 * @return The unique Score instance
	 */
	public static synchronized Score getInstance(String filename) {
		if (instance == null) {
			instance = new Score(filename);
		}
		return instance;
	}
	
	// ============ Observer Pattern Methods (Subject<ScoreObserver> Implementation) ============
	
	/**
	 * Attaches an observer to receive score updates.
	 * 
	 * Implements Subject interface. Once attached, the observer will receive
	 * notifications whenever scores change. Duplicate observers are not added
	 * to prevent redundant notifications.
	 * 
	 * @param observer The ScoreObserver to attach. Null observers are ignored.
	 */
	@Override
	public void attach(ScoreObserver observer) {
		if (observer != null && !observers.contains(observer)) {
			observers.add(observer);
		}
	}
	
	/**
	 * Detaches an observer from receiving score updates.
	 * 
	 * Implements Subject interface. After detachment, the observer will no
	 * longer receive notifications. If the observer was not attached, this
	 * method has no effect.
	 * 
	 * @param observer The ScoreObserver to detach
	 */
	@Override
	public void detach(ScoreObserver observer) {
		observers.remove(observer);
	}
	
	/**
	 * Notifies all attached observers of score change.
	 * 
	 * Implements Subject interface. Called when the current score or high score
	 * changes. Calls onScoreChanged() on each registered observer with the
	 * current score and high score values.
	 */
	@Override
	public void notifyObservers() {
		int highScore = highScoreEntries.isEmpty() ? 0 : highScoreEntries.get(0).getScore();
		for (ScoreObserver observer : observers) {
			observer.onScoreChanged(currentScore, highScore);
		}
	}
	
	/**
	 * Notifies observers when a new high score is achieved.
	 * 
	 * Called when the current score exceeds the previous high score.
	 * Observers can use this to display celebration effects.
	 * 
	 * @param newHighScore The new high score value
	 */
	private void notifyHighScoreBeaten(int newHighScore) {
		for (ScoreObserver observer : observers) {
			observer.onHighScoreBeaten(newHighScore);
		}
	}
	
	/**
	 * Notifies observers when a score is added to high scores list.
	 * 
	 * Called when a game ends and the score qualifies for the top 10.
	 * 
	 * @param score The score that was added
	 * @param position The position in the list (0 = highest)
	 */
	private void notifyScoreAddedToHighScores(int score, int position) {
		for (ScoreObserver observer : observers) {
			observer.onScoreAddedToHighScores(score, position);
		}
	}
	
	// ============ Score Management Methods ============
	
	/**
	 * Updates the current score and notifies observers.
	 * 
	 * This method updates the current score being tracked and notifies all
	 * observers. If the score exceeds the high score, additional notification
	 * is sent.
	 * 
	 * @param score The new current score
	 */
	public void updateCurrentScore(int score) {
		int oldHighScore = highScoreEntries.isEmpty() ? 0 : highScoreEntries.get(0).getScore();
		this.currentScore = score;
		
		// Check if new high score
		if (score > oldHighScore) {
			notifyHighScoreBeaten(score);
		}
		
		notifyObservers();
	}
	
	/**
	 * Loads high scores from persistent storage.
	 * Attempts to load JSON format first. If not found or invalid, attempts to
	 * migrate from legacy plain-text format.
	 */
	private void loadHighScores() {
		highScoreEntries = new ArrayList<>();
		
		// Try to load JSON format
		if (loadFromJSON()) {
			return;
		}
		
		// Try to migrate from legacy format
		if (migrateFromLegacyFormat()) {
			saveHighScores(); // Save migrated data
			return;
		}
		
		// No data found, start with empty list
		highScoreEntries = new ArrayList<>();
	}
	
	/**
	 * Load high scores from JSON file
	 * 
	 * @return true if successfully loaded, false otherwise
	 */
	private boolean loadFromJSON() {
		try {
			File jsonFile = getHighScoreFile();
			if (!jsonFile.exists()) {
				return false;
			}
			
			try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
				TypeToken<List<HighScoreEntry>> typeToken = new TypeToken<List<HighScoreEntry>>() {};
				highScoreEntries = gson.fromJson(reader, typeToken.getType());
				
				if (highScoreEntries == null) {
					highScoreEntries = new ArrayList<>();
					return false;
				}
				
				// Sort and validate
				Collections.sort(highScoreEntries);
				if (highScoreEntries.size() > 10) {
					highScoreEntries = new ArrayList<>(highScoreEntries.subList(0, 10));
				}
				
				return true;
			}
		} catch (Exception e) {
			highScoreEntries = new ArrayList<>();
			return false;
		}
	}
	
	/**
	 * Migrate from legacy plain-text format to new JSON format
	 * 
	 * @return true if migration successful, false otherwise
	 */
	private boolean migrateFromLegacyFormat() {
		String legacyFile = file.replace(".json", ".txt");
		
		try {
			List<Integer> legacyScores = new ArrayList<>();
			
			// Try to load from resources first
			InputStream is = Score.class.getResourceAsStream("/" + legacyFile);
			BufferedReader in;
			if (is != null) {
				in = new BufferedReader(new InputStreamReader(is));
			} else {
				// Fallback to file system
				File legacyFileObj = new File("src/main/resources/" + legacyFile);
				if (!legacyFileObj.exists()) {
					legacyFileObj = new File(legacyFile);
				}
				if (!legacyFileObj.exists()) {
					return false;
				}
				in = new BufferedReader(new FileReader(legacyFileObj));
			}
			
			String line = in.readLine();
			while (line != null) {
				String trimmed = line.trim();
				if (!trimmed.isEmpty()) {
					try {
						int score = Integer.parseInt(trimmed);
						if (score >= 0) {
							legacyScores.add(score);
						}
					} catch (NumberFormatException e) {
						// Skip invalid lines
					}
				}
				line = in.readLine();
			}
			in.close();
			
			// Convert to new format
			for (int score : legacyScores) {
				highScoreEntries.add(new HighScoreEntry(score));
			}
			
			// Sort and keep top 10
			Collections.sort(highScoreEntries);
			if (highScoreEntries.size() > 10) {
				highScoreEntries = new ArrayList<>(highScoreEntries.subList(0, 10));
			}
			
			return !highScoreEntries.isEmpty();
			
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Get the high score file handle
	 * 
	 * @return File object for high scores
	 */
	private File getHighScoreFile() {
		File resourceFile = new File("src/main/resources/" + file);
		if (resourceFile.exists() || resourceFile.getParentFile().exists()) {
			return resourceFile;
		}
		return new File(file);
	}
	
	/**
	 * Save high scores to JSON file
	 */
	private void saveHighScores() {
		try {
			File outputFile = getHighScoreFile();
			outputFile.getParentFile().mkdirs();
			
			try (Writer writer = new FileWriter(outputFile)) {
				gson.toJson(highScoreEntries, writer);
			}
		} catch (IOException e) {
			System.err.println("Failed to save high scores: " + e.getMessage());
		}
	}
	
	/**
	 * Adds a new score to the high scores list and notifies observers.
	 * This is a legacy method for backwards compatibility.
	 * 
	 * @param i The score value to add
	 * @throws IOException If an error occurs while writing to file
	 * @throws IllegalArgumentException If score is negative
	 * @deprecated Use addHighScore(HighScoreEntry) instead
	 */
	@Deprecated
	public void addHighScore(int i) throws IOException {
		// Create entry with default values
		HighScoreEntry entry = new HighScoreEntry(i);
		addHighScore(entry);
	}
	
	/**
	 * Adds a new high score entry to the list and notifies observers.
	 * 
	 * This method performs the following operations:
	 * 1. Validates score is non-negative
	 * 2. Adds the new entry to the list
	 * 3. Sorts the list in descending order by score
	 * 4. Keeps only the top 10 scores
	 * 5. Persists the updated list to file
	 * 6. Notifies observers if score qualifies for top 10
	 * 
	 * @param entry The high score entry to add
	 * @throws IOException If an error occurs while writing to file
	 * @throws IllegalArgumentException If score is negative
	 */
	public void addHighScore(HighScoreEntry entry) throws IOException {
		// Validate score is non-negative (natural number)
		if (entry.getScore() < 0) {
			throw new IllegalArgumentException("Score must be a non-negative integer, got: " + entry.getScore());
		}
		
		// Check if score qualifies for high scores before adding
		boolean qualifiesForHighScores = highScoreEntries.size() < 10 || 
				entry.getScore() > highScoreEntries.get(highScoreEntries.size() - 1).getScore();
		
		highScoreEntries.add(entry);
		Collections.sort(highScoreEntries);
		
		// Keep only top 10
		if (highScoreEntries.size() > 10) {
			highScoreEntries = new ArrayList<>(highScoreEntries.subList(0, 10));
		}
		
		// Find position of newly added score
		int position = -1;
		if (qualifiesForHighScores) {
			position = highScoreEntries.indexOf(entry);
		}
		
		// Save to file
		saveHighScores();
		
		// Notify observers if score made it to high scores
		if (qualifiesForHighScores && position >= 0) {
			notifyScoreAddedToHighScores(entry.getScore(), position);
			notifyObservers();
		}
	}

	/**
	 * Returns the list of high scores as integers (for backwards compatibility).
	 * 
	 * @return List of high scores in descending order
	 * @deprecated Use getHighScoreEntries() instead
	 */
	@Deprecated
	public List<Integer> getHighScores() {
		List<Integer> scores = new ArrayList<>();
		for (HighScoreEntry entry : highScoreEntries) {
			scores.add(entry.getScore());
		}
 		return scores;
	}
	
	/**
	 * Returns the list of high score entries.
	 * 
	 * @return List of high score entries in descending order by score
	 */
	public List<HighScoreEntry> getHighScoreEntries() {
		return new ArrayList<>(highScoreEntries);
	}
	
	/**
	 * Check if a score qualifies for the high score list
	 * 
	 * @param score The score to check
	 * @return true if the score qualifies for top 10
	 */
	public boolean isHighScore(int score) {
		return highScoreEntries.size() < 10 || 
			   score > highScoreEntries.get(highScoreEntries.size() - 1).getScore();
	}
}

