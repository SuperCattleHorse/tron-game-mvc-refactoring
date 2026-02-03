package com.tron.model.game.factory;

import com.tron.model.game.SurvivalGameModel;
import com.tron.model.game.TronGameModel;

/**
 * Concrete factory for creating Survival game models.
 * 
 * This factory creates SurvivalGameModel instances configured for
 * single-player survival mode with AI opponents.
 * 
 * Configuration:
 * - High score file path: "HighScores.json"
 * - AI opponent count: 1 (one human vs one AI)
 * - Map dimensions: 500x500 pixels (set by SurvivalGameModel constructor)
 * - Player velocity: 3 pixels per frame (set by SurvivalGameModel constructor)
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0 (Factory Method Pattern Implementation)
 */
public class SurvivalGameModelFactory extends GameModelFactory {
    
    private static final String HIGH_SCORE_FILE = "HighScores.json";
    private static final int AI_OPPONENT_COUNT = 1;
    
    /**
     * Creates a SurvivalGameModel configured for survival mode.
     * 
     * The model is initialized with high score tracking capabilities.
     * 
     * @return A new SurvivalGameModel instance with survival configuration
     */
    @Override
    public TronGameModel createGameModel() {
        return new SurvivalGameModel(HIGH_SCORE_FILE, AI_OPPONENT_COUNT);
    }
}
