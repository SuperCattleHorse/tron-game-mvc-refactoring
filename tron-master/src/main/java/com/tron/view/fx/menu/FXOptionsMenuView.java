package com.tron.view.fx.menu;

import java.io.IOException;

import com.tron.audio.AudioManager;
import com.tron.audio.AudioManager.SoundEffect;
import com.tron.config.AudioSettings;
import com.tron.config.BackgroundColorSettings;
import com.tron.config.GameSettings;
import com.tron.controller.fx.FXGameController;
import com.tron.model.util.MapType;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * FXOptionsMenuView - JavaFX Options Menu View
 * 
 * Provides the options/settings menu interface for configuring game preferences,
 * including background color customization.
 * 
 * Design Pattern: MVC Architecture
 * - Pure View component, no business logic
 * - Delegates user actions to Controller and Settings
 * - Uses FXML for declarative UI definition
 * 
 * SOLID Principles:
 * - Single Responsibility: Only handles Options UI and user interaction
 * - Dependency Inversion: Depends on BackgroundColorSettings abstraction
 * 
 * Layout Structure:
 * - TOP: "OPTIONS" title
 * - CENTER: Background color selector with 8 color buttons
 * - BOTTOM: Back button to return to main menu
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 2.0
 */
public class FXOptionsMenuView {
    
    @FXML
    private BorderPane root;
    
    @FXML
    private Button backButton;
    
    // Color selection buttons
    @FXML
    private Button colorBlackButton;
    
    @FXML
    private Button colorDarkGrayButton;
    
    @FXML
    private Button colorNavyButton;
    
    @FXML
    private Button colorDarkGreenButton;
    
    @FXML
    private Button colorMaroonButton;
    
    @FXML
    private Button colorPurpleButton;
    
    @FXML
    private Button colorTealButton;
    
    @FXML
    private Button colorOliveButton;
    
    @FXML
    private Label currentColorLabel;
    
    // Audio settings checkboxes
    @FXML
    private CheckBox bgmCheckBox;
    
    @FXML
    private CheckBox soundEffectsCheckBox;
    
    // Gameplay settings checkboxes
    @FXML
    private CheckBox hardAICheckBox;
    
    // Map selection
    @FXML
    private ChoiceBox<String> mapTypeChoiceBox;
    
    private FXGameController controller;
    private BackgroundColorSettings colorSettings;
    private AudioSettings audioSettings;
    private GameSettings gameSettings;
    private AudioManager audioManager;
    
    /**
     * Constructor with controller reference
     * 
     * @param controller The game controller for handling user actions
     */
    public FXOptionsMenuView(FXGameController controller) {
        this.controller = controller;
        this.colorSettings = BackgroundColorSettings.getInstance();
        this.audioSettings = AudioSettings.getInstance();
        this.gameSettings = GameSettings.getInstance();
        this.audioManager = AudioManager.getInstance();
        loadFXML();
        initializeUI();
    }
    
    /**
     * Load the FXML file
     */
    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/OptionsMenu.fxml"));
        loader.setController(this);
        try {
            root = loader.load();
            
            // Load custom CSS for ChoiceBox styling
            String cssPath = getClass().getResource("/OptionsMenu.css").toExternalForm();
            root.getStylesheets().add(cssPath);
        } catch (IOException e) {
            System.err.println("Failed to load OptionsMenu.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize the UI components after FXML loading
     */
    private void initializeUI() {
        // Set back button action to return to main menu
        backButton.setOnAction(e -> {
            if (audioManager != null) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
            controller.showMainMenu();
        });
        
        // Set up color selection buttons
        setupColorButton(colorBlackButton, BackgroundColorSettings.COLOR_BLACK);
        setupColorButton(colorDarkGrayButton, BackgroundColorSettings.COLOR_DARK_GRAY);
        setupColorButton(colorNavyButton, BackgroundColorSettings.COLOR_NAVY);
        setupColorButton(colorDarkGreenButton, BackgroundColorSettings.COLOR_DARK_GREEN);
        setupColorButton(colorMaroonButton, BackgroundColorSettings.COLOR_MAROON);
        setupColorButton(colorPurpleButton, BackgroundColorSettings.COLOR_PURPLE);
        setupColorButton(colorTealButton, BackgroundColorSettings.COLOR_TEAL);
        setupColorButton(colorOliveButton, BackgroundColorSettings.COLOR_OLIVE);
        
        // Update UI to reflect current color
        updateCurrentColorDisplay();
        
        // Set up audio controls
        initializeAudioControls();
        
        // Set up gameplay controls
        initializeGameplayControls();
    }
    
    /**
     * Set up a color selection button
     * 
     * @param button The button to configure
     * @param color The color this button represents
     */
    private void setupColorButton(Button button, String color) {
        button.setOnAction(e -> {
            if (audioManager != null) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
            colorSettings.setBackgroundColor(color);
            updateCurrentColorDisplay();
            highlightSelectedButton(button);
        });
        
        // Highlight if this is the current color
        if (color.equals(colorSettings.getCurrentColor())) {
            highlightSelectedButton(button);
        }
    }
    
    /**
     * Highlight the selected color button
     * 
     * @param selectedButton The button to highlight
     */
    private void highlightSelectedButton(Button selectedButton) {
        // Reset all buttons to normal border
        resetAllButtonBorders();
        
        // Highlight the selected button with cyan border
        selectedButton.setStyle(selectedButton.getStyle().replace("white", "cyan") + 
                                "; -fx-border-width: 4;");
    }
    
    /**
     * Reset all color button borders to default
     */
    private void resetAllButtonBorders() {
        resetButtonBorder(colorBlackButton, BackgroundColorSettings.COLOR_BLACK);
        resetButtonBorder(colorDarkGrayButton, BackgroundColorSettings.COLOR_DARK_GRAY);
        resetButtonBorder(colorNavyButton, BackgroundColorSettings.COLOR_NAVY);
        resetButtonBorder(colorDarkGreenButton, BackgroundColorSettings.COLOR_DARK_GREEN);
        resetButtonBorder(colorMaroonButton, BackgroundColorSettings.COLOR_MAROON);
        resetButtonBorder(colorPurpleButton, BackgroundColorSettings.COLOR_PURPLE);
        resetButtonBorder(colorTealButton, BackgroundColorSettings.COLOR_TEAL);
        resetButtonBorder(colorOliveButton, BackgroundColorSettings.COLOR_OLIVE);
    }
    
    /**
     * Reset a single button's border
     * 
     * @param button The button to reset
     * @param color The background color of the button
     */
    private void resetButtonBorder(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + 
                       "; -fx-border-color: white; -fx-border-width: 2; -fx-cursor: hand;");
    }
    
    /**
     * Update the current color display label
     */
    private void updateCurrentColorDisplay() {
        String colorName = BackgroundColorSettings.getColorDisplayName(
            colorSettings.getCurrentColor()
        );
        currentColorLabel.setText("Current: " + colorName);
    }
    
    /**
     * Initialize audio control checkboxes
     */
    private void initializeAudioControls() {
        // Initialize checkbox states from settings
        bgmCheckBox.setSelected(audioSettings.isBgmEnabled());
        soundEffectsCheckBox.setSelected(audioSettings.isSoundEffectsEnabled());
        
        // Set up BGM checkbox listener
        bgmCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            audioSettings.setBgmEnabled(newValue);
            audioManager.setBgmEnabledFromSettings(newValue);
            
            // Play click sound if sound effects are enabled
            if (audioSettings.isSoundEffectsEnabled()) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
        });
        
        // Set up sound effects checkbox listener
        soundEffectsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            audioSettings.setSoundEffectsEnabled(newValue);
            audioManager.setSoundEffectsEnabledFromSettings(newValue);
            
            // Only play click sound if enabling (not when disabling)
            if (newValue) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
        });
    }
    
    /**
     * Initialize gameplay control checkboxes
     */
    private void initializeGameplayControls() {
        // Initialize checkbox state from settings
        hardAICheckBox.setSelected(gameSettings.isHardAIEnabled());
        
        // Set up Hard AI checkbox listener
        hardAICheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            gameSettings.setHardAIEnabled(newValue);
            
            // Play click sound if sound effects are enabled
            if (audioSettings.isSoundEffectsEnabled()) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
        });
        
        // Initialize map type selection
        if (mapTypeChoiceBox != null) {
            mapTypeChoiceBox.setItems(FXCollections.observableArrayList(
                MapType.DEFAULT.getDisplayName(),
                MapType.MAP_1.getDisplayName(),
                MapType.MAP_2.getDisplayName(),
                MapType.MAP_3.getDisplayName()
            ));
            
            // Set current selection
            mapTypeChoiceBox.setValue(gameSettings.getSelectedMapType().getDisplayName());
            
            // Apply CSS to make dropdown text white
            mapTypeChoiceBox.setStyle(
                "-fx-background-color: #2a2a2a; " +
                "-fx-font-size: 16; " +
                "-fx-cursor: hand; " +
                "-fx-text-fill: white; " +
                "-fx-prompt-text-fill: white;"
            );
            
            // Force white text color for dropdown items
            mapTypeChoiceBox.getStyleClass().add("choice-box-white-text");
            
            // Add change listener
            mapTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        MapType selectedType = MapType.DEFAULT;
                        for (MapType type : MapType.values()) {
                            if (type.getDisplayName().equals(newValue)) {
                                selectedType = type;
                                break;
                            }
                        }
                        gameSettings.setSelectedMapType(selectedType);
                        
                        // Play click sound if sound effects are enabled
                        if (audioSettings.isSoundEffectsEnabled()) {
                            audioManager.playSoundEffect(SoundEffect.CLICK);
                        }
                    }
                }
            );
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
