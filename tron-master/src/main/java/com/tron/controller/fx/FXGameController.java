package com.tron.controller.fx;

import com.tron.audio.AudioManager;
import com.tron.audio.AudioManager.SoundEffect;
import com.tron.view.fx.FXBossBattleGameView;
import com.tron.view.fx.FXStoryGameView;
import com.tron.view.fx.FXSurvivalGameView;
import com.tron.view.fx.FXTwoPlayerGameView;
import com.tron.view.fx.menu.FXMainMenuView;
import com.tron.view.fx.menu.FXOptionsMenuView;
import com.tron.view.fx.menu.FXPlayMenuView;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * FXGameController - JavaFX Singleton Game Controller
 * 
 * This is the JavaFX version of GameController, managing the overall game flow
 * and navigation between different views. Maintains same functionality as the
 * Swing version but uses JavaFX components.
 * 
 * Design Pattern: Singleton Pattern (Creational)
 * - Ensures only one game controller instance exists
 * - Provides global access point via getInstance()
 * - Thread-safe lazy initialization
 * 
 * Design Pattern: MVC Architecture
 * - Controller coordinates between Model and View layers
 * - Handles scene transitions and user navigation
 * - Delegates game logic to Model layer
 * 
 * Responsibilities:
 * - Manage primary stage (main window)
 * - Handle scene switching between menus and game modes
 * - Coordinate view lifecycle
 * - Maintain game state at high level
 * 
 * Improvements over Swing version:
 * - Reduced View coupling - uses scene switching instead of direct component manipulation
 * - Cleaner separation of concerns
 * - Better encapsulation of view management
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class FXGameController {
    
    private static FXGameController instance;
    
    private Stage primaryStage;
    private Scene currentScene;
    private AudioManager audioManager;
    
    // View references
    private FXMainMenuView mainMenuView;
    private FXPlayMenuView playMenuView;
    private FXOptionsMenuView optionsMenuView;
    private FXSurvivalGameView survivalGameView;
    private FXTwoPlayerGameView twoPlayerGameView;
    private FXStoryGameView storyGameView;
    private FXBossBattleGameView bossBattleGameView;
    
    // Window dimensions (adjusted to fit all 4 buttons in main menu)
    private static final int WINDOW_WIDTH = 1040;
    private static final int WINDOW_HEIGHT = 650;
    
    /**
     * Private constructor enforces Singleton pattern
     */
    private FXGameController() {
    }
    
    /**
     * Get the singleton instance of FXGameController
     * Thread-safe lazy initialization
     * 
     * @return The unique FXGameController instance
     */
    public static synchronized FXGameController getInstance() {
        if (instance == null) {
            instance = new FXGameController();
        }
        return instance;
    }
    
    /**
     * Initialize and start the game controller with the primary stage
     * 
     * @param stage The primary stage from JavaFX Application
     */
    public void start(Stage stage) {
        this.primaryStage = stage;
        
        // Initialize audio system
        audioManager = AudioManager.getInstance();
        
        // Initialize views
        initializeViews();
        
        // Show main menu
        showMainMenu();
    }
    
    /**
     * Initialize all view components
     * Creates view instances but doesn't display them yet
     */
    private void initializeViews() {
        mainMenuView = new FXMainMenuView(this);
        playMenuView = new FXPlayMenuView(this);
        optionsMenuView = new FXOptionsMenuView(this);
        // Game views will be created on demand to save resources
    }
    
    /**
     * Show the main menu scene
     * Entry point of the application UI
     */
    public void showMainMenu() {
        // Stop BGM when returning to menu
        if (audioManager != null) {
            audioManager.stopBGM();
        }
        
        if (currentScene == null) {
            currentScene = new Scene(mainMenuView.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
            primaryStage.setScene(currentScene);
        } else {
            currentScene.setRoot(mainMenuView.getRoot());
        }
        primaryStage.setTitle("TRON Game - Main Menu");
    }
    
    /**
     * Show the play menu (game mode selection)
     */
    public void showPlayMenu() {
        if (currentScene == null) {
            currentScene = new Scene(playMenuView.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
            primaryStage.setScene(currentScene);
        } else {
            currentScene.setRoot(playMenuView.getRoot());
        }
        primaryStage.setTitle("TRON Game - Select Mode");
    }
    
    /**
     * Show the options menu (game settings)
     */
    public void showOptionsMenu() {
        if (currentScene == null) {
            currentScene = new Scene(optionsMenuView.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
            primaryStage.setScene(currentScene);
        } else {
            currentScene.setRoot(optionsMenuView.getRoot());
        }
        primaryStage.setTitle("TRON Game - Options");
    }
    
    /**
     * Start Survival mode game
     */
    public void startSurvivalMode() {
        // Play click sound
        if (audioManager != null) {
            audioManager.playSoundEffect(SoundEffect.CLICK);
        }
        
        if (survivalGameView == null) {
            survivalGameView = new FXSurvivalGameView(this);
        }
        survivalGameView.reset();
        if (currentScene == null) {
            currentScene = new Scene(survivalGameView.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
            primaryStage.setScene(currentScene);
        } else {
            currentScene.setRoot(survivalGameView.getRoot());
        }
        primaryStage.setTitle("TRON Game - Survival Mode");
        survivalGameView.requestFocus();
        
        // Start BGM
        if (audioManager != null) {
            audioManager.startBGM();
        }
    }
    
    /**
     * Start Two Player mode game
     */
    public void startTwoPlayerMode() {
        // Play click sound
        if (audioManager != null) {
            audioManager.playSoundEffect(SoundEffect.CLICK);
        }
        
        if (twoPlayerGameView == null) {
            twoPlayerGameView = new FXTwoPlayerGameView(this);
        }
        twoPlayerGameView.reset();
        if (currentScene == null) {
            currentScene = new Scene(twoPlayerGameView.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
            primaryStage.setScene(currentScene);
        } else {
            currentScene.setRoot(twoPlayerGameView.getRoot());
        }
        primaryStage.setTitle("TRON Game - Two Player Mode");
        twoPlayerGameView.requestFocus();
        
        // Start BGM
        if (audioManager != null) {
            audioManager.startBGM();
        }
    }
    
    /**
     * Start Story mode game
     */
    public void startStoryMode() {
        // Play click sound
        if (audioManager != null) {
            audioManager.playSoundEffect(SoundEffect.CLICK);
        }
        
        if (storyGameView == null) {
            storyGameView = new FXStoryGameView(this);
        }
        storyGameView.reset();
        if (currentScene == null) {
            currentScene = new Scene(storyGameView.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
            primaryStage.setScene(currentScene);
        } else {
            currentScene.setRoot(storyGameView.getRoot());
        }
        primaryStage.setTitle("TRON Game - Story Mode");
        storyGameView.requestFocus();
        
        // Start BGM
        if (audioManager != null) {
            audioManager.startBGM();
        }
    }
    
    /**
     * Start Boss Battle mode game
     */
    public void startBossBattleMode() {
        // Play click sound
        if (audioManager != null) {
            audioManager.playSoundEffect(SoundEffect.CLICK);
        }
        
        if (bossBattleGameView == null) {
            bossBattleGameView = new FXBossBattleGameView(this);
        }
        
        if (currentScene == null) {
            currentScene = new Scene(bossBattleGameView.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
            primaryStage.setScene(currentScene);
        } else {
            currentScene.setRoot(bossBattleGameView.getRoot());
        }
        primaryStage.setTitle("TRON Game - Boss Battle Mode");
        
        // Start the game (this will call reset and start the timer)
        bossBattleGameView.start();
        bossBattleGameView.requestFocus();
        
        // Start BGM
        if (audioManager != null) {
            audioManager.startBGM();
        }
    }
    
    /**
     * Get the Survival Game View instance
     * Used for accessing high scores and other survival mode specific data
     * 
     * @return The FXSurvivalGameView instance, or null if not yet created
     */
    public FXSurvivalGameView getSurvivalGameView() {
        return survivalGameView;
    }
    
    /**
     * Exit the application
     */
    public void exitGame() {
        primaryStage.close();
    }
}
