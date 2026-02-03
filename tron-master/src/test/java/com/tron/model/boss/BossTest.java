package com.tron.model.boss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Test class for Boss entity
 * 
 * Tests:
 * - Initial state (10 HP, alive)
 * - Damage mechanics (2 HP per power-up)
 * - Death condition (HP reaches 0)
 * - Reset functionality
 * - Health percentage calculation
 * 
 * @author Boss Battle Mode Team
 * @version 1.0
 */
@DisplayName("Boss Entity Tests")
class BossTest {
    
    private Boss boss;
    
    @BeforeEach
    void setUp() {
        boss = new Boss();
    }
    
    @Nested
    @DisplayName("Initialization Tests")
    class InitializationTests {
        
        @Test
        @DisplayName("Boss should start with 10 HP")
        void testInitialHealth() {
            assertEquals(10, boss.getCurrentHealth(), "Boss should start with 10 HP");
            assertEquals(10, boss.getMaxHealth(), "Boss max HP should be 10");
        }
        
        @Test
        @DisplayName("Boss should start alive")
        void testInitialAliveState() {
            assertTrue(boss.isAlive(), "Boss should start alive");
        }
        
        @Test
        @DisplayName("Boss should start at 100% health")
        void testInitialHealthPercentage() {
            assertEquals(1.0, boss.getHealthPercentage(), 0.001, "Boss should start at 100% health");
        }
    }
    
    @Nested
    @DisplayName("Damage Tests")
    class DamageTests {
        
        @Test
        @DisplayName("Boss should take 2 damage per power-up")
        void testSingleDamage() {
            boss.takeDamage();
            assertEquals(8, boss.getCurrentHealth(), "Boss should have 8 HP after one hit");
            assertTrue(boss.isAlive(), "Boss should still be alive at 8 HP");
        }
        
        @Test
        @DisplayName("Boss should take cumulative damage")
        void testMultipleDamage() {
            boss.takeDamage();
            boss.takeDamage();
            boss.takeDamage();
            assertEquals(4, boss.getCurrentHealth(), "Boss should have 4 HP after 3 hits");
            assertTrue(boss.isAlive(), "Boss should still be alive at 4 HP");
        }
        
        @Test
        @DisplayName("Boss should die when HP reaches 0")
        void testDeath() {
            // Deal 5 hits (10 damage total)
            for (int i = 0; i < 5; i++) {
                boss.takeDamage();
            }
            assertEquals(0, boss.getCurrentHealth(), "Boss should have 0 HP");
            assertFalse(boss.isAlive(), "Boss should be dead at 0 HP");
        }
        
        @Test
        @DisplayName("Boss should not take damage below 0 HP")
        void testExcessDamage() {
            // Deal 10 hits (20 damage total, but max is 10)
            for (int i = 0; i < 10; i++) {
                boss.takeDamage();
            }
            assertEquals(0, boss.getCurrentHealth(), "Boss HP should not go below 0");
            assertFalse(boss.isAlive(), "Boss should be dead");
        }
        
        @Test
        @DisplayName("Dead Boss should not take further damage")
        void testDamageWhenDead() {
            // Kill Boss
            for (int i = 0; i < 5; i++) {
                boss.takeDamage();
            }
            
            // Try to damage again
            boss.takeDamage();
            assertEquals(0, boss.getCurrentHealth(), "Dead Boss HP should stay at 0");
            assertFalse(boss.isAlive(), "Boss should remain dead");
        }
    }
    
    @Nested
    @DisplayName("Health Percentage Tests")
    class HealthPercentageTests {
        
        @Test
        @DisplayName("Health percentage should be correct at various HP levels")
        void testHealthPercentageValues() {
            assertEquals(1.0, boss.getHealthPercentage(), 0.001, "100% at 10 HP");
            
            boss.takeDamage(); // 8 HP
            assertEquals(0.8, boss.getHealthPercentage(), 0.001, "80% at 8 HP");
            
            boss.takeDamage(); // 6 HP
            assertEquals(0.6, boss.getHealthPercentage(), 0.001, "60% at 6 HP");
            
            boss.takeDamage(); // 4 HP
            assertEquals(0.4, boss.getHealthPercentage(), 0.001, "40% at 4 HP");
            
            boss.takeDamage(); // 2 HP
            assertEquals(0.2, boss.getHealthPercentage(), 0.001, "20% at 2 HP");
            
            boss.takeDamage(); // 0 HP
            assertEquals(0.0, boss.getHealthPercentage(), 0.001, "0% at 0 HP");
        }
    }
    
    @Nested
    @DisplayName("Reset Tests")
    class ResetTests {
        
        @Test
        @DisplayName("Reset should restore full health")
        void testResetHealth() {
            boss.takeDamage();
            boss.takeDamage();
            assertEquals(6, boss.getCurrentHealth(), "Boss should have 6 HP before reset");
            
            boss.reset();
            assertEquals(10, boss.getCurrentHealth(), "Boss should have 10 HP after reset");
            assertTrue(boss.isAlive(), "Boss should be alive after reset");
        }
        
        @Test
        @DisplayName("Reset should revive dead Boss")
        void testResetDeadBoss() {
            // Kill Boss
            for (int i = 0; i < 5; i++) {
                boss.takeDamage();
            }
            assertFalse(boss.isAlive(), "Boss should be dead before reset");
            
            boss.reset();
            assertTrue(boss.isAlive(), "Boss should be alive after reset");
            assertEquals(10, boss.getCurrentHealth(), "Boss should have full HP after reset");
        }
        
        @Test
        @DisplayName("Reset should restore 100% health percentage")
        void testResetHealthPercentage() {
            boss.takeDamage();
            boss.takeDamage();
            boss.takeDamage();
            
            boss.reset();
            assertEquals(1.0, boss.getHealthPercentage(), 0.001, "Boss should be at 100% after reset");
        }
    }
    
    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Boss should die exactly at 5 hits")
        void testExactKill() {
            for (int i = 0; i < 4; i++) {
                boss.takeDamage();
                assertTrue(boss.isAlive(), "Boss should be alive before final hit");
            }
            
            boss.takeDamage(); // Final hit
            assertFalse(boss.isAlive(), "Boss should die on 5th hit");
            assertEquals(0, boss.getCurrentHealth(), "Boss should have 0 HP");
        }
        
        @Test
        @DisplayName("Boss should handle rapid damage correctly")
        void testRapidDamage() {
            for (int i = 0; i < 3; i++) {
                boss.takeDamage();
            }
            assertEquals(4, boss.getCurrentHealth(), "Boss should correctly handle rapid damage");
        }
    }
}
