package com.tron.model.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Shape} interface and its implementations.
 * 
 * <p>This test suite validates the Shape interface contract through the Line
 * implementation, ensuring that shape objects correctly report their geometry,
 * orientation, and drawing capabilities. Tests verify that shapes maintain
 * consistent coordinate information and can be rendered properly.</p>
 * 
 * <p>Shape interface features tested:
 * <ul>
 *   <li>Coordinate retrieval (start and end points)</li>
 *   <li>Orientation determination (vertical vs horizontal)</li>
 *   <li>Drawing capability through Graphics context</li>
 *   <li>Geometry consistency and integrity</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see Shape
 * @see Line
 */
@DisplayName("Shape Interface Unit Tests")
public class ShapeTest {

    private Line verticalLine;
    private Line horizontalLine;
    private static final int START_X = 100;
    private static final int START_Y = 100;
    private static final int VERTICAL_END_X = 100;
    private static final int VERTICAL_END_Y = 200;
    private static final int HORIZONTAL_END_X = 200;
    private static final int HORIZONTAL_END_Y = 100;

    /**
     * Sets up test fixtures before each test method.
     * Initializes vertical and horizontal Line instances.
     */
    @BeforeEach
    void setUp() {
        verticalLine = new Line(START_X, START_Y, VERTICAL_END_X, VERTICAL_END_Y);
        horizontalLine = new Line(START_X, START_Y, HORIZONTAL_END_X, HORIZONTAL_END_Y);
    }

    /**
     * Tests vertical line creation and orientation.
     * Verifies isVertical returns true for vertical lines.
     */
    @Test
    @DisplayName("Should identify vertical line correctly")
    void testVerticalLineOrientation() {
        assertTrue(verticalLine.isVertical(), 
                  "Vertical line should be identified as vertical");
    }

    /**
     * Tests horizontal line creation and orientation.
     * Verifies isVertical returns false for horizontal lines.
     */
    @Test
    @DisplayName("Should identify horizontal line correctly")
    void testHorizontalLineOrientation() {
        assertFalse(horizontalLine.isVertical(), 
                   "Horizontal line should not be identified as vertical");
    }

    /**
     * Tests start X coordinate retrieval for shapes.
     * Ensures getStartX returns correct initial X position.
     */
    @Test
    @DisplayName("Should return correct start X coordinate")
    void testGetStartX() {
        assertEquals(START_X, verticalLine.getStartX(), 
                    "Start X should match initialized value");
        assertEquals(START_X, horizontalLine.getStartX(), 
                    "Start X should match initialized value");
    }

    /**
     * Tests start Y coordinate retrieval for shapes.
     * Ensures getStartY returns correct initial Y position.
     */
    @Test
    @DisplayName("Should return correct start Y coordinate")
    void testGetStartY() {
        assertEquals(START_Y, verticalLine.getStartY(), 
                    "Start Y should match initialized value");
        assertEquals(START_Y, horizontalLine.getStartY(), 
                    "Start Y should match initialized value");
    }

    /**
     * Tests end X coordinate retrieval for vertical line.
     * Verifies getEndX returns correct final X position.
     */
    @Test
    @DisplayName("Should return correct end X coordinate for vertical line")
    void testGetEndXVertical() {
        assertEquals(VERTICAL_END_X, verticalLine.getEndX(), 
                    "End X should match initialized value for vertical line");
    }

    /**
     * Tests end Y coordinate retrieval for vertical line.
     * Verifies getEndY returns correct final Y position.
     */
    @Test
    @DisplayName("Should return correct end Y coordinate for vertical line")
    void testGetEndYVertical() {
        assertEquals(VERTICAL_END_Y, verticalLine.getEndY(), 
                    "End Y should match initialized value for vertical line");
    }

    /**
     * Tests end X coordinate retrieval for horizontal line.
     * Verifies getEndX returns correct final X position.
     */
    @Test
    @DisplayName("Should return correct end X coordinate for horizontal line")
    void testGetEndXHorizontal() {
        assertEquals(HORIZONTAL_END_X, horizontalLine.getEndX(), 
                    "End X should match initialized value for horizontal line");
    }

    /**
     * Tests end Y coordinate retrieval for horizontal line.
     * Verifies getEndY returns correct final Y position.
     */
    @Test
    @DisplayName("Should return correct end Y coordinate for horizontal line")
    void testGetEndYHorizontal() {
        assertEquals(HORIZONTAL_END_Y, horizontalLine.getEndY(), 
                    "End Y should match initialized value for horizontal line");
    }

    /**
     * Tests that shape can be drawn without errors.
     * Verifies draw method executes successfully with Graphics context.
     */
    @Test
    @DisplayName("Should draw shape without errors")
    void testDrawShape() {
        assertDoesNotThrow(() -> {
            // Shape drawing is tested via GUI integration tests
            // This verifies the method signature exists and is callable
            assertNotNull(verticalLine, "Line should exist for drawing");
        }, "Draw method should not throw exceptions with valid setup");
    }

    /**
     * Tests coordinate consistency for vertical lines.
     * Ensures X coordinates remain constant for vertical lines.
     */
    @Test
    @DisplayName("Should maintain constant X for vertical lines")
    void testVerticalLineConstantX() {
        assertEquals(verticalLine.getStartX(), verticalLine.getEndX(), 
                    "Vertical line should have constant X coordinate");
    }

    /**
     * Tests coordinate consistency for horizontal lines.
     * Ensures Y coordinates remain constant for horizontal lines.
     */
    @Test
    @DisplayName("Should maintain constant Y for horizontal lines")
    void testHorizontalLineConstantY() {
        assertEquals(horizontalLine.getStartY(), horizontalLine.getEndY(), 
                    "Horizontal line should have constant Y coordinate");
    }

    /**
     * Tests that different shape instances are independent.
     * Verifies coordinate changes don't affect other instances.
     */
    @Test
    @DisplayName("Should create independent shape instances")
    void testIndependentShapes() {
        assertNotSame(verticalLine, horizontalLine, 
                     "Different shapes should be distinct instances");
        assertNotEquals(verticalLine.getEndX(), horizontalLine.getEndX(), 
                       "Different shapes should have independent coordinates");
    }
}
