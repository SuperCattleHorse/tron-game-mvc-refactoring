package com.tron.view.fx;

import java.io.IOException;

import com.tron.audio.AudioManager;
import com.tron.audio.AudioManager.SoundEffect;
import com.tron.config.BackgroundColorChangeListener;
import com.tron.config.BackgroundColorSettings;
import com.tron.controller.fx.FXGameController;
import com.tron.controller.fx.FXGameInputController;
import com.tron.model.game.TwoPlayerGameModel;
import com.tron.model.game.factory.TwoPlayerGameModelFactory;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * FXTwoPlayerGameView - JavaFX Two Player Game Mode View
 * 
 * Complete implementation with two-player support, score tracking,
 * and game over screen with restart/menu options. Now uses FXML.
 * 
 * Implements BackgroundColorChangeListener to dynamically update background.
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 3.0
 */
public class FXTwoPlayerGameView implements BackgroundColorChangeListener {
    
    @FXML
    private BorderPane root;
    
    @FXML
    private StackPane gameContainer;
    
    @FXML
    private VBox gameOverPanel;
    
    @FXML
    private Label scoreLabel1;
    
    @FXML
    private Label scoreLabel2;
    
    @FXML
    private Label gameOverLabel;
    
    @FXML
    private ImageView resultImageView;
    
    @FXML
    private Button restartButton;
    
    @FXML
    private Button menuButton;
    
    private FXTronGameView gameCanvas;
    private TwoPlayerGameModel model;
    private FXGameInputController inputController;
    private FXGameController mainController;
    private AnimationTimer gameTimer;
    private boolean gameRunning = false;
    private BackgroundColorSettings colorSettings;
    private AudioManager audioManager;
    
    // Win statistics for both players
    private int player1Wins = 0;
    private int player2Wins = 0;
    
    public FXTwoPlayerGameView(FXGameController controller) {
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
        TwoPlayerGameModelFactory factory = new TwoPlayerGameModelFactory();
        model = (TwoPlayerGameModel) factory.initializeGame();
    }
    
    /**
     * Load the FXML file
     */
    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TwoPlayerGame.fxml"));
        loader.setController(this);
        try {
            root = loader.load();
        } catch (IOException e) {
            System.err.println("Failed to load TwoPlayerGame.fxml: " + e.getMessage());
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
        restartButton.setOnAction(e -> restartRound());
        menuButton.setOnAction(e -> returnToGameMenu());
        
        // Input controller for both players with pause support
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
                    checkGameOver();
                    lastUpdate = now;
                }
            }
        };
    }
    
    private void updateScore() {
        if (model.getPlayer() != null) {
            scoreLabel1.setText("Player 1: " + player1Wins + "   Boost: " + model.getPlayer().getBoostsLeft());
        }
        if (model.getPlayer2() != null) {
            scoreLabel2.setText("Player 2: " + player2Wins + "   Boost: " + model.getPlayer2().getBoostsLeft());
        }
    }
    
    private void checkGameOver() {
        if (!gameRunning) return;
        
        if (model.getPlayer() != null && model.getPlayer2() != null) {
            boolean p1Alive = model.getPlayer().getAlive();
            boolean p2Alive = model.getPlayer2().getAlive();
            
            if (!p1Alive || !p2Alive) {
                gameTimer.stop();
                gameRunning = false;
                
                // Pause BGM
                if (audioManager != null) {
                    audioManager.pauseBGM();
                }
                
                // Determine result image based on winner
                String imageName = null;
                
                if (p1Alive) {
                    player1Wins++;
                    imageName = "p1_wins.png";
                    // Player 1 wins - play win sound
                    if (audioManager != null) {
                        audioManager.playSoundEffect(SoundEffect.WIN);
                    }
                } else if (p2Alive) {
                    player2Wins++;
                    imageName = "p2_wins.png";
                    // Player 2 wins - play win sound
                    if (audioManager != null) {
                        audioManager.playSoundEffect(SoundEffect.WIN);
                    }
                } else {
                    imageName = "tie.png";
                    // Tie - play win sound
                    if (audioManager != null) {
                        audioManager.playSoundEffect(SoundEffect.WIN);
                    }
                }
                
                // Load and display result image
                try {
                    Image resultImage = new Image(getClass().getResourceAsStream("/" + imageName));
                    if (resultImageView != null) {
                        resultImageView.setImage(resultImage);
                    }
                } catch (Exception e) {
                    System.err.println("Could not load result image: " + imageName);
                }
                
                // Hide text label since we're using images
                if (gameOverLabel != null) {
                    gameOverLabel.setVisible(false);
                }
                
                if (gameOverPanel != null) {
                    gameOverPanel.setVisible(true);
                }
                updateScore();
            }
        }
    }
    
    private void restartRound() {
        gameOverPanel.setVisible(false);
        model.reset();
        gameRunning = true;
        gameTimer.start();
        
        // Resume BGM when restarting
        if (audioManager != null) {
            audioManager.resumeBGM();
        }
        
        requestFocus(); // Reattach keyboard handlers
    }
    
    /**
     * Resets the game to initial state and starts a new match.
     * Hides the game over panel, resets the game model, restarts the game timer,
     * and resumes background music.
     */
    public void reset() {
        gameOverPanel.setVisible(false);
        model.reset();
        gameRunning = true;
        gameTimer.start();
        
        // Resume BGM when restarting
        if (audioManager != null) {
            audioManager.resumeBGM();
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
        
        // Reset win statistics when returning to game menu
        player1Wins = 0;
        player2Wins = 0;
        mainController.showPlayMenu();
    }
    
    /**
     * Requests keyboard focus for the game view.
     * Sets up the input controller with the stage and back-to-menu callback,
     * configures keyboard event handlers, and requests focus for the game canvas.
     */
    public void requestFocus() {
        // Setup input controller with stage and back-to-menu callback
        if (root.getScene() != null && root.getScene().getWindow() != null) {
            inputController.setOwnerStage((javafx.stage.Stage) root.getScene().getWindow());
            inputController.setBackToMenuCallback(this::returnToGameMenu);
        }
        
        // Use input controller for keyboard handling (supports both players and pause)
        root.setOnKeyPressed(inputController.getKeyPressedHandler());
        gameCanvas.requestFocus();
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
    
    /**
     * Gets the root BorderPane layout container for this view.
     * Used by the controller to add this view to the scene.
     * 
     * @return the root BorderPane containing all UI elements
     */
    public BorderPane getRoot() {
        return root;
    }
}
