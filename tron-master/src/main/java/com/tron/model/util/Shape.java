package com.tron.model.util;

/**
 * Shape - Interface for geometric shapes representing player trails
 * 
 * This interface defines the contract for all trail segment shapes in the game.
 * It is framework-independent, using only primitive coordinates, allowing
 * JavaFX and other UI frameworks to render trails without model dependencies.
 * 
 * <h2>Design Principles</h2>
 * <ul>
 *   <li><b>Framework Independence</b> - No dependency on AWT, JavaFX, or other UI frameworks</li>
 *   <li><b>Coordinate-Based</b> - Simple integer coordinates for rendering</li>
 *   <li><b>Immutable Contract</b> - Getters only, no setters (implementations should be immutable)</li>
 * </ul>
 * 
 * <h2>Current Implementations</h2>
 * <ul>
 *   <li>{@link Line} - Straight line segment (main trail implementation)</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public interface Shape {

	/**
	 * Checks if this shape is oriented vertically (parallel to Y-axis).
	 * Useful for optimizing collision detection and rendering.
	 * 
	 * @return true if the shape has the same X coordinate at start and end, false otherwise
	 */
	public boolean isVertical();
	
	/**
	 * Gets the starting X coordinate of this shape.
	 * 
	 * @return the X coordinate of the shape's start point in pixels
	 */
	public int getStartX();
	
	/**
	 * Gets the starting Y coordinate of this shape.
	 * 
	 * @return the Y coordinate of the shape's start point in pixels
	 */
	public int getStartY();
	
	/**
	 * Gets the ending X coordinate of this shape.
	 * 
	 * @return the X coordinate of the shape's end point in pixels
	 */
	public int getEndX();
	
	/**
	 * Gets the ending Y coordinate of this shape.
	 * 
	 * @return the Y coordinate of the shape's end point in pixels
	 */
	public int getEndY();
	
}

