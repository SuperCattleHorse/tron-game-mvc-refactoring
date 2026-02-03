package com.tron.model.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;

import com.tron.model.data.DrawData;
import com.tron.model.util.Intersection;
import com.tron.model.util.Shape;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link GameObject} abstract class.
 * 
 * <p>This test suite validates the core functionality of game objects, including
 * movement mechanics, boundary clipping, velocity management, collision detection,
 * and coordinate tracking. Since GameObject is abstract, tests use a concrete
 * implementation that provides minimal implementations of abstract methods.</p>
 * 
 * <p>Tests cover critical game physics behaviors:
 * <ul>
 *   <li>Position updates based on velocity</li>
 *   <li>Boundary constraint enforcement</li>
 *   <li>Velocity setting with directional restrictions</li>
 *   <li>Intersection detection between objects</li>
 *   <li>Object dimension management</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see GameObject
 */
@DisplayName("GameObject Class Unit Tests")
public class GameObjectTest {

    /**
     * Concrete implementation of GameObject for testing purposes.
     * Provides minimal implementations of abstract methods.
     */
    private static class TestGameObject extends GameObject {
        private final ArrayList<Shape> path = new ArrayList<>();
        private final boolean alive = true;
        
        public TestGameObject(int x, int y, int velocityX, int velocityY, 
                            int width, int height) {
            super(x, y, velocityX, velocityY, width, height);
        }

        @Override
        public void accelerate() {
            // Minimal implementation for testing
        }
        
        @Override
        public DrawData getDrawData() {
            // Minimal implementation for testing - returns null for abstract testing
            return null;
        }
        
        @Override
        public boolean getAlive() {
            return alive;
        }

        @Override
        public ArrayList<Shape> getPath() {
            return path;
        }
    }

    private TestGameObject gameObject;
    private static final int INITIAL_X = 100;
    private static final int INITIAL_Y = 100;
    private static final int VELOCITY_X = 5;
    private static final int VELOCITY_Y = 0;
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;
    private static final int MAP_WIDTH = 500;
    private static final int MAP_HEIGHT = 500;

    /**
     * Sets up test fixtures before each test method.
     * Initializes a TestGameObject with standard parameters.
     */
    @BeforeEach
    void setUp() {
        gameObject = new TestGameObject(INITIAL_X, INITIAL_Y, VELOCITY_X, 
                                       VELOCITY_Y, WIDTH, HEIGHT);
        gameObject.setBounds(MAP_WIDTH, MAP_HEIGHT);
    }

    /**
     * Tests successful creation of GameObject instance.
     * Verifies constructor initializes all fields correctly.
     */
    @Test
    @DisplayName("Should create GameObject instance successfully")
    void testGameObjectCreation() {
        assertNotNull(gameObject, "GameObject should not be null");
        assertEquals(INITIAL_X, gameObject.x, "X position should match");
        assertEquals(INITIAL_Y, gameObject.y, "Y position should match");
    }

    /**
     * Tests boundary setting functionality.
     * Ensures setBounds correctly calculates right and bottom bounds.
     */
    @Test
    @DisplayName("Should set boundaries correctly")
    void testSetBounds() {
        gameObject.setBounds(MAP_WIDTH, MAP_HEIGHT);
        assertEquals(MAP_WIDTH - WIDTH, gameObject.rightBound, 
                    "Right bound should account for object width");
        assertEquals(MAP_HEIGHT - HEIGHT, gameObject.bottomBound, 
                    "Bottom bound should account for object height");
    }

    /**
     * Tests horizontal movement mechanics.
     * Verifies position updates correctly based on X velocity.
     */
    @Test
    @DisplayName("Should move object horizontally")
    void testHorizontalMovement() {
        int initialX = gameObject.x;
        gameObject.move();
        assertEquals(initialX + VELOCITY_X, gameObject.x, 
                    "X position should increase by velocity");
    }

    /**
     * Tests vertical movement mechanics.
     * Verifies position updates correctly based on Y velocity.
     */
    @Test
    @DisplayName("Should move object vertically")
    void testVerticalMovement() {
        gameObject = new TestGameObject(INITIAL_X, INITIAL_Y, 0, 5, WIDTH, HEIGHT);
        gameObject.setBounds(MAP_WIDTH, MAP_HEIGHT);
        int initialY = gameObject.y;
        gameObject.move();
        assertEquals(initialY + 5, gameObject.y, 
                    "Y position should increase by velocity");
    }

    /**
     * Tests X velocity setting with valid direction.
     * Ensures velocity changes when new direction doesn't oppose current.
     */
    @Test
    @DisplayName("Should set X velocity when direction is valid")
    void testSetXVelocityValid() {
        gameObject.setXVelocity(10);
        assertEquals(10, gameObject.velocityX, 
                    "X velocity should update to new value");
    }

    /**
     * Tests X velocity rejection when opposing current direction.
     * Prevents instant 180-degree reversals.
     */
    @Test
    @DisplayName("Should not set X velocity when direction opposes current")
    void testSetXVelocityOpposing() {
        gameObject.setXVelocity(-5);
        assertEquals(VELOCITY_X, gameObject.velocityX, 
                    "X velocity should remain unchanged");
    }

    /**
     * Tests Y velocity setting with valid direction.
     * Ensures velocity changes when new direction doesn't oppose current.
     */
    @Test
    @DisplayName("Should set Y velocity when direction is valid")
    void testSetYVelocityValid() {
        gameObject.setYVelocity(5);
        assertEquals(5, gameObject.velocityY, 
                    "Y velocity should update to new value");
    }

    /**
     * Tests Y velocity rejection when opposing current direction.
     * Prevents instant 180-degree reversals.
     */
    @Test
    @DisplayName("Should not set Y velocity when direction opposes current")
    void testSetYVelocityOpposing() {
        gameObject = new TestGameObject(INITIAL_X, INITIAL_Y, 0, 5, WIDTH, HEIGHT);
        gameObject.setYVelocity(-3);
        assertEquals(5, gameObject.velocityY, 
                    "Y velocity should remain unchanged");
    }

    /**
     * Tests boundary clipping at left edge.
     * Ensures object cannot move beyond left boundary.
     */
    @Test
    @DisplayName("Should clip object at left boundary")
    void testClipLeftBoundary() {
        gameObject.x = -10;
        gameObject.clip();
        assertEquals(0, gameObject.x, "X should be clipped to 0");
    }

    /**
     * Tests boundary clipping at right edge.
     * Ensures object cannot exceed right boundary.
     */
    @Test
    @DisplayName("Should clip object at right boundary")
    void testClipRightBoundary() {
        gameObject.x = MAP_WIDTH;
        gameObject.clip();
        assertEquals(MAP_WIDTH - WIDTH, gameObject.x, 
                    "X should be clipped to right bound");
    }

    /**
     * Tests boundary clipping at top edge.
     * Ensures object cannot move above top boundary.
     */
    @Test
    @DisplayName("Should clip object at top boundary")
    void testClipTopBoundary() {
        gameObject.y = -10;
        gameObject.clip();
        assertEquals(0, gameObject.y, "Y should be clipped to 0");
    }

    /**
     * Tests boundary clipping at bottom edge.
     * Ensures object cannot exceed bottom boundary.
     */
    @Test
    @DisplayName("Should clip object at bottom boundary")
    void testClipBottomBoundary() {
        gameObject.y = MAP_HEIGHT;
        gameObject.clip();
        assertEquals(MAP_HEIGHT - HEIGHT, gameObject.y, 
                    "Y should be clipped to bottom bound");
    }

    /**
     * Tests intersection detection with another object.
     * Verifies collision detection when objects overlap.
     */
    @Test
    @DisplayName("Should detect intersection with overlapping object")
    void testIntersectsWithOverlap() {
        TestGameObject other = new TestGameObject(105, 105, 0, 0, WIDTH, HEIGHT);
        Intersection result = gameObject.intersects(other);
        assertEquals(Intersection.UP, result, 
                    "Should detect intersection when objects overlap");
    }

    /**
     * Tests no intersection with distant object.
     * Ensures no collision detected when objects are separated.
     */
    @Test
    @DisplayName("Should not detect intersection with separated object")
    void testIntersectsNoOverlap() {
        TestGameObject other = new TestGameObject(200, 200, 0, 0, WIDTH, HEIGHT);
        Intersection result = gameObject.intersects(other);
        assertNotEquals(Intersection.UP, result, 
                       "Should not detect intersection when objects are separated");
    }
}
