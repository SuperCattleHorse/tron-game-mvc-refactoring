package com.tron.model.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.tron.model.powerup.PowerUp;
import com.tron.model.powerup.PowerUpType;

/**
 * BossBattlePowerUpManager - Manages power-ups specifically for Boss Battle mode
 * 
 * Key Differences from Story Mode PowerUpManager:
 * - Power-ups only spawn in left half of screen (player area)
 * - Power-ups deal 2 damage to Boss when collected
 * - Separate instance to avoid interference with story mode
 * 
 * Spawning Rules:
 * - First power-up spawns 5 seconds after battle start
 * - Subsequent power-ups spawn every 5 seconds
 * - Multiple power-ups can exist simultaneously
 * - Power-ups only spawn in left half (player activity area)
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class BossBattlePowerUpManager {
    
    private static final double SPAWN_INTERVAL_SECONDS = 5.0;
    private static final int SPAWN_MARGIN = 50; // Pixels from edge
    
    private List<PowerUp> activePowerUps;
    private double timeSinceLastSpawn;
    private boolean enabled;
    private Random random;
    
    // Map boundaries (only left half for spawning)
    private int playerAreaWidth; // Left half width
    private int mapHeight;
    
    /**
     * Create a new BossBattlePowerUpManager
     * 
     * @param playerAreaWidth Width of player activity area (left half)
     * @param mapHeight Height of game map
     */
    public BossBattlePowerUpManager(int playerAreaWidth, int mapHeight) {
        this.playerAreaWidth = playerAreaWidth;
        this.mapHeight = mapHeight;
        this.activePowerUps = new ArrayList<>();
        this.random = new Random();
        this.enabled = false;
        this.timeSinceLastSpawn = 0.0;
    }
    
    /**
     * Start power-up spawning system
     */
    public void start() {
        this.enabled = true;
        this.timeSinceLastSpawn = 0.0;
        this.activePowerUps.clear();
    }
    
    /**
     * Stop power-up spawning
     */
    public void stop() {
        this.enabled = false;
        this.activePowerUps.clear();
    }
    
    /**
     * Reset manager state
     */
    public void reset() {
        this.activePowerUps.clear();
        this.timeSinceLastSpawn = 0.0;
    }
    
    /**
     * Update power-up manager (called each game tick)
     * 
     * @param deltaTime Time elapsed since last update (in seconds)
     */
    public void update(double deltaTime) {
        if (!enabled) return;
        
        timeSinceLastSpawn += deltaTime;
        
        // Spawn new power-up if interval elapsed
        if (timeSinceLastSpawn >= SPAWN_INTERVAL_SECONDS) {
            spawnPowerUp();
            timeSinceLastSpawn = 0.0;
        }
    }
    
    /**
     * Spawn a new power-up at random position in player area
     */
    private void spawnPowerUp() {
        // Random position in left half (player area) with margin
        int x = SPAWN_MARGIN + random.nextInt(playerAreaWidth - 2 * SPAWN_MARGIN);
        int y = SPAWN_MARGIN + random.nextInt(mapHeight - 2 * SPAWN_MARGIN);
        
        PowerUp powerUp = new PowerUp(x, y, PowerUpType.BOSS_DAMAGE);
        activePowerUps.add(powerUp);
    }
    
    /**
     * Check if player position collides with any active power-up
     * 
     * @param playerX Player X coordinate
     * @param playerY Player Y coordinate
     * @return Collected PowerUp or null if no collision
     */
    public PowerUp checkCollision(double playerX, double playerY) {
        for (PowerUp powerUp : activePowerUps) {
            if (powerUp.isActive() && powerUp.intersects(playerX, playerY)) {
                powerUp.collect();
                activePowerUps.remove(powerUp);
                return powerUp;
            }
        }
        return null;
    }
    
    /**
     * Get list of active power-ups for rendering
     * 
     * @return List of active PowerUp instances
     */
    public List<PowerUp> getActivePowerUps() {
        return new ArrayList<>(activePowerUps);
    }
    
    /**
     * Check if spawning is enabled
     * 
     * @return true if spawning is active
     */
    public boolean isEnabled() {
        return enabled;
    }
}
