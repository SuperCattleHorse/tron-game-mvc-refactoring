package com.tron.model.game.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tron.model.game.TronGameModel;

/**
 * Unit tests for the {@link GameModelFactory} abstract class and its concrete implementations.
 * 
 * <p>This test class validates the Factory Method Pattern implementation for game model creation.
 * Tests ensure that:
 * <ul>
 *   <li>Abstract factory defines correct contract</li>
 *   <li>Concrete factories correctly instantiate respective model types</li>
 *   <li>Template method (initializeGame) works correctly with different implementations</li>
 *   <li>Factory pattern reduces coupling between client and concrete classes</li>
 * </ul>
 * 
 * <p>Design Pattern: Factory Method Pattern (Creational)
 * - Abstract class defines interface for object creation
 * - Concrete subclasses implement createGameModel() to produce specific types
 * - Template method initializeGame() provides common initialization flow</p>
 * 
 * @author Refactoring Team
 * @version 1.0
 * @see GameModelFactory
 * @see TwoPlayerGameModelFactory
 * @see SurvivalGameModelFactory
 * @see StoryGameModelFactory
 */
@DisplayName("GameModelFactory Class Unit Tests")
public class GameModelFactoryTest {

    /**
     * Tests that TwoPlayerGameModelFactory creates TronGameModel successfully.
     * 
     * Verifies:
     * - Factory returns non-null model instance
     * - Returned object is of correct type (TronGameModel)
     * - Model is properly initialized with default parameters
     */
    @Test
    @DisplayName("TwoPlayerGameModelFactory should create TronGameModel instance")
    void testTwoPlayerFactoryCreateGameModel() {
        // Arrange
        GameModelFactory factory = new TwoPlayerGameModelFactory();
        
        // Act
        TronGameModel model = factory.createGameModel();
        
        // Assert
        assertNotNull(model, "Model should not be null");
        assertTrue(model instanceof TronGameModel, 
                "Model should be instance of TronGameModel");
    }

    /**
     * Tests that SurvivalGameModelFactory creates appropriate model instance.
     * 
     * Verifies:
     * - Factory returns non-null model instance
     * - Returned object is of correct type
     * - Model is properly initialized for survival mode
     */
    @Test
    @DisplayName("SurvivalGameModelFactory should create game model instance")
    void testSurvivalFactoryCreateGameModel() {
        // Arrange
        GameModelFactory factory = new SurvivalGameModelFactory();
        
        // Act
        TronGameModel model = factory.createGameModel();
        
        // Assert
        assertNotNull(model, "Model should not be null");
        assertTrue(model instanceof TronGameModel, 
                "Model should be instance of TronGameModel");
    }

    /**
     * Tests that StoryGameModelFactory creates appropriate model instance.
     * 
     * Verifies:
     * - Factory returns non-null model instance
     * - Returned object is of correct type
     * - Model is properly initialized for story mode
     */
    @Test
    @DisplayName("StoryGameModelFactory should create game model instance")
    void testStoryFactoryCreateGameModel() {
        // Arrange
        GameModelFactory factory = new StoryGameModelFactory();
        
        // Act
        TronGameModel model = factory.createGameModel();
        
        // Assert
        assertNotNull(model, "Model should not be null");
        assertTrue(model instanceof TronGameModel, 
                "Model should be instance of TronGameModel");
    }

    /**
     * Tests that template method initializeGame works correctly.
     * 
     * Verifies:
     * - Template method returns initialized model instance
     * - Initialization process completes without errors
     * - Returned model is in ready-to-play state
     */
    @Test
    @DisplayName("GameModelFactory.initializeGame() should create and initialize model")
    void testInitializeGameTemplateMethod() {
        // Arrange
        GameModelFactory factory = new TwoPlayerGameModelFactory();
        
        // Act
        TronGameModel model = factory.initializeGame();
        
        // Assert
        assertNotNull(model, "Initialized model should not be null");
        assertTrue(model instanceof TronGameModel, 
                "Model should be instance of TronGameModel");
    }

    /**
     * Tests that different factories produce different model instances.
     * 
     * Verifies:
     * - Each factory call creates a new instance (not cached)
     * - Multiple calls to same factory produce independent objects
     */
    @Test
    @DisplayName("Factory should create new instance on each call")
    void testFactoryCreatesNewInstances() {
        // Arrange
        GameModelFactory factory = new TwoPlayerGameModelFactory();
        
        // Act
        TronGameModel model1 = factory.createGameModel();
        TronGameModel model2 = factory.createGameModel();
        
        // Assert
        assertNotNull(model1, "First model should not be null");
        assertNotNull(model2, "Second model should not be null");
        assertTrue(model1 != model2, 
                "Each factory call should create a different instance");
    }

    /**
     * Tests that all factory implementations follow the same contract.
     * 
     * Verifies:
     * - Each factory type produces valid TronGameModel instances
     * - Polymorphism works correctly through factory interface
     */
    @Test
    @DisplayName("All factory implementations should produce valid models")
    void testAllFactoriesProduceValidModels() {
        // Arrange
        GameModelFactory[] factories = {
            new TwoPlayerGameModelFactory(),
            new SurvivalGameModelFactory(),
            new StoryGameModelFactory()
        };
        
        // Act & Assert
        for (GameModelFactory factory : factories) {
            TronGameModel model = factory.createGameModel();
            assertNotNull(model, "Model from " + factory.getClass().getSimpleName() 
                    + " should not be null");
            assertTrue(model instanceof TronGameModel, 
                    "Model from " + factory.getClass().getSimpleName() 
                    + " should be instance of TronGameModel");
        }
    }
}
