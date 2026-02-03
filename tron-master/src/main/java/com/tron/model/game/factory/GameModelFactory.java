package com.tron.model.game.factory;

import com.tron.model.game.TronGameModel;

/**
 * Abstract Factory for creating TronGameModel instances.
 * 
 * This factory provides a standard interface for creating game models
 * for different game modes (Two-Player, Survival, Story, etc.).
 * By using this factory pattern, we achieve:
 * 
 * - Decoupling: Clients don't need to know concrete model class names
 * - Extensibility: New game modes can be added by creating new factory implementations
 * - Consistency: All game models are created through a unified interface
 * - Maintainability: Changes to model creation logic are centralized in factories
 * 
 * Design Pattern: Factory Method Pattern (Creational)
 * - Defines an interface for creating TronGameModel objects
 * - Lets subclasses decide which concrete TronGameModel class to instantiate
 * - Delegates object creation to subclasses through abstract createGameModel() method
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0 (Factory Method Pattern Implementation)
 */
public abstract class GameModelFactory {
    
    /**
     * Factory method to create a TronGameModel instance.
     * 
     * This method is implemented by concrete factories to create
     * specific game model types. This allows subclasses to determine
     * the exact model class that gets instantiated.
     * 
     * @return A new TronGameModel instance for the specific game mode
     */
    public abstract TronGameModel createGameModel();
    
    /**
     * Template method that initializes a game model.
     * 
     * This method provides a standard initialization flow:
     * 1. Create the model using the factory method
     * 2. Initialize the model (reset players, set initial state)
     * 3. Return the initialized model
     * 
     * Subclasses can override this method to add mode-specific initialization logic.
     * 
     * @return An initialized TronGameModel ready for use
     */
    public TronGameModel initializeGame() {
        TronGameModel model = createGameModel();
        model.reset();
        return model;
    }
}
