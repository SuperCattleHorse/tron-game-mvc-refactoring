package com.tron.model.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * <p>
 * Integration-style unit tests that safeguard the GUI-independent mappings in
 * {@link GameInput}. The enum acts as the canonical contract between any
 * controller (Swing, JavaFX, or others) and the {@code TronGameModel}, so the
 * conversion helpers must remain stable across refactors.
 * </p>
 *
 * <p>
 * Each test verifies that specific platform key codes resolve to the correct
 * {@link GameInput} constant and that unmapped keys gracefully fall back to
 * {@link GameInput#NONE}. This mirrors the guarantees that the Test-Driven
 * Refactoring (TDR) plan outlined for the input layer.
 * </p>
 */
@DisplayName("GameInput Mapping Tests")
class GameInputTest {

    /**
     * <p>Ensures the legacy AWT key codes still point to the same commands.</p>
     */
    @ParameterizedTest(name = "AWT key {0} -> {1}")
    @CsvSource({
        "37, MOVE_LEFT",
        "38, MOVE_UP",
        "39, MOVE_RIGHT",
        "40, MOVE_DOWN",
        "32, JUMP",
        "66, BOOST"
    })
    void testFromAwtKeyCodeMapsLegacyValues(int keyCode, String expectedName) {
        GameInput expected = GameInput.valueOf(expectedName);
        assertSame(expected, GameInput.fromAWTKeyCode(keyCode),
            "The AWT mapping should remain stable for " + expectedName);
    }

    /**
     * <p>Validates that an unknown AWT key code collapses to {@link GameInput#NONE}.</p>
     */
    @Test
    void testFromAwtKeyCodeDefaultsToNone() {
        assertEquals(GameInput.NONE, GameInput.fromAWTKeyCode(999),
            "Unexpected AWT key codes must not break the enum contract");
    }

    /**
     * <p>Verifies JavaFX {@code KeyCode} names translate to primary and secondary
     * control schemes.</p>
     */
    @ParameterizedTest(name = "JavaFX key {0} -> {1}")
    @CsvSource({
        "LEFT, MOVE_LEFT",
        "RIGHT, MOVE_RIGHT",
        "UP, MOVE_UP",
        "DOWN, MOVE_DOWN",
        "SPACE, JUMP",
        "B, BOOST",
        "A, P2_MOVE_LEFT",
        "D, P2_MOVE_RIGHT",
        "W, P2_MOVE_UP",
        "S, P2_MOVE_DOWN",
        "Q, P2_JUMP",
        "DIGIT1, P2_BOOST",
        "NUMPAD1, P2_BOOST"
    })
    void testFromJavaFxKeyCodeCoversBothControlSchemes(String keyName, String expectedName) {
        GameInput expected = GameInput.valueOf(expectedName);
        assertSame(expected, GameInput.fromJavaFXKeyCode(keyName),
            "JavaFX key " + keyName + " should map to " + expectedName);
    }

    /**
     * <p>Ensures the JavaFX helper tolerates null/unknown input without throwing.</p>
     */
    @Test
    void testFromJavaFxKeyCodeHandlesNull() {
        assertEquals(GameInput.NONE, GameInput.fromJavaFXKeyCode(null),
            "Null JavaFX key names should fall back to NONE");
    }
}
