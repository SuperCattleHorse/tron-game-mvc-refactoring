package com.tron.model.powerup;

/**
 * PowerUp - Represents a collectible power-up item in the game
 * 
 * Power-ups appear on the game map and can be collected by players or AI.
 * Each power-up has a position, type, and visual representation (white star).
 * 
 * Design Considerations:
 * - Immutable position after creation
 * - Simple collision detection with circular hitbox
 * - Type-based effects handled by game model
 * 
 * Rendering:
 * - Appears as a white filled 5-point star
 * - Size: 10x10 pixels (5-pixel radius)
 * - Visible until collected or level ends
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class PowerUp {
    
    private final double x;
    private final double y;
    private final PowerUpType type;
    private final double radius;
    private boolean active;
    
    /**
     * Create a new power-up at specified position
     * 
     * @param x X coordinate (center of power-up)
     * @param y Y coordinate (center of power-up)
     * @param type Type of power-up (BOOST or BOSS_DAMAGE)
     */
    public PowerUp(double x, double y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.radius = 5.0; // 5-pixel radius for collision detection
        this.active = true;
    }
    
    /**
     * Get X coordinate of power-up center
     * 
     * @return X coordinate
     */
    public double getX() {
        return x;
    }
    
    /**
     * Get Y coordinate of power-up center
     * 
     * @return Y coordinate
     */
    public double getY() {
        return y;
    }
    
    /**
     * Get power-up type
     * 
     * @return PowerUpType enum value
     */
    public PowerUpType getType() {
        return type;
    }
    
    /**
     * Get collision radius
     * 
     * @return Radius in pixels
     */
    public double getRadius() {
        return radius;
    }
    
    /**
     * Check if power-up is active (not yet collected)
     * 
     * @return true if active, false if collected
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Mark power-up as collected (deactivate)
     */
    public void collect() {
        this.active = false;
    }
    
    /**
     * Check if a point collides with this power-up
     * Uses circular collision detection
     * 
     * @param pointX X coordinate of point to check
     * @param pointY Y coordinate of point to check
     * @return true if point is within power-up radius
     */
    public boolean intersects(double pointX, double pointY) {
        if (!active) return false;
        
        double dx = pointX - x;
        double dy = pointY - y;
        double distanceSquared = dx * dx + dy * dy;
        double radiusSquared = radius * radius;
        
        return distanceSquared <= radiusSquared;
    }
    
    @Override
    public String toString() {
        return "PowerUp{" +
                "type=" + type +
                ", pos=(" + x + "," + y + ")" +
                ", active=" + active +
                '}';
    }
}
