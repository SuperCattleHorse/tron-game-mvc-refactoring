package com.tron.model.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tron.config.GameSettings;
import com.tron.model.score.HighScoreEntry;
import com.tron.model.score.Score;
import com.tron.model.util.MapConfig;
import com.tron.view.fx.FXPlayerInfoDialog;

import javafx.application.Platform;

/**
 * SurvivalGameModel - Survival mode game model
 * 
 * Responsibilities:
 * - Manage single-player survival mode game logic
 * - Handle high score records
 * - Inherit base game model functionality
 * 
 * Game Rules:
 * - Single player against AI
 * - Game ends when player dies
 * - Scores recorded to high score board
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 2.0 (MVC Complete)
 */
public class SurvivalGameModel extends TronGameModel {
    
    private final Score highScoreManager;
    private List<Integer> highScores;
    private boolean scoreSaved = false; // Flag to prevent duplicate saves
    private MapConfig mapConfig; // Not final - needs to reload on reset
    
    /**
     * Constructor for Survival mode
     * 
     * @param highScoreFile Path to high score file
     * @param playerCount Number of AI players (excluding human player)
     */
    public SurvivalGameModel(String highScoreFile, int playerCount) {
        super(500, 500, 3, playerCount);
        
        // Initialize map configuration from settings
        GameSettings gameSettings = GameSettings.getInstance();
        this.mapConfig = MapConfig.createMap(gameSettings.getSelectedMapType());
        
        this.highScoreManager = Score.getInstance(highScoreFile);
        this.highScores = new ArrayList<>();
        this.scoreSaved = false;
        loadHighScores();
    }
    
    /**
     * Load high scores from file
     */
    private void loadHighScores() {
        try {
            this.highScores = highScoreManager.getHighScores();
        } catch (Exception e) {
            this.highScores = new ArrayList<>();
        }
    }
    
    /**
     * Override start to apply map configuration to all players
     */
    @Override
    public void start() {
        super.start();
        
        // Apply map configuration to all players
        if (player != null) {
            player.setMapConfig(mapConfig);
        }
        for (Player p : players) {
            if (p != null) {
                p.setMapConfig(mapConfig);
            }
        }
    }
    
    /**
     * Override tick to check if player is alive
     */
    @Override
    public void tick() {
        boolean runningBeforeTick = isRunning;
        super.tick();
        
        // If the run ended during this tick, persist the score once
        if (runningBeforeTick && player != null && !player.getAlive() && !scoreSaved) {
            saveScore();
            scoreSaved = true;
        }
    }
    
    /**
     * Save current score to high scores
     * If score qualifies, show dialog to collect player information
     */
    public void saveScore() {
        // Prevent duplicate saves
        if (scoreSaved) {
            return;
        }
        
        // Check if score qualifies for high score list
        if (!highScoreManager.isHighScore(currentScore)) {
            scoreSaved = true;
            return;
        }
        
        // Score qualifies - show dialog on JavaFX thread
        Platform.runLater(() -> {
            FXPlayerInfoDialog dialog = new FXPlayerInfoDialog(currentScore);
            Optional<HighScoreEntry> result = dialog.showAndGetResult();
            
            if (result.isPresent()) {
                try {
                    highScoreManager.addHighScore(result.get());
                    loadHighScores(); // Reload to get updated list
                } catch (IOException e) {
                    System.err.println("Failed to save high score: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid score: " + e.getMessage());
                }
            }
        });
        
        scoreSaved = true;
    }
    
    /**
     * Get the list of high scores
     * 
     * @return List of top scores
     */
    public List<Integer> getHighScores() {
        return new ArrayList<>(highScores);
    }
    
    /**
     * Get the high score manager
     * 
     * @return Score manager instance
     */
    public Score getHighScoreManager() {
        return highScoreManager;
    }
    
    /**
     * Override reset to clear score saved flag and reload map config
     */
    @Override
    public void reset() {
        super.reset();
        scoreSaved = false; // Reset flag for next game
        loadHighScores();
        
        // Reload map configuration from current settings (in case user changed it in Options)
        GameSettings gameSettings = GameSettings.getInstance();
        MapConfig newMapConfig = MapConfig.createMap(gameSettings.getSelectedMapType());
        this.mapConfig = newMapConfig;
        
        // Apply map configuration to all newly created players
        if (player != null) {
            player.setMapConfig(mapConfig);
        }
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null) {
                players[i].setMapConfig(mapConfig);
            }
        }
    }
    
    /**
     * Get the map configuration for rendering obstacles
     * 
     * @return Current map configuration
     */
    public MapConfig getMapConfig() {
        return mapConfig;
    }
}
