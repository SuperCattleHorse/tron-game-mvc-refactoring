package com.tron.model.game.decorator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tron.model.data.DrawData;
import com.tron.model.game.AIBehaviorStrategy;
import com.tron.model.game.HumanBehaviorStrategy;
import com.tron.model.game.Player;
import com.tron.model.game.PlayerAI;
import com.tron.model.game.PlayerBehaviorStrategy;
import com.tron.model.game.PlayerHuman;
import com.tron.model.util.PlayerColor;

/**
 * Integration Tests for Decorator Pattern with Player Classes
 * 
 * This test class verifies that the Decorator Pattern integrates correctly with
 * Player, PlayerAI, and PlayerHuman classes. It tests:
 * - Players can use decorated strategies
 * - Strategy replacement with decorators works correctly
 * - Decorator chains can be applied to players
 * - Player movement works with decorated strategies
 * - getBehaviorStrategy() returns correct decorated strategy
 * - setBehaviorStrategy() correctly replaces strategies
 * - PlayerAI.unwrapToBaseStrategy() works correctly
 * - Multiple decorators can be stacked
 * 
 * Testing Philosophy:
 * Integration tests ensure that the Decorator Pattern implementation doesn't break
 * existing game mechanics while providing new flexibility for runtime behavior enhancement.
 * 
 * @author Test Development Team
 * @see BehaviorStrategyDecorator
 * @see LoggingBehaviorDecorator
 * @see PlayerAI
 * @see PlayerHuman
 */
@DisplayName("Player-Decorator Integration Tests")
public class PlayerDecoratorIntegrationTest {
	
	/**
	 * AI player instance for testing.
	 */
	private PlayerAI aiPlayer;
	
	/**
	 * Human player instance for testing.
	 */
	private PlayerHuman humanPlayer;
	
	/**
	 * Set up test fixtures before each test case.
	 * Initializes AI and Human players at default positions.
	 */
	@BeforeEach
	void setUp() {
		aiPlayer = new PlayerAI(250, 250, 3, 0, PlayerColor.RED);
		aiPlayer.setBounds(500, 500);
		
		humanPlayer = new PlayerHuman(100, 100, 0, 0, PlayerColor.BLUE);
		humanPlayer.setBounds(500, 500);
	}
	
	/**
	 * Test Case: TC-INTEGRATION-DEC-001
	 * Verifies that PlayerAI has initial behavior strategy.
	 * 
	 * Scenario:
	 * - Create PlayerAI
	 * - Call getBehaviorStrategy()
	 * - Verify strategy exists and is AIBehaviorStrategy
	 * 
	 * Expected Result:
	 * - getBehaviorStrategy() returns non-null
	 * - Returned strategy is AIBehaviorStrategy
	 */
	@Test
	@DisplayName("PlayerAI should have initial AIBehaviorStrategy")
	void testPlayerAIHasInitialStrategy() {
		// Act
		PlayerBehaviorStrategy strategy = aiPlayer.getBehaviorStrategy();
		
		// Assert
		assertNotNull(strategy, "AI player should have a behavior strategy");
		assertInstanceOf(AIBehaviorStrategy.class, strategy,
		                "Initial strategy should be AIBehaviorStrategy");
	}
	
	/**
	 * Test Case: TC-INTEGRATION-DEC-002
	 * Verifies that PlayerHuman has initial behavior strategy.
	 * 
	 * Scenario:
	 * - Create PlayerHuman
	 * - Call getBehaviorStrategy()
	 * - Verify strategy exists and is HumanBehaviorStrategy
	 * 
	 * Expected Result:
	 * - getBehaviorStrategy() returns non-null
	 * - Returned strategy is HumanBehaviorStrategy
	 */
	@Test
	@DisplayName("PlayerHuman should have initial HumanBehaviorStrategy")
	void testPlayerHumanHasInitialStrategy() {
		// Act
		PlayerBehaviorStrategy strategy = humanPlayer.getBehaviorStrategy();
		
		// Assert
		assertNotNull(strategy, "Human player should have a behavior strategy");
		assertInstanceOf(HumanBehaviorStrategy.class, strategy,
		                "Initial strategy should be HumanBehaviorStrategy");
	}
	
	/**
	 * Test Case: TC-INTEGRATION-DEC-003
	 * Verifies that PlayerAI can use a decorated strategy.
	 * 
	 * Scenario:
	 * - Get AI's initial strategy
	 * - Wrap it with LoggingBehaviorDecorator
	 * - Set decorated strategy back to player
	 * - Execute move
	 * - Verify logging occurred
	 * 
	 * Expected Result:
	 * - Player accepts decorated strategy
	 * - Move executes successfully
	 * - Decorator tracks method calls
	 */
	@Test
	@DisplayName("PlayerAI should work with decorated strategy")
	void testPlayerAIWithDecoratedStrategy() {
		// Arrange
		PlayerBehaviorStrategy originalStrategy = aiPlayer.getBehaviorStrategy();
		LoggingBehaviorDecorator decorator = new LoggingBehaviorDecorator(originalStrategy);
		aiPlayer.setBehaviorStrategy(decorator);
		
		Player[] players = {aiPlayer};
		aiPlayer.addPlayers(players);
		
		// Act
		aiPlayer.move();
		
		// Assert
		assertEquals(1, decorator.getMoveDecisionCount(),
		            "Decorator should track move decision");
		DrawData drawData = aiPlayer.getDrawData();
		assertTrue(drawData.getX() >= 0 && drawData.getX() <= 500,
		          "Player should remain within bounds");
	}
	
	/**
	 * Test Case: TC-INTEGRATION-DEC-004
	 * Verifies that PlayerHuman can use a decorated strategy.
	 * 
	 * Scenario:
	 * - Get Human's initial strategy
	 * - Wrap it with LoggingBehaviorDecorator
	 * - Set decorated strategy back to player
	 * - Set velocity and execute move
	 * - Verify logging occurred
	 * 
	 * Expected Result:
	 * - Player accepts decorated strategy
	 * - Move executes successfully
	 * - Decorator tracks method calls
	 */
	@Test
	@DisplayName("PlayerHuman should work with decorated strategy")
	void testPlayerHumanWithDecoratedStrategy() {
		// Arrange
		PlayerBehaviorStrategy originalStrategy = humanPlayer.getBehaviorStrategy();
		LoggingBehaviorDecorator decorator = new LoggingBehaviorDecorator(originalStrategy);
		humanPlayer.setBehaviorStrategy(decorator);
		
		// Set velocity
		humanPlayer.setXVelocity(3);
		humanPlayer.setYVelocity(0);
		
		// Act
		humanPlayer.move();
		
		// Assert
		assertEquals(1, decorator.getMoveDecisionCount(),
		            "Decorator should track move decision");
	}
	
	/**
	 * Test Case: TC-INTEGRATION-DEC-005
	 * Verifies that setBehaviorStrategy correctly replaces strategy.
	 * 
	 * Scenario:
	 * - Get initial strategy
	 * - Create decorated strategy
	 * - Set decorated strategy
	 * - Verify getBehaviorStrategy() returns decorated strategy
	 * 
	 * Expected Result:
	 * - getBehaviorStrategy() returns the decorated strategy
	 * - Strategy replacement is successful
	 */
	@Test
	@DisplayName("setBehaviorStrategy should replace strategy correctly")
	void testSetBehaviorStrategyReplacement() {
		// Arrange
		PlayerBehaviorStrategy originalStrategy = aiPlayer.getBehaviorStrategy();
		LoggingBehaviorDecorator decorator = new LoggingBehaviorDecorator(originalStrategy);
		
		// Act
		aiPlayer.setBehaviorStrategy(decorator);
		PlayerBehaviorStrategy retrieved = aiPlayer.getBehaviorStrategy();
		
		// Assert
		assertSame(decorator, retrieved,
		          "getBehaviorStrategy should return the decorated strategy");
		assertInstanceOf(LoggingBehaviorDecorator.class, retrieved,
		                "Retrieved strategy should be LoggingBehaviorDecorator");
	}
	
	/**
	 * Test Case: TC-INTEGRATION-DEC-006
	 * Verifies that PlayerAI.addPlayers works with decorated strategy.
	 * 
	 * Scenario:
	 * - Decorate AI's strategy
	 * - Call addPlayers()
	 * - Verify no exceptions
	 * - Execute move and verify behavior
	 * 
	 * Expected Result:
	 * - addPlayers() completes without error
	 * - Decorator unwrapping works correctly
	 * - AI can still detect other players
	 */
	@Test
	@DisplayName("PlayerAI.addPlayers should work with decorated strategy")
	void testAddPlayersWithDecoratedStrategy() {
		// Arrange
		PlayerBehaviorStrategy originalStrategy = aiPlayer.getBehaviorStrategy();
		LoggingBehaviorDecorator decorator = new LoggingBehaviorDecorator(originalStrategy);
		aiPlayer.setBehaviorStrategy(decorator);
		
		PlayerHuman opponent = new PlayerHuman(400, 250, -3, 0, PlayerColor.GREEN);
		Player[] players = {aiPlayer, opponent};
		
		// Act & Assert - Should not throw exception
		assertDoesNotThrow(() -> aiPlayer.addPlayers(players),
		                  "addPlayers should work with decorated strategy");
		
		// Verify AI can still move
		assertDoesNotThrow(() -> aiPlayer.move(),
		                  "AI should be able to move after addPlayers");
	}
	
	/**
	 * Test Case: TC-INTEGRATION-DEC-007
	 * Verifies that multiple decorators can be stacked.
	 * 
	 * Scenario:
	 * - Get initial strategy
	 * - Wrap with first LoggingBehaviorDecorator
	 * - Wrap again with second LoggingBehaviorDecorator
	 * - Set to player
	 * - Execute move
	 * - Verify both decorators track calls
	 * 
	 * Expected Result:
	 * - Multiple decorators can be stacked
	 * - All decorators in chain track calls
	 * - Player behavior works correctly
	 */
	@Test
	@DisplayName("Multiple decorators can be stacked")
	void testMultipleDecoratorStacking() {
		// Arrange
		PlayerBehaviorStrategy baseStrategy = aiPlayer.getBehaviorStrategy();
		LoggingBehaviorDecorator decorator1 = new LoggingBehaviorDecorator(baseStrategy);
		LoggingBehaviorDecorator decorator2 = new LoggingBehaviorDecorator(decorator1);
		
		aiPlayer.setBehaviorStrategy(decorator2);
		Player[] players = {aiPlayer};
		aiPlayer.addPlayers(players);
		
		// Act
		aiPlayer.move();
		
		// Assert
		assertEquals(1, decorator1.getMoveDecisionCount(),
		            "Inner decorator should track call");
		assertEquals(1, decorator2.getMoveDecisionCount(),
		            "Outer decorator should track call");
	}
	
	/**
	 * Test Case: TC-INTEGRATION-DEC-008
	 * Verifies decorator chain traversal with getDecoratedStrategy().
	 * 
	 * Scenario:
	 * - Create 3-layer decorator chain
	 * - Set to player
	 * - Traverse chain using getDecoratedStrategy()
	 * - Verify correct unwrapping
	 * 
	 * Expected Result:
	 * - Can traverse through all decorator layers
	 * - Eventually reaches base strategy
	 */
	@Test
	@DisplayName("Decorator chain can be traversed")
	void testDecoratorChainTraversal() {
		// Arrange
		PlayerBehaviorStrategy baseStrategy = aiPlayer.getBehaviorStrategy();
		LoggingBehaviorDecorator layer1 = new LoggingBehaviorDecorator(baseStrategy);
		LoggingBehaviorDecorator layer2 = new LoggingBehaviorDecorator(layer1);
		LoggingBehaviorDecorator layer3 = new LoggingBehaviorDecorator(layer2);
		
		aiPlayer.setBehaviorStrategy(layer3);
		
		// Act - Traverse chain
		PlayerBehaviorStrategy current = aiPlayer.getBehaviorStrategy();
		int depth = 0;
		
		while (current instanceof BehaviorStrategyDecorator) {
			depth++;
			current = ((BehaviorStrategyDecorator) current).getDecoratedStrategy();
		}
		
		// Assert
		assertEquals(3, depth, "Should traverse 3 decorator layers");
		assertInstanceOf(AIBehaviorStrategy.class, current,
		                "Should reach base AIBehaviorStrategy");
	}
	
	/**
	 * Test Case: TC-INTEGRATION-DEC-009
	 * Verifies that Human player movement works with decorated strategy.
	 * 
	 * Scenario:
	 * - Decorate Human's strategy
	 * - Set velocity (external input simulation)
	 * - Execute multiple moves
	 * - Verify decorator tracking
	 * 
	 * Expected Result:
	 * - Movement executes without errors
	 * - Decorator tracks all move decisions
	 * - HumanBehaviorStrategy's decideMoveDirection is called (even if it's a no-op)
	 * 
	 * Note: HumanBehaviorStrategy.decideMoveDirection() is empty by design,
	 * as human players are controlled by external input. This test verifies
	 * the decorator correctly wraps and tracks the strategy calls.
	 */
	@Test
	@DisplayName("Human player movement should work with decorated strategy")
	void testHumanMovementWithDecorator() {
		// Arrange
		PlayerBehaviorStrategy originalStrategy = humanPlayer.getBehaviorStrategy();
		LoggingBehaviorDecorator decorator = new LoggingBehaviorDecorator(originalStrategy);
		humanPlayer.setBehaviorStrategy(decorator);
		
		humanPlayer.setXVelocity(3);
		humanPlayer.setYVelocity(0);
		
		// Act
		humanPlayer.move();
		humanPlayer.move();
		humanPlayer.move();
		
		// Assert - Verify decorator tracks calls correctly
		assertEquals(3, decorator.getMoveDecisionCount(),
		            "Should track 3 move decisions");
		
		// Verify player state is valid (no exceptions during decorated moves)
		DrawData drawData = humanPlayer.getDrawData();
		assertNotNull(drawData, "DrawData should not be null");
		assertTrue(drawData.getX() >= 0 && drawData.getX() <= 500,
		          "Player should remain within X bounds");
	}
	
	/**
	 * Test Case: TC-INTEGRATION-DEC-010
	 * Verifies that AI movement works correctly with decorated strategy.
	 * 
	 * Scenario:
	 * - Decorate AI's strategy
	 * - Execute multiple moves
	 * - Verify movement and logging
	 * 
	 * Expected Result:
	 * - AI moves correctly with decorated strategy
	 * - Decorator tracks all decisions
	 * - AI stays within bounds
	 */
	@Test
	@DisplayName("AI movement should work with decorated strategy")
	void testAIMovementWithDecorator() {
		// Arrange
		PlayerBehaviorStrategy originalStrategy = aiPlayer.getBehaviorStrategy();
		LoggingBehaviorDecorator decorator = new LoggingBehaviorDecorator(originalStrategy);
		aiPlayer.setBehaviorStrategy(decorator);
		
		Player[] players = {aiPlayer};
		aiPlayer.addPlayers(players);
		
		// Act
		for (int i = 0; i < 10; i++) {
			aiPlayer.move();
		}
		
		// Assert
		assertEquals(10, decorator.getMoveDecisionCount(),
		            "Should track 10 move decisions");
		DrawData drawData = aiPlayer.getDrawData();
		assertTrue(drawData.getX() >= 0 && drawData.getX() <= 500,
		          "AI should stay within X bounds");
		assertTrue(drawData.getY() >= 0 && drawData.getY() <= 500,
		          "AI should stay within Y bounds");
	}
	
	/**
	 * Test Case: TC-INTEGRATION-DEC-011
	 * Verifies that strategy can be replaced at runtime.
	 * 
	 * Scenario:
	 * - Start with undecorated strategy
	 * - Execute some moves
	 * - Replace with decorated strategy mid-game
	 * - Execute more moves
	 * - Verify decorator only tracks calls after replacement
	 * 
	 * Expected Result:
	 * - Strategy can be swapped at runtime
	 * - Decorator only tracks post-replacement calls
	 * - Game continues normally
	 */
	@Test
	@DisplayName("Strategy can be replaced at runtime")
	void testRuntimeStrategyReplacement() {
		// Arrange
		Player[] players = {aiPlayer};
		aiPlayer.addPlayers(players);
		
		// Act - Move with original strategy
		aiPlayer.move();
		aiPlayer.move();
		
		// Replace with decorated strategy
		PlayerBehaviorStrategy originalStrategy = aiPlayer.getBehaviorStrategy();
		LoggingBehaviorDecorator decorator = new LoggingBehaviorDecorator(originalStrategy);
		aiPlayer.setBehaviorStrategy(decorator);
		
		// Verify decorator starts at 0
		assertEquals(0, decorator.getMoveDecisionCount(),
		            "Decorator should start with 0 count");
		
		// Move with decorated strategy
		aiPlayer.move();
		aiPlayer.move();
		aiPlayer.move();
		
		// Assert
		assertEquals(3, decorator.getMoveDecisionCount(),
		            "Decorator should track only post-replacement moves");
	}
	
	/**
	 * Test Case: TC-INTEGRATION-DEC-012
	 * Verifies that decorator statistics can be reset mid-game.
	 * 
	 * Scenario:
	 * - Use decorated strategy
	 * - Execute some moves
	 * - Reset decorator counters
	 * - Execute more moves
	 * - Verify counters restart from zero
	 * 
	 * Expected Result:
	 * - resetCounters() clears statistics
	 * - New moves increment from zero
	 * - Game behavior unchanged
	 */
	@Test
	@DisplayName("Decorator statistics can be reset mid-game")
	void testDecoratorStatisticsReset() {
		// Arrange
		PlayerBehaviorStrategy originalStrategy = aiPlayer.getBehaviorStrategy();
		LoggingBehaviorDecorator decorator = new LoggingBehaviorDecorator(originalStrategy);
		aiPlayer.setBehaviorStrategy(decorator);
		
		Player[] players = {aiPlayer};
		aiPlayer.addPlayers(players);
		
		// Act - Initial moves
		aiPlayer.move();
		aiPlayer.move();
		aiPlayer.move();
		
		assertEquals(3, decorator.getMoveDecisionCount(),
		            "Should have count 3 before reset");
		
		// Reset counters
		decorator.resetCounters();
		
		// More moves
		aiPlayer.move();
		aiPlayer.move();
		
		// Assert
		assertEquals(2, decorator.getMoveDecisionCount(),
		            "Should have count 2 after reset");
	}
	
	/**
	 * Test Case: TC-INTEGRATION-DEC-013
	 * Verifies that two players can have independent decorated strategies.
	 * 
	 * Scenario:
	 * - Create two AI players
	 * - Give each a separate decorated strategy
	 * - Execute moves on both
	 * - Verify decorators track independently
	 * 
	 * Expected Result:
	 * - Each player has independent decorator
	 * - Counters don't interfere
	 * - Both players function correctly
	 */
	@Test
	@DisplayName("Multiple players can have independent decorated strategies")
	void testIndependentDecoratedStrategies() {
		// Arrange
		PlayerAI ai2 = new PlayerAI(400, 400, -3, 0, PlayerColor.GREEN);
		ai2.setBounds(500, 500);
		
		LoggingBehaviorDecorator decorator1 = new LoggingBehaviorDecorator(
			aiPlayer.getBehaviorStrategy()
		);
		LoggingBehaviorDecorator decorator2 = new LoggingBehaviorDecorator(
			ai2.getBehaviorStrategy()
		);
		
		aiPlayer.setBehaviorStrategy(decorator1);
		ai2.setBehaviorStrategy(decorator2);
		
		Player[] players = {aiPlayer, ai2};
		aiPlayer.addPlayers(players);
		ai2.addPlayers(players);
		
		// Act
		aiPlayer.move();
		aiPlayer.move();
		ai2.move();
		
		// Assert
		assertEquals(2, decorator1.getMoveDecisionCount(),
		            "Player 1 decorator should have count 2");
		assertEquals(1, decorator2.getMoveDecisionCount(),
		            "Player 2 decorator should have count 1");
	}
}
