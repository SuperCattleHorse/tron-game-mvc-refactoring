package com.tron.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import com.tron.model.util.MapType;

/**
 * GameSettings - Singleton configuration manager for gameplay preferences
 * 
 * Manages gameplay-related settings including AI difficulty options.
 * Provides persistence to maintain user preferences across sessions.
 * 
 * Features:
 * - Hard AI toggle for Story Mode (default: disabled)
 * - Persistent configuration storage
 * - Thread-safe singleton access
 * 
 * Design Patterns:
 * - Singleton Pattern (Creational): Ensures single source of truth for game settings
 * 
 * SOLID Principles:
 * - Single Responsibility: Only manages gameplay configuration
 * - Open/Closed: Can add new gameplay options without modifying existing code
 * 
 * Thread Safety:
 * - Synchronized getInstance() for thread-safe lazy initialization
 * 
 * Usage:
 * <pre>
 * GameSettings settings = GameSettings.getInstance();
 * if (settings.isHardAIEnabled()) {
 *     // Use Hard AI behavior strategy
 * }
 * </pre>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see AudioSettings
 * @see BackgroundColorSettings
 */
public class GameSettings {
    
    private static GameSettings instance;
    private static final Object LOCK = new Object();
    
    // Configuration file path
    private static final String CONFIG_FILE = "config/game_settings.properties";
    private static final String HARD_AI_KEY = "hardAIEnabled";
    private static final String MAP_TYPE_KEY = "mapType";
    
    // Gameplay preferences
    private boolean hardAIEnabled;
    private MapType selectedMapType;
    
    /**
     * Private constructor enforces Singleton pattern.
     * Initializes default settings and loads persisted configuration.
     */
    private GameSettings() {
        // Default: Hard AI disabled
        this.hardAIEnabled = false;
        this.selectedMapType = MapType.DEFAULT;
        loadConfiguration();
    }
    
    /**
     * Get the singleton instance with thread-safe lazy initialization.
     * 
     * @return The unique GameSettings instance
     */
    public static GameSettings getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new GameSettings();
                }
            }
        }
        return instance;
    }
    
    /**
     * Check if Hard AI is enabled for Story Mode.
     * 
     * @return true if Hard AI should be used, false for normal AI
     */
    public boolean isHardAIEnabled() {
        return hardAIEnabled;
    }
    
    /**
     * Set Hard AI enabled state for Story Mode.
     * Automatically persists the change to disk.
     * 
     * @param enabled true to enable Hard AI, false for normal AI
     */
    public void setHardAIEnabled(boolean enabled) {
        this.hardAIEnabled = enabled;
        saveConfiguration();
    }
    
    /**
     * Get the selected map type for Survival mode.
     * 
     * @return The currently selected MapType
     */
    public MapType getSelectedMapType() {
        return selectedMapType;
    }
    
    /**
     * Set the map type for Survival mode.
     * Automatically persists the change to disk.
     * 
     * @param mapType The map type to use
     */
    public void setSelectedMapType(MapType mapType) {
        this.selectedMapType = mapType;
        saveConfiguration();
    }
    
    /**
     * Load configuration from properties file.
     * Creates default configuration if file doesn't exist.
     */
    private void loadConfiguration() {
        Path configPath = Paths.get(CONFIG_FILE);
        
        if (!Files.exists(configPath)) {
            // Create default configuration
            saveConfiguration();
            return;
        }
        
        Properties props = new Properties();
        try (InputStream input = Files.newInputStream(configPath)) {
            props.load(input);
            hardAIEnabled = Boolean.parseBoolean(props.getProperty(HARD_AI_KEY, "false"));
            
            // Load map type
            String mapTypeName = props.getProperty(MAP_TYPE_KEY, "DEFAULT");
            try {
                selectedMapType = MapType.valueOf(mapTypeName);
            } catch (IllegalArgumentException e) {
                selectedMapType = MapType.DEFAULT;
            }
        } catch (IOException e) {
            System.err.println("Failed to load game settings: " + e.getMessage());
            // Keep default values on error
        }
    }
    
    /**
     * Save current configuration to properties file.
     * Creates config directory if it doesn't exist.
     */
    private void saveConfiguration() {
        Path configPath = Paths.get(CONFIG_FILE);
        
        try {
            // Ensure config directory exists
            Files.createDirectories(configPath.getParent());
            
            Properties props = new Properties();
            props.setProperty(HARD_AI_KEY, String.valueOf(hardAIEnabled));
            props.setProperty(MAP_TYPE_KEY, selectedMapType.name());
            
            try (OutputStream output = Files.newOutputStream(configPath)) {
                props.store(output, "Tron Game Settings - Gameplay Configuration");
            }
        } catch (IOException e) {
            System.err.println("Failed to save game settings: " + e.getMessage());
        }
    }
    
    /**
     * Reset all settings to default values.
     * Useful for testing or user-requested reset.
     */
    public void resetToDefaults() {
        hardAIEnabled = false;
        selectedMapType = MapType.DEFAULT;
        saveConfiguration();
    }
}
