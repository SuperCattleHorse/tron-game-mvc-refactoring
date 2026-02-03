package com.tron.model.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Line} class.
 * 
 * <p>This test class validates the basic functionality of the Line utility class,
 * including coordinate retrieval, vertical line detection, and line construction.
 * Tests ensure that Line objects correctly store and retrieve their start and end
 * positions and accurately determine their orientation.</p>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see Line
 */
@DisplayName("Line Class Unit Tests")
public class LineTest {

    private Line horizontalLine;
    private Line verticalLine;
    private Line diagonalLine;
    
    /**
     * Sets up test fixtures before each test method.
     * Initializes three types of lines: horizontal, vertical, and diagonal.
     */
    @BeforeEach
    void setUp() {
        horizontalLine = new Line(10, 20, 50, 20);
        verticalLine = new Line(30, 10, 30, 80);
        diagonalLine = new Line(0, 0, 40, 60);
    }
    
    /**
     * Tests that a Line instance can be successfully created.
     * Verifies that the constructor properly initializes Line objects.
     */
    @Test
    @DisplayName("Should create Line instance successfully")
    void testLineCreation() {
        assertNotNull(horizontalLine, "Horizontal line should not be null");
        assertNotNull(verticalLine, "Vertical line should not be null");
        assertNotNull(diagonalLine, "Diagonal line should not be null");
    }
    
    /**
     * Tests retrieval of starting X coordinate.
     * Ensures getStartX() returns the correct initial X position.
     */
    @Test
    @DisplayName("Should return correct start X coordinate")
    void testGetStartX() {
        assertEquals(10, horizontalLine.getStartX());
        assertEquals(30, verticalLine.getStartX());
        assertEquals(0, diagonalLine.getStartX());
    }
    
    /**
     * Tests retrieval of starting Y coordinate.
     * Ensures getStartY() returns the correct initial Y position.
     */
    @Test
    @DisplayName("Should return correct start Y coordinate")
    void testGetStartY() {
        assertEquals(20, horizontalLine.getStartY());
        assertEquals(10, verticalLine.getStartY());
        assertEquals(0, diagonalLine.getStartY());
    }
    
    /**
     * Tests retrieval of ending X coordinate.
     * Ensures getEndX() returns the correct terminal X position.
     */
    @Test
    @DisplayName("Should return correct end X coordinate")
    void testGetEndX() {
        assertEquals(50, horizontalLine.getEndX());
        assertEquals(30, verticalLine.getEndX());
        assertEquals(40, diagonalLine.getEndX());
    }
    
    /**
     * Tests retrieval of ending Y coordinate.
     * Ensures getEndY() returns the correct terminal Y position.
     */
    @Test
    @DisplayName("Should return correct end Y coordinate")
    void testGetEndY() {
        assertEquals(20, horizontalLine.getEndY());
        assertEquals(80, verticalLine.getEndY());
        assertEquals(60, diagonalLine.getEndY());
    }
    
    /**
     * Tests vertical line detection for truly vertical lines.
     * Verifies that isVertical() returns true when start and end X coordinates match.
     */
    @Test
    @DisplayName("Should correctly identify vertical lines")
    void testIsVerticalTrue() {
        assertTrue(verticalLine.isVertical(), "Vertical line should be identified as vertical");
    }
    
    /**
     * Tests vertical line detection for non-vertical lines.
     * Verifies that isVertical() returns false for horizontal and diagonal lines.
     */
    @Test
    @DisplayName("Should correctly identify non-vertical lines")
    void testIsVerticalFalse() {
        assertFalse(horizontalLine.isVertical(), "Horizontal line should not be vertical");
        assertFalse(diagonalLine.isVertical(), "Diagonal line should not be vertical");
    }
    
    /**
     * Tests edge case where line has zero length (point).
     * Ensures the Line class can handle degenerate cases.
     */
    @Test
    @DisplayName("Should handle zero-length line (point)")
    void testZeroLengthLine() {
        Line pointLine = new Line(15, 25, 15, 25);
        assertNotNull(pointLine);
        assertEquals(15, pointLine.getStartX());
        assertEquals(25, pointLine.getStartY());
        assertEquals(15, pointLine.getEndX());
        assertEquals(25, pointLine.getEndY());
        assertTrue(pointLine.isVertical(), "Point should be considered vertical");
    }
    
    /**
     * Tests line creation with negative coordinates.
     * Verifies that the Line class correctly handles negative position values.
     */
    @Test
    @DisplayName("Should handle negative coordinates")
    void testNegativeCoordinates() {
        Line negativeLine = new Line(-10, -20, -30, -40);
        assertEquals(-10, negativeLine.getStartX());
        assertEquals(-20, negativeLine.getStartY());
        assertEquals(-30, negativeLine.getEndX());
        assertEquals(-40, negativeLine.getEndY());
    }
}
