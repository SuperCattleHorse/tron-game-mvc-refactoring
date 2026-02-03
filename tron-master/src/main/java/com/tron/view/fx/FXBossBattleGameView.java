package com.tron.view.fx;

import java.io.IOException;

import com.tron.audio.AudioManager;
import com.tron.audio.AudioManager.SoundEffect;
import com.tron.config.BackgroundColorChangeListener;
import com.tron.config.BackgroundColorSettings;
import com.tron.controller.fx.FXGameController;
import com.tron.controller.fx.FXGameInputController;
import com.tron.model.boss.BossBattleGameModel;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * FXBossBattleGameView - JavaFX Boss Battle Mode View
 * 
 * Responsibilities:
 * - Initialize Boss Battle game model and view
 * - Handle keyboard input for player control
 * - Manage game loop (50 FPS)
 * - Show victory/defeat dialogs
 * - Handle pause functionality
 * 
 * Implements BackgroundColorChangeListener to dynamically update background.
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class FXBossBattleGameView implements BackgroundColorChangeListener {
    
    @FXML
    private BorderPane root;
    
    @FXML
    private StackPane gameContainer;
    
    @FXML
    private VBox victoryPanel;
    
    @FXML
    private VBox gameOverPanel;
    
    @FXML
    private VBox pausePanel;
    
    @FXML
    private Button returnMenuButton;
    
    @FXML
    private Button restartButton;
    
    @FXML
    private Button gameMenuButton;
    
    private FXBossBattleView gameCanvas;
    private BossBattleGameModel model;
    private FXGameInputController inputController;
    private FXGameController mainController;
    private AnimationTimer gameTimer;
    private boolean gameRunning = false;
    private BackgroundColorSettings colorSettings;
    private AudioManager audioManager;
    private boolean victoryShown = false;
    private boolean gameOverShown = false;
    
    /**
     * Constructor
     * 
     * @param controller Main game controller
     */
    public FXBossBattleGameView(FXGameController controller) {
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
    
    /**
     * Initialize Boss Battle game model
     */
    private void initializeModel() {
        // Create Boss Battle model (900x600 map: 600px player area + 300px Boss area)
        model = new BossBattleGameModel(900, 600, 3);
    }
    
    /**
     * Load BossBattle.fxml
     */
    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BossBattle.fxml"));
        loader.setController(this);
        try {
            root = loader.load();
        } catch (IOException e) {
            System.err.println("Failed to load BossBattle.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize UI components and button actions
     */
    private void initializeUI() {
        if (gameContainer == null) {
            System.err.println("FXML components not properly injected!");
            return;
        }
        
        // Add game canvas to container
        gameCanvas = new FXBossBattleView(model);
        gameContainer.getChildren().add(0, gameCanvas); // Add as first child
        
        // Set button actions
        returnMenuButton.setOnAction(e -> returnToGameMenu());
        restartButton.setOnAction(e -> reset());
        gameMenuButton.setOnAction(e -> returnToGameMenu());
        
        // Input controller with stage for pause dialog
        inputController = new FXGameInputController(model);
        
        // Set owner stage for pause dialog after scene is created
        javafx.application.Platform.runLater(() -> {
            if (root.getScene() != null && root.getScene().getWindow() != null) {
                inputController.setOwnerStage((javafx.stage.Stage) root.getScene().getWindow());
            }
        });
        
        // Set keyboard event handlers on canvas
        gameCanvas.setOnKeyPressed(inputController.getKeyPressedHandler());
        gameCanvas.setOnKeyReleased(inputController.getKeyReleasedHandler());
        
        // Make canvas focusable
        gameCanvas.setFocusTraversable(true);
    }
    
    /**
     * Initialize game loop (50 FPS)
     */
    private void initializeGameLoop() {
        gameTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            
            @Override
            public void handle(long now) {
                // Skip updates when paused
                if (model.isPaused()) {
                    return;
                }
                
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                
                // Target 50 FPS = 20ms per frame
                long elapsed = now - lastUpdate;
                if (elapsed >= 20_000_000L) { // 20ms in nanoseconds
                    model.tick();
                    checkGameState();
                    lastUpdate = now;
                }
            }
        };
    }
    
    /**
     * Check game state for victory or defeat
     */
    private void checkGameState() {
        if (!model.isRunning()) {
            if (model.isVictory() && !victoryShown) {
                showVictoryScreen();
                victoryShown = true;
            } else if (!model.isVictory() && !gameOverShown) {
                showGameOverScreen();
                gameOverShown = true;
            }
        }
    }
    
    /**
     * Show victory screen
     */
    private void showVictoryScreen() {
        gameRunning = false;
        victoryPanel.setVisible(true);
        
        // Stop BGM and play victory sound
        if (audioManager != null) {
            audioManager.stopBGM();
            audioManager.playSoundEffect(SoundEffect.WIN);
        }
    }
    
    /**
     * Show game over screen
     */
    private void showGameOverScreen() {
        gameRunning = false;
        gameOverPanel.setVisible(true);
        
        // Stop BGM and play lose sound
        if (audioManager != null) {
            audioManager.stopBGM();
            audioManager.playSoundEffect(SoundEffect.LOSE);
        }
    }
    
    /**
     * Toggle pause state
     */
    private void togglePause() {
        if (!gameRunning) return;
        
        boolean wasPaused = model.isPaused();
        if (wasPaused) {
            model.setPaused(false);
        } else {
            model.setPaused(true);
        }
        pausePanel.setVisible(model.isPaused());
        
        if (audioManager != null) {
            audioManager.playSoundEffect(SoundEffect.CLICK);
        }
    }
    
    /**
     * Reset Boss Battle to initial state
     */
    public void reset() {
        // Hide all panels
        victoryPanel.setVisible(false);
        gameOverPanel.setVisible(false);
        pausePanel.setVisible(false);
        
        // Reset flags
        victoryShown = false;
        gameOverShown = false;
        
        // Reset model
        model.reset();
        model.start();
        gameRunning = true;
        
        // Restart BGM when restarting game
        if (audioManager != null) {
            audioManager.startBGM();
        }
        
        // Request focus for keyboard input
        gameCanvas.requestFocus();
        
        // Play click sound
        if (audioManager != null) {
            audioManager.playSoundEffect(SoundEffect.CLICK);
        }
    }
    
    /**
     * Return to game menu
     */
    private void returnToGameMenu() {
        stop();
        
        // Stop BGM when returning to menu
        if (audioManager != null) {
            audioManager.stopBGM();
        }
        
        mainController.showPlayMenu();
        
        if (audioManager != null) {
            audioManager.playSoundEffect(SoundEffect.CLICK);
        }
    }
    
    /**
     * Start Boss Battle
     */
    public void start() {
        reset();
        
        if (gameTimer != null) {
            gameTimer.start();
        } else {
            System.err.println("[ERROR] gameTimer is null!");
        }
        
        // Ensure keyboard focus with delay to allow UI to fully initialize
        javafx.application.Platform.runLater(() -> {
            if (gameCanvas != null) {
                gameCanvas.requestFocus();
            }
        });
    }
    
    /**
     * Stop Boss Battle
     */
    public void stop() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        gameRunning = false;
        model.stop();
    }
    
    /**
     * Get root BorderPane for scene creation
     * 
     * @return Root BorderPane
     */
    public BorderPane getRoot() {
        return root;
    }
    
    /**
     * Request keyboard focus
     */
    public void requestFocus() {
        if (gameCanvas != null) {
            gameCanvas.requestFocus();
        }
        
        // Set up keyboard input on both canvas and scene
        if (gameCanvas != null) {
            gameCanvas.setOnKeyPressed(inputController.getKeyPressedHandler());
            gameCanvas.setOnKeyReleased(inputController.getKeyReleasedHandler());
        }
        
        if (root.getScene() != null) {
            root.getScene().setOnKeyPressed(inputController.getKeyPressedHandler());
            root.getScene().setOnKeyReleased(inputController.getKeyReleasedHandler());
        }
    }
    
    /**
     * Cleanup on view destruction
     */
    public void cleanup() {
        stop();
    }
    
    /**
     * Implementation of BackgroundColorChangeListener
     */
    @Override
    public void onBackgroundColorChanged(String newColor) {
        applyBackgroundColor(newColor);
    }
    
    /**
     * Apply background color to game canvas
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
}
