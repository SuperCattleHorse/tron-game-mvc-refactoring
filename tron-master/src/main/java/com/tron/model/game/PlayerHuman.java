package com.tron.model.game;

import com.tron.model.util.Line;
import com.tron.model.util.PlayerColor;

/**
 * Human-Controlled Player Implementation using Strategy Pattern
 * 
 * This class encapsulates human player behavior using the HumanBehaviorStrategy and
 * supports the Decorator Pattern for dynamic behavior enhancement.
 * 
 * Key features:
 * - Uses HumanBehaviorStrategy for behavior encapsulation
 * - Supports dynamic decoration of behavior strategies at runtime
 * - Behavior can be enhanced with decorators (e.g., LoggingBehaviorDecorator)
 * - Maintains 1:1 parity with original PlayerHuman movement logic
 * 
 * Design Pattern Usage:
 * - Strategy Pattern: Encapsulates human behavior in HumanBehaviorStrategy
 * - Decorator Pattern: Allows runtime behavior enhancement through decorator wrapping
 * 
 * @author MattBrown
 * @author MattBrown
 * @see HumanBehaviorStrategy
 * @see PlayerBehaviorStrategy
 * @see com.tron.model.game.decorator.BehaviorStrategyDecorator
 */
public class PlayerHuman extends Player {

	/**
	 * The behavior strategy for this human player.
	 * Can be wrapped with decorators for additional functionality.
	 */
	private PlayerBehaviorStrategy behaviorStrategy;

	/**
	 * Constructs a human player with the specified initial conditions.
	 * Initializes the behavior strategy with HumanBehaviorStrategy.
	 * 
	 * @param randX initial x position
	 * @param randY initial y position
	 * @param velx initial x velocity
	 * @param vely initial y velocity
	 * @param color the player's color
	 */
	public PlayerHuman(int randX, int randY, int velx, int vely, PlayerColor color) {
		super(randX, randY, velx, vely, color);
		this.behaviorStrategy = new HumanBehaviorStrategy(this);
	}
	
	/**
	 * Sets the behavior strategy for this player.
	 * Supports dynamic strategy changes and decorator wrapping at runtime.
	 * 
	 * @param strategy the new behavior strategy
	 * @throws IllegalArgumentException if strategy is null
	 */
	public void setBehaviorStrategy(PlayerBehaviorStrategy strategy) {
		if (strategy == null) {
			throw new IllegalArgumentException("Behavior strategy cannot be null");
		}
		this.behaviorStrategy = strategy;
	}
	
	/**
	 * Gets the current behavior strategy.
	 * Supports introspection for debugging decorator chains.
	 * 
	 * @return the current PlayerBehaviorStrategy
	 */
	@Override
	public PlayerBehaviorStrategy getBehaviorStrategy() {
		return behaviorStrategy;
	}
	
	// does nothing because human players can see screen
	// only needed for AI, but required for abstract class
	@Override
	public void addPlayers(Player[] players) {
	}
	
	/**
	 * Moves the human player based on external input.
	 * The behavior strategy (potentially decorated) is consulted for behavior consistency.
	 * Human players rely primarily on keyboard input for direction control,
	 * while the strategy handles boost decisions and validation.
	 * 
	 * This method maintains movement trail generation and collision detection
	 * from the original PlayerHuman implementation.
	 */
	@Override
	public void move() {
		int a = x;
		int b = y;
		
		// Use strategy for behavior consistency (including any decorators)
		behaviorStrategy.decideMoveDirection();
		
		boost();
		
		if (!jump) {
			x += velocityX;
			y += velocityY;
			if (lines.size() > 1) {
				Line l1 = (Line) lines.get(lines.size() - 2);
				Line l2 = (Line) lines.get(lines.size() - 1);
				if (a == l1.getStartX() && 
						l1.getEndY() == l2.getStartY()) {
					lines.add(new Line(l1.getStartX(), l1.getStartY(),
							l2.getEndX(), l2.getEndY()));
					lines.remove(lines.size() - 2);
					lines.remove(lines.size() - 2);
				} else if (b == l1.getStartY() && 
						l1.getEndX() == l2.getStartX()) {
					lines.add(new Line(l1.getStartX(), l1.getStartY(), 
							l2.getEndX(), l2.getEndY()));
					lines.remove(lines.size() - 2);
					lines.remove(lines.size() - 2);
				} 
			}
			lines.add(new Line(a, b, x, y));
		} else {
			if (velocityX > 0) {
				x += JUMPHEIGHT;
			} else if (velocityX < 0) {
				x -= JUMPHEIGHT;
			} else if (velocityY > 0) {
				y += JUMPHEIGHT;
			} else if (velocityY < 0) {
				y -= JUMPHEIGHT;
			}
			jump = false;
		}
		accelerate();
		clip();
	}
	
}

