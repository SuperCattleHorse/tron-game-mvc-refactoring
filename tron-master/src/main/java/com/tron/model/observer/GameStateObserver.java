package com.tron.model.observer;

/**
 * Observer interface for game state changes.
 * Views implement this to receive notifications when the model changes.
 */
public interface GameStateObserver {
    
    /**
     * Called when the game state has changed and view needs to be updated.
     */
    void onGameStateChanged();
    
    /**
     * Called when the score has changed.
     * @param playerIndex The index of the player whose score changed
     * @param newScore The new score value
     */
    void onScoreChanged(int playerIndex, int newScore);
    
    /**
     * Called when a player's boost count has changed.
     * @param playerIndex The index of the player
     * @param boostCount The new boost count
     */
    void onBoostChanged(int playerIndex, int boostCount);
    
    /**
     * Called when a player crashes or dies.
     * @param playerIndex The index of the player
     */
    void onPlayerCrashed(int playerIndex);
    
    /**
     * Called when the game is reset.
     */
    void onGameReset();
}
