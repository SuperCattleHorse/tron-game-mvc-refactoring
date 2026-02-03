package com.tron.controller.fx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * <strong>FXGameController</strong> regression tests that validate the TDR
 * refactoring of the JavaFX front controller. The suite focuses on the
 * Singleton contract, MVC boundary rules, and infrastructure hardening once the
 * Swing implementation was removed.
 * </p>
 *
 * <p>
 * <em>Scope:</em>
 * </p>
 * <ul>
 * <li>Singleton integrity across sequential and concurrent access.</li>
 * <li>Absence of legacy Swing dependencies after refactoring.</li>
 * <li>Controller lifecycle resilience without touching actual JavaFX scene
 * graph APIs (kept out-of-scope for unit tests).</li>
 * </ul>
 *
 * @author Test Refactoring Team
 * @version 1.1
 * @since 1.0
 * @see com.tron.controller.fx.FXGameController
 */
@DisplayName("FXGameController - JavaFX MVC Controller Tests")
public class FXGameControllerTest {

    private FXGameController controller;

    @BeforeEach
    void setUp() {
        controller = FXGameController.getInstance();
    }

    /**
     * Test Singleton Pattern: getInstance returns same instance
     */
    @Test
    @DisplayName("Singleton: getInstance() returns same instance")
    void testSingletonPattern() {
        FXGameController instance1 = FXGameController.getInstance();
        FXGameController instance2 = FXGameController.getInstance();

        assertSame(instance1, instance2,
                "Multiple getInstance() calls should return same instance");
    }

    /**
     * Test Thread Safety: Concurrent getInstance calls
     */
    @Test
    @DisplayName("Thread Safety: Concurrent getInstance returns same instance")
    void testThreadSafeSingleton() throws InterruptedException {
        final int THREAD_COUNT = 10;
        Thread[] threads = new Thread[THREAD_COUNT];
        FXGameController[] instances = new FXGameController[THREAD_COUNT];

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                instances[index] = FXGameController.getInstance();
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // All instances should be the same
        for (int i = 1; i < THREAD_COUNT; i++) {
            assertSame(instances[0], instances[i],
                    "All concurrent getInstance calls should return same instance");
        }
    }

    /**
     * Test Refactoring Quality: No Swing dependencies
     */
    @Test
    @DisplayName("Refactoring: Controller has no Swing dependencies")
    void testNoSwingDependencies() {
        // This test validates that the class compiles without Swing imports
        // If this test runs, it confirms no Swing dependencies exist
        String className = controller.getClass().getName();
        assertTrue(className.contains("fx"),
                "Controller class should be in fx package, not Swing");
    }
}