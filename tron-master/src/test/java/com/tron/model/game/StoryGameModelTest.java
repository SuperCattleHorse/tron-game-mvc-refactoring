package com.tron.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link StoryGameModel} class.
 * 
 * <p>This test class validates the Story mode game implementation,
 * ensuring proper level progression, AI scaling, and score calculation
 * for the single-player story campaign.</p>
 * 
 * <p>Tests cover:</p>
 * <ul>
 *   <li>Initial level configuration and player setup</li>
 *   <li>Level progression mechanics</li>
 *   <li>Score calculation based on defeated AIs</li>
 *   <li>Game state management across levels</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see StoryGameModel
 */
@DisplayName("StoryGameModel Tests")
public class StoryGameModelTest {

    private StoryGameModel gameModel;
    private static final int MAP_WIDTH = 500;
    private static final int MAP_HEIGHT = 500;
    private static final int VELOCITY = 3;

    @BeforeEach
    void setUp() {
        gameModel = new StoryGameModel(MAP_WIDTH, MAP_HEIGHT, VELOCITY);
        gameModel.reset();
    }

    /**
     * Test Case: StoryGameModelTest.testConstructorInitialization()
     * 
     * Tests that the constructor properly initializes the story mode.
     * 
     * Class and Method under test: StoryGameModel(int, int, int)
     * Test Inputs/Preconditions: Valid map dimensions and velocity
     * Expected Outcome: Game model created with level 1 configuration
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testConstructorInitialization - Constructor initializes story mode")
    void testConstructorInitialization() {
        // Arrange & Act
        StoryGameModel model = new StoryGameModel(MAP_WIDTH, MAP_HEIGHT, VELOCITY);

        // Assert
        assertNotNull(model, "Game model should not be null");
        assertEquals(MAP_WIDTH, model.getMapWidth(), "Map width should match constructor parameter");
        assertEquals(MAP_HEIGHT, model.getMapHeight(), "Map height should match constructor parameter");
    }

    /**
     * Test Case: StoryGameModelTest.testInitialLevelIsOne()
     * 
     * Tests that the game starts at level 1.
     * 
     * Class and Method under test: StoryGameModel.getCurrentLevel()
     * Test Inputs/Preconditions: Fresh game model
     * Expected Outcome: Current level is 1
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testInitialLevelIsOne - Game starts at level 1")
    void testInitialLevelIsOne() {
        // Arrange & Act
        int currentLevel = gameModel.getCurrentLevel();

        // Assert
        assertEquals(1, currentLevel, "Game should start at level 1");
    }

    /**
     * Test Case: StoryGameModelTest.testResetCreatesPlayersForLevel()
     * 
     * Tests that reset() creates correct number of players for level 1.
     * 
     * Class and Method under test: StoryGameModel.reset()
     * Test Inputs/Preconditions: Fresh game model
     * Expected Outcome: Players array has 2 players (1 human + 1 AI)
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testResetCreatesPlayersForLevel - Reset creates players for current level")
    void testResetCreatesPlayersForLevel() {
        // Arrange & Act (reset called in setUp)
        Player[] players = gameModel.getPlayers();

        // Assert
        assertNotNull(players, "Players array should not be null");
        assertEquals(2, players.length, "Level 1 should have 2 players (1 human + 1 AI)");
        assertNotNull(players[0], "Human player should not be null");
        assertTrue(players[0] instanceof PlayerHuman, "First player should be human");
    }

    /**
     * Test Case: StoryGameModelTest.testInitialScoreIsZero()
     * 
     * Tests that the score starts at zero.
     * 
     * Class and Method under test: StoryGameModel.reset()
     * Test Inputs/Preconditions: Fresh game model after reset
     * Expected Outcome: Current score is 0
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testInitialScoreIsZero - Score starts at zero")
    void testInitialScoreIsZero() {
        // Arrange & Act (reset called in setUp)

        // Assert
        assertEquals(0, gameModel.getCurrentScore(), "Initial score should be 0");
    }

    /**
     * Test Case: StoryGameModelTest.testGameStartsRunning()
     * 
     * Tests that the game starts in running state.
     * 
     * Class and Method under test: StoryGameModel.reset()
     * Test Inputs/Preconditions: Fresh game model after reset
     * Expected Outcome: isRunning flag is true
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testGameStartsRunning - Game is running after reset")
    void testGameStartsRunning() {
        // Arrange & Act (reset called in setUp)

        // Assert
        assertTrue(gameModel.isRunning(), "Game should be running after reset");
    }

    /**
     * Test Case: StoryGameModelTest.testTickExecutesWithoutError()
     * 
     * Tests that tick() executes without throwing exceptions.
     * 
     * Class and Method under test: StoryGameModel.tick()
     * Test Inputs/Preconditions: Running game
     * Expected Outcome: No exceptions thrown
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testTickExecutesWithoutError - Tick executes successfully")
    void testTickExecutesWithoutError() {
        // Arrange & Act & Assert
        try {
            gameModel.tick();
            // If we reach here, no exception was thrown
            assertTrue(true, "Tick should execute without exceptions");
        } catch (Exception e) {
            assertTrue(false, "Tick should not throw exceptions: " + e.getMessage());
        }
    }

    /**
     * Test Case: StoryGameModelTest.testCompleteLevelMethod()
     * 
     * Tests that completeLevel() method executes correctly.
     * 
     * Class and Method under test: StoryGameModel.completeLevel()
     * Test Inputs/Preconditions: Fresh game model at level 1
     * Expected Outcome: Returns true (more levels available)
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testCompleteLevelMethod - Complete level advances progress")
    void testCompleteLevelMethod() {
        // Arrange & Act
        boolean hasMoreLevels = gameModel.completeLevel();

        // Assert
        assertTrue(hasMoreLevels, "Should have more levels after completing level 1");
        assertTrue(gameModel.getCurrentScore() > 0, "Score should increase after completing level");
    }

    /**
     * Test Case: StoryGameModelTest.testHumanPlayerExists()
     * 
     * Tests that a human player is always present.
     * 
     * Class and Method under test: StoryGameModel.reset()
     * Test Inputs/Preconditions: Fresh game model after reset
     * Expected Outcome: Human player is not null and alive
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testHumanPlayerExists - Human player is created")
    void testHumanPlayerExists() {
        // Arrange & Act
        Player humanPlayer = gameModel.getPlayer();

        // Assert
        assertNotNull(humanPlayer, "Human player should not be null");
        assertTrue(humanPlayer instanceof PlayerHuman, "Player should be human type");
        assertTrue(humanPlayer.getAlive(), "Human player should start alive");
    }

    /**
     * Test Case: StoryGameModelTest.testStopGame()
     * 
     * Tests that stop() halts the game.
     * 
     * Class and Method under test: StoryGameModel.stop()
     * Test Inputs/Preconditions: Running game
     * Expected Outcome: isRunning becomes false
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testStopGame - Stop halts game execution")
    void testStopGame() {
        // Arrange
        assertTrue(gameModel.isRunning(), "Game should start running");

        // Act
        gameModel.stop();

        // Assert
        assertEquals(false, gameModel.isRunning(), "Game should stop after stop() call");
    }
}
