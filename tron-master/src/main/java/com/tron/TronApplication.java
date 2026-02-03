package com.tron;

import com.tron.controller.fx.FXGameController;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * TronApplication - Main JavaFX Application Entry Point
 * 
 * This class serves as the entry point for the JavaFX version of the Tron game.
 * It initializes the JavaFX runtime and delegates game control to FXGameController.
 * 
 * Design Pattern: MVC Architecture + Singleton Pattern
 * - Application layer initializes the framework
 * - Delegates to FXGameController (Singleton) for game management
 * - Maintains separation between framework initialization and game logic
 * 
 * Responsibilities:
 * - Initialize JavaFX application context
 * - Create primary stage (main window)
 * - Start game controller
 * - Handle application lifecycle events
 * 
 * Usage:
 * Run with: mvn javafx:run
 * Or: java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar tron-game.jar
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class TronApplication extends Application {
    
    /**
     * JavaFX start method - called when application is ready
     * 
     * @param primaryStage The primary stage provided by JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        // Set window properties
        primaryStage.setTitle("TRON Game - JavaFX Edition");
        primaryStage.setResizable(false);
        
        // Get singleton controller and initialize with stage
        FXGameController controller = FXGameController.getInstance();
        controller.start(primaryStage);
        
        // Show the window
        primaryStage.show();
    }
    
    /**
     * Application entry point
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Handle application shutdown
     * Clean up resources before exit
     */
    @Override
    public void stop() {
        System.out.println("TRON Application shutting down...");
    }
}
