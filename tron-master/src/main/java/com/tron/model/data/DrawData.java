package com.tron.model.data;

import java.util.List;

import com.tron.model.util.PlayerColor;
import com.tron.model.util.Shape;

/**
 * DrawData - Data transfer object containing rendering information for game objects
 * 
 * This class separates model data from view rendering logic, following the MVC pattern.
 * It acts as a value object that encapsulates all information needed to draw a game object
 * without exposing the internal model structure.
 * 
 * Design Pattern: Data Transfer Object (DTO)
 * - Immutable data container (all fields final)
 * - Framework-independent data format
 * - Clean separation between Model and View layers
 * 
 * Benefits:
 * - Model doesn't depend on view frameworks
 * - View only accesses rendering data, not game logic
 * - Thread-safe due to immutability
 * - Easy to test and mock
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class DrawData {
    
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final PlayerColor color;
    private final List<Shape> path;
    private final boolean isAlive;
    private final boolean isJumping;
    
    /**
     * Constructs a DrawData object with complete rendering information.
     * 
     * @param x X coordinate for rendering (top-left corner)
     * @param y Y coordinate for rendering (top-left corner)
     * @param width Width of the object in pixels
     * @param height Height of the object in pixels
     * @param color Player color (framework-independent)
     * @param path Trail/path shapes for rendering
     * @param isAlive Whether the object is still active
     * @param isJumping Whether the object is currently jumping (affects trail rendering)
     */
    public DrawData(int x, int y, int width, int height, PlayerColor color, 
                    List<Shape> path, boolean isAlive, boolean isJumping) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.path = path;
        this.isAlive = isAlive;
        this.isJumping = isJumping;
    }
    
    /**
     * Gets the X coordinate for rendering.
     * 
     * @return The X position (top-left corner) in pixels
     */
    public int getX() {
        return x;
    }
    
    /**
     * Gets the Y coordinate for rendering.
     * 
     * @return The Y position (top-left corner) in pixels
     */
    public int getY() {
        return y;
    }
    
    /**
     * Gets the width of the object.
     * 
     * @return The width in pixels
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Gets the height of the object.
     * 
     * @return The height in pixels
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Gets the player color for rendering.
     * 
     * @return The PlayerColor enum value (framework-independent)
     */
    public PlayerColor getColor() {
        return color;
    }
    
    /**
     * Gets the trail/path shapes for rendering.
     * 
     * @return List of Shape objects representing the player's trail
     */
    public List<Shape> getPath() {
        return path;
    }
    
    /**
     * Checks if the object is alive/active.
     * 
     * @return true if the object is alive, false if crashed/dead
     */
    public boolean isAlive() {
        return isAlive;
    }
    
    /**
     * Checks if the object is currently jumping.
     * When jumping, the trail may be rendered differently (e.g., dashed).
     * 
     * @return true if jumping is active, false otherwise
     */
    public boolean isJumping() {
        return isJumping;
    }
}
