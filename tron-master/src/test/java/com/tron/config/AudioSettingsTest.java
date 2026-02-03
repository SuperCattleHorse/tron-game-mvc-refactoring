package com.tron.config;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * AudioSettingsTest - Unit tests for audio configuration manager
 * 
 * Tests singleton pattern, BGM/sound effects toggles, settings persistence,
 * and independent control of audio features using Given-When-Then format.
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 2.0
 */
@DisplayName("AudioSettings - Audio Configuration Tests")
class AudioSettingsTest {
    
    private static final String TEST_CONFIG_FILE = "config/audio_settings.properties";
    private AudioSettings audioSettings;
    
    @BeforeEach
    void setUp() throws Exception {
        // Clean up any existing config file
        File configFile = new File(TEST_CONFIG_FILE);
        if (configFile.exists()) {
            configFile.delete();
        }
        
        // Reset singleton instance using reflection for clean test state
        Field instanceField = AudioSettings.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        
        audioSettings = AudioSettings.getInstance();
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
     * Given: AudioSettings class exists
     * When: getInstance() is called multiple times
     * Then: The same instance should be returned
     */
    @Test
    @DisplayName("Singleton: getInstance() returns same instance")
    void testSingletonInstance() {
        // Given: AudioSettings class exists
        
        // When: getInstance() is called multiple times
        AudioSettings instance1 = AudioSettings.getInstance();
        AudioSettings instance2 = AudioSettings.getInstance();
        
        // Then: The same instance should be returned
        assertSame(instance1, instance2, "AudioSettings should return the same instance");
    }
    
    /**
     * Test: Default audio settings are both enabled
     * 
     * Given: AudioSettings is initialized
     * When: No settings are changed
     * Then: BGM and sound effects should both be enabled by default
     */
    @Test
    @DisplayName("Default: BGM and sound effects enabled by default")
    void testDefaultSettings() {
        // Given: AudioSettings is initialized (in setUp)
        
        // When: No settings are changed
        
        // Then: BGM and sound effects should both be enabled
        assertTrue(audioSettings.isBgmEnabled(), "BGM should be enabled by default");
        assertTrue(audioSettings.isSoundEffectsEnabled(), "Sound effects should be enabled by default");
    }
    
    /**
     * Test: BGM can be toggled on and off
     * 
     * Given: AudioSettings with default settings
     * When: BGM is disabled then re-enabled
     * Then: BGM state should change accordingly
     */
    @Test
    @DisplayName("Toggle: BGM can be enabled and disabled")
    void testSetBgmEnabled() {
        // Given: AudioSettings with default settings (BGM enabled)
        assertTrue(audioSettings.isBgmEnabled());
        
        // When: BGM is disabled
        audioSettings.setBgmEnabled(false);
        
        // Then: BGM state should be disabled
        assertFalse(audioSettings.isBgmEnabled(), "BGM should be disabled");
        
        // When: BGM is re-enabled
        audioSettings.setBgmEnabled(true);
        
        // Then: BGM state should be enabled
        assertTrue(audioSettings.isBgmEnabled(), "BGM should be enabled");
    }
    
    /**
     * Test: Sound effects can be toggled independently
     * 
     * Given: AudioSettings with default settings
     * When: Sound effects are disabled then re-enabled
     * Then: Sound effects state should change accordingly
     */
    @Test
    @DisplayName("Toggle: Sound effects can be enabled and disabled")
    void testSetSoundEffectsEnabled() {
        // Given: AudioSettings with default settings (sound effects enabled)
        assertTrue(audioSettings.isSoundEffectsEnabled());
        
        // When: Sound effects are disabled
        audioSettings.setSoundEffectsEnabled(false);
        
        // Then: Sound effects state should be disabled
        assertFalse(audioSettings.isSoundEffectsEnabled(), "Sound effects should be disabled");
        
        // When: Sound effects are re-enabled
        audioSettings.setSoundEffectsEnabled(true);
        
        // Then: Sound effects state should be enabled
        assertTrue(audioSettings.isSoundEffectsEnabled(), "Sound effects should be enabled");
    }
    
    /**
     * Test: Settings are persisted to file
     * 
     * Given: AudioSettings with modified settings
     * When: Settings are changed
     * Then: Settings file should be created and settings should persist
     */
    @Test
    @DisplayName("Persistence: Settings are saved to file")
    void testSettingsPersistence() {
        // Given: AudioSettings with modified settings
        
        // When: Settings are changed
        audioSettings.setBgmEnabled(false);
        audioSettings.setSoundEffectsEnabled(false);
        
        // Then: Settings file should be created
        Path configPath = Paths.get(TEST_CONFIG_FILE);
        assertTrue(Files.exists(configPath), "Settings file should be created");
        
        // And: Settings should persist
        assertFalse(audioSettings.isBgmEnabled(), "BGM should remain disabled");
        assertFalse(audioSettings.isSoundEffectsEnabled(), "Sound effects should remain disabled");
    }
    
    /**
     * Test: Reset restores default settings
     * 
     * Given: AudioSettings with both features disabled
     * When: resetToDefaults() is called
     * Then: Both features should be enabled again
     */
    @Test
    @DisplayName("Reset: resetToDefaults() restores enabled state")
    void testResetToDefaults() {
        // Given: AudioSettings with both features disabled
        audioSettings.setBgmEnabled(false);
        audioSettings.setSoundEffectsEnabled(false);
        assertFalse(audioSettings.isBgmEnabled());
        assertFalse(audioSettings.isSoundEffectsEnabled());
        
        // When: resetToDefaults() is called
        audioSettings.resetToDefaults();
        
        // Then: Both features should be enabled
        assertTrue(audioSettings.isBgmEnabled(), "BGM should be enabled after reset");
        assertTrue(audioSettings.isSoundEffectsEnabled(), "Sound effects should be enabled after reset");
    }
    
    /**
     * Test: BGM and sound effects are controlled independently
     * 
     * Given: AudioSettings with default settings
     * When: Only BGM is disabled
     * Then: Sound effects should remain enabled
     */
    @Test
    @DisplayName("Independence: BGM and sound effects controlled separately")
    void testIndependentSettings() {
        // Given: AudioSettings with default settings (both enabled)
        assertTrue(audioSettings.isBgmEnabled());
        assertTrue(audioSettings.isSoundEffectsEnabled());
        
        // When: Only BGM is disabled
        audioSettings.setBgmEnabled(false);
        
        // Then: Sound effects should remain enabled
        assertTrue(audioSettings.isSoundEffectsEnabled(), "Sound effects should remain enabled");
        assertFalse(audioSettings.isBgmEnabled(), "BGM should be disabled");
    }
    
    /**
     * Test: Singleton is thread-safe
     * 
     * Given: Multiple threads accessing AudioSettings
     * When: getInstance() is called concurrently
     * Then: All threads should receive the same instance
     */
    @Test
    @DisplayName("Thread Safety: Concurrent getInstance returns same instance")
    void testThreadSafety() throws InterruptedException {
        // Given: Multiple threads accessing AudioSettings
        int threadCount = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        AudioSettings[] instances = new AudioSettings[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    // When: getInstance() is called concurrently
                    startLatch.await();
                    instances[index] = AudioSettings.getInstance();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }
        
        startLatch.countDown();
        assertTrue(endLatch.await(5, TimeUnit.SECONDS), "All threads should complete");
        
        // Then: All threads should receive the same instance
        AudioSettings first = instances[0];
        for (int i = 1; i < threadCount; i++) {
            assertSame(first, instances[i], "All instances should be identical");
        }
    }
}
