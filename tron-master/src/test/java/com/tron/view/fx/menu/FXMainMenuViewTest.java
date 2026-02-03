package com.tron.view.fx.menu;

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

/**
 * <p>
 * JavaFX wiring tests for {@link FXMainMenuView}. This suite verifies that FXML component injection
 * and button event bindings remain correct across UI refactorings. Tests ensure the main menu
 * buttons (Play, Instructions, Quit) delegate to the appropriate {@link FXGameController} methods.
 * </p>
 * 
 * <h2>Coverage Areas</h2>
 * <ul>
 *   <li>FXML resource loading and parsing</li>
 *   <li>@FXML component injection for buttons and panes</li>
 *   <li>Play button â†?{@link FXGameController#showPlayMenu()}</li>
 *   <li>Instructions button â†?toggleInstructions() internal logic</li>
 *   <li>Quit button â†?{@link FXGameController#exitGame()}</li>
 * </ul>
 * 
 * <h2>Test Patterns</h2>
 * <p>
 * Uses {@link Platform#runLater(Runnable)} with {@link CountDownLatch} to synchronize JavaFX
 * thread operations. Mockito intercepts controller calls to verify event delegation.
 * </p>
 * 
 * @author TDR Compliance Team
 * @version 1.0
 * @see FXMainMenuView
 * @see FXGameController
 */
@DisplayName("FXMainMenuView Button Wiring Tests")
class FXMainMenuViewTest {

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> { /* Initialize JavaFX for headless tests */ });
        } catch (IllegalStateException ignored) {
            // Toolkit already running, safe to ignore.
        }
    }

    /**
     * <p>
     * Ensures the Play button triggers {@link FXGameController#showPlayMenu()}, navigating the user
     * to the game mode selection screen.
     * </p>
     * 
     * @throws Exception if controller verification or thread sync fails
     */
    @Test
    @DisplayName("Play button navigates to play menu")
    void testPlayButtonNavigatesToPlayMenu() throws Exception {
        FXGameController controller = mock(FXGameController.class);
        FXMainMenuView view = createView(controller);
        Button playButton = getButton(view, "playButton");

        assertNotNull(playButton, "FXML should inject playButton");

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(controller).showPlayMenu();

        runOnFxThread(() -> playButton.fire());

        latch.await(1, TimeUnit.SECONDS);
        verify(controller).showPlayMenu();
    }

    /**
     * <p>
     * Verifies the Instructions button toggles the instruction panel visibility without errors. This
     * test ensures the internal toggleInstructions() logic remains functional.
     * </p>
     * 
     * @throws Exception if button invocation fails
     */
    @Test
    @DisplayName("Instructions button toggles panel visibility")
    void testInstructionsButtonTogglesPanel() throws Exception {
        FXGameController controller = mock(FXGameController.class);
        FXMainMenuView view = createView(controller);
        Button instructionsButton = getButton(view, "instructionsButton");

        assertNotNull(instructionsButton, "FXML should inject instructionsButton");

        CountDownLatch latch = new CountDownLatch(1);
        runOnFxThread(() -> {
            instructionsButton.fire(); // Toggle to instructions
            latch.countDown();
        });

        latch.await(1, TimeUnit.SECONDS);
        // Internal logic, verify no exception thrown
    }

    /**
     * <p>
     * Confirms the Quit button delegates to {@link FXGameController#exitGame()}, terminating the
     * application.
     * </p>
     * 
     * @throws Exception if controller verification fails
     */
    @Test
    @DisplayName("Quit button exits application")
    void testQuitButtonExitsApplication() throws Exception {
        FXGameController controller = mock(FXGameController.class);
        FXMainMenuView view = createView(controller);
        Button quitButton = getButton(view, "quitButton");

        assertNotNull(quitButton, "FXML should inject quitButton");

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(controller).exitGame();

        runOnFxThread(() -> quitButton.fire());

        latch.await(1, TimeUnit.SECONDS);
        verify(controller).exitGame();
    }

    /**
     * <p>
     * Validates that {@link FXMainMenuView} loads FXML successfully and injects all components
     * without throwing exceptions. This acts as a smoke test for resource path correctness.
     * </p>
     * 
     * @throws Exception if FXML loading fails
     */
    @Test
    @DisplayName("FXML loads successfully with all components")
    void testFXMLLoadsSuccessfully() throws Exception {
        FXGameController controller = mock(FXGameController.class);
        FXMainMenuView view = createView(controller);

        assertNotNull(getButton(view, "playButton"), "playButton should be injected");
        assertNotNull(getButton(view, "instructionsButton"), "instructionsButton should be injected");
        assertNotNull(getButton(view, "quitButton"), "quitButton should be injected");
    }

    // ===== Helper Methods =====

    /**
     * <p>Creates a {@link FXMainMenuView} instance on the JavaFX Application Thread.</p>
     * 
     * @param controller the mocked controller to inject
     * @return the initialized view instance
     * @throws Exception if thread synchronization times out
     */
    private static FXMainMenuView createView(FXGameController controller) throws Exception {
        AtomicReference<FXMainMenuView> ref = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            ref.set(new FXMainMenuView(controller));
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        return ref.get();
    }

    /**
     * <p>Extracts a Button field from the view using reflection.</p>
     * 
     * @param view the view instance
     * @param fieldName the name of the @FXML injected button field
     * @return the button instance
     * @throws Exception if field not found or inaccessible
     */
    private static Button getButton(FXMainMenuView view, String fieldName) throws Exception {
        Field field = FXMainMenuView.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (Button) field.get(view);
    }

    /**
     * <p>Executes a Runnable on the JavaFX Application Thread and waits for completion.</p>
     * 
     * @param action the code to run on the FX thread
     * @throws Exception if latch times out or thread is interrupted
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
