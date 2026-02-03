package com.tron.model.game;

import java.util.ArrayList;

import com.tron.model.data.DrawData;
import com.tron.model.util.Intersection;
import com.tron.model.util.Shape;

/**
 * GameObject - Abstract base class for all interactive game objects
 * 
 * This class provides common functionality for movable entities in the Tron game,
 * including position tracking, velocity management, boundary checking, and collision detection.
 * 
 * Responsibilities:
 * - Maintain position and velocity state
 * - Enforce movement bounds
 * - Detect intersections with other game objects and trails
 * - Provide drawing data for view rendering
 * 
 * Subclasses (Player, Boss, etc.) implement specific behavior through:
 * - accelerate(): Handle boundary behavior (collision vs wrap-around)
 * - getDrawData(): Provide visual representation data
 * - getAlive(): Report if object is still active
 * - getPath(): Provide trail/path information
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public abstract class GameObject {
	int x; // x and y coordinates upper left
	int y;

	int width; // width and height of the court
	int height;

	int velocityX; // Pixels to move each time move() is called.
	int velocityY;

	int rightBound; // Maximum permissible x, y values.
	int bottomBound;

	/**
	 * Constructs a new GameObject with specified position, velocity, and dimensions.
	 * 
	 * @param x Initial X coordinate
	 * @param y Initial Y coordinate
	 * @param velocityX Initial horizontal velocity (pixels per tick)
	 * @param velocityY Initial vertical velocity (pixels per tick)
	 * @param width Width of the object (for collision detection)
	 * @param height Height of the object (for collision detection)
	 */
	public GameObject(int x, int y, int velocityX, int velocityY, int width,
			int height) {
		this.x = x;
		this.y = y;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Get X coordinate
	 * @return X position
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Get Y coordinate
	 * @return Y position
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Sets the movement bounds for this object.
	 * Adjusts the maximum permissible position based on court dimensions.
	 * 
	 * @param width Width of the game court
	 * @param height Height of the game court
	 */
	public void setBounds(int width, int height) {
		rightBound = width - this.width;
		bottomBound = height - this.height;
	}
	
	/**
	 * Sets the object's horizontal velocity.
	 * Only accepts velocity changes that don't oppose current motion
	 * (prevents 180-degree turns in Tron).
	 * 
	 * @param velocityX The new horizontal velocity (pixels per tick)
	 */
	public void setXVelocity(int velocityX) {
		if (!(velocityX > 0 && this.velocityX < 0)
				&& !(velocityX < 0 && this.velocityX > 0)) {
			this.velocityX = velocityX;
		}
	}
	
	/**
	 * Sets the object's vertical velocity.
	 * Only accepts velocity changes that don't oppose current motion
	 * (prevents 180-degree turns in Tron).
	 * 
	 * @param velocityY The new vertical velocity (pixels per tick)
	 */
	public void setYVelocity(int velocityY) {
		if (!(velocityY > 0 && this.velocityY < 0)
				&& !(velocityY < 0 && this.velocityY > 0)) {
			this.velocityY = velocityY;
		}
	}

	/**
	 * Moves the object according to its current velocity.
	 * Updates position, applies boundary acceleration, and clips to bounds.
	 * Called every game tick to update object position.
	 */
	public void move() {
		x += velocityX;
		y += velocityY;

		accelerate();
		clip();
	}

	/**
	 * Keeps the object within the bounds of the game court.
	 * Prevents the object from moving beyond the defined boundaries
	 * by clipping position to rightBound and bottomBound.
	 */
	public void clip() {
		if (x < 0)
			x = 0;
		else if (x > rightBound)
			x = rightBound;

		if (y < 0)
			y = 0;
		else if (y > bottomBound)
			y = bottomBound;
	}

	/**
	 * Compute whether an object intersects a shape.
	 * 
	 * @param other
	 *            The other game object to test for intersection with.
	 * @return NONE if the objects do not intersect, otherwise UP.
	 * 
	 */
	public Intersection intersects(GameObject other) {
		if (other != this) {
			if (other.y - other.height/2 <= y + height/2 &&
				other.y + other.height/2 >= y - height/2 &&
				other.x - other.width/2 <= x + width/2 &&
				other.x + other.width/2 >= x - width/2) {
				return Intersection.UP;
			}
		}
		ArrayList<Shape> pa = other.getPath();
		for (int i = 0; i < pa.size() - 1; i++) {
			Shape k = pa.get(i);
			int x1 = k.getStartX();
			int y1 = k.getStartY();
			int x2 = k.getEndX();
			int y2 = k.getEndY();

			if (y1 == y2) {
				if (Math.abs(y1 - y) <= height/2 && 
					(x >= Math.min(x1, x2) && x <= Math.max(x1, x2))) {
					return Intersection.UP;
				}
			} else if (x1 == x2) {
				if (Math.abs(x1 - x) <= width/2 &&
					(y >= Math.min(y1, y2) && y <= Math.max(y1, y2))) {					
					return Intersection.UP;
				}
			}
		}
		return Intersection.NONE;
	}
	
	/**
	 * Handles behavior when the object crosses screen boundaries.
	 * Abstract method implemented by subclasses to define boundary behavior:
	 * - Player: Death on collision or wrap-around based on map type
	 * - Other objects: Custom boundary handling
	 */
	public abstract void accelerate();

	/**
	 * Gets drawing data for this game object.
	 * This method separates model data from view rendering.
	 * @return DrawData containing all information needed to render this object
	 */
	public abstract DrawData getDrawData();
	
	/**
	 * Checks if this game object is still active/alive.
	 * 
	 * @return true if the object is alive and active, false otherwise
	 */
	public abstract boolean getAlive();
	
	/**
	 * Gets the trail/path of this game object.
	 * Used for collision detection with trails.
	 * 
	 * @return ArrayList of Shape objects representing the object's path
	 */
	public abstract ArrayList<Shape> getPath();
}

