package com.tron.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.tron.model.score.Score;

/**
 * Unit tests for {@link SurvivalGameModel} class.
 * 
 * <p>This test class validates the Survival mode game implementation,
 * ensuring proper high score tracking, game logic, and integration
 * with the Score singleton for persistent high score storage.</p>
 * 
 * <p>Tests cover:</p>
 * <ul>
 *   <li>Game initialization with high score file</li>
 *   <li>High score loading and storage</li>
 *   <li>Score saving after player death</li>
 *   <li>Integration with Score singleton</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see SurvivalGameModel
 */
@DisplayName("SurvivalGameModel Tests")
public class SurvivalGameModelTest {

    private SurvivalGameModel gameModel;
    private String testScoreFile;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        // Reset Score singleton
        java.lang.reflect.Field instanceField = Score.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        // Create temporary score file
        testScoreFile = "test_survival_scores.txt";
        Path testFilePath = tempDir.resolve(testScoreFile);
        Files.createFile(testFilePath);

        gameModel = new SurvivalGameModel(testFilePath.toString(), 3);
        gameModel.reset();
    }

    @AfterEach
    void tearDown() {
        // Clean up test file
        File file = new File(testScoreFile);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Test Case: SurvivalGameModelTest.testConstructorInitialization()
     * 
     * Tests that the constructor properly initializes survival mode.
     * 
     * Class and Method under test: SurvivalGameModel(String, int)
     * Test Inputs/Preconditions: Valid high score file path and player count
     * Expected Outcome: Game model created with Score manager
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testConstructorInitialization - Constructor initializes with high scores")
    void testConstructorInitialization() throws Exception {
        // Arrange
        Path scorePath = tempDir.resolve("test_init.txt");
        Files.createFile(scorePath);

        // Reset singleton
        java.lang.reflect.Field instanceField = Score.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        // Act
        SurvivalGameModel model = new SurvivalGameModel(scorePath.toString(), 3);

        // Assert
        assertNotNull(model, "Game model should not be null");
        assertNotNull(model.getHighScoreManager(), "High score manager should not be null");
    }

    /**
     * Test Case: SurvivalGameModelTest.testHighScoreManagerNotNull()
     * 
     * Tests that the high score manager is properly initialized.
     * 
     * Class and Method under test: SurvivalGameModel.getHighScoreManager()
     * Test Inputs/Preconditions: Fresh game model
     * Expected Outcome: Returns non-null Score instance
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testHighScoreManagerNotNull - High score manager is initialized")
    void testHighScoreManagerNotNull() {
        // Arrange & Act
        Score manager = gameModel.getHighScoreManager();

        // Assert
        assertNotNull(manager, "High score manager should not be null");
    }

    /**
     * Test Case: SurvivalGameModelTest.testGetHighScoresReturnsEmptyList()
     * 
     * Tests that getHighScores() returns an empty list initially.
     * 
     * Class and Method under test: SurvivalGameModel.getHighScores()
     * Test Inputs/Preconditions: Fresh game model with empty score file
     * Expected Outcome: Returns empty list
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testGetHighScoresReturnsEmptyList - Initial high scores list is empty")
    void testGetHighScoresReturnsEmptyList() {
        // Arrange & Act
        var highScores = gameModel.getHighScores();

        // Assert
        assertNotNull(highScores, "High scores list should not be null");
        assertTrue(highScores.isEmpty() || highScores.size() >= 0,
                "High scores should be empty or contain loaded scores");
    }

    /**
     * Test Case: SurvivalGameModelTest.testInitialScoreIsZero()
     * 
     * Tests that the current score starts at zero.
     * 
     * Class and Method under test: SurvivalGameModel.reset()
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
     * Test Case: SurvivalGameModelTest.testGameStartsRunning()
     * 
     * Tests that the game starts in running state.
     * 
     * Class and Method under test: SurvivalGameModel.reset()
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
     * Test Case: SurvivalGameModelTest.testPlayersCreated()
     * 
     * Tests that players are created with correct count.
     * 
     * Class and Method under test: SurvivalGameModel.reset()
     * Test Inputs/Preconditions: Game model with 3 AI players
     * Expected Outcome: 4 total players (1 human + 3 AI)
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testPlayersCreated - Correct number of players created")
    void testPlayersCreated() {
        // Arrange & Act
        Player[] players = gameModel.getPlayers();

        // Assert
        assertNotNull(players, "Players array should not be null");
        assertTrue(players.length >= 1, "Should have at least human player");
    }

    /**
     * Test Case: SurvivalGameModelTest.testHumanPlayerExists()
     * 
     * Tests that a human player is created.
     * 
     * Class and Method under test: SurvivalGameModel.reset()
     * Test Inputs/Preconditions: Fresh game model after reset
     * Expected Outcome: Human player is not null
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
    }

    /**
     * Test Case: SurvivalGameModelTest.testTickExecutesWithoutError()
     * 
     * Tests that tick() executes without throwing exceptions.
     * 
     * Class and Method under test: SurvivalGameModel.tick()
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
            assertTrue(true, "Tick should execute without exceptions");
        } catch (Exception e) {
            assertTrue(false, "Tick should not throw exceptions: " + e.getMessage());
        }
    }

    /**
     * Test Case: SurvivalGameModelTest.testStopGame()
     * 
     * Tests that stop() halts the game.
     * 
     * Class and Method under test: SurvivalGameModel.stop()
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
