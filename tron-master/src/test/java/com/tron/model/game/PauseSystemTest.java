package com.tron.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the pause system functionality
 * Tests the pause/resume behavior in TronGameModel
 * 
 * @author MattBrown
 * @version 1.0
 */
class PauseSystemTest {
    
    private TronGameModel gameModel;
    
    @BeforeEach
    void setUp() {
        // Create a basic game model for testing
        gameModel = new TronGameModel(500, 500, 3, 2);
        gameModel.reset();
    }
    
    @Test
    @DisplayName("Should pause game when running")
    void testPauseRunningGame() {
        gameModel.start();
        gameModel.pause();
        
        assertTrue(gameModel.isPaused(), "Game should be paused");
        assertTrue(gameModel.isRunning(), "Game should still be in running state");
    }
    
    @Test
    @DisplayName("Should not pause when game is not running")
    void testCannotPauseWhenNotRunning() {
        gameModel.stop();
        gameModel.pause();
        
        assertFalse(gameModel.isPaused(), "Game should not be paused when not running");
    }
    
    @Test
    @DisplayName("Should resume game from pause")
    void testResumeFromPause() {
        gameModel.start();
        gameModel.pause();
        gameModel.resume();
        
        assertFalse(gameModel.isPaused(), "Game should not be paused after resume");
        assertTrue(gameModel.isRunning(), "Game should still be running");
    }
    
    @Test
    @DisplayName("Should not update game state when paused")
    void testTickDoesNotUpdateWhenPaused() {
        gameModel.start();
        int initialScore = gameModel.getCurrentScore();
        
        gameModel.pause();
        
        // Call tick multiple times
        for (int i = 0; i < 10; i++) {
            gameModel.tick();
        }
        
        // Score should not change when paused
        assertEquals(initialScore, gameModel.getCurrentScore(), 
            "Score should not change when game is paused");
    }
    
    @Test
    @DisplayName("Should update game state when resumed")
    void testTickUpdatesAfterResume() {
        gameModel.start();
        gameModel.pause();
        gameModel.resume();
        
        int initialScore = gameModel.getCurrentScore();
        
        // Call tick once
        gameModel.tick();
        
        // Score should increase after resume
        assertTrue(gameModel.getCurrentScore() > initialScore, 
            "Score should increase after game is resumed");
    }
    
    @Test
    @DisplayName("Should reset pause state when game resets")
    void testPauseStateResetOnGameReset() {
        gameModel.start();
        gameModel.pause();
        
        assertTrue(gameModel.isPaused(), "Game should be paused before reset");
        
        gameModel.reset();
        
        assertFalse(gameModel.isPaused(), "Pause state should be cleared after reset");
        assertTrue(gameModel.isRunning(), "Game should be running after reset");
    }
    
    @Test
    @DisplayName("Should not allow double pause")
    void testDoublePauseHasNoEffect() {
        gameModel.start();
        gameModel.pause();
        
        assertTrue(gameModel.isPaused(), "Game should be paused");
        
        // Try to pause again
        gameModel.pause();
        
        assertTrue(gameModel.isPaused(), "Game should still be paused");
    }
    
    @Test
    @DisplayName("Should not allow resume when not paused")
    void testResumeWhenNotPausedHasNoEffect() {
        gameModel.start();
        
        assertFalse(gameModel.isPaused(), "Game should not be paused initially");
        
        // Try to resume when not paused
        gameModel.resume();
        
        assertFalse(gameModel.isPaused(), "Game should still not be paused");
        assertTrue(gameModel.isRunning(), "Game should still be running");
    }
    
    @Test
    @DisplayName("Should handle pause-resume-pause sequence correctly")
    void testMultiplePauseResumeCycles() {
        gameModel.start();
        
        // First cycle
        gameModel.pause();
        assertTrue(gameModel.isPaused(), "Should be paused after first pause");
        
        gameModel.resume();
        assertFalse(gameModel.isPaused(), "Should not be paused after first resume");
        
        // Second cycle
        gameModel.pause();
        assertTrue(gameModel.isPaused(), "Should be paused after second pause");
        
        gameModel.resume();
        assertFalse(gameModel.isPaused(), "Should not be paused after second resume");
        
        assertTrue(gameModel.isRunning(), "Game should still be running");
    }
}
