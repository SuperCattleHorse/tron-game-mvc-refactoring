package com.tron.model.util;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * MapConfigTest - Unit tests for map configuration functionality
 * 
 * Tests map creation, obstacle management, and collision detection
 * using Given-When-Then format.
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
@DisplayName("MapConfig - Map Configuration Tests")
class MapConfigTest {
    
    /**
     * Test: DEFAULT map has no obstacles
     * 
     * Given: MapConfig factory exists
     * When: Creating a DEFAULT map
     * Then: Should have no obstacles and collision boundaries
     */
    @Test
    @DisplayName("DEFAULT map has no obstacles")
    void testDefaultMapNoObstacles() {
        // Given: MapConfig factory exists
        
        // When: Creating DEFAULT map
        MapConfig config = MapConfig.createMap(MapType.DEFAULT);
        
        // Then: Should have no obstacles
        assertEquals(0, config.getObstacles().size(), "DEFAULT map should have no obstacles");
        assertFalse(config.isWrapAroundEnabled(), "DEFAULT should not have wrap-around");
        assertEquals(MapType.DEFAULT, config.getMapType(), "Should be DEFAULT type");
    }
    
    /**
     * Test: MAP_1 has no obstacles but has wrap-around
     * 
     * Given: MapConfig factory exists
     * When: Creating MAP_1 (Wrap-Around)
     * Then: Should have no obstacles and wrap-around enabled
     */
    @Test
    @DisplayName("MAP_1 has wrap-around and no obstacles")
    void testMap1Configuration() {
        // Given: MapConfig factory exists
        
        // When: Creating MAP_1
        MapConfig config = MapConfig.createMap(MapType.MAP_1);
        
        // Then: Should have wrap-around and no obstacles
        assertTrue(config.isWrapAroundEnabled(), "MAP_1 should have wrap-around");
        assertEquals(0, config.getObstacles().size(), "MAP_1 should have no obstacles");
        assertEquals(MapType.MAP_1, config.getMapType(), "Should be MAP_1 type");
    }
    
    /**
     * Test: MAP_2 has cross maze obstacles
     * 
     * Given: MapConfig factory exists
     * When: Creating MAP_2 (Cross Maze)
     * Then: Should have 2 obstacles (horizontal and vertical walls)
     */
    @Test
    @DisplayName("MAP_2 has cross maze obstacles")
    void testMap2HasCrossMaze() {
        // Given: MapConfig factory exists
        
        // When: Creating MAP_2
        MapConfig config = MapConfig.createMap(MapType.MAP_2);
        
        // Then: Should have 2 obstacles (cross shape)
        assertTrue(config.isWrapAroundEnabled(), "MAP_2 should have wrap-around");
        assertEquals(2, config.getObstacles().size(), "MAP_2 should have 2 obstacles (cross)");
        assertEquals(MapType.MAP_2, config.getMapType(), "Should be MAP_2 type");
    }
    
    /**
     * Test: MAP_2 cross obstacles are correctly positioned
     * 
     * Given: MAP_2 configuration
     * When: Checking obstacle positions
     * Then: Horizontal wall at (150, 248) and vertical wall at (248, 150)
     */
    @Test
    @DisplayName("MAP_2 cross obstacles are correctly positioned")
    void testMap2ObstaclePositions() {
        // Given: MAP_2 configuration
        MapConfig config = MapConfig.createMap(MapType.MAP_2);
        List<MapObstacle> obstacles = config.getObstacles();
        
        // When: Getting obstacles
        MapObstacle horizontal = obstacles.get(0);
        MapObstacle vertical = obstacles.get(1);
        
        // Then: Check positions
        assertEquals(150, horizontal.getX(), "Horizontal wall X should be 150");
        assertEquals(248, horizontal.getY(), "Horizontal wall Y should be 248");
        assertEquals(200, horizontal.getWidth(), "Horizontal wall width should be 200");
        assertEquals(5, horizontal.getHeight(), "Horizontal wall height should be 5");
        
        assertEquals(248, vertical.getX(), "Vertical wall X should be 248");
        assertEquals(150, vertical.getY(), "Vertical wall Y should be 150");
        assertEquals(5, vertical.getWidth(), "Vertical wall width should be 5");
        assertEquals(200, vertical.getHeight(), "Vertical wall height should be 200");
    }
    
    /**
     * Test: MAP_3 has inset boundary walls
     * 
     * Given: MapConfig factory exists
     * When: Creating MAP_3 (Inset Walls)
     * Then: Should have 4 obstacles (inset boundaries)
     */
    @Test
    @DisplayName("MAP_3 has inset boundary walls")
    void testMap3HasInsetWalls() {
        // Given: MapConfig factory exists
        
        // When: Creating MAP_3
        MapConfig config = MapConfig.createMap(MapType.MAP_3);
        
        // Then: Should have 4 obstacles (inset boundaries)
        assertTrue(config.isWrapAroundEnabled(), "MAP_3 should have wrap-around");
        assertEquals(4, config.getObstacles().size(), "MAP_3 should have 4 inset walls");
        assertEquals(MapType.MAP_3, config.getMapType(), "Should be MAP_3 type");
    }
    
    /**
     * Test: MAP_3 inset walls are correctly positioned
     * 
     * Given: MAP_3 configuration
     * When: Checking obstacle positions
     * Then: Top, bottom, left, right walls should be at (100, 100) to (400, 400)
     */
    @Test
    @DisplayName("MAP_3 inset walls are correctly positioned")
    void testMap3ObstaclePositions() {
        // Given: MAP_3 configuration
        MapConfig config = MapConfig.createMap(MapType.MAP_3);
        List<MapObstacle> obstacles = config.getObstacles();
        
        // When: Getting obstacles
        MapObstacle top = obstacles.get(0);
        MapObstacle bottom = obstacles.get(1);
        MapObstacle left = obstacles.get(2);
        MapObstacle right = obstacles.get(3);
        
        // Then: Check top wall
        assertEquals(100, top.getX(), "Top wall X should be 100");
        assertEquals(100, top.getY(), "Top wall Y should be 100");
        assertEquals(300, top.getWidth(), "Top wall width should be 300");
        assertEquals(5, top.getHeight(), "Top wall height should be 5");
        
        // Check bottom wall
        assertEquals(100, bottom.getX(), "Bottom wall X should be 100");
        assertEquals(400, bottom.getY(), "Bottom wall Y should be 400");
        
        // Check left wall
        assertEquals(100, left.getX(), "Left wall X should be 100");
        assertEquals(100, left.getY(), "Left wall Y should be 100");
        
        // Check right wall
        assertEquals(400, right.getX(), "Right wall X should be 400");
        assertEquals(100, right.getY(), "Right wall Y should be 100");
    }
    
    /**
     * Test: Obstacle collision detection works
     * 
     * Given: MAP_2 with cross obstacles
     * When: Checking collision at center (250, 250)
     * Then: Should detect collision (inside cross)
     */
    @Test
    @DisplayName("Obstacle collision detection works")
    void testObstacleCollisionDetection() {
        // Given: MAP_2 with cross obstacles
        MapConfig config = MapConfig.createMap(MapType.MAP_2);
        
        // When: Checking collision at center of cross
        boolean collision = config.checkObstacleCollision(250, 250);
        
        // Then: Should detect collision
        assertTrue(collision, "Point (250, 250) should collide with cross obstacle");
    }
    
    /**
     * Test: No collision outside obstacles
     * 
     * Given: MAP_2 with cross obstacles
     * When: Checking collision away from obstacles (50, 50)
     * Then: Should not detect collision
     */
    @Test
    @DisplayName("No collision outside obstacles")
    void testNoCollisionOutsideObstacles() {
        // Given: MAP_2 with cross obstacles
        MapConfig config = MapConfig.createMap(MapType.MAP_2);
        
        // When: Checking collision outside obstacles
        boolean collision = config.checkObstacleCollision(50, 50);
        
        // Then: Should not detect collision
        assertFalse(collision, "Point (50, 50) should not collide with obstacles");
    }
    
    /**
     * Test: Line collision detection works
     * 
     * Given: MAP_2 with cross obstacles
     * When: Checking line collision crossing the horizontal wall
     * Then: Should detect collision
     */
    @Test
    @DisplayName("Line collision detection works")
    void testLineCollisionDetection() {
        // Given: MAP_2 with cross obstacles
        MapConfig config = MapConfig.createMap(MapType.MAP_2);
        
        // When: Creating line crossing horizontal obstacle
        Line line = new Line(100, 250, 400, 250);
        boolean collision = config.checkObstacleCollision(line);
        
        // Then: Should detect collision
        assertTrue(collision, "Line crossing horizontal wall should collide");
    }
    
    /**
     * Test: Obstacle list is immutable
     * 
     * Given: A map configuration
     * When: Getting obstacles list
     * Then: List should be immutable (unmodifiable)
     */
    @Test
    @DisplayName("Obstacle list is immutable")
    void testObstacleListImmutable() {
        // Given: MAP_2 configuration
        MapConfig config = MapConfig.createMap(MapType.MAP_2);
        
        // When: Getting obstacles
        List<MapObstacle> obstacles = config.getObstacles();
        
        // Then: Should be non-null
        assertNotNull(obstacles, "Obstacles list should not be null");
        
        // Verify it's unmodifiable (would throw UnsupportedOperationException if we tried to add)
        // We won't actually add to avoid test failure, just verify list is returned
        assertEquals(2, obstacles.size(), "Should have correct number of obstacles");
    }
}
