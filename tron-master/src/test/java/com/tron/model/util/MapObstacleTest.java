package com.tron.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * MapObstacleTest - Unit tests for map obstacle functionality
 * 
 * Tests obstacle creation, collision detection with points and lines
 * using Given-When-Then format.
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
@DisplayName("MapObstacle - Obstacle Collision Detection Tests")
class MapObstacleTest {
    
    private MapObstacle obstacle;
    
    @BeforeEach
    void setUp() {
        // Create a test obstacle at (100, 100) with size 50x30
        obstacle = new MapObstacle(100, 100, 50, 30);
    }
    
    /**
     * Test: Obstacle stores correct dimensions
     * 
     * Given: An obstacle is created with specific dimensions
     * When: Querying its properties
     * Then: Should return the correct x, y, width, height
     */
    @Test
    @DisplayName("Obstacle stores correct dimensions")
    void testObstacleDimensions() {
        // Given: Obstacle created in setUp
        
        // When: Querying properties
        int x = obstacle.getX();
        int y = obstacle.getY();
        int width = obstacle.getWidth();
        int height = obstacle.getHeight();
        
        // Then: Should match constructor values
        assertEquals(100, x, "X coordinate should be 100");
        assertEquals(100, y, "Y coordinate should be 100");
        assertEquals(50, width, "Width should be 50");
        assertEquals(30, height, "Height should be 30");
    }
    
    /**
     * Test: Point inside obstacle boundary detects collision
     * 
     * Given: An obstacle at (100, 100) with size 50x30
     * When: Testing a point inside the obstacle (125, 115)
     * Then: Should return true (collision detected)
     */
    @Test
    @DisplayName("Point inside obstacle detects collision")
    void testPointInsideObstacle() {
        // Given: Obstacle created in setUp
        
        // When: Testing point inside obstacle
        boolean collision = obstacle.intersects(125, 115);
        
        // Then: Should detect collision
        assertTrue(collision, "Point (125, 115) should be inside obstacle");
    }
    
    /**
     * Test: Point at obstacle corner detects collision
     * 
     * Given: An obstacle at (100, 100) with size 50x30
     * When: Testing a point at the top-left corner (100, 100)
     * Then: Should return true (collision detected)
     */
    @Test
    @DisplayName("Point at obstacle corner detects collision")
    void testPointAtCorner() {
        // Given: Obstacle created in setUp
        
        // When: Testing point at corner
        boolean collision = obstacle.intersects(100, 100);
        
        // Then: Should detect collision
        assertTrue(collision, "Point (100, 100) should be on obstacle boundary");
    }
    
    /**
     * Test: Point outside obstacle does not detect collision
     * 
     * Given: An obstacle at (100, 100) with size 50x30
     * When: Testing a point outside the obstacle (50, 50)
     * Then: Should return false (no collision)
     */
    @Test
    @DisplayName("Point outside obstacle does not detect collision")
    void testPointOutsideObstacle() {
        // Given: Obstacle created in setUp
        
        // When: Testing point outside obstacle
        boolean collision = obstacle.intersects(50, 50);
        
        // Then: Should not detect collision
        assertFalse(collision, "Point (50, 50) should be outside obstacle");
    }
    
    /**
     * Test: Point at obstacle right edge detects collision
     * 
     * Given: An obstacle at (100, 100) with size 50x30 (right edge at x=150)
     * When: Testing a point at right edge (150, 115)
     * Then: Should return true (collision detected)
     */
    @Test
    @DisplayName("Point at right edge detects collision")
    void testPointAtRightEdge() {
        // Given: Obstacle created in setUp
        
        // When: Testing point at right edge
        boolean collision = obstacle.intersects(150, 115);
        
        // Then: Should detect collision
        assertTrue(collision, "Point (150, 115) should be on right edge");
    }
    
    /**
     * Test: Point at obstacle bottom edge detects collision
     * 
     * Given: An obstacle at (100, 100) with size 50x30 (bottom edge at y=130)
     * When: Testing a point at bottom edge (125, 130)
     * Then: Should return true (collision detected)
     */
    @Test
    @DisplayName("Point at bottom edge detects collision")
    void testPointAtBottomEdge() {
        // Given: Obstacle created in setUp
        
        // When: Testing point at bottom edge
        boolean collision = obstacle.intersects(125, 130);
        
        // Then: Should detect collision
        assertTrue(collision, "Point (125, 130) should be on bottom edge");
    }
    
    /**
     * Test: Line with endpoint inside obstacle detects collision
     * 
     * Given: An obstacle at (100, 100) with size 50x30
     * When: Testing a line with one endpoint inside (50, 50) to (125, 115)
     * Then: Should return true (collision detected)
     */
    @Test
    @DisplayName("Line with endpoint inside obstacle detects collision")
    void testLineWithEndpointInside() {
        // Given: Obstacle created in setUp
        
        // When: Testing line with endpoint inside
        Line line = new Line(50, 50, 125, 115);
        boolean collision = obstacle.intersects(line);
        
        // Then: Should detect collision
        assertTrue(collision, "Line ending inside obstacle should collide");
    }
    
    /**
     * Test: Line completely outside obstacle does not detect collision
     * 
     * Given: An obstacle at (100, 100) with size 50x30
     * When: Testing a line completely outside (10, 10) to (50, 50)
     * Then: Should return false (no collision)
     */
    @Test
    @DisplayName("Line completely outside obstacle does not detect collision")
    void testLineCompletelyOutside() {
        // Given: Obstacle created in setUp
        
        // When: Testing line completely outside
        Line line = new Line(10, 10, 50, 50);
        boolean collision = obstacle.intersects(line);
        
        // Then: Should not detect collision
        assertFalse(collision, "Line completely outside should not collide");
    }
    
    /**
     * Test: Line crossing through obstacle detects collision
     * 
     * Given: An obstacle at (100, 100) with size 50x30
     * When: Testing a line crossing through (90, 115) to (160, 115)
     * Then: Should return true (collision detected)
     */
    @Test
    @DisplayName("Line crossing through obstacle detects collision")
    void testLineCrossingThrough() {
        // Given: Obstacle created in setUp
        
        // When: Testing line crossing through obstacle
        Line line = new Line(90, 115, 160, 115);
        boolean collision = obstacle.intersects(line);
        
        // Then: Should detect collision
        assertTrue(collision, "Line crossing through obstacle should collide");
    }
    
    /**
     * Test: Vertical line crossing obstacle detects collision
     * 
     * Given: An obstacle at (100, 100) with size 50x30
     * When: Testing a vertical line crossing (125, 90) to (125, 140)
     * Then: Should return true (collision detected)
     */
    @Test
    @DisplayName("Vertical line crossing obstacle detects collision")
    void testVerticalLineCrossing() {
        // Given: Obstacle created in setUp
        
        // When: Testing vertical line crossing
        Line line = new Line(125, 90, 125, 140);
        boolean collision = obstacle.intersects(line);
        
        // Then: Should detect collision
        assertTrue(collision, "Vertical line crossing should collide");
    }
}
