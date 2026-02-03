package com.tron.model.game;

import java.util.ArrayList;
import java.util.List;

import com.tron.model.data.DrawData;
import com.tron.model.observer.PlayerObserver;
import com.tron.model.observer.Subject;
import com.tron.model.util.Intersection;
import com.tron.model.util.MapConfig;
import com.tron.model.util.PlayerColor;
import com.tron.model.util.Shape;

/**
 * Abstract base class for all players in the Tron game implementing Observer Pattern.
 * 
 * This class represents the Subject in the Observer Pattern for player state changes.
 * It maintains a list of PlayerObserver instances and notifies them when player state
 * changes (movement, collision, death, etc.).
 * 
 * Design Patterns:
 * - Observer Pattern (Behavioral): Acts as Subject that notifies PlayerObservers
 * - Strategy Pattern (Behavioral): Uses PlayerBehaviorStrategy for AI/Human behavior
 * - Template Method (Behavioral): Abstract move() and addPlayers() methods
 * 
 * Custom Observer Implementation:
 * This is a custom Observer Pattern implementation. We do NOT use Java's built-in
 * Observable class, PropertyChangeSupport, or JavaFX InvalidationListener as those
 * are pre-made implementations. Benefits:
 * - Type-safe observer management through {@code Subject<PlayerObserver>} interface
 * - Explicit notification methods for different player events
 * - Better control over notification timing and conditions
 * - Clear separation between player logic and observer notification
 * - Easier testing with mock observers
 * 
 * Key Responsibilities:
 * - Managing player position, velocity, and movement bounds
 * - Maintaining player trails and collision detection state
 * - Managing boost and jump mechanics
 * - Providing drawing data for the view layer
 * - Notifying observers of player state changes
 * - Supporting behavior strategies through subclass implementations
 * 
 * Observer Pattern Benefits:
 * - Enables multiple systems to respond to player events (view, audio, effects)
 * - Decouples player state from responsive systems
 * - Allows dynamic addition/removal of observers at runtime
 * - Facilitates death animations, collision effects, sound playback
 * 
 * Subclasses (PlayerHuman and PlayerAI) implement the move() and addPlayers() methods
 * using different behavior strategies.
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 3.0 (Observer Pattern Complete)
 */
public abstract class Player extends GameObject implements Subject<PlayerObserver> {
	
	// player's color (using PlayerColor enum instead of AWT Color)
	PlayerColor color;
	
	// states of the player
	boolean alive = true;
	boolean jump = false;
	boolean booster = false;
	
	// initial conditions
	int startVel = 0;
	int boostLeft = 3;

	// static values to be used by all Player objects
	static int WIDTH = 5;
	static int HEIGHT = 5;
	static int VELBOOST = 5;
	static int JUMPHEIGHT = 16;
	
	// Boost duration tracking (framework-independent)
	// At 50 FPS, 15 ticks = 300ms boost duration
	private static final int BOOST_DURATION_TICKS = 15;
	private int boostTicksRemaining = 0;
		
	// Player object's path
	ArrayList<Shape> lines = new ArrayList<>();
	
	// Map configuration for boundary behavior and obstacles
	protected MapConfig mapConfig;
	
	// Observer pattern - list of observers to notify of player state changes
	private final List<PlayerObserver> observers = new ArrayList<>();
	
	/**
	 * Gets the abstract behavior strategy for this player.
	 * This method must be implemented by subclasses to return their specific strategy.
	 * Supports the Decorator Pattern for dynamic behavior enhancement.
	 * 
	 * @return the PlayerBehaviorStrategy used by this player
	 */
	public abstract PlayerBehaviorStrategy getBehaviorStrategy();
	
	// ============ Observer Pattern Methods (Subject<PlayerObserver> Implementation) ============
	
	/**
	 * Attaches an observer to receive player state updates.
	 * 
	 * Implements Subject interface. Once attached, the observer will receive
	 * notifications whenever this player's state changes. Duplicate observers
	 * are not added to prevent redundant notifications.
	 * 
	 * @param observer The PlayerObserver to attach. Null observers are ignored.
	 */
	@Override
	public void attach(PlayerObserver observer) {
		if (observer != null && !observers.contains(observer)) {
			observers.add(observer);
		}
	}
	
	/**
	 * Detaches an observer from receiving player state updates.
	 * 
	 * Implements Subject interface. After detachment, the observer will no
	 * longer receive notifications. If the observer was not attached, this
	 * method has no effect.
	 * 
	 * @param observer The PlayerObserver to detach
	 */
	@Override
	public void detach(PlayerObserver observer) {
		observers.remove(observer);
	}
	
	/**
	 * Notifies all attached observers of general player state change.
	 * 
	 * Implements Subject interface. This is the general notification method
	 * called after player state changes (movement, velocity change, etc.).
	 * Calls onPlayerStateChanged() on each registered observer.
	 * 
	 * This method is typically called:
	 * - After each movement step
	 * - After velocity changes
	 * - After position updates
	 */
	@Override
	public void notifyObservers() {
		for (PlayerObserver observer : observers) {
			observer.onPlayerStateChanged(this);
		}
	}
	
	/**
	 * Notifies observers when this player dies.
	 * 
	 * Called when the player's alive status changes to false due to collision
	 * or going out of bounds. Observers can use this to:
	 * - Display death animation
	 * - Play crash sound effect
	 * - Update game state
	 * - Record player statistics
	 */
	protected void notifyPlayerDied() {
		for (PlayerObserver observer : observers) {
			observer.onPlayerDied(this);
		}
	}
	
	/**
	 * Notifies observers when this player collides with another object.
	 * 
	 * Called during collision detection, before the player potentially dies.
	 * Observers can use this to:
	 * - Display collision effects
	 * - Play collision sound
	 * - Implement collision-based mechanics
	 * 
	 * @param other The game object this player collided with
	 */
	protected void notifyPlayerCollision(GameObject other) {
		for (PlayerObserver observer : observers) {
			observer.onPlayerCollision(this, other);
		}
	}
	
	/**
	 * Notifies observers when this player changes direction.
	 * 
	 * Called when the player's velocity vector changes, typically due to
	 * player input or AI decision. Observers can use this to:
	 * - Add a new trail segment
	 * - Play direction change sound
	 * - Update movement indicators
	 * 
	 * @param newVelX The new X velocity
	 * @param newVelY The new Y velocity
	 */
	protected void notifyDirectionChanged(int newVelX, int newVelY) {
		for (PlayerObserver observer : observers) {
			observer.onPlayerDirectionChanged(this, newVelX, newVelY);
		}
	}
	
	/**
	 * Notifies observers when this player activates boost.
	 * 
	 * Called when the player uses one of their boost charges. Observers can
	 * use this to display boost effects, play sounds, update UI.
	 */
	protected void notifyBoostActivated() {
		for (PlayerObserver observer : observers) {
			observer.onPlayerBoostActivated(this, boostLeft);
		}
	}
	
	// ============ Constructor and Basic Methods ============
	
	/**
	 * Constructs a new Player with specified position, velocity, and color.
	 * Initializes the player's state, movement parameters, and observer list.
	 * 
	 * @param randX Initial X coordinate on the game map
	 * @param randY Initial Y coordinate on the game map
	 * @param velx Initial X velocity (pixels per tick)
	 * @param vely Initial Y velocity (pixels per tick)
	 * @param color The player's visual color (PlayerColor enum)
	 */
	public Player(int randX, int randY, int velx, int vely, PlayerColor color) {
		super(randX, randY, velx, vely, WIDTH, HEIGHT);
		startVel = Math.max(Math.abs(velx), Math.abs(vely));
		this.color = color;
	}
	

	
	/**
	 * Gets the color of this player.
	 * The color is used for rendering the player's trail and identifying players.
	 * 
	 * @return the PlayerColor enum value representing this player's color
	 */
	public PlayerColor getColor() {
		return color;
	}
	
	/**
	 * Gets the number of boost charges remaining for this player.
	 * Used by the UI to display boost availability.
	 * 
	 * @return the number of unused boost charges
	 */
	public int getBoostsLeft() {
		return boostLeft;
	}
	
	/**
	 * Add one boost charge to the player
	 * Used when collecting power-ups
	 */
	public void addBoost() {
		boostLeft++;
	}
	
	/**
	 * Sets the X velocity and notifies observers of direction change.
	 * Overrides GameObject method to add Observer Pattern notification.
	 * Only changes velocity if it doesn't oppose current motion.
	 * 
	 * @param velocityX The new X velocity
	 */
	@Override
	public void setXVelocity(int velocityX) {
		int oldVelX = this.velocityX;
		super.setXVelocity(velocityX);
		// Notify observers if velocity actually changed
		if (oldVelX != this.velocityX) {
			notifyDirectionChanged(this.velocityX, this.velocityY);
		}
	}
	
	/**
	 * Sets the Y velocity and notifies observers of direction change.
	 * Overrides GameObject method to add Observer Pattern notification.
	 * Only changes velocity if it doesn't oppose current motion.
	 * 
	 * @param velocityY The new Y velocity
	 */
	@Override
	public void setYVelocity(int velocityY) {
		int oldVelY = this.velocityY;
		super.setYVelocity(velocityY);
		// Notify observers if velocity actually changed
		if (oldVelY != this.velocityY) {
			notifyDirectionChanged(this.velocityX, this.velocityY);
		}
	}
	
	// changes state of Player if it exits the bounds
	@Override
	public void accelerate() {
		// Check if wrap-around is enabled for current map
		if (mapConfig != null && mapConfig.isWrapAroundEnabled()) {
			// Wrap-around mode (like Snake game)
			if (x < 0) {
				x = rightBound;
			} else if (x > rightBound) {
				x = 0;
			}
			
			if (y < 0) {
				y = bottomBound;
			} else if (y > bottomBound) {
				y = 0;
			}
		} else {
			// Original collision mode
			if (x < 0 || x > rightBound) {
				velocityX = 0;
				alive = false;
				notifyPlayerDied();  // Notify observers of death
			}
			if (y < 0 || y > bottomBound) {
				velocityY = 0;
				alive = false;
				notifyPlayerDied();  // Notify observers of death
			}
		}
		
		// Check obstacle collision (for all map types with obstacles)
		if (mapConfig != null && mapConfig.checkObstacleCollision(x, y)) {
			velocityX = 0;
			velocityY = 0;
			alive = false;
			notifyPlayerDied();  // Notify observers of death
		}
	}
	
	/**
	 * Activates jump mode for this player.
	 * When jumping is active, the player's trail becomes temporarily non-solid,
	 * allowing them to cross their own or other players' trails without collision.
	 */
	public void jump() {
		jump = true;
	}	/**
	 * Activates a speed boost if charges are available.
	 * Consumes one boost charge and temporarily increases the player's movement speed.
	 * Notifies observers when boost is activated.
	 * The boost lasts for BOOST_DURATION_TICKS game ticks.
	 */
	public void startBoost() {
		if (boostLeft > 0) {
			booster = true;
			boostTicksRemaining = BOOST_DURATION_TICKS;
			boostLeft--;
			notifyBoostActivated();  // Notify observers of boost activation
		}
	}
	
	/**
	 * Updates boost state and applies boosted velocity.
	 * This method should be called every game tick to:
	 * - Decrement boost timer
	 * - Apply increased velocity when boost is active
	 * - Restore normal velocity when boost expires
	 */
	public void boost() {
		// Update boost timer (framework-independent timing)
		if (booster && boostTicksRemaining > 0) {
			boostTicksRemaining--;
			if (boostTicksRemaining <= 0) {
				booster = false;
			}
		}
		
		if (booster) {
			if (velocityX > 0) {
				velocityX = VELBOOST;
			} else if (velocityX < 0) {
				velocityX = -VELBOOST;
			} else if (velocityY > 0) {
				velocityY = VELBOOST;
			} else if (velocityY < 0) {
				velocityY = -VELBOOST;
			}
		} else {
			if (velocityX > 0) {
				velocityX = startVel;
			} else if (velocityX < 0) {
				velocityX = -startVel;
			} else if (velocityY > 0) {
				velocityY = startVel;
			} else if (velocityY < 0) {
				velocityY = -startVel;
			}
		}
	}
	
	/**
	 * Gets drawing data for this player.
	 * Replaces the draw(Graphics g) method to separate model from view.
	 * @return DrawData containing position, size, color, path, and state
	 */
	@Override
	public DrawData getDrawData() {
		return new DrawData(
			x - WIDTH/2,  // x position for drawing
			y - HEIGHT/2,  // y position for drawing
			WIDTH,
			HEIGHT,
			color,
			new ArrayList<>(lines),  // Copy of path
			alive,
			jump
		);
	}

	
	// returns the state of the Player
	@Override
	public boolean getAlive() {
		return alive;
	}
	
	// returns the Player's path
	@Override
	public ArrayList<Shape> getPath() {
		return lines;
	}
	
	/**
	 * Set the map configuration for this player.
	 * Used to enable boundary wrapping and obstacle collision detection.
	 * 
	 * @param mapConfig The map configuration to apply
	 */
	public void setMapConfig(MapConfig mapConfig) {
		this.mapConfig = mapConfig;
	}
	
	/**
	 * Handles player collision with game objects or trails.
	 * Sets the player's alive status to false and stops movement.
	 * Notifies all observers that this player has died.
	 * 
	 * @param i The type of intersection/collision that occurred
	 */
	public void crash(Intersection i) {
		if (i == Intersection.UP) {
			velocityX = 0;
			velocityY = 0;
			alive = false;
			notifyPlayerDied();  // Notify observers of death
		}
	}
	
	/**
	 * Moves the player on the screen based on its current velocity.
	 * This abstract method must be implemented by subclasses (PlayerHuman, PlayerAI)
	 * to define specific movement behavior using their behavior strategies.
	 * Called every game tick to update player position.
	 */
	@Override
	public abstract void move();
	
	// adds Player objects to the field
	
	/**
	 * Adds other players for collision detection
	 * Made public for legacy code compatibility
	 * @param players array of players to check collisions against
	 */
	public abstract void addPlayers(Player[] players);
	
}

