package com.tron.model.game;

import com.tron.model.util.Line;
import com.tron.model.util.PlayerColor;

/**
 * AI-Controlled Player Implementation using Strategy Pattern and Decorator Pattern
 * 
 * This class encapsulates AI player behavior using the AIBehaviorStrategy and
 * supports the Decorator Pattern for dynamic behavior enhancement.
 * 
 * Key features:
 * - Uses AIBehaviorStrategy for AI decision-making encapsulation
 * - Supports dynamic decoration of behavior strategies at runtime
 * - Behavior can be enhanced with decorators (e.g., LoggingBehaviorDecorator)
 * - Maintains 1:1 parity with original PlayerAI movement and trail generation logic
 * - All AI decision logic is delegated to the behavior strategy
 * 
 * Design Pattern Usage:
 * - Strategy Pattern: Encapsulates AI behavior in AIBehaviorStrategy
 * - Decorator Pattern: Allows runtime behavior enhancement through decorator wrapping
 * 
 * Implementation Note:
 * The decorator pattern allows for runtime enhancement of AI behavior without modifying
 * this class or the core strategy logic. For example, a LoggingBehaviorDecorator could be
 * wrapped around the AIBehaviorStrategy to track all AI decisions and provide debugging info.
 * 
 * @author MattBrown
 * @author MattBrown
 * @see AIBehaviorStrategy
 * @see PlayerBehaviorStrategy
 * @see com.tron.model.game.decorator.BehaviorStrategyDecorator
 */
public class PlayerAI extends Player {
	
	/**
	 * The behavior strategy for this AI player.
	 * Can be wrapped with decorators for additional functionality.
	 */
	private PlayerBehaviorStrategy behaviorStrategy;
	
	/**
	 * Constructs an AI player with the specified initial conditions.
	 * Initializes the behavior strategy with AIBehaviorStrategy.
	 * 
	 * @param randX initial x position
	 * @param randY initial y position
	 * @param velx initial x velocity
	 * @param vely initial y velocity
	 * @param color the player's color
	 */
	public PlayerAI(int randX, int randY, int velx, int vely, PlayerColor color) {
		super(randX, randY, velx, vely, color);
		this.behaviorStrategy = new AIBehaviorStrategy(this);
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
	
	// must be called so that the AI knows where trails are
	@Override
	public void addPlayers(Player[] players) {
		// Get the base AIBehaviorStrategy from the decorator chain if present
		PlayerBehaviorStrategy baseStrategy = unwrapToBaseStrategy();
		if (baseStrategy instanceof AIBehaviorStrategy) {
			((AIBehaviorStrategy) baseStrategy).addPlayers(players);
		}
	}
	
	/**
	 * Unwraps the decorator chain to find the base AIBehaviorStrategy.
	 * This is necessary to call strategy-specific methods like addPlayers.
	 * 
	 * @return the unwrapped AIBehaviorStrategy
	 */
	private PlayerBehaviorStrategy unwrapToBaseStrategy() {
		PlayerBehaviorStrategy strategy = behaviorStrategy;
		
		// Unwrap decorators to find the base strategy
		while (strategy instanceof com.tron.model.game.decorator.BehaviorStrategyDecorator) {
			strategy = ((com.tron.model.game.decorator.BehaviorStrategyDecorator) strategy).getDecoratedStrategy();
		}
		
		return strategy;
	}
	
	/**
	 * Moves the AI player based on its behavior strategy.
	 * The actual AI decision-making is delegated to the strategy (potentially decorated).
	 * Movement trail generation logic is maintained here to ensure 1:1 parity with original PlayerAI.
	 * 
	 * This method:
	 * 1. Calls the strategy's decideMoveDirection() to determine movement
	 * 2. Checks the strategy's shouldBoost() to determine boost activation
	 * 3. Updates position and generates movement trails
	 * 4. Handles collision detection and bounds checking
	 */
	@Override
	public void move() {		
		int a = x;
		int b = y;
		
		// Use strategy (including decorators) to make AI decisions
		behaviorStrategy.decideMoveDirection();
		
		// Check for boost decision from strategy
		if (behaviorStrategy.shouldBoost()) {
			startBoost();
		}
		
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

