package com.tron.model.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.tron.model.util.Shape;

/**
 * AI Player Behavior Strategy Implementation
 * 
 * This strategy encapsulates all AI decision-making logic for the Tron game.
 * It handles:
 * - Obstacle and collision avoidance
 * - Boundary detection and reaction
 * - Random movement decisions
 * - Random boost activation
 * 
 * By extracting this logic into a dedicated strategy class, we achieve:
 * 1. Clear separation of concerns - behavior logic is isolated
 * 2. Better testability - AI logic can be tested without the full Player class
 * 3. Runtime flexibility - strategies can be swapped dynamically
 * 4. Code reuse - AI logic is not duplicated across multiple classes
 * 5. Maintainability - changes to AI logic happen in one place
 * 
 * IMPORTANT: This implementation maintains 1:1 parity with the original PlayerAI.move()
 * method to ensure identical movement behavior and prevent missing movement trails.
 * 
 * @author MattBrown
 * @author MattBrown
 * @see PlayerBehaviorStrategy
 * @see PlayerAI
 */
public class AIBehaviorStrategy implements PlayerBehaviorStrategy {
	
	protected final PlayerAI player;
	protected Player[] players = new Player[1];
	private final Random rand = new Random();
	
	// Timer for decision-making intervals
	private int time = 40;
	
	/**
	 * Constructs an AIBehaviorStrategy for the given AI player.
	 * @param player the AI player this strategy controls
	 */
	public AIBehaviorStrategy(PlayerAI player) {
		this.player = player;
		players[0] = player;
	}
	
	/**
	 * Sets the other players on the court for collision detection.
	 * Must be called before game starts to enable AI to detect other players' trails.
	 * @param players array of all players including this AI player
	 */
	public void addPlayers(Player[] players) {
		this.players = players;
	}
	
	/**
	 * Decides the AI's move direction based on proximity reactions and random decisions.
	 * This method contains the complete AI decision-making logic, 1:1 copied from
	 * the original PlayerAI.move() method to ensure identical behavior.
	 */
	@Override
	public void decideMoveDirection() {
		reactProximity();
	}
	
	/**
	 * Determines whether the AI should boost randomly.
	 * @return true if the AI decides to boost (1 in 100 chance)
	 */
	@Override
	public boolean shouldBoost() {
		int r = rand.nextInt(100);
		return r == 1;  // Random boost with 1 in 100 chance
	}
	
	/**
	 * Resets the AI strategy state for a new game.
	 */
	@Override
	public void reset() {
		time = 40;
	}
	
	/**
	 * Returns the current velocity of the AI player.
	 * @return array containing [velocityX, velocityY]
	 */
	@Override
	public int[] getVelocity() {
		return new int[] { player.velocityX, player.velocityY };
	}
	
	/**
	 * Reacts to proximity of obstacles and trails.
	 * This is the core AI decision-making logic.
	 * 
	 * Logic flow:
	 * 1. Check for lines/trails in immediate proximity
	 * 2. React appropriately (change direction)
	 * 3. Check for boundary proximity
	 * 4. React appropriately (change direction away from boundary)
	 * 5. Make random directional choices if no obstacles detected
	 * 
	 * This implementation maintains 100% parity with original PlayerAI.reactProximity()
	 * to ensure identical AI behavior and movement trail generation.
	 */
	private void reactProximity() {
		int velocity = Math.max(Math.abs(player.velocityX), Math.abs(player.velocityY));
		
		// Random boost with 1 in 100 chance
		int r = rand.nextInt(100);
		if (r == 1) {
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
		
		// Check for obstacles in the path
		for (int i = lines.size() - 1; i > 0; i--) {
			Shape l = lines.get(i);
			int maxX = Math.max(l.getStartX(), l.getEndX());
			int minX = Math.min(l.getStartX(), l.getEndX());
			int maxY = Math.max(l.getStartY(), l.getEndY());
			int minY = Math.min(l.getStartY(), l.getEndY());
			
			// Moving right: check for vertical lines ahead
			if (player.velocityX > 0 && l.isVertical() && player.y >= minY && player.y <= maxY) {
				if (l.getStartX() - player.x < 6 && l.getStartX() - player.x > 0) {
					boolean b = false;
					for (int j = lines.size() - 1; j > 0; j--) {
						Shape k = lines.get(j);
						if (!k.isVertical() && player.y - k.getEndY() < 6 && 
								player.y - k.getEndY() > 0) {
							b = true;
						}
					}
					if (b) {
						player.velocityY = velocity;
					} else {
						player.velocityY = -velocity;
					}
					player.velocityX = 0;
					time = 40;
					return;
				}
			}
			
			// Moving left: check for vertical lines ahead
			if (player.velocityX < 0 && l.isVertical() && player.y >= minY && player.y <= maxY) {
				if (player.x - l.getStartX() < 6 && player.x - l.getStartX() > 0) {
					boolean b = false;
					for (int j = lines.size() - 1; j > 0; j--) {
						Shape k = lines.get(j);
						if (!k.isVertical() && player.y - k.getEndY() < 6 && 
								player.y - k.getEndY() > 0) {
							b = true;
						}
					}
					if (b) {
						player.velocityY = velocity;
					} else {
						player.velocityY = -velocity;
					}
					player.velocityX = 0;
					time = 40;
					return;
				}
			}
			
			// Moving down: check for horizontal lines ahead
			if (player.velocityY > 0 && !l.isVertical() && player.x >= minX && player.x <= maxX) {
				if (l.getStartY() - player.y < 6 && l.getStartY() - player.y > 0) {
					boolean b = false;
					for (int j = lines.size() - 1; j > 0; j--) {
						Shape k = lines.get(j);
						if (k.isVertical() && player.x - k.getEndX() < 6 && 
								player.x - k.getEndX() > 0) {
							b = true;
						}
					}
					if (b) {
						player.velocityX = velocity;
					} else {
						player.velocityX = -velocity;
					}
					player.velocityY = 0;
					time = 40;
					return;
				}
			}
			
			// Moving up: check for horizontal lines ahead
			if (player.velocityY < 0 && !l.isVertical() && player.x >= minX && player.x <= maxX) {
				if (player.y - l.getStartY() < 6 && player.y - l.getStartY() > 0) {
					boolean b = false;
					for (int j = lines.size() - 1; j > 0; j--) {
						Shape k = lines.get(j);
						if (k.isVertical() && player.x - k.getEndX() < 6 && 
								player.x - k.getEndX() > 0) {
							b = true;
						}
					}
					if (b) {
						player.velocityX = velocity;
					} else {
						player.velocityX = -velocity;
					}
					player.velocityY = 0;
					time = 40;
					return;
				}
			}
		}
		
		// Check if too close to left edge
		if (player.x < 6 && player.velocityX != 0) {
			if (player.y < 250) {
				player.velocityY = velocity;
			} else {
				player.velocityY = -velocity;
			}
			player.velocityX = 0;
			time = 40;
			return;
		}
		
		// Check if too close to right edge
		if (player.rightBound - player.x < 6 && player.velocityX != 0) {
			if (player.y < 250) {
				player.velocityY = velocity;
			} else {
				player.velocityY = -velocity;
			}
			player.velocityX = 0;
			time = 40;
			return;
		}
		
		// Check if too close to top edge
		if (player.y < 6 && player.velocityY != 0) {
			if (player.x < 250) {
				player.velocityX = velocity;
			} else {
				player.velocityX = -velocity;
			}
			player.velocityY = 0;
			time = 40;
			return;
		}
		
		// Check if too close to bottom edge
		if (player.bottomBound - player.y < 6 && player.velocityY != 0) {
			if (player.x < 250) {
				player.velocityX = velocity;
			} else {
				player.velocityX = -velocity;
			}
			player.velocityY = 0;
			time = 40;
			return;
		}
		
		// Make random movement decisions if no obstacles/boundaries detected
		if (time == 0) {
			int rando = rand.nextInt(4);
			if (rando == 0 && player.velocityX != velocity) {
				if (player.x > 6) {
					player.velocityX = -velocity;
					player.velocityY = 0;
				}
			} else if (rando == 1 && player.velocityX != -velocity) {
				if (player.rightBound - player.x > 6) {
					player.velocityX = velocity;
					player.velocityY = 0;
				}
			} else if (rando == 2 && player.velocityY != velocity) {
				if (player.y > 6) {
					player.velocityX = 0;
					player.velocityY = -velocity;
				}
			} else if (rando == 3 && player.velocityY != -velocity) {
				if (player.bottomBound - player.y > 6) {
					player.velocityX = 0;
					player.velocityY = velocity;
				}
			}
			time = 40;
		}
		time--;
	}
}
