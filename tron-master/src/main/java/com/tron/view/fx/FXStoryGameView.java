package com.tron.view.fx;

import java.io.IOException;

import com.tron.audio.AudioManager;
import com.tron.audio.AudioManager.SoundEffect;
import com.tron.config.BackgroundColorChangeListener;
import com.tron.config.BackgroundColorSettings;
import com.tron.controller.fx.FXGameController;
import com.tron.controller.fx.FXGameInputController;
import com.tron.model.game.StoryGameModel;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * FXStoryGameView - JavaFX Story Game Mode View
 * 
 * Complete implementation with AI opponents, progressive levels,
 * and game over screen with restart/menu options. Now uses FXML.
 * 
 * Implements BackgroundColorChangeListener to dynamically update background.
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 3.0
 */
public class FXStoryGameView implements BackgroundColorChangeListener {
    
    @FXML
    private BorderPane root;
    
    @FXML
    private StackPane gameContainer;
    
    @FXML
    private VBox gameOverPanel;
    
    @FXML
    private VBox levelCompletePanel;
    
    @FXML
    private Label scoreLabel;
    
    @FXML
    private Label levelLabel;
    
    @FXML
    private Label boostLabel;
    
    @FXML
    private Label finalScoreLabel;
    
    @FXML
    private Label levelCompleteLabel;
    
    @FXML
    private Button restartButton;
    
    @FXML
    private Button menuButton;
    
    private FXTronGameView gameCanvas;
    private StoryGameModel model;
    private FXGameInputController inputController;
    private FXGameController mainController;
    private AnimationTimer gameTimer;
    private boolean gameRunning = false;
    private BackgroundColorSettings colorSettings;
    private AudioManager audioManager;
    
    public FXStoryGameView(FXGameController controller) {
        this.mainController = controller;
        this.colorSettings = BackgroundColorSettings.getInstance();
        this.audioManager = AudioManager.getInstance();
        initializeModel();
        loadFXML();
        initializeUI();
        initializeGameLoop();
        
        // Register as listener and apply current color
        colorSettings.addListener(this);
        applyBackgroundColor(colorSettings.getCurrentColor());
    }
    
    private void initializeModel() {
        model = new StoryGameModel(500, 500, 3);
    }
    
    /**
     * Load the FXML file
     */
    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/StoryGame.fxml"));
        loader.setController(this);
        try {
            root = loader.load();
        } catch (IOException e) {
            System.err.println("Failed to load StoryGame.fxml: " + e.getMessage());
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
        gameContainer.getChildren().add(0, gameCanvas); // Add as first child
        
        // Set button actions
        restartButton.setOnAction(e -> reset());
        menuButton.setOnAction(e -> returnToGameMenu());
        // Level complete panel auto-advances after 2 seconds
        
        // Bind final score label to score label
        if (finalScoreLabel != null) {
            finalScoreLabel.textProperty().bind(scoreLabel.textProperty());
        }
        
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
                // Continue timer but skip updates when paused
                if (model.isPaused()) {
                    return;
                }
                
                if (now - lastUpdate >= 20_000_000) { // 20ms = 50 FPS
                    model.tick();
                    updateScore();
                    checkLevelComplete();
                    lastUpdate = now;
                }
            }
        };
    }
    
    private void updateScore() {
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + model.getCurrentScore());
        }
        if (levelLabel != null) {
            levelLabel.setText("Level: " + model.getCurrentLevel());
        }
        
        if (model.getPlayer() != null && boostLabel != null) {
            boostLabel.setText("Boost: " + model.getPlayer().getBoostsLeft());
        }
    }
    
    private void checkLevelComplete() {
        if (!gameRunning) return;
        
        if (model.getPlayer() != null) {
            boolean playerAlive = model.getPlayer().getAlive();
            
            if (!playerAlive) {
                // Player died - game over
                gameTimer.stop();
                gameRunning = false;
                
                // Stop BGM and play lose sound
                if (audioManager != null) {
                    audioManager.stopBGM();
                    audioManager.playSoundEffect(SoundEffect.LOSE);
                }
                
                gameOverPanel.setVisible(true);
            } else if (model.isLevelComplete()) {
                // Level complete!
                gameTimer.stop();
                gameRunning = false;
                model.completeLevel();
                updateScore();
                
                if (model.isGameComplete()) {
                    // Won the game!
                    if (audioManager != null) {
                        audioManager.stopBGM();
                        audioManager.playSoundEffect(SoundEffect.WIN);
                    }
                    showVictory();
                } else {
                    // Next level
                    if (audioManager != null) {
                        audioManager.playSoundEffect(SoundEffect.WIN);
                    }
                    showLevelComplete();
                }
            }
        }
    }
    
    private void showLevelComplete() {
        if (levelCompletePanel != null) {
            levelCompletePanel.setVisible(true);
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> startNextLevel());
            pause.play();
        }
    }
    
    private void showVictory() {
        if (levelCompleteLabel != null) {
            levelCompleteLabel.setText("You Win!");
            levelCompleteLabel.setTextFill(Color.GOLD);
        }
        if (gameOverPanel != null) {
            gameOverPanel.setVisible(true);
        }
    }
    
    private void startNextLevel() {
        levelCompletePanel.setVisible(false);
        model.startNextLevel();
        updateScore();
        gameRunning = true;
        gameTimer.start();
        requestFocus();
    }
    
    public void reset() {
        gameOverPanel.setVisible(false);
        levelCompletePanel.setVisible(false);
        
        model.resetToFirstLevel();
        
        updateScore();
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
    
    /**
     * Implementation of BackgroundColorChangeListener
     */
    @Override
    public void onBackgroundColorChanged(String newColor) {
        applyBackgroundColor(newColor);
    }
    
    /**
     * Apply background color to game canvas only (not external container)
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
    
    public void requestFocus() {
        // Setup input controller with stage and back-to-menu callback
        if (root.getScene() != null && root.getScene().getWindow() != null) {
            inputController.setOwnerStage((javafx.stage.Stage) root.getScene().getWindow());
            inputController.setBackToMenuCallback(this::returnToGameMenu);
        }
        
        root.setOnKeyPressed(inputController.getKeyPressedHandler());
        gameCanvas.requestFocus();
        
        // Start game if not already running
        if (!gameRunning && model.isRunning()) {
            gameRunning = true;
            updateScore(); // Update UI to show correct level
            gameTimer.start();
            
            // Start BGM
            if (audioManager != null) {
                audioManager.startBGM();
            }
        }
    }
    
    public BorderPane getRoot() {
        return root;
    }
}
