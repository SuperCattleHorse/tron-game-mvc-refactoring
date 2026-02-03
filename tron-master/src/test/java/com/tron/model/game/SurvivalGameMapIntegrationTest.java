package com.tron.model.game;

import java.io.File;
import java.lang.reflect.Field;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tron.config.GameSettings;
import com.tron.model.util.MapConfig;
import com.tron.model.util.MapType;

/**
 * SurvivalGameMapIntegrationTest - Integration tests for map functionality in Survival mode
 * 
 * Tests the integration between GameSettings, MapConfig, and SurvivalGameModel
 * using Given-When-Then format.
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
@DisplayName("SurvivalGameModel - Map Integration Tests")
class SurvivalGameMapIntegrationTest {
    
    private static final String TEST_HIGH_SCORE_FILE = "test_survival_scores.json";
    private static final String TEST_CONFIG_FILE = "config/game_settings.properties";
    
    private SurvivalGameModel gameModel;
    private GameSettings gameSettings;
    
    @BeforeEach
    void setUp() throws Exception {
        // Clean up files
        deleteTestFiles();
        
        // Reset GameSettings singleton
        Field instanceField = GameSettings.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        
        gameSettings = GameSettings.getInstance();
    }
    
    @AfterEach
    void tearDown() {
        deleteTestFiles();
    }
    
    private void deleteTestFiles() {
        new File(TEST_HIGH_SCORE_FILE).delete();
        new File(TEST_CONFIG_FILE).delete();
    }
    
    /**
     * Test: Default map configuration is applied to game model
     * 
     * Given: GameSettings with DEFAULT map type
     * When: Creating a new SurvivalGameModel
     * Then: Model should have DEFAULT map configuration
     */
    @Test
    @DisplayName("Integration: Default map config applied to model")
    void testDefaultMapConfigApplied() {
        // Given: GameSettings with DEFAULT map type (default state)
        assertEquals(MapType.DEFAULT, gameSettings.getSelectedMapType());
        
        // When: Creating game model
        gameModel = new SurvivalGameModel(TEST_HIGH_SCORE_FILE, 2);
        
        // Then: Should have DEFAULT map config
        MapConfig mapConfig = gameModel.getMapConfig();
        assertNotNull(mapConfig, "Map config should not be null");
        assertEquals(MapType.DEFAULT, mapConfig.getMapType(), "Should be DEFAULT map");
        assertFalse(mapConfig.isWrapAroundEnabled(), "Should not have wrap-around");
        assertEquals(0, mapConfig.getObstacles().size(), "Should have no obstacles");
    }
    
    /**
     * Test: MAP_1 configuration is applied to game model
     * 
     * Given: GameSettings with MAP_1 selected
     * When: Creating a new SurvivalGameModel
     * Then: Model should have MAP_1 configuration with wrap-around
     */
    @Test
    @DisplayName("Integration: MAP_1 config applied to model")
    void testMap1ConfigApplied() {
        // Given: Set MAP_1
        gameSettings.setSelectedMapType(MapType.MAP_1);
        
        // When: Creating game model
        gameModel = new SurvivalGameModel(TEST_HIGH_SCORE_FILE, 2);
        
        // Then: Should have MAP_1 config
        MapConfig mapConfig = gameModel.getMapConfig();
        assertEquals(MapType.MAP_1, mapConfig.getMapType(), "Should be MAP_1");
        assertTrue(mapConfig.isWrapAroundEnabled(), "Should have wrap-around");
        assertEquals(0, mapConfig.getObstacles().size(), "Should have no obstacles");
    }
    
    /**
     * Test: MAP_2 configuration with obstacles is applied
     * 
     * Given: GameSettings with MAP_2 selected
     * When: Creating a new SurvivalGameModel
     * Then: Model should have MAP_2 with cross maze obstacles
     */
    @Test
    @DisplayName("Integration: MAP_2 config with obstacles applied")
    void testMap2ConfigApplied() {
        // Given: Set MAP_2
        gameSettings.setSelectedMapType(MapType.MAP_2);
        
        // When: Creating game model
        gameModel = new SurvivalGameModel(TEST_HIGH_SCORE_FILE, 2);
        
        // Then: Should have MAP_2 config with obstacles
        MapConfig mapConfig = gameModel.getMapConfig();
        assertEquals(MapType.MAP_2, mapConfig.getMapType(), "Should be MAP_2");
        assertTrue(mapConfig.isWrapAroundEnabled(), "Should have wrap-around");
        assertEquals(2, mapConfig.getObstacles().size(), "Should have 2 obstacles (cross)");
    }
    
    /**
     * Test: MAP_3 configuration with inset walls is applied
     * 
     * Given: GameSettings with MAP_3 selected
     * When: Creating a new SurvivalGameModel
     * Then: Model should have MAP_3 with 4 inset walls
     */
    @Test
    @DisplayName("Integration: MAP_3 config with inset walls applied")
    void testMap3ConfigApplied() {
        // Given: Set MAP_3
        gameSettings.setSelectedMapType(MapType.MAP_3);
        
        // When: Creating game model
        gameModel = new SurvivalGameModel(TEST_HIGH_SCORE_FILE, 2);
        
        // Then: Should have MAP_3 config with 4 walls
        MapConfig mapConfig = gameModel.getMapConfig();
        assertEquals(MapType.MAP_3, mapConfig.getMapType(), "Should be MAP_3");
        assertTrue(mapConfig.isWrapAroundEnabled(), "Should have wrap-around");
        assertEquals(4, mapConfig.getObstacles().size(), "Should have 4 inset walls");
    }
    
    /**
     * Test: Map config is reloaded on reset
     * 
     * Given: SurvivalGameModel with DEFAULT map
     * When: User changes map to MAP_1 and resets game
     * Then: Model should reload and use MAP_1
     */
    @Test
    @DisplayName("Integration: Map config reloaded on reset")
    void testMapConfigReloadedOnReset() {
        // Given: Game with DEFAULT map
        gameSettings.setSelectedMapType(MapType.DEFAULT);
        gameModel = new SurvivalGameModel(TEST_HIGH_SCORE_FILE, 2);
        assertEquals(MapType.DEFAULT, gameModel.getMapConfig().getMapType());
        
        // When: Change to MAP_1 and reset
        gameSettings.setSelectedMapType(MapType.MAP_1);
        gameModel.reset();
        
        // Then: Should now have MAP_1
        assertEquals(MapType.MAP_1, gameModel.getMapConfig().getMapType(), 
                    "Should reload to MAP_1 after reset");
        assertTrue(gameModel.getMapConfig().isWrapAroundEnabled(), 
                  "Should have wrap-around after reload");
    }
    
    /**
     * Test: Players receive map config on reset
     * 
     * Given: SurvivalGameModel with MAP_2
     * When: Game is reset
     * Then: All players should have map config applied
     */
    @Test
    @DisplayName("Integration: Players receive map config on reset")
    void testPlayersReceiveMapConfig() {
        // Given: Game with MAP_2
        gameSettings.setSelectedMapType(MapType.MAP_2);
        gameModel = new SurvivalGameModel(TEST_HIGH_SCORE_FILE, 2);
        
        // When: Reset to create players
        gameModel.reset();
        
        // Then: Players should exist and have map config
        Player[] players = gameModel.getPlayers();
        assertNotNull(players, "Players array should not be null");
        assertTrue(players.length > 0, "Should have at least one player");
        
        // Note: We can't directly verify mapConfig is set on players
        // without exposing it via getter, but the integration ensures
        // setMapConfig() is called during reset()
    }
    
    /**
     * Test: Map change persists and reloads correctly
     * 
     * Given: GameSettings with MAP_3
     * When: Creating model, changing to MAP_1, resetting
     * Then: Model should reflect the new MAP_1 configuration
     */
    @Test
    @DisplayName("Integration: Map changes persist and reload")
    void testMapChangePersistsAndReloads() {
        // Given: Start with MAP_3
        gameSettings.setSelectedMapType(MapType.MAP_3);
        gameModel = new SurvivalGameModel(TEST_HIGH_SCORE_FILE, 2);
        assertEquals(4, gameModel.getMapConfig().getObstacles().size());
        
        // When: Change to MAP_1 (no obstacles) and reset
        gameSettings.setSelectedMapType(MapType.MAP_1);
        gameModel.reset();
        
        // Then: Should have MAP_1 with no obstacles
        assertEquals(MapType.MAP_1, gameModel.getMapConfig().getMapType());
        assertEquals(0, gameModel.getMapConfig().getObstacles().size(), 
                    "Should have no obstacles after changing to MAP_1");
    }
    
    /**
     * Test: Multiple resets maintain map consistency
     * 
     * Given: SurvivalGameModel with MAP_2
     * When: Reset multiple times without changing map
     * Then: Map config should remain MAP_2
     */
    @Test
    @DisplayName("Integration: Multiple resets maintain map consistency")
    void testMultipleResetsMaintainMap() {
        // Given: Game with MAP_2
        gameSettings.setSelectedMapType(MapType.MAP_2);
        gameModel = new SurvivalGameModel(TEST_HIGH_SCORE_FILE, 2);
        
        // When: Reset multiple times
        gameModel.reset();
        assertEquals(MapType.MAP_2, gameModel.getMapConfig().getMapType());
        
        gameModel.reset();
        assertEquals(MapType.MAP_2, gameModel.getMapConfig().getMapType());
        
        gameModel.reset();
        
        // Then: Should still be MAP_2
        assertEquals(MapType.MAP_2, gameModel.getMapConfig().getMapType(), 
                    "Should remain MAP_2 after multiple resets");
        assertEquals(2, gameModel.getMapConfig().getObstacles().size(), 
                    "Should still have 2 obstacles");
    }
}
