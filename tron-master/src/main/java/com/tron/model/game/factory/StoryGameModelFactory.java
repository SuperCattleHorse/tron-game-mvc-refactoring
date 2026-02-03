package com.tron.model.game.factory;

import com.tron.model.game.StoryGameModel;
import com.tron.model.game.TronGameModel;

/**
 * Concrete factory for creating Story mode game models.
 * 
 * This factory creates StoryGameModel instances configured for
 * story/campaign mode gameplay with progressive difficulty.
 * 
 * Configuration:
 * - Map dimensions: 500x500 pixels
 * - Player velocity: 3 pixels per frame
 * - Starts with 2 players (one human, one AI)
 * - Progressively adds more AI opponents per level
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 2.0 (MVC Complete)
 */
public class StoryGameModelFactory extends GameModelFactory {
    
    /**
     * Creates a StoryGameModel configured for story mode.
     * 
     * @return A new StoryGameModel instance with story mode configuration
     */
    @Override
    public TronGameModel createGameModel() {
        return new StoryGameModel(500, 500, 3);
    }
}
