package com.tron.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * MapTypeTest - Unit tests for map type enumeration
 * 
 * Tests map type properties, display names, and wrap-around behavior
 * using Given-When-Then format.
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
@DisplayName("MapType - Map Type Enumeration Tests")
class MapTypeTest {
    
    /**
     * Test: DEFAULT map has collision boundaries
     * 
     * Given: MapType.DEFAULT exists
     * When: Checking wrap-around behavior
     * Then: Should return false (collision boundaries, not wrap-around)
     */
    @Test
    @DisplayName("DEFAULT map has collision boundaries")
    void testDefaultMapHasCollisionBoundaries() {
        // Given: MapType.DEFAULT exists
        MapType defaultMap = MapType.DEFAULT;
        
        // When: Checking wrap-around behavior
        boolean hasWrapAround = defaultMap.hasWrapAroundBoundaries();
        
        // Then: Should return false
        assertFalse(hasWrapAround, "DEFAULT map should have collision boundaries, not wrap-around");
    }
    
    /**
     * Test: MAP_1 has wrap-around boundaries
     * 
     * Given: MapType.MAP_1 exists
     * When: Checking wrap-around behavior
     * Then: Should return true (wrap-around like Snake game)
     */
    @Test
    @DisplayName("MAP_1 has wrap-around boundaries")
    void testMap1HasWrapAround() {
        // Given: MapType.MAP_1 exists
        MapType map1 = MapType.MAP_1;
        
        // When: Checking wrap-around behavior
        boolean hasWrapAround = map1.hasWrapAroundBoundaries();
        
        // Then: Should return true
        assertTrue(hasWrapAround, "MAP_1 should have wrap-around boundaries");
    }
    
    /**
     * Test: MAP_2 has wrap-around boundaries
     * 
     * Given: MapType.MAP_2 exists
     * When: Checking wrap-around behavior
     * Then: Should return true
     */
    @Test
    @DisplayName("MAP_2 has wrap-around boundaries")
    void testMap2HasWrapAround() {
        // Given: MapType.MAP_2 exists
        MapType map2 = MapType.MAP_2;
        
        // When: Checking wrap-around behavior
        boolean hasWrapAround = map2.hasWrapAroundBoundaries();
        
        // Then: Should return true
        assertTrue(hasWrapAround, "MAP_2 should have wrap-around boundaries");
    }
    
    /**
     * Test: MAP_3 has wrap-around boundaries
     * 
     * Given: MapType.MAP_3 exists
     * When: Checking wrap-around behavior
     * Then: Should return true
     */
    @Test
    @DisplayName("MAP_3 has wrap-around boundaries")
    void testMap3HasWrapAround() {
        // Given: MapType.MAP_3 exists
        MapType map3 = MapType.MAP_3;
        
        // When: Checking wrap-around behavior
        boolean hasWrapAround = map3.hasWrapAroundBoundaries();
        
        // Then: Should return true
        assertTrue(hasWrapAround, "MAP_3 should have wrap-around boundaries");
    }
    
    /**
     * Test: All map types have display names
     * 
     * Given: All MapType values
     * When: Getting display names
     * Then: All should have non-null, non-empty display names
     */
    @Test
    @DisplayName("All map types have display names")
    void testAllMapTypesHaveDisplayNames() {
        // Given: All MapType values
        MapType[] allMaps = MapType.values();
        
        // When & Then: Check each has a valid display name
        for (MapType mapType : allMaps) {
            String displayName = mapType.getDisplayName();
            assertNotNull(displayName, mapType + " should have a display name");
            assertFalse(displayName.isEmpty(), mapType + " display name should not be empty");
        }
    }
    
    /**
     * Test: Display names are descriptive
     * 
     * Given: Specific map types
     * When: Getting display names
     * Then: Should return expected descriptive names
     */
    @Test
    @DisplayName("Display names are descriptive")
    void testDisplayNamesAreDescriptive() {
        // Given & When & Then: Check specific display names
        assertEquals("Default (Bounded)", MapType.DEFAULT.getDisplayName(), 
                    "DEFAULT should have correct display name");
        assertEquals("Map 1: Wrap-Around", MapType.MAP_1.getDisplayName(), 
                    "MAP_1 should have correct display name");
        assertEquals("Map 2: Cross Maze", MapType.MAP_2.getDisplayName(), 
                    "MAP_2 should have correct display name");
        assertEquals("Map 3: Inset Walls", MapType.MAP_3.getDisplayName(), 
                    "MAP_3 should have correct display name");
    }
    
    /**
     * Test: Exactly four map types exist
     * 
     * Given: MapType enumeration
     * When: Counting map types
     * Then: Should have exactly 4 map types
     */
    @Test
    @DisplayName("Exactly four map types exist")
    void testMapTypeCount() {
        // Given: MapType enumeration
        MapType[] allMaps = MapType.values();
        
        // When: Counting map types
        int count = allMaps.length;
        
        // Then: Should have exactly 4 map types
        assertEquals(4, count, "Should have exactly 4 map types (DEFAULT, MAP_1, MAP_2, MAP_3)");
    }
}
