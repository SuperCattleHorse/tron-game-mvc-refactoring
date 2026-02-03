package com.tron.controller.fx;

import com.tron.audio.AudioManager;
import com.tron.audio.AudioManager.SoundEffect;
import com.tron.model.game.TronGameModel;
import com.tron.model.input.GameInput;
import com.tron.view.fx.FXPauseDialog;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * FXGameInputController - JavaFX Game Input Controller
 * 
 * This is the JavaFX version of GameInputController, translating JavaFX KeyEvents
 * to GUI-independent GameInput commands for the Model layer.
 * 
 * Design Pattern: MVC Architecture
 * - Controller translates framework-specific events to generic commands
 * - Model remains independent of JavaFX
 * - Parallel to Swing's GameInputController but for JavaFX
 * 
 * Responsibilities:
 * - Listen to JavaFX keyboard input
 * - Convert KeyEvent to GameInput enum
 * - Delegate to Model for processing
 * - Handle pause key (P) and show pause dialog
 * 
 * MVC Principles:
 * - No business logic (delegates to Model)
 * - Coordinates between View and Model
 * - Pure translation and coordination layer
 * 
 * Usage:
 * <pre>
 * TronGameModel model = new TronGameModel(...);
 * FXTronGameView view = new FXTronGameView(model);
 * FXGameInputController controller = new FXGameInputController(model, view, stage);
 * scene.setOnKeyPressed(controller.getKeyPressedHandler());
 * </pre>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 2.0 (Added Pause System)
 */
public class FXGameInputController {
    
    private final TronGameModel model;
    private final EventHandler<KeyEvent> keyPressedHandler;
    private final EventHandler<KeyEvent> keyReleasedHandler;
    private Stage ownerStage;
    private Runnable backToMenuCallback;
    private AudioManager audioManager;
    
    /**
     * Constructor with model reference
     * 
     * @param model The game model to control
     */
    public FXGameInputController(TronGameModel model) {
        this(model, null, null);
    }
    
    /**
     * Constructor with model, stage, and back-to-menu callback
     * 
     * @param model The game model to control
     * @param ownerStage The stage that owns the game (for centering pause dialog)
     * @param backToMenuCallback Callback to execute when returning to menu
     */
    public FXGameInputController(TronGameModel model, Stage ownerStage, Runnable backToMenuCallback) {
        this.model = model;
        this.ownerStage = ownerStage;
        this.backToMenuCallback = backToMenuCallback;
        this.audioManager = AudioManager.getInstance();
        this.keyPressedHandler = this::handleKeyPressed;
        this.keyReleasedHandler = this::handleKeyReleased;
    }
    
    /**
     * Set the owner stage for pause dialog
     * 
     * @param stage The stage that owns the game
     */
    public void setOwnerStage(Stage stage) {
        this.ownerStage = stage;
    }
    
    /**
     * Set the back to menu callback
     * 
     * @param callback Callback to execute when returning to menu
     */
    public void setBackToMenuCallback(Runnable callback) {
        this.backToMenuCallback = callback;
    }
    
    /**
     * Get the key pressed event handler
     * Attach this to Scene or Node's onKeyPressed property
     * 
     * @return EventHandler for key press events
     */
    public EventHandler<KeyEvent> getKeyPressedHandler() {
        return keyPressedHandler;
    }
    
    /**
     * Get the key released event handler
     * Attach this to Scene or Node's onKeyReleased property
     * 
     * @return EventHandler for key release events
     */
    public EventHandler<KeyEvent> getKeyReleasedHandler() {
        return keyReleasedHandler;
    }
    
    /**
     * Handle key press events
     * Converts JavaFX KeyEvent to GameInput and delegates to Model
     * 
     * MVC Principle: Controller performs translation only, no business logic
     * Model layer decides whether to process the input based on game state
     * 
     * Special handling for P key (pause) - shows pause dialog
     * 
     * @param event JavaFX KeyEvent from user input
     */
    private void handleKeyPressed(KeyEvent event) {
        // Handle pause key (P) - special case handled by Controller
        if (event.getCode() == KeyCode.P) {
            handlePauseRequest();
            event.consume();
            return;
        }
        
        // Convert JavaFX KeyCode to GUI-independent GameInput
        GameInput input = GameInput.fromJavaFXKeyCode(event.getCode().name());
        
        // Delegate to model - no business logic here
        model.handleInput(input);
        
        // Consume event to prevent further propagation
        event.consume();
    }
    
    /**
     * Handle pause key press (P)
     * Shows pause dialog and manages game pause/resume state
     * 
     * MVC Principle: Controller coordinates between View (dialog) and Model (pause state)
     * This is appropriate for Controller as it involves UI coordination
     */
    private void handlePauseRequest() {
        // Only allow pause if game is running and not already paused
        if (!model.isRunning() || model.isPaused()) {
            return;
        }
        
        // Pause the game
        model.pause();
        
        // Pause BGM and play pause sound
        if (audioManager != null) {
            audioManager.pauseBGM();
            audioManager.playSoundEffect(SoundEffect.PAUSE);
        }
        
        // Show pause dialog (requires stage reference)
        if (ownerStage != null) {
            FXPauseDialog pauseDialog = new FXPauseDialog(ownerStage);
            
            pauseDialog.showAndWait(shouldContinue -> {
                if (shouldContinue) {
                    // Continue game - resume from pause
                    model.resume();
                    
                    // Resume BGM and play unpause sound
                    if (audioManager != null) {
                        audioManager.resumeBGM();
                        audioManager.playSoundEffect(SoundEffect.UNPAUSE);
                    }
                } else {
                    // Back to menu - stop game and navigate
                    model.stop();
                    
                    // Stop BGM
                    if (audioManager != null) {
                        audioManager.stopBGM();
                    }
                    
                    if (backToMenuCallback != null) {
                        backToMenuCallback.run();
                    }
                }
            });
        } else {
            // Fallback: if no stage reference, just resume immediately
            // This shouldn't happen in normal operation
            model.resume();
            if (audioManager != null) {
                audioManager.resumeBGM();
            }
        }
    }
    
    /**
     * Handle key release events
     * Currently not used, but provided for future extension
     * 
     * @param event JavaFX KeyEvent from user input
     */
    private void handleKeyReleased(KeyEvent event) {
        // Future: Could be used for continuous movement control
        event.consume();
    }
}
