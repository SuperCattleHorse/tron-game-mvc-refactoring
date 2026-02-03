package com.tron.model.util;

/**
 * MapObstacle - Represents a static rectangular obstacle on the game map
 * 
 * Used to define walls, barriers, and other collision zones that players
 * cannot pass through. Obstacles are axis-aligned rectangles defined by
 * position (x, y) and dimensions (width, height).
 * 
 * This class is immutable - all fields are final and set via constructor.
 * 
 * Design Pattern: Value Object (immutable data holder)
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class MapObstacle {
    
    private final int x;      // Top-left corner X coordinate
    private final int y;      // Top-left corner Y coordinate
    private final int width;  // Width of obstacle
    private final int height; // Height of obstacle
    
    /**
     * Constructor for rectangular obstacle
     * 
     * @param x Left edge X coordinate
     * @param y Top edge Y coordinate
     * @param width Obstacle width in pixels
     * @param height Obstacle height in pixels
     */
    public MapObstacle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    /**
     * Check if a point collides with this obstacle
     * 
     * @param px Point X coordinate
     * @param py Point Y coordinate
     * @return true if point is inside obstacle bounds
     */
    public boolean intersects(int px, int py) {
        return px >= x && px <= x + width && 
               py >= y && py <= y + height;
    }
    
    /**
     * Check if a line segment intersects this obstacle
     * 
     * @param line The line to test for collision
     * @return true if line intersects obstacle
     */
    public boolean intersects(Line line) {
        int x1 = line.getStartX();
        int y1 = line.getStartY();
        int x2 = line.getEndX();
        int y2 = line.getEndY();
        
        // Check if either endpoint is inside obstacle
        if (intersects(x1, y1) || intersects(x2, y2)) {
            return true;
        }
        
        // Check if line crosses obstacle boundaries
        // For simplicity, check if bounding boxes overlap
        int lineMinX = Math.min(x1, x2);
        int lineMaxX = Math.max(x1, x2);
        int lineMinY = Math.min(y1, y2);
        int lineMaxY = Math.max(y1, y2);
        
        return !(lineMaxX < x || lineMinX > x + width ||
                 lineMaxY < y || lineMinY > y + height);
    }
    
    /**
     * Get the X coordinate of the obstacle's left edge
     * 
     * @return X coordinate in pixels
     */
    public int getX() { return x; }
    
    /**
     * Get the Y coordinate of the obstacle's top edge
     * 
     * @return Y coordinate in pixels
     */
    public int getY() { return y; }
    
    /**
     * Get the width of the obstacle
     * 
     * @return Width in pixels
     */
    public int getWidth() { return width; }
    
    /**
     * Get the height of the obstacle
     * 
     * @return Height in pixels
     */
    public int getHeight() { return height; }
}
