package com.tron.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TwoPlayerGameModel} class.
 * 
 * <p>This test class validates the Two Player game mode implementation,
 * ensuring proper player setup, score tracking, and game logic for
 * competitive two-player gameplay.</p>
 * 
 * <p>Tests cover:</p>
 * <ul>
 *   <li>Proper initialization of two human players</li>
 *   <li>Independent score tracking for each player</li>
 *   <li>Game reset functionality</li>
 *   <li>Player movement and collision detection</li>
 *   <li>Game termination conditions</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see TwoPlayerGameModel
 */
@DisplayName("TwoPlayerGameModel Tests")
public class TwoPlayerGameModelTest {

    private TwoPlayerGameModel gameModel;
    private static final int MAP_WIDTH = 500;
    private static final int MAP_HEIGHT = 500;
    private static final int VELOCITY = 3;

    @BeforeEach
    void setUp() {
        gameModel = new TwoPlayerGameModel(MAP_WIDTH, MAP_HEIGHT, VELOCITY);
        gameModel.reset();
    }

    /**
     * Test Case: TwoPlayerGameModelTest.testConstructorInitialization()
     * 
     * Tests that the constructor properly initializes the game model.
     * 
     * Class and Method under test: TwoPlayerGameModel(int, int, int)
     * Test Inputs/Preconditions: Valid map dimensions and velocity
     * Expected Outcome: Game model is created with correct dimensions
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testConstructorInitialization - Constructor creates valid game model")
    void testConstructorInitialization() {
        // Arrange & Act
        TwoPlayerGameModel model = new TwoPlayerGameModel(MAP_WIDTH, MAP_HEIGHT, VELOCITY);

        // Assert
        assertNotNull(model, "Game model should not be null");
        assertEquals(MAP_WIDTH, model.getMapWidth(), "Map width should match constructor parameter");
        assertEquals(MAP_HEIGHT, model.getMapHeight(), "Map height should match constructor parameter");
    }

    /**
     * Test Case: TwoPlayerGameModelTest.testResetCreatesTwoPlayers()
     * 
     * Tests that reset() creates exactly two human players.
     * 
     * Class and Method under test: TwoPlayerGameModel.reset()
     * Test Inputs/Preconditions: Fresh game model
     * Expected Outcome: Two non-null human player instances created
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testResetCreatesTwoPlayers - Reset creates two human players")
    void testResetCreatesTwoPlayers() {
        // Arrange & Act (reset called in setUp)
        Player[] players = gameModel.getPlayers();

        // Assert
        assertNotNull(players, "Players array should not be null");
        assertEquals(2, players.length, "Should have exactly 2 players");
        assertNotNull(players[0], "Player 1 should not be null");
        assertNotNull(players[1], "Player 2 should not be null");
        assertTrue(players[0] instanceof PlayerHuman, "Player 1 should be human");
        assertTrue(players[1] instanceof PlayerHuman, "Player 2 should be human");
    }

    /**
     * Test Case: TwoPlayerGameModelTest.testInitialScoresAreZero()
     * 
     * Tests that both player scores start at zero after reset.
     * 
     * Class and Method under test: TwoPlayerGameModel.reset()
     * Test Inputs/Preconditions: Fresh game model after reset
     * Expected Outcome: Both scores initialized to 0
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testInitialScoresAreZero - Both scores start at zero")
    void testInitialScoresAreZero() {
        // Arrange & Act (reset called in setUp)

        // Assert
        assertEquals(0, gameModel.getCurrentScore(), "Player 1 score should start at 0");
        assertEquals(0, gameModel.getPlayer2Score(), "Player 2 score should start at 0");
    }

    /**
     * Test Case: TwoPlayerGameModelTest.testGameStartsRunning()
     * 
     * Tests that game starts in running state after reset.
     * 
     * Class and Method under test: TwoPlayerGameModel.reset()
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
     * Test Case: TwoPlayerGameModelTest.testPlayersHaveUniqueColors()
     * 
     * Tests that two players are assigned different colors.
     * 
     * Class and Method under test: TwoPlayerGameModel.reset()
     * Test Inputs/Preconditions: Fresh game model after reset
     * Expected Outcome: Player 1 and Player 2 have different colors
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testPlayersHaveUniqueColors - Players have different colors")
    void testPlayersHaveUniqueColors() {
        // Arrange & Act
        Player[] players = gameModel.getPlayers();

        // Assert
        assertNotNull(players[0].getColor(), "Player 1 color should not be null");
        assertNotNull(players[1].getColor(), "Player 2 color should not be null");
        assertFalse(players[0].getColor().equals(players[1].getColor()),
                "Players should have different colors");
    }

    /**
     * Test Case: TwoPlayerGameModelTest.testTickExecutesWithoutError()
     * 
     * Tests that tick() executes without throwing exceptions.
     * 
     * Class and Method under test: TwoPlayerGameModel.tick()
     * Test Inputs/Preconditions: Game running with two players
     * Expected Outcome: No exceptions thrown during tick execution
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testTickExecutesWithoutError - Tick executes successfully")
    void testTickExecutesWithoutError() {
        // Arrange & Act & Assert
        try {
            gameModel.tick();
            assertTrue(true, "Tick should execute without exceptions");
        } catch (Exception e) {
            assertTrue(false, "Tick should not throw exceptions: " + e.getMessage());
        }
    }

    /**
     * Test Case: TwoPlayerGameModelTest.testStopGame()
     * 
     * Tests that stop() halts the game.
     * 
     * Class and Method under test: TwoPlayerGameModel.stop()
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
        assertFalse(gameModel.isRunning(), "Game should stop after stop() call");
    }

    /**
     * Test Case: TwoPlayerGameModelTest.testPlayer2ControlMethods()
     * 
     * Tests that player 2 control methods execute without errors.
     * 
     * Class and Method under test: TwoPlayerGameModel player2 control methods
     * Test Inputs/Preconditions: Running game with player 2
     * Expected Outcome: Control methods execute without exceptions
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testPlayer2ControlMethods - Player 2 controls work")
    void testPlayer2ControlMethods() {
        // Arrange & Act & Assert
        try {
            gameModel.player2Left();
            gameModel.player2Right();
            gameModel.player2Up();
            gameModel.player2Down();
            assertTrue(true, "Player 2 controls should execute without exceptions");
        } catch (Exception e) {
            assertTrue(false, "Player 2 controls should not throw exceptions: " + e.getMessage());
        }
    }

    /**
     * Test Case: TwoPlayerGameModelTest.testPlayer2ScoreGetter()
     * 
     * Tests that getPlayer2Score() returns the correct value.
     * 
     * Class and Method under test: TwoPlayerGameModel.getPlayer2Score()
     * Test Inputs/Preconditions: Fresh game model
     * Expected Outcome: Returns 0 initially
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testPlayer2ScoreGetter - getPlayer2Score returns correct value")
    void testPlayer2ScoreGetter() {
        // Arrange & Act
        int score = gameModel.getPlayer2Score();

        // Assert
        assertEquals(0, score, "Initial player 2 score should be 0");
    }

    /**
     * Test Case: TwoPlayerGameModelTest.testBothPlayersAliveInitially()
     * 
     * Tests that both players start alive.
     * 
     * Class and Method under test: TwoPlayerGameModel.reset()
     * Test Inputs/Preconditions: Fresh game model after reset
     * Expected Outcome: Both players have alive status true
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testBothPlayersAliveInitially - Both players start alive")
    void testBothPlayersAliveInitially() {
        // Arrange & Act
        Player[] players = gameModel.getPlayers();

        // Assert
        assertTrue(players[0].getAlive(), "Player 1 should be alive initially");
        assertTrue(players[1].getAlive(), "Player 2 should be alive initially");
    }
}
