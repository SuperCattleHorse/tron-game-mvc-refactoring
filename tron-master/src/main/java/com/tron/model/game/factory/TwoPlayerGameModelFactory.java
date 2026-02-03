package com.tron.model.game.factory;

import com.tron.model.game.TronGameModel;
import com.tron.model.game.TwoPlayerGameModel;

/**
 * Concrete factory for creating Two-Player game models.
 * 
 * This factory creates TwoPlayerGameModel instances configured for
 * two-player competitive gameplay.
 * 
 * Configuration:
 * - Map dimensions: 500x500 pixels
 * - Player velocity: 3 pixels per frame
 * - Player count: 2 (two human players)
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 2.0 (MVC Complete)
 */
public class TwoPlayerGameModelFactory extends GameModelFactory {
    
    /**
     * Creates a TwoPlayerGameModel configured for two-player mode.
     * 
     * @return A new TwoPlayerGameModel instance with two-player configuration
     */
    @Override
    public TronGameModel createGameModel() {
        return new TwoPlayerGameModel(500, 500, 3);
    }
}
