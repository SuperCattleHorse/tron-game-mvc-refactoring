package com.tron.config;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * BackgroundColorSettingsTest - Unit tests for background color configuration
 * 
 * Tests singleton pattern, configuration persistence, observer notifications,
 * and thread safety of the BackgroundColorSettings class.
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class BackgroundColorSettingsTest {
    
    private static final String TEST_CONFIG_FILE = "config/background_color.properties";
    private BackgroundColorSettings settings;
    
    @BeforeEach
    void setUp() throws Exception {
        // Clean up any existing config file
        File configFile = new File(TEST_CONFIG_FILE);
        if (configFile.exists()) {
            configFile.delete();
        }
        
        // Reset singleton instance using reflection for clean test state
        Field instanceField = BackgroundColorSettings.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        
        // Get fresh instance
        settings = BackgroundColorSettings.getInstance();
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
     * Test singleton pattern - getInstance() returns same instance
     */
    @Test
    @DisplayName("Singleton pattern: getInstance() returns same instance")
    void testSingletonPattern() {
        BackgroundColorSettings instance1 = BackgroundColorSettings.getInstance();
        BackgroundColorSettings instance2 = BackgroundColorSettings.getInstance();
        
        assertSame(instance1, instance2, "getInstance() should return the same instance");
    }
    
    /**
     * Test default color is black
     */
    @Test
    @DisplayName("Default background color is black")
    void testDefaultColor() {
        assertEquals(BackgroundColorSettings.COLOR_BLACK, settings.getCurrentColor(),
            "Default color should be black");
    }
    
    /**
     * Test setting valid colors
     */
    @Test
    @DisplayName("Set valid colors successfully")
    void testSetValidColors() {
        settings.setBackgroundColor(BackgroundColorSettings.COLOR_NAVY);
        assertEquals(BackgroundColorSettings.COLOR_NAVY, settings.getCurrentColor());
        
        settings.setBackgroundColor(BackgroundColorSettings.COLOR_PURPLE);
        assertEquals(BackgroundColorSettings.COLOR_PURPLE, settings.getCurrentColor());
    }
    
    /**
     * Test setting invalid color throws exception
     */
    @Test
    @DisplayName("Setting invalid color throws IllegalArgumentException")
    void testSetInvalidColor() {
        assertThrows(IllegalArgumentException.class, () -> {
            settings.setBackgroundColor("#FF0000"); // Red - conflicts with player color
        }, "Should throw exception for invalid color");
    }
    
    /**
     * Test available colors list
     */
    @Test
    @DisplayName("Available colors list contains 8 colors")
    void testAvailableColorsList() {
        List<String> colors = settings.getAvailableColors();
        
        assertEquals(8, colors.size(), "Should have 8 available colors");
        assertTrue(colors.contains(BackgroundColorSettings.COLOR_BLACK));
        assertTrue(colors.contains(BackgroundColorSettings.COLOR_DARK_GRAY));
        assertTrue(colors.contains(BackgroundColorSettings.COLOR_NAVY));
        assertTrue(colors.contains(BackgroundColorSettings.COLOR_DARK_GREEN));
        assertTrue(colors.contains(BackgroundColorSettings.COLOR_MAROON));
        assertTrue(colors.contains(BackgroundColorSettings.COLOR_PURPLE));
        assertTrue(colors.contains(BackgroundColorSettings.COLOR_TEAL));
        assertTrue(colors.contains(BackgroundColorSettings.COLOR_OLIVE));
    }
    
    /**
     * Test observer notification on color change
     */
    @Test
    @DisplayName("Observers are notified when color changes")
    void testObserverNotification() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger callCount = new AtomicInteger(0);
        final String[] receivedColor = new String[1];
        
        BackgroundColorChangeListener listener = newColor -> {
            receivedColor[0] = newColor;
            callCount.incrementAndGet();
            latch.countDown();
        };
        
        settings.addListener(listener);
        settings.setBackgroundColor(BackgroundColorSettings.COLOR_TEAL);
        
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Listener should be called");
        assertEquals(BackgroundColorSettings.COLOR_TEAL, receivedColor[0]);
        assertEquals(1, callCount.get(), "Listener should be called exactly once");
    }
    
    /**
     * Test observer not notified when setting same color
     */
    @Test
    @DisplayName("Observers not notified when setting same color")
    void testNoNotificationForSameColor() throws InterruptedException {
        AtomicInteger callCount = new AtomicInteger(0);
        
        BackgroundColorChangeListener listener = newColor -> callCount.incrementAndGet();
        
        settings.setBackgroundColor(BackgroundColorSettings.COLOR_NAVY);
        settings.addListener(listener);
        settings.setBackgroundColor(BackgroundColorSettings.COLOR_NAVY); // Same color
        
        Thread.sleep(100); // Give time for potential notification
        
        assertEquals(0, callCount.get(), "Listener should not be called for same color");
    }
    
    /**
     * Test removing listener
     */
    @Test
    @DisplayName("Removed listeners are not notified")
    void testRemoveListener() throws InterruptedException {
        AtomicInteger callCount = new AtomicInteger(0);
        
        BackgroundColorChangeListener listener = newColor -> callCount.incrementAndGet();
        
        settings.addListener(listener);
        settings.setBackgroundColor(BackgroundColorSettings.COLOR_PURPLE);
        
        Thread.sleep(100);
        int countAfterFirstChange = callCount.get();
        
        settings.removeListener(listener);
        settings.setBackgroundColor(BackgroundColorSettings.COLOR_MAROON);
        
        Thread.sleep(100);
        
        assertEquals(countAfterFirstChange, callCount.get(), 
            "Listener should not be called after removal");
    }
    
    /**
     * Test color display names
     */
    @Test
    @DisplayName("Color display names are correct")
    void testColorDisplayNames() {
        assertEquals("Black", BackgroundColorSettings.getColorDisplayName(
            BackgroundColorSettings.COLOR_BLACK));
        assertEquals("Dark Gray", BackgroundColorSettings.getColorDisplayName(
            BackgroundColorSettings.COLOR_DARK_GRAY));
        assertEquals("Navy", BackgroundColorSettings.getColorDisplayName(
            BackgroundColorSettings.COLOR_NAVY));
        assertEquals("Purple", BackgroundColorSettings.getColorDisplayName(
            BackgroundColorSettings.COLOR_PURPLE));
        assertEquals("Unknown", BackgroundColorSettings.getColorDisplayName("#INVALID"));
    }
    
    /**
     * Test thread safety of singleton
     */
    @Test
    @DisplayName("Singleton is thread-safe")
    void testThreadSafety() throws InterruptedException {
        int threadCount = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        BackgroundColorSettings[] instances = new BackgroundColorSettings[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    instances[index] = BackgroundColorSettings.getInstance();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }
        
        startLatch.countDown(); // Start all threads
        assertTrue(endLatch.await(5, TimeUnit.SECONDS), "All threads should complete");
        
        // All instances should be the same
        BackgroundColorSettings first = instances[0];
        for (int i = 1; i < threadCount; i++) {
            assertSame(first, instances[i], "All instances should be identical");
        }
    }
}
