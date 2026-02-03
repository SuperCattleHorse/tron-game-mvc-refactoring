package com.tron.model.input;

/**
 * GameInput - GUI-independent input command enumeration
 * 
 * This enum represents all possible game input commands without any dependency
 * on specific GUI frameworks (AWT/Swing/JavaFX). This design allows the Model
 * layer to remain completely independent of the View/Controller implementation.
 * 
 * Design Pattern: MVC Architecture
 * - Model uses this enum for all input processing
 * - Controller translates framework-specific events (KeyEvent, KeyCode) to GameInput
 * - Promotes loose coupling and testability
 * 
 * Benefits:
 * - Model can be tested without any GUI framework
 * - Easy to switch between Swing, JavaFX, or any other UI framework
 * - Clear separation of concerns between layers
 * - Input commands are self-documenting
 * 
 * Usage:
 * <pre>
 * // In JavaFX Controller:
 * GameInput input = GameInput.fromJavaFXKeyCode(event.getCode());
 * model.handleInput(input);
 * 
 * // In Swing Controller:
 * GameInput input = GameInput.fromAWTKeyCode(event.getKeyCode());
 * model.handleInput(input);
 * </pre>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public enum GameInput {
    
    /** Move player left */
    MOVE_LEFT,
    
    /** Move player right */
    MOVE_RIGHT,
    
    /** Move player up */
    MOVE_UP,
    
    /** Move player down */
    MOVE_DOWN,
    
    /** Activate boost */
    BOOST,
    
    /** Jump command */
    JUMP,
    
    /** Player 2 move left (A key) */
    P2_MOVE_LEFT,
    
    /** Player 2 move right (D key) */
    P2_MOVE_RIGHT,
    
    /** Player 2 move up (W key) */
    P2_MOVE_UP,
    
    /** Player 2 move down (S key) */
    P2_MOVE_DOWN,
    
    /** Player 2 activate boost (1 key) */
    P2_BOOST,
    
    /** Player 2 jump command (Q key) */
    P2_JUMP,
    
    /** No action / unknown input */
    NONE;
    
    /**
     * Convert AWT KeyEvent keyCode to GameInput
     * Used for Swing-based controllers
     * 
     * @param keyCode The keyCode from java.awt.event.KeyEvent
     * @return Corresponding GameInput command
     */
    public static GameInput fromAWTKeyCode(int keyCode) {
        // KeyEvent constants without importing java.awt.event.KeyEvent
        // VK_LEFT = 37, VK_UP = 38, VK_RIGHT = 39, VK_DOWN = 40
        // VK_SPACE = 32, VK_B = 66
        switch (keyCode) {
            case 37: // VK_LEFT
                return MOVE_LEFT;
            case 39: // VK_RIGHT
                return MOVE_RIGHT;
            case 38: // VK_UP
                return MOVE_UP;
            case 40: // VK_DOWN
                return MOVE_DOWN;
            case 32: // VK_SPACE
                return JUMP;
            case 66: // VK_B
                return BOOST;
            default:
                return NONE;
        }
    }
    
    /**
     * Convert JavaFX KeyCode to GameInput
     * Used for JavaFX-based controllers
     * 
     * @param keyCodeName The KeyCode from javafx.scene.input.KeyCode
     * @return Corresponding GameInput command
     */
    public static GameInput fromJavaFXKeyCode(String keyCodeName) {
        if (keyCodeName == null) {
            return NONE;
        }
        
        switch (keyCodeName) {
            // Player 1 controls (Arrow keys, Space, B)
            case "LEFT":
                return MOVE_LEFT;
            case "RIGHT":
                return MOVE_RIGHT;
            case "UP":
                return MOVE_UP;
            case "DOWN":
                return MOVE_DOWN;
            case "SPACE":
                return JUMP;
            case "B":
                return BOOST;
            // Player 2 controls (WASD, Q, 1)
            case "A":
                return P2_MOVE_LEFT;
            case "D":
                return P2_MOVE_RIGHT;
            case "W":
                return P2_MOVE_UP;
            case "S":
                return P2_MOVE_DOWN;
            case "Q":
                return P2_JUMP;
            case "DIGIT1":
            case "NUMPAD1":
                return P2_BOOST;
            default:
                return NONE;
        }
    }
}
