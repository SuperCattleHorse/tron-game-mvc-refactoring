package com.tron.model.game;

/**
 * TwoPlayerGameModel - Two Player mode game model
 * 
 * Responsibilities:
 * - Manage two-player competitive game logic
 * - Support two human players
 * - Track separate scores for each player
 * 
 * Game Rules:
 * - Two human players compete
 * - Game ends when either player dies
 * - Scores tracked separately
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 2.0 (MVC Complete)
 */
public class TwoPlayerGameModel extends TronGameModel {
    
    protected PlayerHuman player2;
    private int player2Score = 0;
    
    /**
     * Constructor for Two Player mode
     * 
     * @param mapWidth Width of the game area
     * @param mapHeight Height of the game area  
     * @param velocity Player movement speed
     */
    public TwoPlayerGameModel(int mapWidth, int mapHeight, int velocity) {
        super(mapWidth, mapHeight, velocity, 2);
    }
    
    /**
     * Reset the game to initial state
     * Creates two human players
     */
    @Override
    public void reset() {
        // Create player 1 (CYAN)
        int[] start1 = getRandomStart();
        player = new PlayerHuman(start1[0], start1[1], start1[2], start1[3], PLAYER_COLORS[0]);
        players[0] = player;
        
        // Create player 2 (PINK) 
        int[] start2 = getRandomStart();
        player2 = new PlayerHuman(start2[0], start2[1], start2[2], start2[3], PLAYER_COLORS[1]);
        players[1] = player2;
        
        // Give both players reference to all players
        for (Player p : players) {
            if (p != null) {
                p.addPlayers(players);
            }
        }
        
        currentScore = 0;
        player2Score = 0;
        isRunning = true;
        
        notifyGameReset();
    }
    
    /**
     * Override tick to check both players
     */
    @Override
    public void tick() {
        if (!isRunning) return;
        
        // Move all players
        for (Player p : players) {
            if (p != null) {
                p.setBounds(mapWidth, mapHeight);
                p.move();
            }
        }
        
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
        
        // Check if either player died
        if ((player != null && !player.getAlive()) || (player2 != null && !player2.getAlive())) {
            isRunning = false;
            
            // Update scores
            if (player2 != null && player2.getAlive()) {
                player2Score++;
                notifyScoreChanged(1, player2Score);
            } else if (player != null && player.getAlive()) {
                currentScore++;
                notifyScoreChanged(0, currentScore);
            }
            // If both died, it's a tie - no score change
            
            if (player != null && !player.getAlive()) {
                notifyPlayerCrashed(0);
            }
            if (player2 != null && !player2.getAlive()) {
                notifyPlayerCrashed(1);
            }
        }
        
        // Notify observers of state change
        notifyGameStateChanged();
    }
    
    /**
     * Get player 2
     * 
     * @return Player 2 instance
     */
    public PlayerHuman getPlayer2() {
        return player2;
    }
    
    /**
     * Get player 2 score
     * 
     * @return Player 2's score
     */
    public int getPlayer2Score() {
        return player2Score;
    }
    
    /**
     * Control player 2 - set left velocity
     */
    public void player2Left() {
        if (player2 != null && player2.getAlive()) {
            player2.setXVelocity(-velocity);
            player2.setYVelocity(0);
        }
    }
    
    /**
     * Control player 2 - set right velocity
     */
    public void player2Right() {
        if (player2 != null && player2.getAlive()) {
            player2.setXVelocity(velocity);
            player2.setYVelocity(0);
        }
    }
    
    /**
     * Control player 2 - set up velocity
     */
    public void player2Up() {
        if (player2 != null && player2.getAlive()) {
            player2.setYVelocity(-velocity);
            player2.setXVelocity(0);
        }
    }
    
    /**
     * Control player 2 - set down velocity
     */
    public void player2Down() {
        if (player2 != null && player2.getAlive()) {
            player2.setYVelocity(velocity);
            player2.setXVelocity(0);
        }
    }
    
    /**
     * Control player 2 - jump
     */
    public void player2Jump() {
        if (player2 != null && player2.getAlive()) {
            player2.jump();
            notifyBoostChanged(1, player2.getBoostsLeft());
        }
    }
    
    /**
     * Control player 2 - boost
     */
    public void player2Boost() {
        if (player2 != null && player2.getAlive()) {
            player2.startBoost();
            notifyBoostChanged(1, player2.getBoostsLeft());
        }
    }
}
