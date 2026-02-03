package com.tron.view.fx;

import java.io.IOException;
import java.util.function.Consumer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXPauseDialog - Pause dialog view for all game modes
 * 
 * Displays when player presses 'P' key during gameplay.
 * Provides options to continue game or return to main menu.
 * 
 * Design Pattern: MVC Architecture
 * - View layer component that displays pause state
 * - Uses callback pattern to communicate user choice to Controller
 * - Completely reusable across Story, Survival, and Two-Player modes
 * 
 * Responsibilities:
 * - Display pause dialog with centered modal window
 * - Handle Continue button click (resume game)
 * - Handle Back to Menu button click (exit to menu)
 * - Support keyboard shortcut (P key) to resume
 * 
 * Usage:
 * <pre>
 * FXPauseDialog pauseDialog = new FXPauseDialog(parentStage);
 * pauseDialog.showAndWait(shouldContinue -> {
 *     if (shouldContinue) {
 *         gameModel.resume();
 *     } else {
 *         returnToMainMenu();
 *     }
 * });
 * </pre>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class FXPauseDialog {
    
    private Stage dialogStage;
    private Button continueButton;
    private Button backToMenuButton;
    private Consumer<Boolean> resultCallback;
    
    /**
     * Create pause dialog with parent stage
     * 
     * @param owner Parent stage (game window) - dialog will be centered on this
     */
    public FXPauseDialog(Stage owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/PauseDialog.fxml")
            );
            VBox root = loader.load();
            
            // Get buttons from FXML
            continueButton = (Button) root.lookup("#continueButton");
            backToMenuButton = (Button) root.lookup("#backToMenuButton");
            
            // Setup dialog stage
            dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(owner);
            dialogStage.initStyle(StageStyle.TRANSPARENT); // No window decorations
            
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT); // Transparent background
            dialogStage.setScene(scene);
            
            // Setup button actions
            setupButtonHandlers();
            
            // Setup keyboard shortcut (P key to resume)
            setupKeyboardHandlers(scene);
            
            // Make dialog non-resizable
            dialogStage.setResizable(false);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to load PauseDialog.fxml: " + e.getMessage(), e);
        }
    }
    
    /**
     * Setup button click handlers
     */
    private void setupButtonHandlers() {
        continueButton.setOnAction(e -> {
            if (resultCallback != null) {
                resultCallback.accept(true); // true = continue game
            }
            dialogStage.close();
        });
        
        backToMenuButton.setOnAction(e -> {
            if (resultCallback != null) {
                resultCallback.accept(false); // false = back to menu
            }
            dialogStage.close();
        });
        
        // Add hover effects
        continueButton.setOnMouseEntered(e -> 
            continueButton.setStyle("-fx-font-size: 20px; -fx-background-color: #00dddd; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;")
        );
        continueButton.setOnMouseExited(e -> 
            continueButton.setStyle("-fx-font-size: 20px; -fx-background-color: #00ffff; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;")
        );
        
        backToMenuButton.setOnMouseEntered(e -> 
            backToMenuButton.setStyle("-fx-font-size: 20px; -fx-background-color: #dddddd; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;")
        );
        backToMenuButton.setOnMouseExited(e -> 
            backToMenuButton.setStyle("-fx-font-size: 20px; -fx-background-color: #ffffff; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;")
        );
    }
    
    /**
     * Setup keyboard handlers (P key to resume, ESC to close)
     */
    private void setupKeyboardHandlers(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.P || e.getCode() == KeyCode.ESCAPE) {
                // P or ESC key resumes game
                if (resultCallback != null) {
                    resultCallback.accept(true);
                }
                dialogStage.close();
                e.consume();
            }
        });
    }
    
    /**
     * Show pause dialog and wait for user response
     * 
     * This is a blocking call - execution will pause until user makes a choice.
     * The callback function will be invoked with the user's decision before returning.
     * 
     * @param callback Callback function: true = continue game, false = back to menu
     */
    public void showAndWait(Consumer<Boolean> callback) {
        this.resultCallback = callback;
        dialogStage.showAndWait();
    }
    
    /**
     * Close dialog programmatically
     * Useful for cleanup or forced closure
     */
    public void close() {
        dialogStage.close();
    }
}
