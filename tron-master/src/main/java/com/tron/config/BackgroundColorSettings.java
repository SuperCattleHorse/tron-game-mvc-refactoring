package com.tron.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * BackgroundColorSettings - Singleton configuration manager for background color
 * 
 * Manages the global background color setting for all game modes.
 * Ensures all game views use the same background color and provides
 * persistence to maintain user preferences across sessions.
 * 
 * Design Patterns:
 * - Singleton Pattern (Creational): Ensures single source of truth for background color
 * - Observer Pattern (Behavioral): Notifies listeners when color changes
 * 
 * SOLID Principles:
 * - Single Responsibility: Only manages background color configuration
 * - Open/Closed: Can add new color options without modifying existing code
 * - Dependency Inversion: Uses listener interface for loose coupling
 * 
 * Thread Safety:
 * - Synchronized getInstance() for thread-safe lazy initialization
 * - CopyOnWriteArrayList for thread-safe listener management
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class BackgroundColorSettings {
    
    private static BackgroundColorSettings instance;
    private static final Object LOCK = new Object();
    
    // Configuration file path
    private static final String CONFIG_FILE = "config/background_color.properties";
    private static final String COLOR_KEY = "backgroundColor";
    
    // Available colors (excluding player colors: cyan, red, orange, yellow, lime, magenta)
    public static final String COLOR_BLACK = "black";
    public static final String COLOR_DARK_GRAY = "#2a2a2a";
    public static final String COLOR_NAVY = "#000080";
    public static final String COLOR_DARK_GREEN = "#006400";
    public static final String COLOR_MAROON = "#800000";
    public static final String COLOR_PURPLE = "#800080";
    public static final String COLOR_TEAL = "#008080";
    public static final String COLOR_OLIVE = "#808000";
    
    private static final List<String> AVAILABLE_COLORS = Arrays.asList(
        COLOR_BLACK,
        COLOR_DARK_GRAY,
        COLOR_NAVY,
        COLOR_DARK_GREEN,
        COLOR_MAROON,
        COLOR_PURPLE,
        COLOR_TEAL,
        COLOR_OLIVE
    );
    
    private String currentColor;
    private final List<BackgroundColorChangeListener> listeners;
    
    /**
     * Private constructor enforces Singleton pattern
     */
    private BackgroundColorSettings() {
        this.listeners = new CopyOnWriteArrayList<>();
        this.currentColor = COLOR_BLACK; // Default color
        loadConfiguration();
    }
    
    /**
     * Get the singleton instance with thread-safe lazy initialization
     * 
     * @return The unique BackgroundColorSettings instance
     */
    public static BackgroundColorSettings getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new BackgroundColorSettings();
                }
            }
        }
        return instance;
    }
    
    /**
     * Get the current background color
     * 
     * @return Current background color in CSS format
     */
    public String getCurrentColor() {
        return currentColor;
    }
    
    /**
     * Set the background color and notify all listeners
     * 
     * @param color New background color (must be from available colors)
     * @throws IllegalArgumentException if color is not in available colors list
     */
    public void setBackgroundColor(String color) {
        if (!AVAILABLE_COLORS.contains(color)) {
            throw new IllegalArgumentException("Invalid color: " + color + 
                ". Must be one of: " + AVAILABLE_COLORS);
        }
        
        if (!this.currentColor.equals(color)) {
            this.currentColor = color;
            saveConfiguration();
            notifyListeners();
        }
    }
    
    /**
     * Get list of all available background colors
     * 
     * @return Unmodifiable list of available colors
     */
    public List<String> getAvailableColors() {
        return Collections.unmodifiableList(AVAILABLE_COLORS);
    }
    
    /**
     * Register a listener to be notified of color changes
     * 
     * @param listener The listener to register
     */
    public void addListener(BackgroundColorChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Unregister a listener
     * 
     * @param listener The listener to remove
     */
    public void removeListener(BackgroundColorChangeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify all registered listeners of color change
     */
    private void notifyListeners() {
        for (BackgroundColorChangeListener listener : listeners) {
            try {
                listener.onBackgroundColorChanged(currentColor);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
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
                    String savedColor = props.getProperty(COLOR_KEY);
                    if (savedColor != null && AVAILABLE_COLORS.contains(savedColor)) {
                        this.currentColor = savedColor;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load background color configuration: " + e.getMessage());
            // Use default color on error
        }
    }
    
    /**
     * Save configuration to file
     */
    private void saveConfiguration() {
        try {
            Properties props = new Properties();
            props.setProperty(COLOR_KEY, currentColor);
            
            try (OutputStream output = Files.newOutputStream(Paths.get(CONFIG_FILE))) {
                props.store(output, "Tron Game Background Color Configuration");
            }
        } catch (IOException e) {
            System.err.println("Failed to save background color configuration: " + e.getMessage());
        }
    }
    
    /**
     * Get color display name for UI
     * 
     * @param color The color code
     * @return Human-readable color name
     */
    public static String getColorDisplayName(String color) {
        switch (color) {
            case COLOR_BLACK: return "Black";
            case COLOR_DARK_GRAY: return "Dark Gray";
            case COLOR_NAVY: return "Navy";
            case COLOR_DARK_GREEN: return "Dark Green";
            case COLOR_MAROON: return "Maroon";
            case COLOR_PURPLE: return "Purple";
            case COLOR_TEAL: return "Teal";
            case COLOR_OLIVE: return "Olive";
            default: return "Unknown";
        }
    }
}
