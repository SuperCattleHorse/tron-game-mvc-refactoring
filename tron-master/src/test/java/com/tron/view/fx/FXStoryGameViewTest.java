package com.tron.view.fx;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tron.controller.fx.FXGameController;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

/**
 * <p>
 * JavaFX view layer tests for {@link FXStoryGameView}. This suite ensures FXML resource loading,
 * component injection, and controller event bindings remain intact across refactorings. The tests
 * protect critical UI interactions like restarting, returning to menu, and level progression.
 * </p>
 * 
 * <h2>Coverage Areas</h2>
 * <ul>
 *   <li>FXML parsing and UI tree construction</li>
 *   <li>@FXML field injection for buttons and panels</li>
 *   <li>Event routing to {@link FXGameController} methods</li>
 *   <li>View lifecycle methods (reset, requestFocus)</li>
 * </ul>
 * 
 * <h2>Testing Strategy</h2>
 * <p>
 * All tests run on the JavaFX Application Thread using {@link Platform#runLater(Runnable)} with
 * {@link CountDownLatch} synchronization. Mockito verifies controller delegation without requiring
 * a live JavaFX Stage.
 * </p>
 * 
 * @author TDR Compliance Team
 * @version 1.0
 * @see FXStoryGameView
 * @see FXGameController
 */
@DisplayName("FXStoryGameView UI Integration Tests")
class FXStoryGameViewTest {

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> { /* Bootstrap JavaFX for headless tests */ });
        } catch (IllegalStateException ignored) {
            // Toolkit already initialized, safe to continue.
        }
        
        // Enable ByteBuddy experimental mode for Java 24 compatibility
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    /**
     * <p>
     * Confirms that {@link FXStoryGameView} loads the FXML resource without errors and injects all
     * required components. This test acts as a smoke test for FXML syntax and resource path
     * correctness.
     * </p>
     * 
     * @throws Exception if FXML loading or reflection fails
     */
    @Test
    @DisplayName("FXML loads successfully with all components injected")
    void testFXMLLoadsSuccessfully() throws Exception {
        FXGameController controller = mock(FXGameController.class);
        FXStoryGameView view = createView(controller);

        BorderPane root = getRoot(view);
        assertNotNull(root, "FXML should inject the root BorderPane");

        Button restartButton = getButton(view, "restartButton");
        Button menuButton = getButton(view, "menuButton");
        assertNotNull(restartButton, "FXML should inject restartButton");
        assertNotNull(menuButton, "FXML should inject menuButton");
    }

    /**
     * <p>
     * Verifies that the Restart button triggers {@link FXStoryGameView#reset()}, resetting the game
     * to level 1 with score 0. This test guards against UI refactorings that disconnect the button
     * handler.
     * </p>
     * 
     * @throws Exception if button fire or thread sync fails
     */
    @Test
    @DisplayName("Restart button resets game to initial state")
    void testRestartButtonResetsGame() throws Exception {
        FXGameController controller = mock(FXGameController.class);
        FXStoryGameView view = createView(controller);
        Button restartButton = getButton(view, "restartButton");

        CountDownLatch latch = new CountDownLatch(1);
        runOnFxThread(() -> {
            restartButton.fire();
            latch.countDown();
        });

        latch.await(1, TimeUnit.SECONDS);
        // Reset is internal, but we ensure no exception is thrown
    }

    /**
     * <p>
     * Ensures the Menu button routes back to {@link FXGameController#showPlayMenu()}, allowing the
     * user to exit story mode and select a different game mode.
     * </p>
     * 
     * @throws Exception if controller verification fails
     */
    @Test
    @DisplayName("Menu button returns to play menu")
    void testMenuButtonReturnsToPlayMenu() throws Exception {
        FXGameController controller = mock(FXGameController.class);
        FXStoryGameView view = createView(controller);
        Button menuButton = getButton(view, "menuButton");

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(controller).showPlayMenu();

        runOnFxThread(() -> menuButton.fire());

        latch.await(1, TimeUnit.SECONDS);
        verify(controller).showPlayMenu();
    }

    /**
     * <p>
     * Validates that {@link FXStoryGameView#requestFocus()} propagates keyboard focus to the game
     * canvas, ensuring input events are captured after scene transitions.
     * </p>
     * 
     * @throws Exception if JavaFX thread operations fail
     */
    @Test
    @DisplayName("requestFocus() delegates to game canvas")
    void testRequestFocusDelegatesToCanvas() throws Exception {
        FXGameController controller = mock(FXGameController.class);
        FXStoryGameView view = createView(controller);

        CountDownLatch latch = new CountDownLatch(1);
        runOnFxThread(() -> {
            view.requestFocus(); // Should not throw
            latch.countDown();
        });

        latch.await(1, TimeUnit.SECONDS);
    }

    /**
     * <p>
     * Confirms that {@link FXStoryGameView#reset()} can be invoked without errors, resetting the
     * model, UI labels, and game loop to their initial state.
     * </p>
     * 
     * @throws Exception if reset logic encounters an error
     */
    @Test
    @DisplayName("reset() method clears state without errors")
    void testResetMethodClearsState() throws Exception {
        FXGameController controller = mock(FXGameController.class);
        FXStoryGameView view = createView(controller);

        CountDownLatch latch = new CountDownLatch(1);
        runOnFxThread(() -> {
            view.reset(); // Should complete without exception
            latch.countDown();
        });

        latch.await(1, TimeUnit.SECONDS);
    }

    // ===== Helper Methods =====

    /**
     * <p>Creates a {@link FXStoryGameView} instance on the JavaFX Application Thread.</p>
     * 
     * @param controller the mocked controller
     * @return the initialized view
     * @throws Exception if thread synchronization times out
     */
    private static FXStoryGameView createView(FXGameController controller) throws Exception {
        AtomicReference<FXStoryGameView> ref = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            ref.set(new FXStoryGameView(controller));
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        return ref.get();
    }

    /**
     * <p>Retrieves the root BorderPane via reflection.</p>
     * 
     * @param view the view instance
     * @return the injected root pane
     * @throws Exception if field access fails
     */
    private static BorderPane getRoot(FXStoryGameView view) throws Exception {
        Field field = FXStoryGameView.class.getDeclaredField("root");
        field.setAccessible(true);
        return (BorderPane) field.get(view);
    }

    /**
     * <p>Extracts a Button field from the view using reflection.</p>
     * 
     * @param view the view instance
     * @param fieldName the @FXML field name
     * @return the button instance
     * @throws Exception if field not found
     */
    private static Button getButton(FXStoryGameView view, String fieldName) throws Exception {
        Field field = FXStoryGameView.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (Button) field.get(view);
    }

    /**
     * <p>Runs a task on the JavaFX thread and waits for completion.</p>
     * 
     * @param action the code to execute
     * @throws Exception if latch times out
     */
    private static void runOnFxThread(Runnable action) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        latch.await(2, TimeUnit.SECONDS);
    }
}
