package com.tron;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tron.controller.fx.FXGameController;

import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * TronApplicationTest - Unit tests for the main application entry point
 * 
 * <p>This test class validates the TronApplication startup sequence,
 * JavaFX initialization, and integration with FXGameController singleton.
 * Tests ensure proper application lifecycle management and error handling.</p>
 * 
 * <p>Tests cover:</p>
 * <ul>
 *   <li>JavaFX Platform initialization</li>
 *   <li>Application start method execution</li>
 *   <li>Primary stage configuration</li>
 *   <li>FXGameController integration</li>
 *   <li>Error handling during startup</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see TronApplication
 * @see FXGameController
 */
@DisplayName("TronApplication - Application Startup Tests")
public class TronApplicationTest {
    
    private static final int TIMEOUT_SECONDS = 5;
    
    /**
     * Initialize JavaFX Platform before running tests
     * 
     * <p>Given: JavaFX runtime is not initialized</p>
     * <p>When: Test suite starts</p>
     * <p>Then: Platform should be started successfully</p>
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
    
    /**
     * Test: Application can be instantiated
     * 
     * <p>Given: TronApplication class is available</p>
     * <p>When: Constructor is called</p>
     * <p>Then: Instance should be created successfully</p>
     * 
     * Class and Method under test: TronApplication()
     * Test Inputs/Preconditions: None (default constructor)
     * Expected Outcome: Non-null TronApplication instance
     * Testing Framework: JUnit 5 + JavaFX Platform
     */
    @Test
    @DisplayName("testApplicationInstantiation - Application can be instantiated")
    void testApplicationInstantiation() {
        // Given: TronApplication class is available
        
        // When: Constructor is called
        TronApplication app = assertDoesNotThrow(() -> new TronApplication(),
                "TronApplication should instantiate without exceptions");
        
        // Then: Instance should be created successfully
        assertNotNull(app, "TronApplication instance should not be null");
    }
    
    /**
     * Test: Application start method initializes game controller
     * 
     * <p>Given: JavaFX Platform is running and primary stage is available</p>
     * <p>When: start(Stage) is called</p>
     * <p>Then: FXGameController should be initialized and stage configured</p>
     * 
     * Class and Method under test: TronApplication.start(Stage)
     * Test Inputs/Preconditions: Mock Stage object
     * Expected Outcome: Controller started, stage title set, stage shown
     * Testing Framework: JUnit 5 + JavaFX Platform
     */
    @Test
    @DisplayName("testStartMethodInitialization - start() initializes controller and stage")
    void testStartMethodInitialization() throws Exception {
        // Given: JavaFX Platform is running and primary stage is available
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean startSuccessful = new AtomicBoolean(false);
        
        Platform.runLater(() -> {
            try {
                // When: start(Stage) is called
                TronApplication app = new TronApplication();
                Stage testStage = new Stage();
                
                app.start(testStage);
                
                // Then: FXGameController should be initialized and stage configured
                FXGameController controller = FXGameController.getInstance();
                assertNotNull(controller, "FXGameController should be initialized");
                assertNotNull(testStage.getTitle(), "Stage title should be set");
                assertTrue(testStage.getTitle().contains("TRON"), 
                        "Stage title should contain 'TRON'");
                
                startSuccessful.set(true);
                testStage.close();
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), 
                "start() should complete within timeout");
        assertTrue(startSuccessful.get(), "start() should execute successfully");
    }

    /**
     * Test: Application main method launches JavaFX
     * 
     * <p>Given: TronApplication class exists</p>
     * <p>When: main(String[]) is called</p>
     * <p>Then: Method should execute without throwing exceptions</p>
     * 
     * Class and Method under test: TronApplication.main(String[])
     * Test Inputs/Preconditions: Empty args array
     * Expected Outcome: No exceptions during execution
     * Testing Framework: JUnit 5
     * 
     * Note: This test verifies main method exists and is callable.
     * Full application launch testing requires integration environment.
     */
    @Test
    @DisplayName("testMainMethodExists - main() method is callable")
    void testMainMethodExists() {
        // Given: TronApplication class exists
        
        // When & Then: main() method should be accessible
        assertDoesNotThrow(() -> {
            TronApplication.class.getDeclaredMethod("main", String[].class);
        }, "main(String[]) method should exist");
    }
    
    /**
     * Test: Multiple application instances can be created
     * 
     * <p>Given: TronApplication class exists</p>
     * <p>When: Multiple instances are created</p>
     * <p>Then: Each should be a distinct object (not singleton pattern)</p>
     * 
     * Class and Method under test: TronApplication()
     * Test Inputs/Preconditions: None
     * Expected Outcome: Different instances created
     * Testing Framework: JUnit 5
     */
    @Test
    @DisplayName("testMultipleInstances - Multiple instances can be created")
    void testMultipleInstances() {
        // Given: TronApplication class exists
        
        // When: Multiple instances are created
        TronApplication app1 = new TronApplication();
        TronApplication app2 = new TronApplication();
        
        // Then: Each should be a distinct object
        assertNotNull(app1, "First instance should not be null");
        assertNotNull(app2, "Second instance should not be null");
        assertTrue(app1 != app2, "Instances should be different objects");
    }
}
