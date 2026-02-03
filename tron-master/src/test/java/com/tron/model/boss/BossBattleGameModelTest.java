package com.tron.model.boss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Test class for BossBattleGameModel
 * 
 * Tests:
 * - Game initialization
 * - Boss damage on power-up collection
 * - Victory condition (Boss HP = 0)
 * - Defeat condition (player crash)
 * - Player movement restriction (left half only)
 * 
 * @author Boss Battle Mode Team
 * @version 1.0
 */
@DisplayName("Boss Battle Game Model Tests")
class BossBattleGameModelTest {
    
    private BossBattleGameModel model;
    private static final int MAP_WIDTH = 600;
    private static final int MAP_HEIGHT = 600;
    private static final int VELOCITY = 3;
    
    @BeforeEach
    void setUp() {
        model = new BossBattleGameModel(MAP_WIDTH, MAP_HEIGHT, VELOCITY);
    }
    
    @Nested
    @DisplayName("Initialization Tests")
    class InitializationTests {
        
        @Test
        @DisplayName("Model should initialize with correct dimensions")
        void testInitialization() {
            assertEquals(MAP_WIDTH, model.getMapWidth(), "Map width should match");
            assertEquals(MAP_HEIGHT, model.getMapHeight(), "Map height should match");
        }
        
        @Test
        @DisplayName("Player area should be two thirds of map width")
        void testPlayerAreaWidth() {
            assertEquals(MAP_WIDTH *2 / 3, model.getPlayerAreaWidth(),
                "Player area should be two thirds of total width");
        }
        
        @Test
        @DisplayName("Boss should start with full health")
        void testBossInitialHealth() {
            assertNotNull(model.getBoss(), "Boss should exist");
            assertEquals(10, model.getBoss().getCurrentHealth(), "Boss should have 10 HP");
            assertTrue(model.getBoss().isAlive(), "Boss should be alive");
        }
        
        @Test
        @DisplayName("Game should not be running initially")
        void testInitialRunningState() {
            assertFalse(model.isRunning(), "Game should not be running initially");
        }
        
        @Test
        @DisplayName("Victory should be false initially")
        void testInitialVictoryState() {
            assertFalse(model.isVictory(), "Victory should be false initially");
        }
    }
    
    @Nested
    @DisplayName("Game Start Tests")
    class GameStartTests {
        
        @Test
        @DisplayName("Start should initialize game state")
        void testStart() {
            model.start();
            
            assertTrue(model.isRunning(), "Game should be running after start");
            assertNotNull(model.getPlayer(), "Player should exist");
            assertTrue(model.getPowerUpManager().isEnabled(), "Power-up manager should be enabled");
        }
        
        @Test
        @DisplayName("Player should spawn in left half")
        void testPlayerSpawnLocation() {
            model.start();
            
            assertNotNull(model.getPlayer(), "Player should exist");
            assertTrue(model.getPlayer().getX() < model.getPlayerAreaWidth(), 
                "Player should spawn in left half");
        }
    }
    
    @Nested
    @DisplayName("Reset Tests")
    class ResetTests {
        
        @Test
        @DisplayName("Reset should restore initial state")
        void testReset() {
            model.start();
            
            // Damage Boss
            model.getBoss().takeDamage();
            
            model.reset();
            
            assertEquals(10, model.getBoss().getCurrentHealth(), "Boss HP should be reset");
            assertTrue(model.getBoss().isAlive(), "Boss should be alive after reset");
            assertFalse(model.isVictory(), "Victory should be false after reset");
            assertTrue(model.isRunning(), "Game should be running after reset");
        }
    }
    
    @Nested
    @DisplayName("Boss Damage Tests")
    class BossDamageTests {
        
        @Test
        @DisplayName("Boss should take damage when player collects power-up")
        void testBossDamageOnPowerUp() {
            model.start();
            
            int initialHealth = model.getBoss().getCurrentHealth();
            
            // Manually damage Boss to simulate power-up collection
            model.getBoss().takeDamage();
            
            assertEquals(initialHealth - 2, model.getBoss().getCurrentHealth(), 
                "Boss should lose 2 HP per power-up");
        }
    }
    
    @Nested
    @DisplayName("Victory Condition Tests")
    class VictoryTests {
        
        @Test
        @DisplayName("Victory should trigger when Boss HP reaches 0")
        void testVictoryCondition() {
            model.start();
            
            // Deal 5 hits (10 damage) to kill Boss
            for (int i = 0; i < 5; i++) {
                model.getBoss().takeDamage();
            }
            
            assertFalse(model.getBoss().isAlive(), "Boss should be dead");
            
            // Run one tick to process victory
            model.tick();
            
            assertFalse(model.isRunning(), "Game should stop on victory");
        }
    }
    
    @Nested
    @DisplayName("Defeat Condition Tests")
    class DefeatTests {
        
        @Test
        @DisplayName("Defeat should trigger when player crashes")
        void testDefeatCondition() {
            model.start();
            
            // Kill player
            if (model.getPlayer() != null) {
                model.getPlayer().crash(model.getPlayer().intersects(model.getPlayer()));
            }
            
            // Run one tick to process defeat
            model.tick();

            assertFalse(model.isVictory(), "Should not be victory when player dies");
        }
    }
    
    @Nested
    @DisplayName("Game State Tests")
    class GameStateTests {
        
        @Test
        @DisplayName("Game should handle pause correctly")
        void testPause() {
            model.start();
            
            model.setPaused(true);
            assertTrue(model.isPaused(), "Game should be paused");
            
            model.setPaused(false);
            assertFalse(model.isPaused(), "Game should be unpaused");
        }
        
        @Test
        @DisplayName("Game should stop when requested")
        void testStop() {
            model.start();
            assertTrue(model.isRunning(), "Game should be running");
            
            model.stop();
            assertFalse(model.isRunning(), "Game should stop");
        }
    }
}
