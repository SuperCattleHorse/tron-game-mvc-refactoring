package com.tron.config;

/**
 * BackgroundColorChangeListener - Observer interface for background color changes
 * 
 * Design Pattern: Observer Pattern (Behavioral)
 * - Defines the contract for observers that need to be notified when background color changes
 * - Decouples the configuration management from view components
 * 
 * SOLID Principles:
 * - Single Responsibility: Only handles color change notification
 * - Dependency Inversion: Views depend on this abstraction, not concrete implementation
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public interface BackgroundColorChangeListener {
    
    /**
     * Called when the background color has been changed
     * 
     * @param newColor The new background color in CSS format (e.g., "black", "#1a1a1a")
     */
    void onBackgroundColorChanged(String newColor);
}
