package com.tron.model.boss;

/**
 * Boss - Boss entity for Boss Battle Mode
 * 
 * Responsibilities:
 * - Manage Boss health (10 HP max)
 * - Track damage taken from power-ups
 * - Determine Boss death state
 * 
 * Boss Characteristics:
 * - Static entity (doesn't move)
 * - Takes 2 damage per power-up collected by player
 * - Dies when health reaches 0
 * - Displayed as Boss.gif image in right half of split screen
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class Boss {
    
    private static final int MAX_HEALTH = 10;
    private static final int DAMAGE_PER_POWERUP = 2;
    
    private int currentHealth;
    private boolean alive;
    
    /**
     * Constructor - Creates Boss with full health
     */
    public Boss() {
        this.currentHealth = MAX_HEALTH;
        this.alive = true;
    }
    
    /**
     * Take damage from player collecting power-up
     * Each power-up deals 2 damage
     */
    public void takeDamage() {
        if (!alive) return;
        
        currentHealth -= DAMAGE_PER_POWERUP;
        if (currentHealth <= 0) {
            currentHealth = 0;
            alive = false;
        }
    }
    
    /**
     * Get current health value
     * 
     * @return Current health (0-10)
     */
    public int getCurrentHealth() {
        return currentHealth;
    }
    
    /**
     * Get maximum health value
     * 
     * @return Maximum health (10)
     */
    public int getMaxHealth() {
        return MAX_HEALTH;
    }
    
    /**
     * Check if Boss is alive
     * 
     * @return true if health > 0
     */
    public boolean isAlive() {
        return alive;
    }
    
    /**
     * Reset Boss to full health
     * Used when restarting Boss Battle
     */
    public void reset() {
        currentHealth = MAX_HEALTH;
        alive = true;
    }
    
    /**
     * Get health as percentage for health bar rendering
     * 
     * @return Health percentage (0.0 to 1.0)
     */
    public double getHealthPercentage() {
        return (double) currentHealth / MAX_HEALTH;
    }
}
