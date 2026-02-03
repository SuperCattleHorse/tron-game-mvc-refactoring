package com.tron.model.game;

import com.tron.audio.AudioManager;
import com.tron.audio.AudioManager.SoundEffect;
import com.tron.config.GameSettings;
import com.tron.model.boss.Boss;
import com.tron.model.boss.BossBattlePowerUpManager;
import com.tron.model.powerup.PowerUp;
import com.tron.model.powerup.PowerUpManager;
import com.tron.model.powerup.PowerUpType;

/**
 * StoryGameModel - Story mode game model with power-up system and Boss Battle
 * 
 * Responsibilities:
 * - Manage single-player story mode game logic
 * - Handle progressive levels with increasing AI count (Levels 1-7)
 * - Handle Boss Battle level (Level 8)
 * - Track level progress and scoring
 * - Manage power-up spawning and collection
 * 
 * Game Rules:
 * - Single human player against AI opponents (Levels 1-7)
 * - Level 8: Boss Battle (player vs Boss, no AI)
 * - Starts with 1 AI, increases to 7 AIs max (Level 7)
 * - Earn 50 points per AI defeated when level completes
 * - Boss Battle: Collect power-ups to damage Boss (-2 HP per pickup)
 * - Game ends when player dies or Boss defeated
 * 
 * Power-Up System:
 * - Levels 1-7: BOOST type (grants boost charges)
 * - Level 8: BOSS_DAMAGE type (damages Boss)
 * - First power-up spawns 10 seconds after game start
 * - Subsequent power-ups spawn every 10 seconds
 * - Power-ups cleared on level restart or completion
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 4.0 (With Boss Battle Level)
 */
public class StoryGameModel extends TronGameModel {
    
    private int currentLevel;
    private static final int MAX_NORMAL_LEVEL = 7; // 1 human + 7 AI = 8 total
    private static final int BOSS_LEVEL = 8; // Final Boss Battle level
    private static final int POINTS_PER_AI = 50;
    private static final int BOSS_VICTORY_POINTS = 600; // Points for defeating Boss
    
    // Power-up system
    private PowerUpManager powerUpManager;
    private BossBattlePowerUpManager bossPowerUpManager;
    private static final double TICK_INTERVAL = 0.02; // 20ms per tick = 0.02 seconds
    private AudioManager audioManager;
    
    // Boss Battle state
    private Boss boss;
    private boolean isBossLevel;
    private int playerAreaWidth; // For Boss level split-screen
    
    /**
     * Constructor for Story mode
     * Starts at level 1 (1 human + 1 AI)
     * Initializes power-up system and Boss
     * 
     * @param mapWidth Width of the game area
     * @param mapHeight Height of the game area
     * @param velocity Player movement speed
     */
    public StoryGameModel(int mapWidth, int mapHeight, int velocity) {
        super(mapWidth, mapHeight, velocity, 2); // Start with 2 players
        this.currentLevel = 1;
        this.powerUpManager = new PowerUpManager(mapWidth, mapHeight);
        this.powerUpManager.setPowerUpType(PowerUpType.BOOST); // Default type for normal levels
        
        // Initialize Boss Battle components
        // Player area is 2/3 of total width (matches Boss Battle Mode layout)
        this.playerAreaWidth = (int) (mapWidth * 2.0 / 3.0); // For normal levels: 333px
        // Boss level will use 600x600 player area (from 900x600 total canvas)
        this.bossPowerUpManager = new BossBattlePowerUpManager(600, 600);
        this.boss = new Boss();
        this.isBossLevel = false;
        
        this.audioManager = AudioManager.getInstance();
    }
    
    /**
     * Override tick to check level completion and handle power-ups
     */
    @Override
    public void tick() {
        if (!isRunning) return;
        
        // Different logic for Boss level vs normal levels
        if (isBossLevel) {
            tickBossLevel();
        } else {
            tickNormalLevel();
        }
    }
    
    /**
     * Tick logic for normal levels (1-7)
     */
    private void tickNormalLevel() {
        // Update power-up manager
        powerUpManager.update(TICK_INTERVAL);
        
        // Don't increment score during Story mode (score earned on level complete)
        
        // Move all players
        for (Player p : players) {
            if (p != null) {
                p.setBounds(mapWidth, mapHeight);
                p.move();
            }
        }
        
        // Check power-up collisions for all players
        checkPowerUpCollisions();
        
        // Check collisions
        for (Player p1 : players) {
            if (p1 != null) {
                for (Player p2 : players) {
                    if (p2 != null) {
                        p1.crash(p1.intersects(p2));
                    }
                }
            }
        }
        
        // Check if human player died
        if (player != null && !player.getAlive()) {
            isRunning = false;
            powerUpManager.stop(); // Stop spawning on game over
            notifyPlayerCrashed(0);
            return;
        }
        
        // Check if all AI players are dead (level complete)
        int deadCount = 0;
        for (Player p : players) {
            if (p != null && !p.getAlive()) {
                deadCount++;
            }
        }
        
        if (deadCount == players.length - 1) {
            // Player won! Stop game
            isRunning = false;
            powerUpManager.stop(); // Stop spawning on level complete
            // Score is added by view when level completes
        }
        
        // Notify observers of state change
        notifyGameStateChanged();
    }
    
    /**
     * Tick logic for Boss Battle level (level 8)
     */
    private void tickBossLevel() {
        // Update Boss power-up manager
        bossPowerUpManager.update(TICK_INTERVAL);
        
        // Move player (constrained to left half)
        if (player != null) {
            player.setBounds(600, 600); // Boss level uses 900x600 canvas, player area is 600x600
            player.move();
        }
        
        // Check Boss power-up collisions
        checkBossPowerUpCollisions();
        
        // Check player self-collision
        if (player != null) {
            player.crash(player.intersects(player));
            
            // Check if player died
            if (!player.getAlive()) {
                isRunning = false;
                bossPowerUpManager.stop();
                notifyPlayerCrashed(0);
                return;
            }
        }
        
        // Check if Boss is defeated
        if (!boss.isAlive()) {
            isRunning = false;
            bossPowerUpManager.stop();
            // Victory will be detected by view checking isGameComplete()
        }
        
        // Notify observers
        notifyGameStateChanged();
    }
    
    /**
     * Check if any player collects a power-up (normal levels)
     * Applies power-up effect to collecting player
     */
    private void checkPowerUpCollisions() {
        for (Player p : players) {
            if (p != null && p.getAlive()) {
                PowerUp collected = powerUpManager.checkCollision(p.x, p.y);
                if (collected != null) {
                    applyPowerUpEffect(p, collected);
                }
            }
        }
    }
    
    /**
     * Check if player collects Boss power-up
     * Damages Boss when collected
     */
    private void checkBossPowerUpCollisions() {
        if (player != null && player.getAlive()) {
            PowerUp collected = bossPowerUpManager.checkCollision(player.x, player.y);
            if (collected != null) {
                // Deal damage to Boss
                boss.takeDamage();
                
                // Play pickup sound
                if (audioManager != null) {
                    audioManager.playSoundEffect(SoundEffect.PICKUP);
                }
            }
        }
    }
    
    /**
     * Apply power-up effect to player
     * 
     * @param player Player who collected the power-up
     * @param powerUp Collected power-up
     */
    private void applyPowerUpEffect(Player player, PowerUp powerUp) {
        switch (powerUp.getType()) {
            case BOOST:
                // Grant one boost charge
                player.addBoost();
                // Play pickup sound
                if (audioManager != null) {
                    audioManager.playSoundEffect(SoundEffect.PICKUP);
                }
                break;
            case BOSS_DAMAGE:
                // Reserved for future boss battle implementation
                // In boss mode, this would damage the boss instead
                break;
        }
    }
    
    /**
     * Complete current level and advance to next
     * Awards points based on AIs defeated
     * 
     * @return true if more levels available, false if game complete
     */
    public boolean completeLevel() {
        if (isBossLevel) {
            // Boss level complete - award 600 points for defeating Boss
            currentScore += BOSS_VICTORY_POINTS;
            notifyScoreChanged(0, currentScore);
            return false;
        }
        
        // Award points for defeating AIs
        int aiCount = players.length - 1;
        currentScore += POINTS_PER_AI * aiCount;
        notifyScoreChanged(0, currentScore);
        
        // Check if more levels available
        if (currentLevel < BOSS_LEVEL) {
            currentLevel++;
            return true;
        }
        return false;
    }
    
    /**
     * Start next level with more AI opponents or Boss Battle
     * Resets power-up system for new level
     */
    public void startNextLevel() {
        if (currentLevel == BOSS_LEVEL) {
            // Start Boss Battle level
            startBossLevel();
        } else {
            // Start normal level with AI
            startNormalLevel();
        }
    }
    
    /**
     * Start normal level with AI opponents (Levels 1-7)
     */
    private void startNormalLevel() {
        isBossLevel = false;
        
        // Increase player count (add one more AI)
        int newPlayerCount = Math.min(currentLevel + 1, 8);
        players = new Player[newPlayerCount];
        
        // Create players (preserve score)
        resetPlayers();
        
        // Reset and start normal power-up system
        powerUpManager.setPowerUpType(PowerUpType.BOOST);
        powerUpManager.reset();
        powerUpManager.start();
    }
    
    /**
     * Start Boss Battle level (Level 8)
     */
    private void startBossLevel() {
        isBossLevel = true;
        
        // Only 1 player (no AI in Boss level)
        players = new Player[1];
        
        // Reset Boss
        boss.reset();
        
        // Create human player in left half
        int[] start = getRandomStartInPlayerArea();
        player = new PlayerHuman(start[0], start[1], start[2], start[3], PLAYER_COLORS[0]);
        players[0] = player;
        player.addPlayers(players);
        
        // Reset and start Boss power-up system
        bossPowerUpManager.reset();
        bossPowerUpManager.start();
        
        isRunning = true;
        notifyGameReset();
    }
    
    /**
     * Get random start position within player area (left half, for Boss level)
     * 
     * @return Array [x, y, xVel, yVel]
     */
    private int[] getRandomStartInPlayerArea() {
        int margin = 50;
        int x = margin + rand.nextInt(600 - 2 * margin); // Boss level player area is 600px wide
        int y = margin + rand.nextInt(600 - 2 * margin); // Boss level height is 600px
        
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
     * Override reset to preserve score across levels
     * Only resetToFirstLevel() should clear the score
     * Resets power-up system
     */
    @Override
    public void reset() {
        // Save current score before reset
        int savedScore = currentScore;
        
        // Call parent reset (which will set score to 0)
        super.reset();
        
        // Restore score
        currentScore = savedScore;
        
        // Reset and start power-up system
        powerUpManager.reset();
        powerUpManager.start();
    }
    
    /**
     * Reset players without clearing score (for level progression)
     */
    private void resetPlayers() {
        // Create human player
        int[] start = getRandomStart();
        player = new PlayerHuman(start[0], start[1], start[2], start[3], PLAYER_COLORS[0]);
        players[0] = player;
        
        // Create AI players for remaining slots
        GameSettings gameSettings = GameSettings.getInstance();
        boolean useHardAI = gameSettings.isHardAIEnabled();
        
        for (int i = 1; i < players.length; i++) {
            start = getRandomStart();
            PlayerAI aiPlayer = new PlayerAI(
                start[0], start[1], start[2], start[3], PLAYER_COLORS[i % PLAYER_COLORS.length]
            );
            
            // Apply Hard AI behavior strategy if enabled
            if (useHardAI) {
                aiPlayer.setBehaviorStrategy(new HardAIBehaviorStrategy(aiPlayer));
            }
            
            players[i] = aiPlayer;
        }
        
        // Give all players reference to all other players (for collision detection)
        for (Player p : players) {
            if (p != null) {
                p.addPlayers(players);
            }
        }
        
        isRunning = true;
        notifyGameReset();
    }
    
    /**
     * Get current level number
     * 
     * @return Current level (1-7)
     */
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    /**
     * Check if level is complete (all AIs dead or Boss defeated, player alive)
     * 
     * @return true if level complete
     */
    public boolean isLevelComplete() {
        if (player == null || !player.getAlive()) {
            return false;
        }
        
        if (isBossLevel) {
            // Boss level complete when Boss is defeated
            return !boss.isAlive();
        }
        
        // Normal level complete when all AIs dead
        int deadCount = 0;
        for (Player p : players) {
            if (p != null && !p.getAlive()) {
                deadCount++;
            }
        }
        
        return deadCount == players.length - 1;
    }
    
    /**
     * Check if game is fully complete (Boss defeated)
     * 
     * @return true if Boss level complete
     */
    public boolean isGameComplete() {
        return isBossLevel && !boss.isAlive() && player != null && player.getAlive();
    }
    
    /**
     * Reset to level 1
     * Clears power-up system and Boss state
     */
    public void resetToFirstLevel() {
        currentLevel = 1;
        isBossLevel = false;
        players = new Player[2];
        currentScore = 0;
        
        // Reset both power-up systems
        powerUpManager.reset();
        bossPowerUpManager.reset();
        boss.reset();
        
        reset();
    }
    
    /**
     * Get power-up manager instance (for normal levels)
     * 
     * @return PowerUpManager for accessing active power-ups
     */
    public PowerUpManager getPowerUpManager() {
        return powerUpManager;
    }
    
    /**
     * Get Boss power-up manager instance (for Boss level)
     * 
     * @return BossBattlePowerUpManager for Boss level
     */
    public BossBattlePowerUpManager getBossPowerUpManager() {
        return bossPowerUpManager;
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
     * Check if current level is Boss Battle
     * 
     * @return true if on Boss level
     */
    public boolean isBossLevel() {
        return isBossLevel;
    }
    
    /**
     * Get player area width (for Boss level split-screen)
     * Dynamically returns correct width based on level type:
     * - Boss level: 600px (based on 900px total width)
     * - Normal level: 333px (based on 500px total width)
     * 
     * @return Width of player activity area
     */
    public int getPlayerAreaWidth() {
        if (isBossLevel) {
            // Boss level uses 900x600 canvas: 600px player + 300px Boss
            return 600;
        } else {
            // Normal levels use 500x500 canvas: 333px player + 167px (unused)
            return playerAreaWidth;
        }
    }
    
    /**
     * Override getMapWidth to return dynamic width based on level type
     * 
     * @return Map width (900 for Boss level, 500 for normal levels)
     */
    @Override
    public int getMapWidth() {
        return isBossLevel ? 900 : 500;
    }
    
    /**
     * Override getMapHeight to return dynamic height based on level type
     * 
     * @return Map height (600 for Boss level, 500 for normal levels)
     */
    @Override
    public int getMapHeight() {
        return isBossLevel ? 600 : 500;
    }
}
