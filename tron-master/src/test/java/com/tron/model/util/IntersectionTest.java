package com.tron.model.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Intersection} enumeration.
 * 
 * <p>This test class validates the Intersection enum which represents different
 * directions of collision detection in the game. The enum is used throughout
 * the game logic to determine collision orientations and handle player interactions
 * with boundaries and other game objects.</p>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see Intersection
 */
@DisplayName("Intersection Enum Unit Tests")
public class IntersectionTest {

    /**
     * Tests that all expected enum values are present.
     * Verifies that the Intersection enum contains all five directional values.
     */
    @Test
    @DisplayName("Should contain all expected intersection values")
    void testAllValuesPresent() {
        Intersection[] values = Intersection.values();
        assertEquals(5, values.length, "Should have exactly 5 intersection types");
        
        assertNotNull(Intersection.NONE);
        assertNotNull(Intersection.UP);
        assertNotNull(Intersection.LEFT);
        assertNotNull(Intersection.DOWN);
        assertNotNull(Intersection.RIGHT);
    }
    
    /**
     * Tests the valueOf method for NONE intersection type.
     * Ensures string-to-enum conversion works correctly.
     */
    @Test
    @DisplayName("Should correctly resolve NONE from string")
    void testValueOfNone() {
        assertEquals(Intersection.NONE, Intersection.valueOf("NONE"));
    }
    
    /**
     * Tests the valueOf method for directional intersection types.
     * Verifies all four directional enums can be resolved from strings.
     */
    @Test
    @DisplayName("Should correctly resolve directional values from strings")
    void testValueOfDirections() {
        assertEquals(Intersection.UP, Intersection.valueOf("UP"));
        assertEquals(Intersection.LEFT, Intersection.valueOf("LEFT"));
        assertEquals(Intersection.DOWN, Intersection.valueOf("DOWN"));
        assertEquals(Intersection.RIGHT, Intersection.valueOf("RIGHT"));
    }
    
    /**
     * Tests that enum values maintain uniqueness.
     * Ensures each enum constant is distinct.
     */
    @Test
    @DisplayName("Should maintain distinct enum values")
    void testEnumUniqueness() {
        assertNotEquals(Intersection.NONE, Intersection.UP);
        assertNotEquals(Intersection.UP, Intersection.LEFT);
        assertNotEquals(Intersection.LEFT, Intersection.DOWN);
        assertNotEquals(Intersection.DOWN, Intersection.RIGHT);
        assertNotEquals(Intersection.RIGHT, Intersection.NONE);
    }
    
    /**
     * Tests enum comparison using == operator.
     * Verifies that enum constants can be compared using reference equality.
     */
    @Test
    @DisplayName("Should support reference equality comparison")
    void testEnumEquality() {
        Intersection test1 = Intersection.UP;
        Intersection test2 = Intersection.UP;
        assertTrue(test1 == test2, "Same enum values should be reference-equal");
    }
    
    /**
     * Tests invalid valueOf input handling.
     * Ensures appropriate exception is thrown for non-existent enum values.
     */
    @Test
    @DisplayName("Should throw exception for invalid enum value")
    void testInvalidValueOf() {
        assertThrows(IllegalArgumentException.class, () -> {
            Intersection.valueOf("INVALID");
        });
    }
}
