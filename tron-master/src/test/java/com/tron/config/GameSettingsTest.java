package com.tron.config;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tron.model.util.MapType;

/**
 * GameSettingsTest - Unit tests for gameplay configuration manager
 * 
 * Tests singleton pattern, Hard AI toggle, settings persistence,
 * and configuration management using Given-When-Then format.
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
@DisplayName("GameSettings - Gameplay Configuration Tests")
class GameSettingsTest {
    
    private static final String TEST_CONFIG_FILE = "config/game_settings.properties";
    private GameSettings gameSettings;
    
    @BeforeEach
    void setUp() throws Exception {
        // Clean up any existing config file
        File configFile = new File(TEST_CONFIG_FILE);
        if (configFile.exists()) {
            configFile.delete();
        }
        
        // Reset singleton instance using reflection for clean test state
        Field instanceField = GameSettings.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        
        gameSettings = GameSettings.getInstance();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up config file after tests
        File configFile = new File(TEST_CONFIG_FILE);
        if (configFile.exists()) {
            configFile.delete();
        }
    }
    
    /**
     * Test: Singleton pattern ensures single instance
     * 
     * Given: GameSettings class exists
     * When: getInstance() is called multiple times
     * Then: The same instance should be returned
     */
    @Test
    @DisplayName("Singleton: getInstance() returns same instance")
    void testSingletonInstance() {
        // Given: GameSettings class exists
        
        // When: getInstance() is called multiple times
        GameSettings instance1 = GameSettings.getInstance();
        GameSettings instance2 = GameSettings.getInstance();
        
        // Then: The same instance should be returned
        assertSame(instance1, instance2, "GameSettings should return the same instance");
    }
    
    /**
     * Test: Default Hard AI setting is disabled
     * 
     * Given: GameSettings is initialized
     * When: No settings are changed
     * Then: Hard AI should be disabled by default
     */
    @Test
    @DisplayName("Default: Hard AI disabled by default")
    void testDefaultSettings() {
        // Given: GameSettings is initialized (in setUp)
        
        // When: No settings are changed
        
        // Then: Hard AI should be disabled
        assertFalse(gameSettings.isHardAIEnabled(), "Hard AI should be disabled by default");
    }
    
    /**
     * Test: Hard AI can be enabled
     * 
     * Given: GameSettings with default settings (Hard AI disabled)
     * When: Hard AI is enabled
     * Then: Hard AI state should be enabled
     */
    @Test
    @DisplayName("Toggle: Hard AI can be enabled")
    void testEnableHardAI() {
        // Given: GameSettings with default settings (Hard AI disabled)
        assertFalse(gameSettings.isHardAIEnabled());
        
        // When: Hard AI is enabled
        gameSettings.setHardAIEnabled(true);
        
        // Then: Hard AI state should be enabled
        assertTrue(gameSettings.isHardAIEnabled(), "Hard AI should be enabled");
    }
    
    /**
     * Test: Hard AI can be disabled after being enabled
     * 
     * Given: GameSettings with Hard AI enabled
     * When: Hard AI is disabled
     * Then: Hard AI state should be disabled
     */
    @Test
    @DisplayName("Toggle: Hard AI can be disabled")
    void testDisableHardAI() {
        // Given: GameSettings with Hard AI enabled
        gameSettings.setHardAIEnabled(true);
        assertTrue(gameSettings.isHardAIEnabled());
        
        // When: Hard AI is disabled
        gameSettings.setHardAIEnabled(false);
        
        // Then: Hard AI state should be disabled
        assertFalse(gameSettings.isHardAIEnabled(), "Hard AI should be disabled");
    }
    
    /**
     * Test: Hard AI can be toggled multiple times
     * 
     * Given: GameSettings with default settings
     * When: Hard AI is toggled on and off multiple times
     * Then: State should change correctly each time
     */
    @Test
    @DisplayName("Toggle: Hard AI can be toggled multiple times")
    void testMultipleToggles() {
        // Given: GameSettings with default settings
        assertFalse(gameSettings.isHardAIEnabled());
        
        // When/Then: Toggle on
        gameSettings.setHardAIEnabled(true);
        assertTrue(gameSettings.isHardAIEnabled(), "Hard AI should be enabled after first toggle");
        
        // When/Then: Toggle off
        gameSettings.setHardAIEnabled(false);
        assertFalse(gameSettings.isHardAIEnabled(), "Hard AI should be disabled after second toggle");
        
        // When/Then: Toggle on again
        gameSettings.setHardAIEnabled(true);
        assertTrue(gameSettings.isHardAIEnabled(), "Hard AI should be enabled after third toggle");
    }
    
    /**
     * Test: Settings are persisted to file
     * 
     * Given: GameSettings with modified settings
     * When: Hard AI is enabled
     * Then: Settings file should be created and settings should persist
     */
    @Test
    @DisplayName("Persistence: Settings are saved to file")
    void testSettingsPersistence() {
        // Given: GameSettings with default settings
        
        // When: Hard AI is enabled
        gameSettings.setHardAIEnabled(true);
        
        // Then: Settings file should be created
        Path configPath = Paths.get(TEST_CONFIG_FILE);
        assertTrue(Files.exists(configPath), "Settings file should be created");
        
        // And: Settings should persist
        assertTrue(gameSettings.isHardAIEnabled(), "Hard AI should remain enabled");
    }
    
    /**
     * Test: Settings persist after file creation
     * 
     * Given: GameSettings with Hard AI enabled and file created
     * When: Settings are queried
     * Then: Hard AI should remain enabled
     */
    @Test
    @DisplayName("Persistence: Settings remain after file creation")
    void testSettingsPersistenceAfterCreation() {
        // Given: GameSettings with Hard AI enabled
        gameSettings.setHardAIEnabled(true);
        assertTrue(Files.exists(Paths.get(TEST_CONFIG_FILE)), "Config file should exist");
        
        // When: Settings are queried
        boolean isEnabled = gameSettings.isHardAIEnabled();
        
        // Then: Hard AI should remain enabled
        assertTrue(isEnabled, "Hard AI should persist as enabled");
    }
    
    /**
     * Test: Reset restores default settings
     * 
     * Given: GameSettings with Hard AI enabled
     * When: resetToDefaults() is called
     * Then: Hard AI should be disabled again
     */
    @Test
    @DisplayName("Reset: resetToDefaults() restores disabled state")
    void testResetToDefaults() {
        // Given: GameSettings with Hard AI enabled
        gameSettings.setHardAIEnabled(true);
        assertTrue(gameSettings.isHardAIEnabled());
        
        // When: resetToDefaults() is called
        gameSettings.resetToDefaults();
        
        // Then: Hard AI should be disabled
        assertFalse(gameSettings.isHardAIEnabled(), "Hard AI should be disabled after reset");
    }
    
    /**
     * Test: Reset persists to file
     * 
     * Given: GameSettings with Hard AI enabled
     * When: resetToDefaults() is called
     * Then: Settings file should be updated with default values
     */
    @Test
    @DisplayName("Reset: resetToDefaults() persists changes")
    void testResetPersistence() {
        // Given: GameSettings with Hard AI enabled
        gameSettings.setHardAIEnabled(true);
        assertTrue(Files.exists(Paths.get(TEST_CONFIG_FILE)));
        
        // When: resetToDefaults() is called
        gameSettings.resetToDefaults();
        
        // Then: Settings should persist with default values
        assertFalse(gameSettings.isHardAIEnabled(), "Hard AI should be disabled");
        assertTrue(Files.exists(Paths.get(TEST_CONFIG_FILE)), "Settings file should still exist");
    }
    
    /**
     * Test: Configuration file is created when it doesn't exist
     * 
     * Given: No existing configuration file
     * When: GameSettings is initialized
     * Then: Configuration file should be created with default values
     */
    @Test
    @DisplayName("File Creation: Config file created on first use")
    void testConfigFileCreation() {
        // Given: Configuration already loaded in setUp
        
        // When: Settings are saved
        gameSettings.setHardAIEnabled(false); // Trigger save
        
        // Then: Configuration file should exist
        Path configPath = Paths.get(TEST_CONFIG_FILE);
        assertTrue(Files.exists(configPath), "Configuration file should be created");
    }
    
    /**
     * Test: Configuration directory is created if it doesn't exist
     * 
     * Given: No config directory exists
     * When: Settings are saved
     * Then: Config directory should be created automatically
     */
    @Test
    @DisplayName("Directory Creation: Config directory created automatically")
    void testConfigDirectoryCreation() {
        // Given: Clean state from setUp
        
        // When: Settings are saved
        gameSettings.setHardAIEnabled(true);
        
        // Then: Config directory should exist
        Path configDir = Paths.get("config");
        assertTrue(Files.exists(configDir), "Config directory should be created");
        assertTrue(Files.isDirectory(configDir), "Config path should be a directory");
    }
    
    // ============ Map Type Selection Tests ============
    
    /**
     * Test: Default map type is DEFAULT
     * 
     * Given: GameSettings is initialized
     * When: No map type is changed
     * Then: Selected map type should be DEFAULT
     */
    @Test
    @DisplayName("Map Selection: Default map type is DEFAULT")
    void testDefaultMapType() {
        // Given: GameSettings is initialized (in setUp)
        
        // When: No settings are changed
        MapType selectedMap = gameSettings.getSelectedMapType();
        
        // Then: Should be DEFAULT
        assertEquals(MapType.DEFAULT, selectedMap, "Default map type should be DEFAULT");
    }
    
    /**
     * Test: Map type can be changed
     * 
     * Given: GameSettings with default map type
     * When: Map type is changed to MAP_1
     * Then: Selected map type should be MAP_1
     */
    @Test
    @DisplayName("Map Selection: Map type can be changed")
    void testChangeMapType() {
        // Given: GameSettings with default map type
        assertEquals(MapType.DEFAULT, gameSettings.getSelectedMapType());
        
        // When: Changing to MAP_1
        gameSettings.setSelectedMapType(MapType.MAP_1);
        
        // Then: Should be MAP_1
        assertEquals(MapType.MAP_1, gameSettings.getSelectedMapType(), 
                    "Map type should be changed to MAP_1");
    }
    
    /**
     * Test: Map type persists across sessions
     * 
     * Given: GameSettings with map type set to MAP_2
     * When: Settings are reloaded from file
     * Then: Map type should still be MAP_2
     */
    @Test
    @DisplayName("Map Selection: Map type persists across sessions")
    void testMapTypePersistence() throws Exception {
        // Given: Set map type to MAP_2
        gameSettings.setSelectedMapType(MapType.MAP_2);
        assertEquals(MapType.MAP_2, gameSettings.getSelectedMapType());
        
        // When: Resetting singleton and reloading
        Field instanceField = GameSettings.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        GameSettings reloadedSettings = GameSettings.getInstance();
        
        // Then: Map type should persist
        assertEquals(MapType.MAP_2, reloadedSettings.getSelectedMapType(), 
                    "Map type should persist as MAP_2");
    }
    
    /**
     * Test: Reset restores default map type
     * 
     * Given: GameSettings with map type set to MAP_3
     * When: resetToDefaults() is called
     * Then: Map type should be DEFAULT again
     */
    @Test
    @DisplayName("Map Selection: Reset restores default map type")
    void testResetMapType() {
        // Given: Set map type to MAP_3
        gameSettings.setSelectedMapType(MapType.MAP_3);
        assertEquals(MapType.MAP_3, gameSettings.getSelectedMapType());
        
        // When: resetToDefaults() is called
        gameSettings.resetToDefaults();
        
        // Then: Should be DEFAULT
        assertEquals(MapType.DEFAULT, gameSettings.getSelectedMapType(), 
                    "Map type should be DEFAULT after reset");
    }
    
    /**
     * Test: All map types can be set
     * 
     * Given: GameSettings instance
     * When: Setting each map type
     * Then: Each should be correctly stored and retrieved
     */
    @Test
    @DisplayName("Map Selection: All map types can be set")
    void testAllMapTypesCanBeSet() {
        // Given: GameSettings instance
        
        // When & Then: Test each map type
        for (MapType mapType : MapType.values()) {
            gameSettings.setSelectedMapType(mapType);
            assertEquals(mapType, gameSettings.getSelectedMapType(), 
                        "Should be able to set and get " + mapType);
        }
    }
}

