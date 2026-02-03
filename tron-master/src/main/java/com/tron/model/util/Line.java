package com.tron.model.util;

/**
 * Line - Geometric line shape representing player trail segments
 * 
 * This class represents a straight line segment defined by start and end points.
 * It is framework-independent, allowing JavaFX and other views to use the coordinate
 * getters to render trails without coupling the model to specific UI frameworks.
 * 
 * Design Pattern: Value Object (immutable data holder)
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class Line implements Shape {
	
	// positions of the start and end of the line
	private int x;
	private int y;
	private int x2;
	private int y2;
	
	/**
	 * Constructs a line segment with specified start and end points.
	 * 
	 * @param x Starting X coordinate
	 * @param y Starting Y coordinate
	 * @param x2 Ending X coordinate
	 * @param y2 Ending Y coordinate
	 */
	public Line(int x, int y, int x2, int y2) {
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	/**
	 * Checks if this line is vertical (parallel to Y-axis).
	 * 
	 * @return true if the line has the same X coordinate at start and end, false otherwise
	 */
	public boolean isVertical() {
		return (x == x2);
	}
	
	/**
	 * Gets the starting X coordinate of this line.
	 * 
	 * @return The X coordinate of the line's start point
	 */
	public int getStartX() {
		return x;
	}
	
	/**
	 * Gets the starting Y coordinate of this line.
	 * 
	 * @return The Y coordinate of the line's start point
	 */
	public int getStartY() {
		return y;
	}
	
	/**
	 * Gets the ending X coordinate of this line.
	 * 
	 * @return The X coordinate of the line's end point
	 */
	public int getEndX() {
		return x2;
	}
	
	/**
	 * Gets the ending Y coordinate of this line.
	 * 
	 * @return The Y coordinate of the line's end point
	 */
	public int getEndY() {
		return y2;
	}
}

