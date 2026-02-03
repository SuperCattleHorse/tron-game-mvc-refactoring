package com.tron.model.util;

/**
 * MapType - Enumeration of available map configurations for Survival mode
 * 
 * Defines different map layouts with varying obstacle patterns:
 * - DEFAULT: Classic bounded map with collision walls
 * - MAP_1: Wrap-around boundaries with no obstacles (Snake-like)
 * - MAP_2: Wrap-around with cross-shaped central obstacles
 * - MAP_3: Wrap-around with inset boundary walls
 * 
 * Design Pattern: Strategy Pattern (defines map behavior variants)
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public enum MapType {
    /** Classic bounded map with collision walls */
    DEFAULT("Default (Bounded)"),
    
    /** Wrap-around boundaries with no obstacles (Snake-like) */
    MAP_1("Map 1: Wrap-Around"),
    
    /** Wrap-around with cross-shaped central obstacles */
    MAP_2("Map 2: Cross Maze"),
    
    /** Wrap-around with inset boundary walls */
    MAP_3("Map 3: Inset Walls");
    
    private final String displayName;
    
    /**
     * Constructor for MapType enum
     * 
     * @param displayName Human-readable name for UI display
     */
    MapType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Get the display name for this map type
     * 
     * @return Human-readable map name for UI
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Check if this map type has wrap-around boundaries (Snake-like behavior)
     * 
     * Wrap-around allows players to pass through one edge and emerge from the opposite side,
     * similar to the classic Snake game mechanic.
     * 
     * @return true if boundaries wrap around, false if collision walls exist
     */
    public boolean hasWrapAroundBoundaries() {
        return this != DEFAULT;
    }
}
