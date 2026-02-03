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
 * JavaFX wiring tests for {@link FXPlayMenuView}. The suite asserts that each button is wired to
 * the intended {@link FXGameController} action so future refactors cannot silently break the mode
 * selection menu.
 * </p>
 */
@DisplayName("FXPlayMenuView Button Wiring Tests")
class FXPlayMenuViewTest {

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> { /* bootstrap JavaFX for headless tests */ });
        } catch (IllegalStateException ignored) {
            // Toolkit already running in this JVM, safe to ignore.
        }
    }

    /**
     * <p>Ensures the Survival button triggers {@link FXGameController#startSurvivalMode()}.</p>
     */
    @Test
    void testSurvivalButtonStartsSurvivalMode() throws Exception {
        FXGameController controller = mock(FXGameController.class);
        FXPlayMenuView view = createView(controller);
        Button survivalButton = getButton(view, "survivalButton");
        assertNotNull(survivalButton, "FXML should inject the survival button");

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> { latch.countDown(); return null; })
            .when(controller)
            .startSurvivalMode();

        runOnFxThread(() -> survivalButton.fire());

        latch.await(1, TimeUnit.SECONDS);
        verify(controller).startSurvivalMode();
    }

    /**
     * <p>Verifies the Story button continues to route into {@link FXGameController#startStoryMode()}.</p>
     */
    @Test
    void testStoryButtonStartsStoryMode() throws Exception {
        FXGameController controller = mock(FXGameController.class);
        FXPlayMenuView view = createView(controller);
        Button storyButton = getButton(view, "storyButton");

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> { latch.countDown(); return null; })
            .when(controller)
            .startStoryMode();

        runOnFxThread(() -> storyButton.fire());

        latch.await(1, TimeUnit.SECONDS);
        verify(controller).startStoryMode();
    }

    /**
     * <p>Guards the Two Player button mapping.</p>
     */
    @Test
    void testTwoPlayerButtonStartsTwoPlayerMode() throws Exception {
        FXGameController controller = mock(FXGameController.class);
        FXPlayMenuView view = createView(controller);
        Button twoPlayerButton = getButton(view, "twoPlayerButton");

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> { latch.countDown(); return null; })
            .when(controller)
            .startTwoPlayerMode();

        runOnFxThread(() -> twoPlayerButton.fire());

        latch.await(1, TimeUnit.SECONDS);
        verify(controller).startTwoPlayerMode();
    }

    /**
     * <p>Confirms the back button returns to the main menu.</p>
     */
    @Test
    void testBackButtonReturnsToMainMenu() throws Exception {
        FXGameController controller = mock(FXGameController.class);
        FXPlayMenuView view = createView(controller);
        Button backButton = getButton(view, "backButton");

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> { latch.countDown(); return null; })
            .when(controller)
            .showMainMenu();

        runOnFxThread(() -> backButton.fire());

        latch.await(1, TimeUnit.SECONDS);
        verify(controller).showMainMenu();
    }

    private static FXPlayMenuView createView(FXGameController controller) throws Exception {
        AtomicReference<FXPlayMenuView> ref = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            ref.set(new FXPlayMenuView(controller));
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        return ref.get();
    }

    private static Button getButton(FXPlayMenuView view, String fieldName) throws Exception {
        Field field = FXPlayMenuView.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (Button) field.get(view);
    }

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
