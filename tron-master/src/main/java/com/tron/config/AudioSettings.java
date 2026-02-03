package com.tron.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * AudioSettings - Singleton configuration manager for audio preferences
 * 
 * Manages global audio settings including background music (BGM) and sound effects.
 * Provides persistence to maintain user preferences across sessions.
 * 
 * Design Patterns:
 * - Singleton Pattern (Creational): Ensures single source of truth for audio settings
 * 
 * SOLID Principles:
 * - Single Responsibility: Only manages audio configuration
 * - Open/Closed: Can add new audio options without modifying existing code
 * 
 * Thread Safety:
 * - Synchronized getInstance() for thread-safe lazy initialization
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class AudioSettings {
    
    private static AudioSettings instance;
    private static final Object LOCK = new Object();
    
    // Configuration file path
    private static final String CONFIG_FILE = "config/audio_settings.properties";
    private static final String BGM_KEY = "bgmEnabled";
    private static final String SFX_KEY = "soundEffectsEnabled";
    
    // Audio preferences
    private boolean bgmEnabled;
    private boolean soundEffectsEnabled;
    
    /**
     * Private constructor enforces Singleton pattern
     */
    private AudioSettings() {
        // Default: both enabled
        this.bgmEnabled = true;
        this.soundEffectsEnabled = true;
        loadConfiguration();
    }
    
    /**
     * Get the singleton instance with thread-safe lazy initialization
     * 
     * @return The unique AudioSettings instance
     */
    public static AudioSettings getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new AudioSettings();
                }
            }
        }
        return instance;
    }
    
    /**
     * Check if background music is enabled
     * 
     * @return true if BGM should play, false otherwise
     */
    public boolean isBgmEnabled() {
        return bgmEnabled;
    }
    
    /**
     * Set background music enabled state
     * 
     * @param enabled true to enable BGM, false to disable
     */
    public void setBgmEnabled(boolean enabled) {
        if (this.bgmEnabled != enabled) {
            this.bgmEnabled = enabled;
            saveConfiguration();
        }
    }
    
    /**
     * Check if sound effects are enabled
     * 
     * @return true if sound effects should play, false otherwise
     */
    public boolean isSoundEffectsEnabled() {
        return soundEffectsEnabled;
    }
    
    /**
     * Set sound effects enabled state
     * 
     * @param enabled true to enable sound effects, false to disable
     */
    public void setSoundEffectsEnabled(boolean enabled) {
        if (this.soundEffectsEnabled != enabled) {
            this.soundEffectsEnabled = enabled;
            saveConfiguration();
        }
    }
    
    /**
     * Load configuration from file
     */
    private void loadConfiguration() {
        try {
            Path configPath = Paths.get(CONFIG_FILE);
            if (Files.exists(configPath)) {
                Properties props = new Properties();
                try (InputStream input = Files.newInputStream(configPath)) {
                    props.load(input);
                    
                    String bgmValue = props.getProperty(BGM_KEY);
                    if (bgmValue != null) {
                        this.bgmEnabled = Boolean.parseBoolean(bgmValue);
                    }
                    
                    String sfxValue = props.getProperty(SFX_KEY);
                    if (sfxValue != null) {
                        this.soundEffectsEnabled = Boolean.parseBoolean(sfxValue);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load audio settings: " + e.getMessage());
            // Use default values on error
        }
    }
    
    /**
     * Save configuration to file
     */
    private void saveConfiguration() {
        try {
            // Ensure config directory exists
            Path configPath = Paths.get(CONFIG_FILE);
            Path configDir = configPath.getParent();
            if (configDir != null && !Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }
            
            Properties props = new Properties();
            props.setProperty(BGM_KEY, String.valueOf(bgmEnabled));
            props.setProperty(SFX_KEY, String.valueOf(soundEffectsEnabled));
            
            try (OutputStream output = Files.newOutputStream(configPath)) {
                props.store(output, "Tron Game Audio Settings");
            }
        } catch (IOException e) {
            System.err.println("Failed to save audio settings: " + e.getMessage());
        }
    }
    
    /**
     * Reset all settings to defaults
     */
    public void resetToDefaults() {
        this.bgmEnabled = true;
        this.soundEffectsEnabled = true;
        saveConfiguration();
    }
}
