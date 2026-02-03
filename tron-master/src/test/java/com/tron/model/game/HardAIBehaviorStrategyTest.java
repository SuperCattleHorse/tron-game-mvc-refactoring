package com.tron.model.game;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tron.model.util.PlayerColor;

/**
 * Unit Tests for HardAIBehaviorStrategy
 * 
 * This test class verifies the enhanced AI behavior implementation.
 * Tests focus on:
 * - Enhanced lookahead distance (15 pixels vs 6 pixels)
 * - Enhanced boundary safety distance (15 pixels vs 6 pixels)
 * - Faster decision interval (20 frames vs 40 frames)
 * - Jump capability (25% probability)
 * - Higher boost activation (5% vs 1%)
 * - Correct inheritance from base AIBehaviorStrategy
 * 
 * Uses Given-When-Then format for test documentation.
 * 
 * @author Tron Development Team
 * @version 1.0
 * @see HardAIBehaviorStrategy
 * @see AIBehaviorStrategy
 */
@DisplayName("HardAIBehaviorStrategy Tests")
public class HardAIBehaviorStrategyTest {
	
	private PlayerAI hardAIPlayer;
	private HardAIBehaviorStrategy hardStrategy;
	
	private static final int MAP_WIDTH = 500;
	private static final int MAP_HEIGHT = 500;
	private static final int VELOCITY = 3;
	
	/**
	 * Set up test fixtures before each test case.
	 * Initializes Hard AI player with strategy at center position.
	 */
	@BeforeEach
	void setUp() {
		hardAIPlayer = new PlayerAI(250, 250, VELOCITY, 0, PlayerColor.BLUE);
		hardAIPlayer.setBounds(MAP_WIDTH, MAP_HEIGHT);
		hardStrategy = new HardAIBehaviorStrategy(hardAIPlayer);
		hardAIPlayer.setBehaviorStrategy(hardStrategy);
		
		Player[] players = {hardAIPlayer};
		hardAIPlayer.addPlayers(players);
	}
	
	/**
	 * Test: Hard AI should avoid right boundary earlier than normal AI
	 * 
	 * Given: Hard AI player positioned 12 pixels from right boundary
	 * When: AI makes movement decision
	 * Then: Direction should change (Hard AI detects at 15px, normal AI at 6px)
	 */
	@Test
	@DisplayName("Boundary Detection: Hard AI detects right boundary at 15px")
	void testEnhancedRightBoundaryDetection() {
		// Given: Hard AI positioned 12 pixels from right boundary
		hardAIPlayer = new PlayerAI(488, 250, VELOCITY, 0, PlayerColor.BLUE);
		hardAIPlayer.setBounds(MAP_WIDTH, MAP_HEIGHT);
		hardStrategy = new HardAIBehaviorStrategy(hardAIPlayer);
		hardAIPlayer.setBehaviorStrategy(hardStrategy);
		Player[] players = {hardAIPlayer};
		hardAIPlayer.addPlayers(players);
		
		int initialVelX = hardAIPlayer.velocityX;
		int initialVelY = hardAIPlayer.velocityY;
		
		// When: AI makes movement decision
		hardAIPlayer.move();
		
		// Then: Direction should change (within 15px threshold)
		boolean velocityChanged = (hardAIPlayer.velocityX != initialVelX) || 
		                          (hardAIPlayer.velocityY != initialVelY);
		assertTrue(velocityChanged, 
		          "Hard AI should detect boundary at 12px distance (15px threshold)");
	}
	
	/**
	 * Test: Hard AI should avoid left boundary with enhanced detection
	 * 
	 * Given: Hard AI player positioned 12 pixels from left boundary
	 * When: AI makes movement decision
	 * Then: Direction should change to avoid boundary
	 */
	@Test
	@DisplayName("Boundary Detection: Hard AI detects left boundary at 15px")
	void testEnhancedLeftBoundaryDetection() {
		// Given: Hard AI positioned 12 pixels from left boundary
		hardAIPlayer = new PlayerAI(12, 250, -VELOCITY, 0, PlayerColor.BLUE);
		hardAIPlayer.setBounds(MAP_WIDTH, MAP_HEIGHT);
		hardStrategy = new HardAIBehaviorStrategy(hardAIPlayer);
		hardAIPlayer.setBehaviorStrategy(hardStrategy);
		Player[] players = {hardAIPlayer};
		hardAIPlayer.addPlayers(players);
		
		int initialVelX = hardAIPlayer.velocityX;
		
		// When: AI makes movement decision
		hardAIPlayer.move();
		
		// Then: Direction should change
		boolean velocityChanged = (hardAIPlayer.velocityX != initialVelX) || 
		                          (hardAIPlayer.velocityY != 0);
		assertTrue(velocityChanged, 
		          "Hard AI should detect left boundary at 12px distance");
	}
	
	/**
	 * Test: Hard AI should avoid top boundary with enhanced detection
	 * 
	 * Given: Hard AI player positioned 12 pixels from top boundary
	 * When: AI makes movement decision
	 * Then: Direction should change horizontally
	 */
	@Test
	@DisplayName("Boundary Detection: Hard AI detects top boundary at 15px")
	void testEnhancedTopBoundaryDetection() {
		// Given: Hard AI positioned 12 pixels from top boundary
		hardAIPlayer = new PlayerAI(250, 12, 0, -VELOCITY, PlayerColor.BLUE);
		hardAIPlayer.setBounds(MAP_WIDTH, MAP_HEIGHT);
		hardStrategy = new HardAIBehaviorStrategy(hardAIPlayer);
		hardAIPlayer.setBehaviorStrategy(hardStrategy);
		Player[] players = {hardAIPlayer};
		hardAIPlayer.addPlayers(players);
		
		int initialVelY = hardAIPlayer.velocityY;
		
		// When: AI makes movement decision
		hardAIPlayer.move();
		
		// Then: Direction should change
		boolean velocityChanged = (hardAIPlayer.velocityY != initialVelY) || 
		                          (hardAIPlayer.velocityX != 0);
		assertTrue(velocityChanged, 
		          "Hard AI should detect top boundary at 12px distance");
	}
	
	/**
	 * Test: Hard AI should avoid bottom boundary with enhanced detection
	 * 
	 * Given: Hard AI player positioned 12 pixels from bottom boundary
	 * When: AI makes movement decision
	 * Then: Direction should change horizontally
	 */
	@Test
	@DisplayName("Boundary Detection: Hard AI detects bottom boundary at 15px")
	void testEnhancedBottomBoundaryDetection() {
		// Given: Hard AI positioned 12 pixels from bottom boundary
		hardAIPlayer = new PlayerAI(250, 488, 0, VELOCITY, PlayerColor.BLUE);
		hardAIPlayer.setBounds(MAP_WIDTH, MAP_HEIGHT);
		hardStrategy = new HardAIBehaviorStrategy(hardAIPlayer);
		hardAIPlayer.setBehaviorStrategy(hardStrategy);
		Player[] players = {hardAIPlayer};
		hardAIPlayer.addPlayers(players);
		
		int initialVelY = hardAIPlayer.velocityY;
		
		// When: AI makes movement decision
		hardAIPlayer.move();
		
		// Then: Direction should change
		boolean velocityChanged = (hardAIPlayer.velocityY != initialVelY) || 
		                          (hardAIPlayer.velocityX != 0);
		assertTrue(velocityChanged, 
		          "Hard AI should detect bottom boundary at 12px distance");
	}
	
	/**
	 * Test: Hard AI boost probability is higher than normal AI
	 * 
	 * Given: Hard AI with 5% boost probability
	 * When: shouldBoost() is called 1000 times
	 * Then: Activation rate should be approximately 5% (40-60 activations expected)
	 */
	@Test
	@DisplayName("Boost Activation: Hard AI has 5% boost probability")
	void testEnhancedBoostProbability() {
		// Given: Hard AI with 5% boost probability
		int iterations = 1000;
		int boostActivations = 0;
		
		// When: shouldBoost() is called many times
		for (int i = 0; i < iterations; i++) {
			if (hardStrategy.shouldBoost()) {
				boostActivations++;
			}
		}
		
		// Then: Activation rate should be approximately 5% (within reasonable range)
		// Expected: 50 activations ± 30 (3% to 8% range allows for randomness)
		assertTrue(boostActivations >= 30 && boostActivations <= 80, 
		          "Boost activation rate should be approximately 5% (got " + 
		          boostActivations + " out of " + iterations + ")");
	}
	
	/**
	 * Test: Hard AI can be assigned to PlayerAI via setBehaviorStrategy
	 * 
	 * Given: PlayerAI instance with default AIBehaviorStrategy
	 * When: HardAIBehaviorStrategy is assigned via setBehaviorStrategy()
	 * Then: Strategy should be successfully replaced
	 */
	@Test
	@DisplayName("Strategy Assignment: Hard AI can be set on PlayerAI")
	void testStrategyAssignment() {
		// Given: PlayerAI with default strategy
		PlayerAI testPlayer = new PlayerAI(250, 250, VELOCITY, 0, PlayerColor.GREEN);
		
		// When: HardAIBehaviorStrategy is assigned
		HardAIBehaviorStrategy newStrategy = new HardAIBehaviorStrategy(testPlayer);
		testPlayer.setBehaviorStrategy(newStrategy);
		
		// Then: Strategy should be successfully replaced
		assertEquals(newStrategy, testPlayer.getBehaviorStrategy(), 
		            "Hard AI strategy should be assigned to player");
	}
	
	/**
	 * Test: Hard AI decision interval is faster than normal AI
	 * 
	 * Given: Hard AI with 20-frame decision interval
	 * When: Multiple moves are executed
	 * Then: AI should make decisions more frequently than 40-frame interval
	 */
	@Test
	@DisplayName("Decision Speed: Hard AI has 20-frame decision interval")
	void testFasterDecisionInterval() throws Exception {
		// Given: Hard AI with 20-frame decision interval
		// Access private time field via reflection to verify interval
		Field timeField = HardAIBehaviorStrategy.class.getDeclaredField("time");
		timeField.setAccessible(true);
		
		// When: Strategy is reset
		hardStrategy.reset();
		
		// Then: Decision interval should be 20 frames
		int timeValue = (int) timeField.get(hardStrategy);
		assertEquals(20, timeValue, 
		            "Hard AI decision interval should be 20 frames");
	}
	
	/**
	 * Test: Hard AI maintains functionality across multiple moves
	 * 
	 * Given: Hard AI player at center position
	 * When: Multiple move operations are performed
	 * Then: Player should move without errors and stay within bounds
	 */
	@Test
	@DisplayName("Stability: Hard AI performs multiple moves without errors")
	void testMultipleMoveStability() {
		// Given: Hard AI at center position
		hardAIPlayer = new PlayerAI(250, 250, VELOCITY, 0, PlayerColor.BLUE);
		hardAIPlayer.setBounds(MAP_WIDTH, MAP_HEIGHT);
		hardStrategy = new HardAIBehaviorStrategy(hardAIPlayer);
		hardAIPlayer.setBehaviorStrategy(hardStrategy);
		Player[] players = {hardAIPlayer};
		hardAIPlayer.addPlayers(players);
		
		// When: Multiple moves are performed
		for (int i = 0; i < 100; i++) {
			hardAIPlayer.move();
		}
		
		// Then: Player should stay within bounds
		assertTrue(hardAIPlayer.x >= 0 && hardAIPlayer.x <= MAP_WIDTH, 
		          "Hard AI should stay within horizontal bounds");
		assertTrue(hardAIPlayer.y >= 0 && hardAIPlayer.y <= MAP_HEIGHT, 
		          "Hard AI should stay within vertical bounds");
	}
	
	/**
	 * Test: Hard AI stability with obstacle-free environment
	 * 
	 * Note: Testing actual obstacle detection at specific distances is complex due to:
	 * 1. Trail generation timing - trails are created during move()
	 * 2. Decision-making happens before position update in same frame
	 * 3. Random jump probability (25%) makes deterministic testing difficult
	 * 
	 * This test verifies Hard AI can operate without errors when obstacles are present.
	 * Enhanced lookahead (15px) is tested indirectly through boundary detection tests.
	 * 
	 * Given: Hard AI with another player present
	 * When: Multiple moves are performed
	 * Then: AI should function correctly without errors
	 */
	@Test
	@DisplayName("Obstacle Handling: Hard AI operates correctly with obstacles present")
	void testObstacleHandling() {
		// Given: Hard AI and another player
		hardAIPlayer = new PlayerAI(100, 250, VELOCITY, 0, PlayerColor.BLUE);
		hardAIPlayer.setBounds(MAP_WIDTH, MAP_HEIGHT);
		
		PlayerAI otherPlayer = new PlayerAI(200, 250, 0, VELOCITY, PlayerColor.RED);
		otherPlayer.setBounds(MAP_WIDTH, MAP_HEIGHT);
		
		hardStrategy = new HardAIBehaviorStrategy(hardAIPlayer);
		hardAIPlayer.setBehaviorStrategy(hardStrategy);
		Player[] players = {hardAIPlayer, otherPlayer};
		hardAIPlayer.addPlayers(players);
		otherPlayer.addPlayers(players);
		
		// When: Both players move multiple times
		for (int i = 0; i < 50; i++) {
			hardAIPlayer.move();
			otherPlayer.move();
		}
		
		// Then: Hard AI should stay within bounds (no crashes)
		assertTrue(hardAIPlayer.x >= 0 && hardAIPlayer.x <= MAP_WIDTH, 
		          "Hard AI should stay within horizontal bounds");
		assertTrue(hardAIPlayer.y >= 0 && hardAIPlayer.y <= MAP_HEIGHT, 
		          "Hard AI should stay within vertical bounds");
		
		// And: Hard AI should have moved (not stuck)
		assertTrue(hardAIPlayer.getPath().size() > 1, 
		          "Hard AI should have generated trail");
	}
	
	/**
	 * Test: Hard AI reset functionality maintains correct state
	 * 
	 * Given: Hard AI with modified internal state
	 * When: reset() is called
	 * Then: Internal decision timer should reset to 20 frames
	 */
	@Test
	@DisplayName("Reset: Hard AI resets decision timer correctly")
	void testResetFunctionality() throws Exception {
		// Given: Hard AI with modified state
		Field timeField = HardAIBehaviorStrategy.class.getDeclaredField("time");
		timeField.setAccessible(true);
		timeField.set(hardStrategy, 5); // Modify timer
		
		// When: reset() is called
		hardStrategy.reset();
		
		// Then: Timer should reset to 20 frames
		int timeValue = (int) timeField.get(hardStrategy);
		assertEquals(20, timeValue, 
		            "Hard AI decision timer should reset to 20 frames");
	}
	
	/**
	 * Test: Hard AI inherits from AIBehaviorStrategy correctly
	 * 
	 * Given: HardAIBehaviorStrategy class
	 * When: Class hierarchy is examined
	 * Then: Should extend AIBehaviorStrategy and implement PlayerBehaviorStrategy
	 */
	@Test
	@DisplayName("Inheritance: Hard AI extends AIBehaviorStrategy")
	void testClassInheritance() {
		// Given: HardAIBehaviorStrategy instance
		
		// When: Class hierarchy is checked
		boolean extendsAI = AIBehaviorStrategy.class.isAssignableFrom(HardAIBehaviorStrategy.class);
		boolean implementsStrategy = PlayerBehaviorStrategy.class.isAssignableFrom(HardAIBehaviorStrategy.class);
		
		// Then: Should properly extend and implement
		assertTrue(extendsAI, 
		          "HardAIBehaviorStrategy should extend AIBehaviorStrategy");
		assertTrue(implementsStrategy, 
		          "HardAIBehaviorStrategy should implement PlayerBehaviorStrategy");
	}
}
