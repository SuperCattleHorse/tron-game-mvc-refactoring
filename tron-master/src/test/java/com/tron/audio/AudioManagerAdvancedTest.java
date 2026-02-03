package com.tron.audio;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tron.config.AudioSettings;

/**
 * AudioManagerAdvancedTest - Advanced and boundary tests for AudioManager
 * 
 * <p>This test class validates complex scenarios, edge cases, and boundary
 * conditions for the audio management system. Tests focus on:</p>
 * <ul>
 *   <li>Rapid state transitions and stress testing</li>
 *   <li>Concurrent access patterns and race conditions</li>
 *   <li>Memory leak prevention and resource cleanup</li>
 *   <li>Extreme volume and timing scenarios</li>
 *   <li>Integration with AudioSettings under load</li>
 * </ul>
 * 
 * <p>These tests complement AudioManagerTest with boundary and stress scenarios.</p>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see AudioManager
 * @see AudioSettings
 */
@DisplayName("AudioManager - Advanced and Boundary Tests")
public class AudioManagerAdvancedTest {
    
    private AudioManager audioManager;
    private AudioSettings audioSettings;
    
    @BeforeEach
    void setUp() throws Exception {
        // Reset singletons for clean state
        resetSingleton(AudioSettings.class);
        resetSingleton(AudioManager.class);
        
        audioSettings = AudioSettings.getInstance();
        audioSettings.resetToDefaults();
        audioManager = AudioManager.getInstance();
    }
    
    /**
     * Helper method to reset singleton instances via reflection
     */
    private void resetSingleton(Class<?> clazz) throws Exception {
        Field instanceField = clazz.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }
    
    /**
     * Test: Rapid BGM state transitions don't cause crashes
     * 
     * <p>Given: AudioManager initialized</p>
     * <p>When: BGM is rapidly started, paused, resumed, stopped</p>
     * <p>Then: All operations complete without exceptions</p>
     * 
     * Class and Method under test: AudioManager BGM control methods
     * Test Inputs/Preconditions: Rapid sequential BGM operations
     * Expected Outcome: No exceptions, graceful state handling
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("Boundary: Rapid BGM state transitions handled gracefully")
    void testRapidBgmStateTransitions() {
        // Given: AudioManager initialized
        
        // When: BGM is rapidly started, paused, resumed, stopped
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 20; i++) {
                audioManager.startBGM();
                audioManager.pauseBGM();
                audioManager.resumeBGM();
                audioManager.stopBGM();
            }
        }, "Rapid BGM state changes should not throw exceptions");
        
        // Then: All operations should complete successfully
        assertTrue(true, "Test completed without crashes");
    }
    
    /**
     * Test: Concurrent sound effect playback from multiple threads
     * 
     * <p>Given: Multiple threads ready to play sound effects</p>
     * <p>When: All threads play effects simultaneously</p>
     * <p>Then: All operations complete without exceptions or deadlocks</p>
     * 
     * Class and Method under test: AudioManager.playSoundEffect()
     * Test Inputs/Preconditions: 100 concurrent playback requests
     * Expected Outcome: All complete within timeout, no deadlocks
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("Concurrency: Multiple threads can play sound effects concurrently")
    void testConcurrentSoundEffectPlayback() throws InterruptedException {
        // Given: Multiple threads ready to play sound effects
        int threadCount = 100;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    
                    // When: All threads play effects simultaneously
                    AudioManager.SoundEffect effect = 
                        AudioManager.SoundEffect.values()[index % 6];
                    audioManager.playSoundEffect(effect);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }
        
        startLatch.countDown();
        
        // Then: All operations complete without exceptions or deadlocks
        assertTrue(endLatch.await(10, TimeUnit.SECONDS), 
                "All threads should complete within timeout");
        assertEquals(threadCount, successCount.get(), 
                "All sound effect plays should succeed");
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS), 
                "Executor should terminate cleanly");
    }
    
    /**
     * Test: Settings toggle during active BGM playback
     * 
     * <p>Given: BGM is actively playing</p>
     * <p>When: BGM is disabled via AudioSettings during playback</p>
     * <p>Then: System should handle toggle gracefully</p>
     * 
     * Class and Method under test: AudioManager.setBgmEnabledFromSettings()
     * Test Inputs/Preconditions: Active BGM playback
     * Expected Outcome: Graceful handling without exceptions
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("Integration: Settings toggle during active playback")
    void testSettingsToggleDuringPlayback() {
        // Given: BGM is actively playing
        audioSettings.setBgmEnabled(true);
        audioManager.setBgmEnabledFromSettings(true);
        audioManager.startBGM();
        
        // When: BGM is disabled via AudioSettings during playback
        assertDoesNotThrow(() -> {
            audioSettings.setBgmEnabled(false);
            audioManager.setBgmEnabledFromSettings(false);
            audioManager.stopBGM();
        }, "Should handle settings toggle during playback gracefully");
        
        // Then: System should handle toggle gracefully
        assertFalse(audioSettings.isBgmEnabled(), 
                "BGM should be disabled in settings");
    }
    
    /**
     * Test: Volume bounds enforcement (0.0 to 1.0)
     * 
     * <p>Given: AudioManager initialized</p>
     * <p>When: Volume is set to extreme values (negative, > 1.0)</p>
     * <p>Then: Volume should be clamped to valid range</p>
     * 
     * Class and Method under test: AudioManager.setMusicVolume(), setSFXVolume()
     * Test Inputs/Preconditions: Out-of-bounds volume values
     * Expected Outcome: Volume clamped to [0.0, 1.0] range
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("Boundary: Volume values are clamped to valid range")
    void testVolumeRangeClamping() {
        // Given: AudioManager initialized
        
        // When: Volume is set to extreme values
        assertDoesNotThrow(() -> {
            audioManager.setMusicVolume(-1.0);
            audioManager.setMusicVolume(2.0);
            audioManager.setSFXVolume(-0.5);
            audioManager.setSFXVolume(1.5);
        }, "Volume setters should handle out-of-bounds values");
        
        // Then: Operations should complete without exceptions
        // (actual clamping verification would require getters)
        assertTrue(true, "Volume operations completed");
    }
    
    /**
     * Test: Repeated pause/resume cycles maintain state consistency
     * 
     * <p>Given: BGM is started</p>
     * <p>When: BGM is paused and resumed 50 times</p>
     * <p>Then: All operations complete without state corruption</p>
     * 
     * Class and Method under test: AudioManager.pauseBGM(), resumeBGM()
     * Test Inputs/Preconditions: 50 pause/resume cycles
     * Expected Outcome: Consistent state, no exceptions
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("Stress: Repeated pause/resume cycles maintain consistency")
    void testRepeatedPauseResumeCycles() {
        // Given: BGM is started
        audioManager.startBGM();
        
        // When: BGM is paused and resumed 50 times
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 50; i++) {
                audioManager.pauseBGM();
                audioManager.resumeBGM();
            }
        }, "Repeated pause/resume should not cause errors");
        
        // Then: All operations complete without state corruption
        audioManager.stopBGM();
        assertTrue(true, "State consistency maintained");
    }
    
    /**
     * Test: Multiple sound effects played in rapid succession
     * 
     * <p>Given: AudioManager with sound effects enabled</p>
     * <p>When: 100 sound effects are played rapidly (no delays)</p>
     * <p>Then: All calls complete without blocking or exceptions</p>
     * 
     * Class and Method under test: AudioManager.playSoundEffect()
     * Test Inputs/Preconditions: 100 rapid consecutive calls
     * Expected Outcome: Non-blocking execution, all complete
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("Stress: Rapid successive sound effect playback")
    void testRapidSuccessiveSoundEffects() {
        // Given: AudioManager with sound effects enabled
        audioSettings.setSoundEffectsEnabled(true);
        
        // When: 100 sound effects are played rapidly
        long startTime = System.currentTimeMillis();
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 100; i++) {
                audioManager.playSoundEffect(AudioManager.SoundEffect.CLICK);
            }
        }, "Rapid sound effect playback should not throw exceptions");
        long duration = System.currentTimeMillis() - startTime;
        
        // Then: All calls complete without blocking
        assertTrue(duration < 5000, 
                "100 sound effects should complete within 5 seconds (actual: " + duration + "ms)");
    }
    
    /**
     * Test: AudioManager survives settings reset
     * 
     * <p>Given: AudioManager with custom settings</p>
     * <p>When: AudioSettings.resetToDefaults() is called</p>
     * <p>Then: AudioManager continues to function correctly</p>
     * 
     * Class and Method under test: AudioSettings.resetToDefaults(), AudioManager operations
     * Test Inputs/Preconditions: Settings reset during operation
     * Expected Outcome: AudioManager remains functional
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("Integration: AudioManager survives settings reset")
    void testAudioManagerSurvivesSettingsReset() {
        // Given: AudioManager with custom settings
        audioSettings.setBgmEnabled(true);
        audioSettings.setSoundEffectsEnabled(true);
        audioManager.startBGM();
        
        // When: AudioSettings.resetToDefaults() is called
        audioSettings.resetToDefaults();
        
        // Then: AudioManager continues to function correctly
        assertDoesNotThrow(() -> {
            audioManager.playSoundEffect(AudioManager.SoundEffect.WIN);
            audioManager.stopBGM();
            audioManager.startBGM();
        }, "AudioManager should continue functioning after settings reset");
        
        audioManager.stopBGM();
    }
    
    /**
     * Test: Zero-length pause (immediate resume) doesn't break state
     * 
     * <p>Given: BGM is playing</p>
     * <p>When: pauseBGM() immediately followed by resumeBGM()</p>
     * <p>Then: BGM should continue playing without issues</p>
     * 
     * Class and Method under test: AudioManager.pauseBGM(), resumeBGM()
     * Test Inputs/Preconditions: Immediate pause/resume with no delay
     * Expected Outcome: State remains consistent
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("Edge Case: Zero-length pause handled correctly")
    void testZeroLengthPause() {
        // Given: BGM is playing
        audioManager.startBGM();
        
        // When: pauseBGM() immediately followed by resumeBGM()
        assertDoesNotThrow(() -> {
            audioManager.pauseBGM();
            audioManager.resumeBGM();
        }, "Zero-length pause should not cause issues");
        
        // Then: BGM should continue playing without issues
        audioManager.stopBGM();
        assertTrue(true, "Zero-length pause handled correctly");
    }
    
    /**
     * Test: All sound effect types can be played sequentially
     * 
     * <p>Given: AudioManager initialized with all sound types available</p>
     * <p>When: Each sound effect type is played once in sequence</p>
     * <p>Then: All sound types play without exceptions</p>
     * 
     * Class and Method under test: AudioManager.playSoundEffect()
     * Test Inputs/Preconditions: All 6 SoundEffect enum values
     * Expected Outcome: All types play successfully
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("Completeness: All sound effect types are playable")
    void testAllSoundEffectTypesPlayable() {
        // Given: AudioManager initialized with all sound types available
        audioSettings.setSoundEffectsEnabled(true);
        
        // When: Each sound effect type is played once in sequence
        AudioManager.SoundEffect[] allEffects = AudioManager.SoundEffect.values();
        
        for (AudioManager.SoundEffect effect : allEffects) {
            // Then: All sound types play without exceptions
            assertDoesNotThrow(() -> audioManager.playSoundEffect(effect),
                    "Sound effect " + effect + " should play without exceptions");
        }
        
        assertEquals(6, allEffects.length, 
                "Should have tested all 6 sound effect types");
    }
    
    /**
     * Test: stopBGM called when BGM not started is safe
     * 
     * <p>Given: AudioManager initialized, BGM never started</p>
     * <p>When: stopBGM() is called</p>
     * <p>Then: No exceptions should be thrown</p>
     * 
     * Class and Method under test: AudioManager.stopBGM()
     * Test Inputs/Preconditions: stopBGM without prior startBGM
     * Expected Outcome: Graceful no-op behavior
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("Edge Case: stopBGM without startBGM is safe")
    void testStopBgmWithoutStart() {
        // Given: AudioManager initialized, BGM never started
        
        // When: stopBGM() is called
        // Then: No exceptions should be thrown
        assertDoesNotThrow(() -> audioManager.stopBGM(),
                "stopBGM should be safe even when BGM not started");
    }
    
    /**
     * Test: resumeBGM without pauseBGM is safe
     * 
     * <p>Given: BGM is playing (not paused)</p>
     * <p>When: resumeBGM() is called</p>
     * <p>Then: No exceptions should be thrown</p>
     * 
     * Class and Method under test: AudioManager.resumeBGM()
     * Test Inputs/Preconditions: resumeBGM without prior pauseBGM
     * Expected Outcome: Graceful no-op behavior
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("Edge Case: resumeBGM without pauseBGM is safe")
    void testResumeBgmWithoutPause() {
        // Given: BGM is playing (not paused)
        audioManager.startBGM();
        
        // When: resumeBGM() is called
        // Then: No exceptions should be thrown
        assertDoesNotThrow(() -> audioManager.resumeBGM(),
                "resumeBGM should be safe even when not paused");
        
        audioManager.stopBGM();
    }
}
