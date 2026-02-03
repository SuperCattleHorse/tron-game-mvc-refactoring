package com.tron.controller.fx;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tron.model.game.TronGameModel;
import com.tron.model.input.GameInput;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * <p>
 * Regression tests for {@link FXGameInputController}. These tests lock down the
 * translation layer that converts JavaFX {@link KeyEvent} instances into the
 * GUI-agnostic {@link GameInput} commands consumed by the model. Guarding this
 * adapter is crucial to the TDR plan because every UI refactor routes through
 * these two handlers.
 * </p>
 */
@DisplayName("FXGameInputController - JavaFX Input Adapter Tests")
class FXGameInputControllerTest {

    /**
     * <p>Initialises the JavaFX toolkit once for all tests so KeyEvents can be instantiated.</p>
     */
    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> { /* toolkit bootstrap for headless tests */ });
        } catch (IllegalStateException alreadyStarted) {
            // Running inside an environment that already booted JavaFX; safe to ignore.
        }
    }

    /**
     * <p>Ensures arrow keys are mapped to the player-one control scheme.</p>
     */
    @Test
    void testKeyPressedTranslatesArrowKeys() {
        TronGameModel model = mock(TronGameModel.class);
        FXGameInputController controller = new FXGameInputController(model);

        KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.LEFT, false, false, false, false);
        controller.getKeyPressedHandler().handle(event);

        verify(model, times(1)).handleInput(GameInput.MOVE_LEFT);
        assertTrue(event.isConsumed(), "Controller must consume handled events to avoid bubbling");
        verifyNoMoreInteractions(model);
    }

    /**
     * <p>Validates the secondary (player-two) bindings stay wired.</p>
     */
    @Test
    void testKeyPressedTranslatesSecondaryControls() {
        TronGameModel model = mock(TronGameModel.class);
        FXGameInputController controller = new FXGameInputController(model);

        KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.W, false, false, false, false);
        controller.getKeyPressedHandler().handle(event);

        verify(model, times(1)).handleInput(GameInput.P2_MOVE_UP);
        assertTrue(event.isConsumed(), "Player-two bindings should also consume events");
        verifyNoMoreInteractions(model);
    }

    /**
     * <p>Confirms the release handler stays inert but still consumes the event for future extensions.</p>
     */
    @Test
    void testKeyReleasedHandlerConsumesEventWithoutModelCalls() {
        TronGameModel model = mock(TronGameModel.class);
        FXGameInputController controller = new FXGameInputController(model);

        KeyEvent event = new KeyEvent(KeyEvent.KEY_RELEASED, "", "", KeyCode.SPACE, false, false, false, false);
        controller.getKeyReleasedHandler().handle(event);

        assertTrue(event.isConsumed(), "Release handler should consume events even if no action is taken");
        verifyNoMoreInteractions(model);
    }
}
