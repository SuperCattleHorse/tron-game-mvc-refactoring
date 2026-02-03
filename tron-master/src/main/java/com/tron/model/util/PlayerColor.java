package com.tron.model.util;

/**
 * PlayerColor - GUI-independent color enumeration for players
 * 
 * This enum represents player colors using RGB values without any dependency
 * on specific GUI frameworks (AWT/Swing/JavaFX). This design allows the Model
 * layer to remain completely independent of the View implementation.
 * 
 * Design Pattern: MVC Architecture
 * - Model uses this enum to identify player colors
 * - View layer converts to framework-specific Color objects
 * - Supports both AWT and JavaFX color systems
 * 
 * Benefits:
 * - Model can be tested without any GUI framework
 * - Easy color conversion for any UI framework
 * - RGB values provide framework-independent color definition
 * - Extensible for future color schemes
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 2.0 (GUI-independent)
 */
public enum PlayerColor {
    CYAN("Cyan", 0, 255, 255),
    PINK("Pink", 255, 175, 175),
    WHITE("White", 255, 255, 255),
    YELLOW("Yellow", 255, 255, 0),
    BLUE("Blue", 0, 0, 255),
    ORANGE("Orange", 255, 200, 0),
    RED("Red", 255, 0, 0),
    GREEN("Green", 0, 255, 0);
    
    private final String name;
    private final int red;
    private final int green;
    private final int blue;
    
    PlayerColor(String name, int red, int green, int blue) {
        this.name = name;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    
    /**
     * Get the color name
     * @return Human-readable color name
     */
    public String getColorName() {
        return name;
    }
    
    /**
     * Get RGB red component (0-255)
     * @return Red value
     */
    public int getRed() {
        return red;
    }
    
    /**
     * Get RGB green component (0-255)
     * @return Green value
     */
    public int getGreen() {
        return green;
    }
    
    /**
     * Get RGB blue component (0-255)
     * @return Blue value
     */
    public int getBlue() {
        return blue;
    }
    
    /**
     * Convert to JavaFX Color for JavaFX-based views
     * This is the preferred method for JavaFX applications
     * 
     * @return The corresponding JavaFX Color
     */
    public javafx.scene.paint.Color toFXColor() {
        return javafx.scene.paint.Color.rgb(red, green, blue);
    }
    
    /**
     * Gets a PlayerColor by index for player initialization.
     * @param index The player index
     * @return The corresponding PlayerColor
     */
    public static PlayerColor getByIndex(int index) {
        PlayerColor[] colors = values();
        return colors[index % colors.length];
    }
}
