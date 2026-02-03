package com.tron.view.fx.menu;

import java.io.IOException;

import com.tron.audio.AudioManager;
import com.tron.audio.AudioManager.SoundEffect;
import com.tron.controller.fx.FXGameController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * FXMainMenuView - JavaFX Main Menu View
 * 
 * This is the JavaFX version of MainMenuView, providing the main menu interface
 * with Play, Instructions, and Quit buttons. Now uses FXML for UI layout.
 * 
 * Design Pattern: MVC Architecture
 * - Pure View component, no business logic
 * - Delegates user actions to Controller
 * - Uses FXML for declarative UI definition
 * 
 * Layout Structure (matching Swing version):
 * - CENTER: Main menu image or instructions image
 * - BOTTOM: Button panel with Play, Instructions, Quit
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 2.0
 */
public class FXMainMenuView {
    
    @FXML
    private BorderPane root;
    
    @FXML
    private Button playButton;
    
    @FXML
    private Button instructionsButton;
    
    @FXML
    private Button optionsButton;
    
    @FXML
    private Button quitButton;
    
    @FXML
    private StackPane picturePane;
    
    @FXML
    private StackPane instructionsPane;
    
    @FXML
    private ImageView instructionsButtonImage;
    
    @FXML
    private ImageView instructionsImageView;
    
    @FXML
    private Button prevPageButton;
    
    @FXML
    private Button nextPageButton;
    
    @FXML
    private Label pageIndicator;
    
    private FXGameController controller;
    private boolean showingInstructions = false;
    private int currentInstructionPage = 1;
    private static final int TOTAL_INSTRUCTION_PAGES = 2;
    private AudioManager audioManager;
    
    /**
     * Constructor with controller reference
     * 
     * @param controller The game controller for handling user actions
     */
    public FXMainMenuView(FXGameController controller) {
        this.controller = controller;
        this.audioManager = AudioManager.getInstance();
        loadFXML();
        initializeUI();
    }
    
    /**
     * Load the FXML file
     */
    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainMenu.fxml"));
        loader.setController(this);
        try {
            root = loader.load();
        } catch (IOException e) {
            System.err.println("Failed to load MainMenu.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize the UI components after FXML loading
     */
    private void initializeUI() {
        // Set button actions
        playButton.setOnAction(e -> {
            if (audioManager != null) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
            controller.showPlayMenu();
        });
        instructionsButton.setOnAction(e -> {
            if (audioManager != null) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
            toggleInstructions();
        });
        optionsButton.setOnAction(e -> {
            if (audioManager != null) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
            controller.showOptionsMenu();
        });
        quitButton.setOnAction(e -> {
            if (audioManager != null) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
            controller.exitGame();
        });
        
        // Set pagination button actions
        prevPageButton.setOnAction(e -> {
            if (audioManager != null) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
            navigateToPreviousPage();
        });
        nextPageButton.setOnAction(e -> {
            if (audioManager != null) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
            navigateToNextPage();
        });
    }
    
    /**
     * Toggle between main menu and instructions
     */
    private void toggleInstructions() {
        showingInstructions = !showingInstructions;
        if (showingInstructions) {
            // Reset to first page when opening instructions
            currentInstructionPage = 1;
            updateInstructionPage();
            
            picturePane.setVisible(false);
            picturePane.setManaged(false);
            instructionsPane.setVisible(true);
            instructionsPane.setManaged(true);
            root.setCenter(instructionsPane);
            
            // Change button image to main_menu.png
            try {
                Image image = new Image(getClass().getResourceAsStream("/main_menu.png"));
                instructionsButtonImage.setImage(image);
            } catch (Exception e) {
                System.err.println("Failed to load main_menu.png");
            }
        } else {
            instructionsPane.setVisible(false);
            instructionsPane.setManaged(false);
            picturePane.setVisible(true);
            picturePane.setManaged(true);
            root.setCenter(picturePane);
            
            // Change button image back to instructions_before.png
            try {
                Image image = new Image(getClass().getResourceAsStream("/instructions_before.png"));
                instructionsButtonImage.setImage(image);
            } catch (Exception e) {
                System.err.println("Failed to load instructions_before.png");
            }
        }
    }
    
    /**
     * Navigate to previous instruction page
     */
    private void navigateToPreviousPage() {
        if (currentInstructionPage > 1) {
            currentInstructionPage--;
            updateInstructionPage();
        }
    }
    
    /**
     * Navigate to next instruction page
     */
    private void navigateToNextPage() {
        if (currentInstructionPage < TOTAL_INSTRUCTION_PAGES) {
            currentInstructionPage++;
            updateInstructionPage();
        }
    }
    
    /**
     * Update the instruction page display based on current page number
     */
    private void updateInstructionPage() {
        // Update page indicator (simplified format)
        pageIndicator.setText(currentInstructionPage + "/" + TOTAL_INSTRUCTION_PAGES);
        
        // Update prev/next button states
        prevPageButton.setDisable(currentInstructionPage == 1);
        nextPageButton.setDisable(currentInstructionPage == TOTAL_INSTRUCTION_PAGES);
        
        // Load appropriate page image
        String imagePath = "/instructions_page";
        if (currentInstructionPage > 1) {
            imagePath += currentInstructionPage; // instructions_page2.png
        }
        imagePath += ".png";
        
        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            instructionsImageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Failed to load " + imagePath + ": " + e.getMessage());
            // Fall back to page 1 if page 2 doesn't exist yet
            if (currentInstructionPage > 1) {
                try {
                    Image fallbackImage = new Image(getClass().getResourceAsStream("/instructions_page.png"));
                    instructionsImageView.setImage(fallbackImage);
                } catch (Exception ex) {
                    System.err.println("Failed to load fallback instructions_page.png");
                }
            }
        }
    }
    
    /**
     * Get the root pane for adding to scene
     * 
     * @return The root BorderPane
     */
    public BorderPane getRoot() {
        return root;
    }
}
