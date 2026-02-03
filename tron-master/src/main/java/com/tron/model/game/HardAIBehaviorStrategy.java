package com.tron.model.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.tron.model.util.Shape;

/**
 * Hard AI Behavior Strategy Implementation
 * 
 * An enhanced AI strategy that extends the base AI behavior with improved
 * decision-making capabilities. This strategy is specifically designed for
 * Story Mode to provide increased difficulty through:
 * - Enhanced lookahead distance for earlier obstacle detection
 * - Faster decision-making intervals for more responsive behavior
 * - Jump capability as an additional avoidance option
 * - More aggressive boost usage
 * 
 * Hard AI Parameters (compared to base AI):
 * - Obstacle detection distance: 15 pixels (vs 6 pixels)
 * - Boundary safety distance: 15 pixels (vs 6 pixels)
 * - Decision interval: 20 frames / 0.4s (vs 40 frames / 0.8s)
 * - Avoidance strategy: 75% turn + 25% jump (vs 100% turn)
 * - Boost probability: 5% (vs 1%)
 * 
 * Design Patterns:
 * - Strategy Pattern: Implements PlayerBehaviorStrategy interface
 * - Inheritance: Extends AIBehaviorStrategy for code reuse
 * 
 * SOLID Principles:
 * - Open/Closed: Extends base AI without modifying it
 * - Liskov Substitution: Can replace AIBehaviorStrategy seamlessly
 * - Single Responsibility: Only handles Hard AI decision logic
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see AIBehaviorStrategy
 * @see PlayerBehaviorStrategy
 */
public class HardAIBehaviorStrategy extends AIBehaviorStrategy {
	
	// Hard AI tuning parameters
	private static final int HARD_LOOKAHEAD_DISTANCE = 15;
	private static final int HARD_BOUNDARY_DISTANCE = 15;
	private static final int HARD_DECISION_INTERVAL = 20;
	private static final double HARD_BOOST_PROBABILITY = 0.05;
	private static final double JUMP_PROBABILITY = 0.25;
	
	private final Random rand = new Random();
	private int time = HARD_DECISION_INTERVAL;
	
	/**
	 * Constructs a HardAIBehaviorStrategy for the given AI player.
	 * 
	 * @param player the AI player this strategy controls
	 */
	public HardAIBehaviorStrategy(PlayerAI player) {
		super(player);
	}
	
	/**
	 * Make movement decision for the AI player.
	 * Overrides base implementation to use Hard AI logic.
	 */
	@Override
	public void decideMoveDirection() {
		reactProximityHard();
	}
	
	/**
	 * Determine whether the AI should activate boost.
	 * Hard AI has higher boost probability (2% vs 1%).
	 * 
	 * @return true if boost should be activated, false otherwise
	 */
	@Override
	public boolean shouldBoost() {
		return rand.nextDouble() < HARD_BOOST_PROBABILITY;
	}
	
	/**
	 * Reset the decision timer to initial state.
	 * Called when level restarts or player respawns.
	 */
	@Override
	public void reset() {
		time = HARD_DECISION_INTERVAL;
	}
	
	/**
	 * React to proximity of obstacles and boundaries - Hard AI version.
	 * 
	 * Enhanced decision-making logic with:
	 * 1. Increased lookahead distance (15px) for earlier obstacle detection
	 * 2. Jump capability as alternative to turning (25% probability)
	 * 3. Larger boundary safety margins (15px)
	 * 4. Faster decision interval (20 frames)
	 * 5. Higher boost activation rate (5%)
	 * 
	 * Logic flow:
	 * 1. Check for boost activation
	 * 2. Collect all trail lines for collision detection
	 * 3. Scan for obstacles in movement direction (10px lookahead)
	 * 4. React with jump (20%) or turn (80%)
	 * 5. Check boundary proximity (10px margin)
	 * 6. Make random directional choices if clear
	 */
	private void reactProximityHard() {
		int velocity = Math.max(Math.abs(player.velocityX), Math.abs(player.velocityY));
		
		// Enhanced boost activation (2% chance)
		if (shouldBoost()) {
			player.startBoost();
		}
		
		// Collect all trails from all players for collision detection
		ArrayList<Shape> lines = new ArrayList<>();
		lines.addAll(player.getPath());
		Collections.reverse(lines);
		for (Player p : players) {
			if (p != player) {
				lines.addAll(p.getPath());
			}
		}
		
		// Check for obstacles in the path with enhanced lookahead
		for (int i = lines.size() - 1; i > 0; i--) {
			Shape l = lines.get(i);
			int maxX = Math.max(l.getStartX(), l.getEndX());
			int minX = Math.min(l.getStartX(), l.getEndX());
			int maxY = Math.max(l.getStartY(), l.getEndY());
			int minY = Math.min(l.getStartY(), l.getEndY());
			
			// Moving right: check for vertical lines ahead
			if (player.velocityX > 0 && l.isVertical() && player.y >= minY && player.y <= maxY) {
				if (l.getStartX() - player.x < HARD_LOOKAHEAD_DISTANCE && l.getStartX() - player.x > 0) {
					handleObstacleDetected(lines, velocity, true);
					return;
				}
			}
			
			// Moving left: check for vertical lines ahead
			if (player.velocityX < 0 && l.isVertical() && player.y >= minY && player.y <= maxY) {
				if (player.x - l.getStartX() < HARD_LOOKAHEAD_DISTANCE && player.x - l.getStartX() > 0) {
					handleObstacleDetected(lines, velocity, true);
					return;
				}
			}
			
			// Moving down: check for horizontal lines ahead
			if (player.velocityY > 0 && !l.isVertical() && player.x >= minX && player.x <= maxX) {
				if (l.getStartY() - player.y < HARD_LOOKAHEAD_DISTANCE && l.getStartY() - player.y > 0) {
					handleObstacleDetected(lines, velocity, false);
					return;
				}
			}
			
			// Moving up: check for horizontal lines ahead
			if (player.velocityY < 0 && !l.isVertical() && player.x >= minX && player.x <= maxX) {
				if (player.y - l.getStartY() < HARD_LOOKAHEAD_DISTANCE && player.y - l.getStartY() > 0) {
					handleObstacleDetected(lines, velocity, false);
					return;
				}
			}
		}
		
		// Enhanced boundary detection with larger safety margins
		// Check if too close to left edge
		if (player.x < HARD_BOUNDARY_DISTANCE && player.velocityX != 0) {
			if (player.y < 250) {
				player.velocityY = velocity;
			} else {
				player.velocityY = -velocity;
			}
			player.velocityX = 0;
			time = HARD_DECISION_INTERVAL;
			return;
		}
		
		// Check if too close to right edge
		if (player.rightBound - player.x < HARD_BOUNDARY_DISTANCE && player.velocityX != 0) {
			if (player.y < 250) {
				player.velocityY = velocity;
			} else {
				player.velocityY = -velocity;
			}
			player.velocityX = 0;
			time = HARD_DECISION_INTERVAL;
			return;
		}
		
		// Check if too close to top edge
		if (player.y < HARD_BOUNDARY_DISTANCE && player.velocityY != 0) {
			if (player.x < 250) {
				player.velocityX = velocity;
			} else {
				player.velocityX = -velocity;
			}
			player.velocityY = 0;
			time = HARD_DECISION_INTERVAL;
			return;
		}
		
		// Check if too close to bottom edge
		if (player.bottomBound - player.y < HARD_BOUNDARY_DISTANCE && player.velocityY != 0) {
			if (player.x < 250) {
				player.velocityX = velocity;
			} else {
				player.velocityX = -velocity;
			}
			player.velocityY = 0;
			time = HARD_DECISION_INTERVAL;
			return;
		}
		
		// Make random movement decisions with faster interval
		if (time == 0) {
			int rando = rand.nextInt(4);
			if (rando == 0 && player.velocityX != velocity) {
				if (player.x > HARD_BOUNDARY_DISTANCE) {
					player.velocityX = -velocity;
					player.velocityY = 0;
				}
			} else if (rando == 1 && player.velocityX != -velocity) {
				if (player.rightBound - player.x > HARD_BOUNDARY_DISTANCE) {
					player.velocityX = velocity;
					player.velocityY = 0;
				}
			} else if (rando == 2 && player.velocityY != velocity) {
				if (player.y > HARD_BOUNDARY_DISTANCE) {
					player.velocityX = 0;
					player.velocityY = -velocity;
				}
			} else if (rando == 3 && player.velocityY != -velocity) {
				if (player.bottomBound - player.y > HARD_BOUNDARY_DISTANCE) {
					player.velocityX = 0;
					player.velocityY = velocity;
				}
			}
			time = HARD_DECISION_INTERVAL;
		}
		time--;
	}
	
	/**
	 * Handle obstacle detection with jump or turn decision.
	 * 
	 * Hard AI enhancement: 25% chance to jump over obstacle,
	 * 75% chance to turn (using base AI turn logic).
	 * 
	 * @param lines all trail lines for space checking
	 * @param velocity current movement velocity
	 * @param isHorizontalMovement true if moving horizontally, false if vertically
	 */
	private void handleObstacleDetected(ArrayList<Shape> lines, int velocity, boolean isHorizontalMovement) {
		// 25% chance to jump, 75% chance to turn
		if (rand.nextDouble() < JUMP_PROBABILITY) {
			// Use jump to avoid obstacle
			player.jump();
			time = HARD_DECISION_INTERVAL;
			return;
		}
		
		// Turn logic (80% of the time)
		if (isHorizontalMovement) {
			// Was moving horizontally, turn vertically
			boolean shouldTurnDown = checkSpaceBelow(lines);
			if (shouldTurnDown) {
				player.velocityY = velocity;
			} else {
				player.velocityY = -velocity;
			}
			player.velocityX = 0;
		} else {
			// Was moving vertically, turn horizontally
			boolean shouldTurnRight = checkSpaceRight(lines);
			if (shouldTurnRight) {
				player.velocityX = velocity;
			} else {
				player.velocityX = -velocity;
			}
			player.velocityY = 0;
		}
		time = HARD_DECISION_INTERVAL;
	}
	
	/**
	 * Check if there is space below the current position.
	 * 
	 * @param lines all trail lines to check against
	 * @return true if space is clear below, false if obstacle detected
	 */
	private boolean checkSpaceBelow(ArrayList<Shape> lines) {
		for (int j = lines.size() - 1; j > 0; j--) {
			Shape k = lines.get(j);
			if (!k.isVertical() && player.y - k.getEndY() < HARD_LOOKAHEAD_DISTANCE && 
					player.y - k.getEndY() > 0) {
				return false; // Obstacle below, turn up instead
			}
		}
		return true; // Clear below, safe to turn down
	}
	
	/**
	 * Check if there is space to the right of the current position.
	 * 
	 * @param lines all trail lines to check against
	 * @return true if space is clear to the right, false if obstacle detected
	 */
	private boolean checkSpaceRight(ArrayList<Shape> lines) {
		for (int j = lines.size() - 1; j > 0; j--) {
			Shape k = lines.get(j);
			if (k.isVertical() && player.x - k.getEndX() < HARD_LOOKAHEAD_DISTANCE && 
					player.x - k.getEndX() > 0) {
				return false; // Obstacle to the right, turn left instead
			}
		}
		return true; // Clear to the right, safe to turn right
	}
}
