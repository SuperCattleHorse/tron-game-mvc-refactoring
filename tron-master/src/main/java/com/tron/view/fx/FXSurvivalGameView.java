package com.tron.view.fx;

import java.io.IOException;

import com.tron.audio.AudioManager;
import com.tron.audio.AudioManager.SoundEffect;
import com.tron.config.BackgroundColorChangeListener;
import com.tron.config.BackgroundColorSettings;
import com.tron.controller.fx.FXGameController;
import com.tron.controller.fx.FXGameInputController;
import com.tron.model.game.SurvivalGameModel;
import com.tron.model.game.factory.SurvivalGameModelFactory;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * FXSurvivalGameView - JavaFX Survival Game Mode View
 * 
 * Complete game view for Survival mode with score display, game canvas,
 * and game over screen with restart/menu options. Now uses FXML.
 * 
 * Implements BackgroundColorChangeListener to dynamically update background
 * when user changes color settings.
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 3.0
 */
public class FXSurvivalGameView implements BackgroundColorChangeListener {
    
    @FXML
    private BorderPane root;
    
    @FXML
    private StackPane gameContainer;
    
    @FXML
    private VBox gameOverPanel;
    
    @FXML
    private Label scoreLabel;
    
    @FXML
    private Label gameOverScoreLabel;
    
    @FXML
    private Button restartButton;
    
    @FXML
    private Button menuButton;
    
    private FXTronGameView gameCanvas;
    private SurvivalGameModel model;
    private FXGameInputController inputController;
    private FXGameController mainController;
    private AnimationTimer gameTimer;
    private boolean gameRunning = false;
    private BackgroundColorSettings colorSettings;
    private AudioManager audioManager;
    
    public FXSurvivalGameView(FXGameController controller) {
        this.mainController = controller;
        this.colorSettings = BackgroundColorSettings.getInstance();
        this.audioManager = AudioManager.getInstance();
        initializeModel();
        loadFXML();
        initializeUI();
        initializeGameLoop();
        
        // Register as listener for background color changes
        colorSettings.addListener(this);
        
        // Apply current background color
        applyBackgroundColor(colorSettings.getCurrentColor());
    }
    
    private void initializeModel() {
        SurvivalGameModelFactory factory = new SurvivalGameModelFactory();
        model = (SurvivalGameModel) factory.initializeGame();
    }
    
    /**
     * Load the FXML file
     */
    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SurvivalGame.fxml"));
        loader.setController(this);
        try {
            root = loader.load();
        } catch (IOException e) {
            System.err.println("Failed to load SurvivalGame.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeUI() {
        if (gameContainer == null || restartButton == null || menuButton == null) {
            System.err.println("FXML components not properly injected!");
            return;
        }
        
        // Add game canvas to container
        gameCanvas = new FXTronGameView(model);
        gameContainer.getChildren().add(0, gameCanvas); // Add as first child (behind game over panel)
        
        // Set button actions
        restartButton.setOnAction(e -> reset());
        menuButton.setOnAction(e -> returnToGameMenu());
        
        // Input controller with pause support
        inputController = new FXGameInputController(model);
        // Stage will be set when scene is created
        // Callback will be set in requestFocus() method
    }
    
    private void initializeGameLoop() {
        gameTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            
            @Override
            public void handle(long now) {
                // Stop timer when game is paused
                if (model.isPaused()) {
                    return;
                }
                
                if (now - lastUpdate >= 20_000_000) { // 20ms = 50 FPS
                    model.tick();
                    updateScore();
                    checkGameOver();
                    lastUpdate = now;
                }
            }
        };
    }
    
    private void updateScore() {
        if (model.getPlayer() != null && scoreLabel != null) {
            int score = model.getCurrentScore();
            int boosts = model.getPlayer().getBoostsLeft();
            scoreLabel.setText("Score: " + score + "   Boost: " + boosts);
        }
    }
    
    private void checkGameOver() {
        if (!gameRunning) return;
        
        if (model.getPlayer() != null && !model.getPlayer().getAlive()) {
            gameTimer.stop();
            gameRunning = false;
            
            // Stop BGM and play lose sound
            if (audioManager != null) {
                audioManager.stopBGM();
                audioManager.playSoundEffect(SoundEffect.LOSE);
            }
            
            // Save score to high scores before showing game over screen
            model.stop();  // Set isRunning = false in model
            model.saveScore();  // Save current score to HighScores.json
            
            int finalScore = model.getCurrentScore();
            gameOverScoreLabel.setText("Final Score: " + finalScore);
            gameOverPanel.setVisible(true);
        }
    }
    
    public void reset() {
        gameOverPanel.setVisible(false);
        model.reset();
        gameRunning = true;
        gameTimer.start();
        
        // Restart BGM when restarting (use startBGM since stopBGM was called on game over)
        if (audioManager != null) {
            audioManager.startBGM();
        }
        
        requestFocus();
    }
    
    private void returnToGameMenu() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        gameRunning = false;
        
        // Stop BGM when returning to menu
        if (audioManager != null) {
            audioManager.stopBGM();
        }
        
        mainController.showPlayMenu();
    }
    
    public void requestFocus() {
        // Setup input controller with stage and back-to-menu callback
        if (root.getScene() != null && root.getScene().getWindow() != null) {
            inputController.setOwnerStage((javafx.stage.Stage) root.getScene().getWindow());
            inputController.setBackToMenuCallback(this::returnToGameMenu);
        }
        
        root.setOnKeyPressed(inputController.getKeyPressedHandler());
        gameCanvas.requestFocus();
    }
    
    /**
     * Implementation of BackgroundColorChangeListener
     * Updates the background color when user changes settings
     * 
     * @param newColor The new background color in CSS format
     */
    @Override
    public void onBackgroundColorChanged(String newColor) {
        applyBackgroundColor(newColor);
    }
    
    /**
     * Apply background color to game canvas only (not external container)
     * 
     * @param color The background color to apply
     */
    private void applyBackgroundColor(String color) {
        if (gameCanvas != null) {
            // Convert CSS color string to JavaFX Color
            javafx.scene.paint.Color fxColor = convertCssColorToFxColor(color);
            gameCanvas.setBackgroundColor(fxColor);
        }
    }
    
    /**
     * Convert CSS color string to JavaFX Color object
     * 
     * @param cssColor CSS color string (e.g., "black", "#2a2a2a")
     * @return JavaFX Color object
     */
    private javafx.scene.paint.Color convertCssColorToFxColor(String cssColor) {
        if (cssColor.startsWith("#")) {
            return javafx.scene.paint.Color.web(cssColor);
        } else {
            // Handle named colors
            switch (cssColor.toLowerCase()) {
                case "black": return javafx.scene.paint.Color.BLACK;
                case "darkgray": return javafx.scene.paint.Color.DARKGRAY;
                case "navy": return javafx.scene.paint.Color.web("#000080");
                case "darkgreen": return javafx.scene.paint.Color.web("#006400");
                case "maroon": return javafx.scene.paint.Color.web("#800000");
                case "purple": return javafx.scene.paint.Color.web("#800080");
                case "teal": return javafx.scene.paint.Color.web("#008080");
                case "olive": return javafx.scene.paint.Color.web("#808000");
                default: return javafx.scene.paint.Color.BLACK;
            }
        }
    }
    
    /**
     * Get the survival game model instance
     * Used for accessing high scores and game state
     * 
     * @return The SurvivalGameModel instance
     */
    public SurvivalGameModel getModel() {
        return model;
    }
    
    public BorderPane getRoot() {
        return root;
    }
}
