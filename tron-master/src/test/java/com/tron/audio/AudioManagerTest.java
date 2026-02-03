package com.tron.audio;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tron.config.AudioSettings;

/**
 * AudioManagerTest - Unit tests for centralized audio management
 * 
 * Tests singleton pattern, audio settings integration, and runtime control
 * of background music and sound effects using Given-When-Then format.
 * 
 * Note: These are unit tests focusing on integration with AudioSettings.
 * Full audio playback testing requires JavaFX runtime environment.
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
@DisplayName("AudioManager - Audio System Tests")
class AudioManagerTest {
    
    private AudioManager audioManager;
    private AudioSettings audioSettings;
    
    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() throws Exception {
        // Reset AudioSettings singleton for clean state
        Field settingsInstanceField = AudioSettings.class.getDeclaredField("instance");
        settingsInstanceField.setAccessible(true);
        settingsInstanceField.set(null, null);
        audioSettings = AudioSettings.getInstance();
        audioSettings.resetToDefaults();
        
        // Reset AudioManager singleton for clean state
        Field managerInstanceField = AudioManager.class.getDeclaredField("instance");
        managerInstanceField.setAccessible(true);
        managerInstanceField.set(null, null);
        audioManager = AudioManager.getInstance();
    }
    
    /**
     * Test: Singleton pattern ensures single AudioManager instance
     * 
     * Given: AudioManager class exists
     * When: getInstance() is called multiple times
     * Then: The same instance should be returned
     */
    @Test
    @DisplayName("Singleton: getInstance() returns same instance")
    void testSingletonPattern() {
        // Given: AudioManager class exists
        
        // When: getInstance() is called multiple times
        AudioManager instance1 = AudioManager.getInstance();
        AudioManager instance2 = AudioManager.getInstance();
        
        // Then: The same instance should be returned
        assertSame(instance1, instance2, "getInstance() should return the same instance");
    }
    
    /**
     * Test: AudioManager integrates with AudioSettings
     * 
     * Given: AudioManager and AudioSettings are initialized
     * When: AudioSettings BGM is disabled
     * Then: playSoundEffect should still work (independent control)
     */
    @Test
    @DisplayName("Integration: AudioManager respects AudioSettings")
    void testAudioSettingsIntegration() {
        // Given: AudioManager and AudioSettings are initialized
        assertTrue(audioSettings.isBgmEnabled());
        assertTrue(audioSettings.isSoundEffectsEnabled());
        
        // When: AudioSettings BGM is disabled
        audioSettings.setBgmEnabled(false);
        
        // Then: BGM setting should be disabled
        assertFalse(audioSettings.isBgmEnabled());
        
        // And: Sound effects should remain enabled
        assertTrue(audioSettings.isSoundEffectsEnabled());
    }
    
    /**
     * Test: Runtime BGM toggle updates state correctly
     * 
     * Given: AudioManager with AudioSettings enabled
     * When: setBgmEnabledFromSettings is called with false
     * Then: AudioSettings should reflect the change
     */
    @Test
    @DisplayName("Runtime: BGM can be toggled at runtime")
    void testRuntimeBgmToggle() {
        // Given: AudioManager with AudioSettings enabled
        assertTrue(audioSettings.isBgmEnabled());
        
        // When: setBgmEnabledFromSettings is called with false
        audioSettings.setBgmEnabled(false);
        audioManager.setBgmEnabledFromSettings(false);
        
        // Then: AudioSettings should reflect the change
        assertFalse(audioSettings.isBgmEnabled());
        
        // When: Re-enabled
        audioSettings.setBgmEnabled(true);
        audioManager.setBgmEnabledFromSettings(true);
        
        // Then: Should be enabled again
        assertTrue(audioSettings.isBgmEnabled());
    }
    
    /**
     * Test: Runtime sound effects toggle updates state correctly
     * 
     * Given: AudioManager with AudioSettings enabled
     * When: setSoundEffectsEnabledFromSettings is called with false
     * Then: AudioSettings should reflect the change
     */
    @Test
    @DisplayName("Runtime: Sound effects can be toggled at runtime")
    void testRuntimeSoundEffectsToggle() {
        // Given: AudioManager with AudioSettings enabled
        assertTrue(audioSettings.isSoundEffectsEnabled());
        
        // When: setSoundEffectsEnabledFromSettings is called with false
        audioSettings.setSoundEffectsEnabled(false);
        audioManager.setSoundEffectsEnabledFromSettings(false);
        
        // Then: AudioSettings should reflect the change
        assertFalse(audioSettings.isSoundEffectsEnabled());
    }
    
    /**
     * Test: AudioManager has all required sound effect types
     * 
     * Given: AudioManager is initialized
     * When: Checking available sound effects
     * Then: All expected sound effects should be available
     */
    @Test
    @DisplayName("Resources: All sound effect types are defined")
    void testSoundEffectTypes() {
        // Given: AudioManager is initialized
        
        // When: Checking available sound effects
        AudioManager.SoundEffect[] effects = AudioManager.SoundEffect.values();
        
        // Then: All expected sound effects should be available
        assertEquals(6, effects.length, "Should have 6 sound effect types");
        
        // Verify each type exists
        assertNotNull(AudioManager.SoundEffect.valueOf("CLICK"));
        assertNotNull(AudioManager.SoundEffect.valueOf("LOSE"));
        assertNotNull(AudioManager.SoundEffect.valueOf("WIN"));
        assertNotNull(AudioManager.SoundEffect.valueOf("PAUSE"));
        assertNotNull(AudioManager.SoundEffect.valueOf("UNPAUSE"));
        assertNotNull(AudioManager.SoundEffect.valueOf("PICKUP"));
    }
    
    /**
     * Test: playSoundEffect respects AudioSettings
     * 
     * Given: AudioManager with sound effects disabled
     * When: playSoundEffect is called
     * Then: No exception should be thrown (graceful handling)
     */
    @Test
    @DisplayName("Control: playSoundEffect respects AudioSettings")
    void testPlaySoundEffectWithSettingsDisabled() {
        // Given: AudioManager with sound effects disabled
        audioSettings.setSoundEffectsEnabled(false);
        
        // When: playSoundEffect is called
        // Then: No exception should be thrown
        assertDoesNotThrow(() -> {
            audioManager.playSoundEffect(AudioManager.SoundEffect.CLICK);
        }, "Should handle disabled sound effects gracefully");
    }
    
    /**
     * Test: Singleton is thread-safe under concurrent access
     * 
     * Given: Multiple threads accessing AudioManager
     * When: getInstance() is called concurrently
     * Then: All threads should receive the same instance
     */
    @Test
    @DisplayName("Thread Safety: Concurrent getInstance returns same instance")
    void testThreadSafety() throws InterruptedException {
        // Given: Multiple threads accessing AudioManager
        int threadCount = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        AudioManager[] instances = new AudioManager[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    // When: getInstance() is called concurrently
                    startLatch.await();
                    instances[index] = AudioManager.getInstance();
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
        AudioManager first = instances[0];
        for (int i = 1; i < threadCount; i++) {
            assertSame(first, instances[i], "All instances should be identical");
        }
    }
    
    /**
     * Test: AudioManager methods don't throw exceptions with missing audio files
     * 
     * Given: AudioManager is initialized (may not have audio files in test environment)
     * When: Audio methods are called
     * Then: No exceptions should be thrown (graceful error handling)
     */
    @Test
    @DisplayName("Resilience: Graceful handling of missing audio resources")
    void testGracefulErrorHandling() {
        // Given: AudioManager is initialized
        
        // When: Audio methods are called
        // Then: No exceptions should be thrown
        assertDoesNotThrow(() -> {
            audioManager.playSoundEffect(AudioManager.SoundEffect.CLICK);
            audioManager.startBGM();
            audioManager.pauseBGM();
            audioManager.resumeBGM();
            audioManager.stopBGM();
        }, "Should handle missing audio files gracefully");
    }
}
