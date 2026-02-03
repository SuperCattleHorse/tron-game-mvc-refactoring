package com.tron.model.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MapConfig - Configuration manager for game map layouts
 * 
 * Provides map-specific settings including:
 * - Boundary behavior (collision vs wrap-around)
 * - Static obstacle definitions
 * - Collision detection with obstacles
 * 
 * This class uses the Factory Pattern to create appropriate configurations
 * based on MapType. Each map type defines its own obstacle layout and boundary rules.
 * 
 * Design Pattern: Factory Pattern (createMap method)
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class MapConfig {
    
    private final MapType mapType;
    private final List<MapObstacle> obstacles;
    private final boolean wrapAroundEnabled;
    
    /**
     * Private constructor - use factory method
     */
    private MapConfig(MapType mapType, List<MapObstacle> obstacles) {
        this.mapType = mapType;
        this.obstacles = Collections.unmodifiableList(obstacles);
        this.wrapAroundEnabled = mapType.hasWrapAroundBoundaries();
    }
    
    /**
     * Factory method to create map configuration based on MapType
     * 
     * Creates appropriate obstacle layouts for each map variant:
     * - DEFAULT: No obstacles, standard collision boundaries
     * - MAP_1: Wrap-around, no obstacles (Snake-like)
     * - MAP_2: Wrap-around with cross maze obstacles
     * - MAP_3: Wrap-around with inset boundary walls
     * 
     * @param mapType The type of map to create
     * @return Configured MapConfig instance with appropriate obstacles
     */
    public static MapConfig createMap(MapType mapType) {
        List<MapObstacle> obstacles = new ArrayList<>();
        
        switch (mapType) {
            case DEFAULT:
                // No obstacles, standard collision boundaries
                break;
                
            case MAP_1:
                // Wrap-around, no obstacles (Snake-like)
                break;
                
            case MAP_2:
                // Wrap-around with cross maze obstacles
                // Horizontal wall: (150, 250) to (350, 250), thickness 5px
                obstacles.add(new MapObstacle(150, 248, 200, 5));
                // Vertical wall: (250, 150) to (250, 350), thickness 5px
                obstacles.add(new MapObstacle(248, 150, 5, 200));
                break;
                
            case MAP_3:
                // Wrap-around with inset boundary walls (thickness 5px)
                // Top wall: (100, 100) to (400, 100)
                obstacles.add(new MapObstacle(100, 100, 300, 5));
                // Bottom wall: (100, 400) to (400, 400)
                obstacles.add(new MapObstacle(100, 400, 300, 5));
                // Left wall: (100, 100) to (100, 400)
                obstacles.add(new MapObstacle(100, 100, 5, 300));
                // Right wall: (400, 100) to (400, 400)
                obstacles.add(new MapObstacle(400, 100, 5, 300));
                break;
        }
        
        return new MapConfig(mapType, obstacles);
    }
    
    /**
     * Check if wrap-around boundaries are enabled for this map
     * 
     * Wrap-around allows players to pass through one edge and emerge from
     * the opposite side (Snake-like behavior). Non-wrap maps have collision walls.
     * 
     * @return true if boundaries wrap around, false if collision walls exist
     */
    public boolean isWrapAroundEnabled() {
        return wrapAroundEnabled;
    }
    
    /**
     * Get list of static obstacles on this map
     * 
     * @return Immutable list of MapObstacle objects (empty list if no obstacles)
     */
    public List<MapObstacle> getObstacles() {
        return obstacles;
    }
    
    /**
     * Check if a point collides with any obstacle on this map
     * 
     * Iterates through all obstacles and tests for intersection.
     * 
     * @param x Point X coordinate in pixels
     * @param y Point Y coordinate in pixels
     * @return true if the point is inside any obstacle, false otherwise
     */
    public boolean checkObstacleCollision(int x, int y) {
        for (MapObstacle obstacle : obstacles) {
            if (obstacle.intersects(x, y)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if a line segment collides with any obstacle on this map
     * 
     * Useful for detecting if a player's trail crosses an obstacle.
     * Iterates through all obstacles and tests for line-rectangle intersection.
     * 
     * @param line The Line object to test for collision
     * @return true if the line intersects any obstacle, false otherwise
     */
    public boolean checkObstacleCollision(Line line) {
        for (MapObstacle obstacle : obstacles) {
            if (obstacle.intersects(line)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get the map type of this configuration
     * 
     * @return MapType enum value representing this map's type
     */
    public MapType getMapType() {
        return mapType;
    }
}
