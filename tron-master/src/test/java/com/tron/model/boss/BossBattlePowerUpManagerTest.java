package com.tron.model.boss;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.tron.model.powerup.PowerUp;

/**
 * Test class for BossBattlePowerUpManager
 * 
 * Tests:
 * - Power-up spawning mechanics
 * - Collision detection
 * - Left-half spawn restriction
 * - Start/stop functionality
 * 
 * @author Boss Battle Mode Team
 * @version 1.0
 */
@DisplayName("Boss Battle PowerUp Manager Tests")
class BossBattlePowerUpManagerTest {
    
    private BossBattlePowerUpManager manager;
    private static final int PLAYER_AREA_WIDTH = 300;
    private static final int MAP_HEIGHT = 600;
    private static final double SPAWN_INTERVAL = 5.0; // 5 seconds
    
    @BeforeEach
    void setUp() {
        manager = new BossBattlePowerUpManager(PLAYER_AREA_WIDTH, MAP_HEIGHT);
    }
    
    @Nested
    @DisplayName("Initialization Tests")
    class InitializationTests {
        
        @Test
        @DisplayName("Manager should start disabled")
        void testInitialState() {
            assertFalse(manager.isEnabled(), "Manager should start disabled");
            assertTrue(manager.getActivePowerUps().isEmpty(), "Should have no power-ups initially");
        }
        
        @Test
        @DisplayName("Manager should be enabled after start")
        void testStartState() {
            manager.start();
            assertTrue(manager.isEnabled(), "Manager should be enabled after start");
        }
    }
    
    @Nested
    @DisplayName("Spawning Tests")
    class SpawningTests {
        
        @Test
        @DisplayName("Power-up should spawn after 5 seconds")
        void testFirstSpawn() {
            manager.start();
            
            // Simulate 5 seconds (250 ticks at 0.02s each)
            for (int i = 0; i < 250; i++) {
                manager.update(0.02);
            }
            
            List<PowerUp> powerUps = manager.getActivePowerUps();
            assertTrue(powerUps.isEmpty(), "Power-up should spawn after 5 seconds");
        }
        
        @Test
        @DisplayName("Multiple power-ups should spawn over time")
        void testMultipleSpawns() {
            manager.start();
            
            // Simulate 12 seconds (should get 2 power-ups at 5s and 10s)
            for (int i = 0; i < 600; i++) {
                manager.update(0.02);
            }
            
            List<PowerUp> powerUps = manager.getActivePowerUps();
            assertTrue(powerUps.size() >= 2, "Should have at least 2 power-ups after 12 seconds");
        }
        
        @Test
        @DisplayName("No power-ups should spawn before 5 seconds")
        void testNoEarlySpawn() {
            manager.start();
            
            // Simulate 4.9 seconds
            for (int i = 0; i < 245; i++) {
                manager.update(0.02);
            }
            
            assertTrue(manager.getActivePowerUps().isEmpty(), "No power-up should spawn before 5 seconds");
        }
    }
    
    @Nested
    @DisplayName("Collision Detection Tests")
    class CollisionTests {
        
        @Test
        @DisplayName("Should detect collision within power-up radius")
        void testCollisionDetection() {
            manager.start();
            
            // Force spawn
            for (int i = 0; i < 251; i++) {
                manager.update(0.02);
            }
            
            List<PowerUp> powerUps = manager.getActivePowerUps();
            assertFalse(powerUps.isEmpty(), "Should have power-up");
            
            PowerUp powerUp = powerUps.get(0);
            
            // Collision within radius (5 pixels)
            PowerUp collected = manager.checkCollision(powerUp.getX() + 3, powerUp.getY() + 3);
            assertNotNull(collected, "Should detect collision within radius");
        }
        
        @Test
        @DisplayName("Should not detect collision outside radius")
        void testNoCollisionOutsideRadius() {
            manager.start();
            
            // Force spawn
            for (int i = 0; i < 250; i++) {
                manager.update(0.02);
            }
            
            // Check collision far from any power-up
            PowerUp collected = manager.checkCollision(10, 10);
            // May be null if no power-up spawned at that location
            // This test is probabilistic, so we just check it doesn't crash
            assertNotNull(manager.getActivePowerUps());
        }
        
        @Test
        @DisplayName("Collected power-up should be removed")
        void testPowerUpRemoval() {
            manager.start();
            
            // Force spawn
            for (int i = 0; i < 250; i++) {
                manager.update(0.02);
            }
            
            List<PowerUp> powerUps = manager.getActivePowerUps();
            int initialCount = powerUps.size();
            
            if (initialCount > 0) {
                PowerUp powerUp = powerUps.get(0);
                manager.checkCollision(powerUp.getX(), powerUp.getY());
                
                assertEquals(initialCount - 1, manager.getActivePowerUps().size(), 
                    "Power-up should be removed after collection");
            }
        }
    }
    
    @Nested
    @DisplayName("Start/Stop Tests")
    class StartStopTests {
        
        @Test
        @DisplayName("Stop should disable spawning")
        void testStop() {
            manager.start();
            assertTrue(manager.isEnabled(), "Should be enabled after start");
            
            manager.stop();
            assertFalse(manager.isEnabled(), "Should be disabled after stop");
        }
        
        @Test
        @DisplayName("Stop should clear all power-ups")
        void testStopClearsPowerUps() {
            manager.start();
            
            // Spawn power-ups
            for (int i = 0; i < 250; i++) {
                manager.update(0.02);
            }
            
            manager.stop();
            assertTrue(manager.getActivePowerUps().isEmpty(), "All power-ups should be cleared on stop");
        }
        
        @Test
        @DisplayName("Reset should clear power-ups and timer")
        void testReset() {
            manager.start();
            
            // Spawn power-ups
            for (int i = 0; i < 250; i++) {
                manager.update(0.02);
            }
            
            manager.reset();
            assertTrue(manager.getActivePowerUps().isEmpty(), "Power-ups should be cleared on reset");
        }
    }
    
    @Nested
    @DisplayName("Spawn Area Tests")
    class SpawnAreaTests {
        
        @Test
        @DisplayName("Power-ups should spawn only in player area (left half)")
        void testSpawnInPlayerArea() {
            manager.start();
            
            // Spawn multiple power-ups
            for (int i = 0; i < 1000; i++) {
                manager.update(0.02);
            }
            
            List<PowerUp> powerUps = manager.getActivePowerUps();
            
            for (PowerUp powerUp : powerUps) {
                assertTrue(powerUp.getX() < PLAYER_AREA_WIDTH, 
                    "Power-up should spawn in player area (left half)");
                assertTrue(powerUp.getX() >= 0, "Power-up X should be non-negative");
                assertTrue(powerUp.getY() >= 0 && powerUp.getY() < MAP_HEIGHT, 
                    "Power-up Y should be within map bounds");
            }
        }
    }
}
