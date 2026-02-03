package com.tron.model.powerup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * PowerUpManager - Manages power-up spawning, timing, and lifecycle
 * 
 * Design Pattern: Singleton Pattern (creational)
 * - Single instance manages all power-ups in the game
 * - Ensures consistent power-up behavior across game modes
 * 
 * Spawning Rules:
 * - First power-up spawns 5 seconds after game start
 * - Subsequent power-ups spawn every 5 seconds
 * - Multiple power-ups can exist simultaneously
 * - Power-ups persist until collected or level ends
 * 
 * Usage:
 * - Story mode: Player/AI collect to gain boost charges
 * - Boss mode (future): Collect to damage boss
 * 
 * Thread Safety:
 * - Not thread-safe, designed for single-threaded game loop
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class PowerUpManager {
    
    private static final double SPAWN_INTERVAL_SECONDS = 5.0;
    private static final int SPAWN_MARGIN = 50; // Pixels from edge
    
    private List<PowerUp> activePowerUps;
    private double timeSinceLastSpawn;
    private boolean enabled;
    private Random random;
    
    // Map boundaries for spawn location
    private int mapWidth;
    private int mapHeight;
    
    // Power-up type to spawn (default BOOST, can be changed for boss mode)
    private PowerUpType currentType;
    
    /**
     * Create a new PowerUpManager
     * 
     * @param mapWidth Width of game map
     * @param mapHeight Height of game map
     */
    public PowerUpManager(int mapWidth, int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.activePowerUps = new ArrayList<>();
        this.random = new Random();
        this.enabled = false;
        this.timeSinceLastSpawn = 0.0;
        this.currentType = PowerUpType.BOOST; // Default type
    }
    
    /**
     * Start power-up spawning system
     * Resets timer and clears existing power-ups
     */
    public void start() {
        this.enabled = true;
        this.timeSinceLastSpawn = 0.0;
        this.activePowerUps.clear();
    }
    
    /**
     * Stop power-up spawning and clear all power-ups
     */
    public void stop() {
        this.enabled = false;
        this.activePowerUps.clear();
    }
    
    /**
     * Reset manager state (called on level restart or change)
     * Clears power-ups and resets timer
     */
    public void reset() {
        this.activePowerUps.clear();
        this.timeSinceLastSpawn = 0.0;
    }
    
    /**
     * Set the type of power-up to spawn
     * 
     * @param type PowerUpType (BOOST for normal levels, BOSS_DAMAGE for boss)
     */
    public void setPowerUpType(PowerUpType type) {
        this.currentType = type;
    }
    
    /**
     * Update power-up manager (called each game tick)
     * Handles spawn timing and power-up spawning
     * 
     * @param deltaTime Time elapsed since last update (in seconds)
     */
    public void update(double deltaTime) {
        if (!enabled) return;
        
        timeSinceLastSpawn += deltaTime;
        
        // Check if it's time to spawn a new power-up
        if (timeSinceLastSpawn >= SPAWN_INTERVAL_SECONDS) {
            spawnPowerUp();
            timeSinceLastSpawn = 0.0;
        }
    }
    
    /**
     * Spawn a new power-up at random location
     * Multiple power-ups can exist simultaneously
     */
    private void spawnPowerUp() {
        // Generate random position within map bounds (with margin)
        double x = SPAWN_MARGIN + random.nextDouble() * (mapWidth - 2 * SPAWN_MARGIN);
        double y = SPAWN_MARGIN + random.nextDouble() * (mapHeight - 2 * SPAWN_MARGIN);
        
        PowerUp powerUp = new PowerUp(x, y, currentType);
        activePowerUps.add(powerUp);
    }
    
    /**
     * Get all active power-ups
     * 
     * @return List of active PowerUp objects
     */
    public List<PowerUp> getActivePowerUps() {
        return new ArrayList<>(activePowerUps);
    }
    
    /**
     * Check if a player position collects any power-up
     * If collision detected, marks power-up as collected and removes it
     * 
     * @param x Player X coordinate
     * @param y Player Y coordinate
     * @return PowerUp if collected, null otherwise
     */
    public PowerUp checkCollision(double x, double y) {
        for (int i = activePowerUps.size() - 1; i >= 0; i--) {
            PowerUp powerUp = activePowerUps.get(i);
            if (powerUp.isActive() && powerUp.intersects(x, y)) {
                powerUp.collect();
                activePowerUps.remove(i);
                return powerUp;
            }
        }
        return null;
    }
    
    /**
     * Check if power-up spawning is enabled
     * 
     * @return true if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Get time remaining until next spawn
     * 
     * @return Seconds until next spawn
     */
    public double getTimeUntilNextSpawn() {
        return Math.max(0, SPAWN_INTERVAL_SECONDS - timeSinceLastSpawn);
    }
    
    /**
     * Get current power-up type being spawned
     * 
     * @return PowerUpType enum value
     */
    public PowerUpType getCurrentType() {
        return currentType;
    }
}
