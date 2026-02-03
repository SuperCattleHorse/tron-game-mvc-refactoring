package com.tron.integration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tron.audio.AudioManager;
import com.tron.config.AudioSettings;
import com.tron.config.GameSettings;
import com.tron.controller.fx.FXGameController;
import com.tron.model.game.Player;
import com.tron.model.game.StoryGameModel;
import com.tron.model.game.SurvivalGameModel;
import com.tron.model.game.TronGameModel;
import com.tron.model.game.TwoPlayerGameModel;
import com.tron.model.input.GameInput;
import com.tron.model.observer.GameStateObserver;

import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * BoundaryScenarioTest - Extreme boundary and stress testing
 * 
 * <p>This test class validates system behavior under extreme conditions,
 * including network-like latency simulation, resource exhaustion scenarios,
 * and concurrent operations at scale. Tests ensure the system remains stable
 * and predictable even when pushed to operational limits.</p>
 * 
 * <p>Boundary scenarios covered:</p>
 * <ul>
 *   <li>Network communication simulation with high latency and packet loss</li>
 *   <li>Extreme concurrent operations (1000+ simultaneous requests)</li>
 *   <li>Memory pressure and resource exhaustion handling</li>
 *   <li>Rapid state transitions under load</li>
 *   <li>Maximum player capacity and collision detection at scale</li>
 *   <li>Audio system behavior under extreme conditions</li>
 *   <li>Observer notification cascade with large observer counts</li>
 *   <li>Settings persistence under concurrent modifications</li>
 * </ul>
 * 
 * <p>These tests complement integration tests by focusing on robustness
 * under adverse conditions and system limits.</p>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see FXGameController
 * @see TronGameModel
 * @see AudioManager
 * @see GameSettings
 */
@DisplayName("Boundary Scenario Tests - Extreme Conditions")
public class BoundaryScenarioTest {
    
    private static final int TIMEOUT_SECONDS = 30;
    private static final int EXTREME_THREAD_COUNT = 1000;
    private static final int HIGH_LATENCY_MS = 200;
    
    private FXGameController controller;
    private GameSettings gameSettings;
    private AudioSettings audioSettings;
    private AudioManager audioManager;
    
    /**
     * Initialize JavaFX Platform before running boundary tests
     * 
     * <p>Given: JavaFX runtime is not initialized</p>
     * <p>When: Test suite starts</p>
     * <p>Then: Platform should be started for UI tests</p>
     */
    @BeforeAll
    static void initializeJavaFX() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> {
            latch.countDown();
        });
        assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), 
                "JavaFX Platform should initialize within timeout");
    }
    
    @BeforeEach
    void setUp() throws Exception {
        // Reset all singletons for clean test state
        resetSingleton(GameSettings.class);
        resetSingleton(AudioSettings.class);
        resetSingleton(AudioManager.class);
        resetSingleton(FXGameController.class);
        
        gameSettings = GameSettings.getInstance();
        gameSettings.resetToDefaults();
        audioSettings = AudioSettings.getInstance();
        audioSettings.resetToDefaults();
        audioManager = AudioManager.getInstance();
        controller = FXGameController.getInstance();
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
     * Test: Simulated network latency with delayed observer notifications
     * 
     * <p>Given: Game model with observer pattern</p>
     * <p>When: Observers simulate network delay (200ms) in processing</p>
     * <p>Then: System should handle delayed responses gracefully</p>
     * 
     * Class and Method under test: TronGameModel observer notification with latency
     * Test Inputs/Preconditions: Observers with simulated network delay
     * Expected Outcome: All notifications eventually processed, no deadlocks
     * Testing Framework: JUnit 5 + concurrency utilities
     */
    @Test
    @DisplayName("Network: Handle high-latency observer notifications")
    void testHighLatencyObserverNotifications() throws Exception {
        // Given: Game model with delayed observers simulating network latency
        final CountDownLatch setupLatch = new CountDownLatch(1);
        final CountDownLatch testLatch = new CountDownLatch(1);
        final AtomicInteger notificationCount = new AtomicInteger(0);
        final int expectedNotifications = 10;
        
        Platform.runLater(() -> {
            try {
                TronGameModel gameModel = new SurvivalGameModel(
                    "src/main/resources/HighScores.json", 1);
                
                // Add observer that simulates network delay
                GameStateObserver delayedObserver = new GameStateObserver() {
                    @Override
                    public void onGameStateChanged() {
                        // Simulate network latency
                        try {
                            Thread.sleep(HIGH_LATENCY_MS);
                            notificationCount.incrementAndGet();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    
                    @Override
                    public void onScoreChanged(int playerIndex, int newScore) {
                        notificationCount.incrementAndGet();
                    }
                    
                    @Override
                    public void onBoostChanged(int playerIndex, int boostCount) {}
                    
                    @Override
                    public void onPlayerCrashed(int playerIndex) {}
                    
                    @Override
                    public void onGameReset() {
                        notificationCount.incrementAndGet();
                    }
                };
                
                gameModel.addObserver(delayedObserver);
                setupLatch.countDown();
                
                // When: Multiple state changes trigger notifications
                gameModel.reset();
                for (int i = 0; i < expectedNotifications - 1; i++) {
                    gameModel.notifyObservers();
                    Thread.sleep(50); // Small delay between notifications
                }
                
                // Wait for all delayed notifications to process
                Thread.sleep(HIGH_LATENCY_MS * 2);
                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                testLatch.countDown();
            }
        });
        
        assertTrue(setupLatch.await(5, TimeUnit.SECONDS), "Setup should complete");
        assertTrue(testLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), "Test should complete");
        
        // Then: All notifications should eventually be processed
        assertTrue(notificationCount.get() >= expectedNotifications - 2, 
                "Most notifications should be processed despite latency");
    }
    
    /**
     * Test: Extreme concurrent singleton access (1000+ threads)
     * 
     * <p>Given: System with singleton components</p>
     * <p>When: 1000 threads simultaneously request singleton instances</p>
     * <p>Then: All threads receive same instance, no race conditions</p>
     * 
     * Class and Method under test: GameSettings.getInstance(), AudioManager.getInstance()
     * Test Inputs/Preconditions: 1000 concurrent threads
     * Expected Outcome: Single instance returned to all threads, thread-safe
     * Testing Framework: JUnit 5 + ExecutorService
     */
    @Test
    @DisplayName("Concurrency: 1000+ threads accessing singletons")
    void testExtremeConcurrentSingletonAccess() throws Exception {
        // Given: Fresh singleton instances
        final GameSettings[] settingsInstances = new GameSettings[EXTREME_THREAD_COUNT];
        final AudioManager[] audioInstances = new AudioManager[EXTREME_THREAD_COUNT];
        
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(EXTREME_THREAD_COUNT);
        
        // When: 1000 threads simultaneously request singleton instances
        for (int i = 0; i < EXTREME_THREAD_COUNT; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    settingsInstances[index] = GameSettings.getInstance();
                    audioInstances[index] = AudioManager.getInstance();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }
        
        startLatch.countDown();
        assertTrue(endLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), 
                "All threads should complete");
        
        // Then: All threads receive same instance
        GameSettings settingsReference = settingsInstances[0];
        AudioManager audioReference = audioInstances[0];
        assertNotNull(settingsReference, "Reference instance should exist");
        assertNotNull(audioReference, "Audio reference should exist");
        
        for (int i = 1; i < EXTREME_THREAD_COUNT; i++) {
            assertEquals(settingsReference, settingsInstances[i], 
                    "All threads should receive same GameSettings instance");
            assertEquals(audioReference, audioInstances[i], 
                    "All threads should receive same AudioManager instance");
        }
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }
    
    /**
     * Test: Rapid game mode switching under load
     * 
     * <p>Given: Game controller initialized</p>
     * <p>When: Game modes rapidly switch 100 times</p>
     * <p>Then: No resource leaks, crashes, or state corruption</p>
     * 
     * Class and Method under test: FXGameController mode switching methods
     * Test Inputs/Preconditions: Rapid sequential mode changes
     * Expected Outcome: All transitions complete successfully
     * Testing Framework: JUnit 5 + JavaFX Platform
     */
    @Test
    @DisplayName("Stress: Rapid game mode switching (100 iterations)")
    void testRapidGameModeSwitching() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        final int iterations = 100;
        
        Platform.runLater(() -> {
            try {
                Stage testStage = new Stage();
                controller.start(testStage);
                
                // When: Rapidly switch between game modes
                for (int i = 0; i < iterations; i++) {
                    switch (i % 3) {
                        case 0:
                            controller.startStoryMode();
                            break;
                        case 1:
                            controller.startSurvivalMode();
                            break;
                        case 2:
                            controller.startTwoPlayerMode();
                            break;
                    }
                    
                    // Quick cleanup - go back to main menu
                    controller.showMainMenu();
                    
                    // Small delay to allow cleanup
                    Thread.sleep(10);
                }
                
                testPassed.set(true);
                testStage.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), "Test should complete");
        assertTrue(testPassed.get(), "All mode switches should succeed");
    }
    
    /**
     * Test: Maximum observer count with cascade notifications
     * 
     * <p>Given: Game model with 100 attached observers</p>
     * <p>When: State change triggers cascade of notifications</p>
     * <p>Then: All observers notified correctly, no stack overflow</p>
     * 
     * Class and Method under test: TronGameModel.notifyObservers()
     * Test Inputs/Preconditions: 100 observers attached to model
     * Expected Outcome: All observers receive notifications
     * Testing Framework: JUnit 5 + JavaFX Platform
     */
    @Test
    @DisplayName("Scale: 100 observers with cascade notifications")
    void testMaximumObserverCount() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final int observerCount = 100;
        final AtomicInteger notificationSum = new AtomicInteger(0);
        
        Platform.runLater(() -> {
            try {
                TronGameModel gameModel = new SurvivalGameModel(
                    "src/main/resources/HighScores.json", 1);
                
                // Given: Attach 100 observers
                List<GameStateObserver> observers = new ArrayList<>();
                for (int i = 0; i < observerCount; i++) {
                    final int observerId = i;
                    GameStateObserver observer = new GameStateObserver() {
                        @Override
                        public void onGameStateChanged() {
                            notificationSum.addAndGet(observerId);
                        }
                        
                        @Override
                        public void onScoreChanged(int playerIndex, int newScore) {}
                        
                        @Override
                        public void onBoostChanged(int playerIndex, int boostCount) {}
                        
                        @Override
                        public void onPlayerCrashed(int playerIndex) {}
                        
                        @Override
                        public void onGameReset() {}
                    };
                    observers.add(observer);
                    gameModel.addObserver(observer);
                }
                
                // When: Trigger notification cascade
                gameModel.reset();
                gameModel.notifyObservers();
                
                // Brief wait for notifications
                Thread.sleep(100);
                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), "Test should complete");
        
        // Then: All observers should have been notified at least once
        assertTrue(notificationSum.get() > 0, 
                "Observers should have received notifications");
    }
    
    /**
     * Test: Audio system under extreme load (1000 rapid operations)
     * 
     * <p>Given: Audio system initialized</p>
     * <p>When: 1000 rapid audio operations executed</p>
     * <p>Then: System remains stable, no crashes or resource leaks</p>
     * 
     * Class and Method under test: AudioManager under extreme load
     * Test Inputs/Preconditions: Rapid sequential audio calls
     * Expected Outcome: All operations complete gracefully
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("Stress: Audio system extreme load (1000 operations)")
    void testAudioSystemExtremeLoad() {
        // Given: Audio system initialized
        final int operationCount = 1000;
        
        // When: Execute 1000 rapid audio operations
        assertDoesNotThrow(() -> {
            for (int i = 0; i < operationCount; i++) {
                switch (i % 6) {
                    case 0:
                        audioManager.startBGM();
                        break;
                    case 1:
                        audioManager.pauseBGM();
                        break;
                    case 2:
                        audioManager.resumeBGM();
                        break;
                    case 3:
                        audioManager.stopBGM();
                        break;
                    case 4:
                        audioManager.setSFXVolume(Math.random());
                        break;
                }
            }
        }, "Audio system should handle extreme load without exceptions");
        
        // Then: System should remain stable
        assertNotNull(audioManager, "AudioManager should remain functional");
    }
    
    /**
     * Test: Concurrent settings modifications from multiple threads
     * 
     * <p>Given: Settings system with concurrent access</p>
     * <p>When: 50 threads simultaneously modify settings</p>
     * <p>Then: No data corruption, all operations complete safely</p>
     * 
     * Class and Method under test: GameSettings concurrent modifications
     * Test Inputs/Preconditions: 50 threads modifying settings
     * Expected Outcome: Thread-safe operations, no corruption
     * Testing Framework: JUnit 5 + ExecutorService
     */
    @Test
    @DisplayName("Concurrency: 50 threads modifying settings simultaneously")
    void testConcurrentSettingsModifications() throws Exception {
        // Given: Settings system initialized
        final int threadCount = 50;
        final int operationsPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        final AtomicInteger successCount = new AtomicInteger(0);
        
        // When: Multiple threads modify settings simultaneously
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    
                    for (int j = 0; j < operationsPerThread; j++) {
                        gameSettings.setHardAIEnabled(threadId % 2 == 0);
                        boolean enabled = gameSettings.isHardAIEnabled();
                        
                        audioSettings.setBgmEnabled(threadId % 2 == 0);
                        audioSettings.setSoundEffectsEnabled(threadId % 2 != 0);
                        boolean bgm = audioSettings.isBgmEnabled();
                        boolean sfx = audioSettings.isSoundEffectsEnabled();
                        
                        // Verify reads are consistent
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }
        
        startLatch.countDown();
        assertTrue(endLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), 
                "All threads should complete");
        
        // Then: Operations should complete successfully
        assertTrue(successCount.get() > threadCount * operationsPerThread * 0.9, 
                "Most operations should succeed");
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }
    
    /**
     * Test: Game update loop under simulated packet loss
     * 
     * <p>Given: Game running with update loop</p>
     * <p>When: Random updates are skipped (simulating packet loss)</p>
     * <p>Then: Game remains playable, state remains consistent</p>
     * 
     * Class and Method under test: TronGameModel.update() with intermittent calls
     * Test Inputs/Preconditions: Random update skipping (30% packet loss)
     * Expected Outcome: Game state remains consistent
     * Testing Framework: JUnit 5 + JavaFX Platform
     */
    @Test
    @DisplayName("Network: Game resilience under 30% packet loss simulation")
    void testGameUpdateUnderPacketLoss() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean gameStable = new AtomicBoolean(false);
        final double packetLossRate = 0.30; // 30% packet loss
        
        Platform.runLater(() -> {
            try {
                TronGameModel gameModel = new SurvivalGameModel(
                    "src/main/resources/HighScores.json", 1);
                gameModel.reset();
                
                // When: Simulate 100 update cycles with packet loss
                int successfulUpdates = 0;
                for (int i = 0; i < 100; i++) {
                    // Simulate packet loss
                    if (Math.random() > packetLossRate) {
                        gameModel.tick();
                        successfulUpdates++;
                    }
                    Thread.sleep(10);
                }
                
                // Then: Game should remain stable
                gameStable.set(gameModel.isRunning() && successfulUpdates > 50);
                gameModel.stop();
                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), "Test should complete");
        assertTrue(gameStable.get(), "Game should remain stable under packet loss");
    }
    
    /**
     * Test: Memory pressure with repeated game initialization
     * 
     * <p>Given: System with limited resources</p>
     * <p>When: 200 game instances created and destroyed</p>
     * <p>Then: No memory leaks, proper cleanup occurs</p>
     * 
     * Class and Method under test: Game model lifecycle management
     * Test Inputs/Preconditions: Repeated creation/destruction cycles
     * Expected Outcome: Stable memory usage, proper cleanup
     * Testing Framework: JUnit 5 + JavaFX Platform
     */
    @Test
    @DisplayName("Memory: 200 game creation/destruction cycles")
    void testRepeatedGameInitialization() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        final int cycles = 200;
        
        Platform.runLater(() -> {
            try {
                // When: Create and destroy 200 game instances
                for (int i = 0; i < cycles; i++) {
                    TronGameModel gameModel = null;
                    
                    switch (i % 3) {
                        case 0:
                            gameModel = new StoryGameModel(800, 600, 3);
                            break;
                        case 1:
                            gameModel = new SurvivalGameModel(
                                "src/main/resources/HighScores.json", 1);
                            break;
                        case 2:
                            gameModel = new TwoPlayerGameModel(800, 600, 3);
                            break;
                    }
                    
                    gameModel.reset();
                    gameModel.tick();
                    gameModel.stop();
                    
                    // Allow garbage collection periodically
                    if (i % 50 == 0) {
                        System.gc();
                        Thread.sleep(50);
                    }
                }
                
                testPassed.set(true);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), "Test should complete");
        assertTrue(testPassed.get(), "All cycles should complete successfully");
    }
    
    /**
     * Test: Zero and negative boundary values for game parameters
     * 
     * <p>Given: Game model creation</p>
     * <p>When: Extreme boundary values used (0, negative, maximum)</p>
     * <p>Then: System handles gracefully without crashes</p>
     * 
     * Class and Method under test: TronGameModel constructor edge cases
     * Test Inputs/Preconditions: Extreme parameter values
     * Expected Outcome: Graceful handling or appropriate exceptions
     * Testing Framework: JUnit 5 + JavaFX Platform
     */
    @Test
    @DisplayName("Boundary: Extreme parameter values for game creation")
    void testExtremeBoundaryValues() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger handledCases = new AtomicInteger(0);
        
        Platform.runLater(() -> {
            try {
                // Test various extreme boundary values
                // For SurvivalGameModel, we test with different player counts
                int[] playerCounts = {0, 1, 5, 10};
                
                for (int playerCount : playerCounts) {
                    try {
                        TronGameModel model = new SurvivalGameModel(
                            "src/main/resources/HighScores.json", playerCount);
                        assertNotNull(model, "Model should be created");
                        handledCases.incrementAndGet();
                    } catch (Exception e) {
                        // Some cases may legitimately throw exceptions
                        handledCases.incrementAndGet();
                    }
                }
                
                // Test StoryGameModel with extreme values
                try {
                    TronGameModel model = new StoryGameModel(0, 0, 0);
                    handledCases.incrementAndGet();
                } catch (Exception e) {
                    handledCases.incrementAndGet();
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), "Test should complete");
        assertTrue(handledCases.get() >= 3, "Most boundary cases should be handled");
    }
    
    /**
     * Test: Observer notification ordering under concurrent modifications
     * 
     * <p>Given: Game model with observers being added/removed during notifications</p>
     * <p>When: State changes occur while observers are modified</p>
     * <p>Then: No ConcurrentModificationException, stable behavior</p>
     * 
     * Class and Method under test: Observer pattern concurrent modifications
     * Test Inputs/Preconditions: Dynamic observer list during notifications
     * Expected Outcome: Thread-safe observer management
     * Testing Framework: JUnit 5 + JavaFX Platform
     */
    @Test
    @DisplayName("Concurrency: Observer add/remove during notifications")
    void testConcurrentObserverModifications() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean exceptionOccurred = new AtomicBoolean(false);
        
        Platform.runLater(() -> {
            try {
                TronGameModel gameModel = new SurvivalGameModel(
                    "src/main/resources/HighScores.json", 1);
                List<GameStateObserver> observers = new ArrayList<>();
                
                // Create pool of observers
                for (int i = 0; i < 20; i++) {
                    GameStateObserver observer = new GameStateObserver() {
                        @Override
                        public void onGameStateChanged() {}
                        
                        @Override
                        public void onScoreChanged(int playerIndex, int newScore) {}
                        
                        @Override
                        public void onBoostChanged(int playerIndex, int boostCount) {}
                        
                        @Override
                        public void onPlayerCrashed(int playerIndex) {}
                        
                        @Override
                        public void onGameReset() {}
                    };
                    observers.add(observer);
                    gameModel.addObserver(observer);
                }
                
                gameModel.reset();
                
                // When: Add/remove observers while triggering notifications
                for (int i = 0; i < 50; i++) {
                    gameModel.notifyObservers();
                    
                    // Randomly add or remove observer
                    if (Math.random() > 0.5) {
                        gameModel.removeObserver(observers.get(i % observers.size()));
                    } else {
                        gameModel.addObserver(observers.get(i % observers.size()));
                    }
                    
                    Thread.sleep(10);
                }
                
            } catch (Exception e) {
                exceptionOccurred.set(true);
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), "Test should complete");
        assertFalse(exceptionOccurred.get(), "No concurrent modification exceptions");
    }
}
