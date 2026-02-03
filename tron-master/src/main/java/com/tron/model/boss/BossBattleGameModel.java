package com.tron.model.boss;

import com.tron.audio.AudioManager;
import com.tron.audio.AudioManager.SoundEffect;
import com.tron.model.game.PlayerHuman;
import com.tron.model.game.TronGameModel;
import com.tron.model.powerup.PowerUp;
import com.tron.model.util.PlayerColor;

/**
 * BossBattleGameModel - Game model for Boss Battle mode
 * 
 * Responsibilities:
 * - Manage single player vs Boss gameplay
 * - Handle split-screen mechanics (left half = player area, right half = boss display)
 * - Manage Boss health and power-up damage system
 * - Track win/loss conditions
 * 
 * Game Rules:
 * - Player moves in left half of screen only
 * - Boss displayed in right half (static image)
 * - Player collects power-ups to damage Boss (-2 HP per pickup)
 * - Player dies on boundary/self collision
 * - Victory when Boss HP reaches 0
 * - Defeat when player crashes
 * 
 * Map Layout:
 * - Total width: 900px (600px player area + 300px Boss area)
 * - Total height: 600px
 * - Left 2/3: Player activity area (600px)
 * - Right 1/3: Boss display area (300px)
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class BossBattleGameModel extends TronGameModel {
    
    private Boss boss;
    private BossBattlePowerUpManager powerUpManager;
    private static final double TICK_INTERVAL = 0.02; // 20ms per tick = 0.02 seconds
    private AudioManager audioManager;
    
    // Split screen dimensions
    private final int playerAreaWidth; // Left half width
    private boolean victoryAchieved;
    
    /**
     * Constructor for Boss Battle mode
     * 
     * @param mapWidth Total width of game area (600px player + 300px boss = 900px)
     * @param mapHeight Height of game area
     * @param velocity Player movement speed
     */
    public BossBattleGameModel(int mapWidth, int mapHeight, int velocity) {
        super(mapWidth, mapHeight, velocity, 1); // Only 1 player
        
        // Player area is 2/3 of total width (600px), Boss area is 1/3 (300px)
        this.playerAreaWidth = (int) (mapWidth * 2.0 / 3.0);
        this.boss = new Boss();
        this.powerUpManager = new BossBattlePowerUpManager(playerAreaWidth, mapHeight);
        this.audioManager = AudioManager.getInstance();
        this.victoryAchieved = false;
    }
    
    /**
     * Override tick to handle Boss Battle specific logic
     */
    @Override
    public void tick() {
        if (!isRunning || paused) {
            return;
        }
        
        // Update power-up manager
        powerUpManager.update(TICK_INTERVAL);
        
        // Move player (only the human player exists)
        if (player != null) {
            // Constrain player to left half only
            player.setBounds(playerAreaWidth, mapHeight);
            player.move();
        }
        
        // Check power-up collisions
        checkPowerUpCollisions();
        
        // Check player self-collision and boundary collision
        if (player != null) {
            // Self-collision check
            player.crash(player.intersects(player));
            
            // Check if player died
            if (!player.getAlive()) {
                isRunning = false;
                powerUpManager.stop();
                notifyPlayerCrashed(0);
                return;
            }
        }
        
        // Check victory condition
        if (!boss.isAlive() && !victoryAchieved) {
            isRunning = false;
            powerUpManager.stop();
            victoryAchieved = true;
            notifyBossDefeated();
        }
        
        // Notify observers
        notifyGameStateChanged();
    }
    
    /**
     * Check if player collects power-up and apply damage to Boss
     */
    private void checkPowerUpCollisions() {
        if (player != null && player.getAlive()) {
            PowerUp collected = powerUpManager.checkCollision(player.getX(), player.getY());
            if (collected != null) {
                // Deal damage to Boss
                boss.takeDamage();
                
                // Play pickup sound
                if (audioManager != null) {
                    audioManager.playSoundEffect(SoundEffect.PICKUP);
                }
                
                // Notify observers of Boss damage
                notifyGameStateChanged();
            }
        }
    }
    
    /**
     * Start the Boss Battle
     */
    @Override
    public void start() {
        reset();
        isRunning = true;
        powerUpManager.start();
    }
    
    /**
     * Reset the Boss Battle to initial state
     */
    @Override
    public void reset() {
        super.reset();
        
        // Reset Boss
        boss.reset();
        victoryAchieved = false;
        
        // Create human player in left half
        int[] start = getRandomStartInPlayerArea();
        player = new PlayerHuman(start[0], start[1], start[2], start[3], PlayerColor.CYAN);
        players[0] = player;
        player.addPlayers(players); // For self-collision detection
        
        // Reset power-up system
        powerUpManager.reset();
        powerUpManager.start();
        
        isRunning = true;
        notifyGameReset();
    }
    
    /**
     * Get random start position within player area (left half)
     * 
     * @return Array [x, y, xVel, yVel]
     */
    private int[] getRandomStartInPlayerArea() {
        int margin = 50;
        int x = margin + rand.nextInt(playerAreaWidth - 2 * margin);
        int y = margin + rand.nextInt(mapHeight - 2 * margin);
        
        // Random initial direction
        int[] directions = {0, 1, 2, 3}; // up, right, down, left
        int dir = directions[rand.nextInt(directions.length)];
        
        int xVel = 0, yVel = 0;
        switch (dir) {
            case 0: yVel = -velocity; break; // up
            case 1: xVel = velocity; break;  // right
            case 2: yVel = velocity; break;  // down
            case 3: xVel = -velocity; break; // left
        }
        
        return new int[]{x, y, xVel, yVel};
    }
    
    /**
     * Get Boss instance
     * 
     * @return Boss entity
     */
    public Boss getBoss() {
        return boss;
    }
    
    /**
     * Get power-up manager
     * 
     * @return BossBattlePowerUpManager instance
     */
    public BossBattlePowerUpManager getPowerUpManager() {
        return powerUpManager;
    }
    
    /**
     * Get player area width (left half)
     * 
     * @return Width of player activity area
     */
    public int getPlayerAreaWidth() {
        return playerAreaWidth;
    }
    
    /**
     * Check if player achieved victory
     * 
     * @return true if Boss is defeated
     */
    public boolean isVictory() {
        return victoryAchieved;
    }
    
    /**
     * Set pause state
     * 
     * @param paused true to pause, false to resume
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
    
    /**
     * Notify observers that Boss was defeated (victory)
     */
    private void notifyBossDefeated() {
        // Use existing observer notification system
        notifyGameStateChanged();
    }
}
