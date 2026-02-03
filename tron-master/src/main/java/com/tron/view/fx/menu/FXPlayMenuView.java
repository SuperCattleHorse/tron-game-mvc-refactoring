package com.tron.view.fx.menu;

import java.io.IOException;
import java.util.List;

import com.tron.audio.AudioManager;
import com.tron.audio.AudioManager.SoundEffect;
import com.tron.controller.fx.FXGameController;
import com.tron.model.score.HighScoreEntry;
import com.tron.model.score.Score;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * FXPlayMenuView - JavaFX Play Menu (Game Mode Selection)
 * 
 * Provides interface for selecting game modes: Survival, Two Player, Story
 * Now uses FXML for UI layout.
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 3.0
 */
public class FXPlayMenuView {
    
    @FXML
    private BorderPane root;
    
    @FXML
    private VBox upperSection;
    
    @FXML
    private VBox gameModesPanel;
    
    @FXML
    private VBox highScoresPanel;
    
    @FXML
    private GridPane scoresGrid;
    
    @FXML
    private Button storyButton;
    
    @FXML
    private Button survivalButton;
    
    @FXML
    private Button twoPlayerButton;
    
    @FXML
    private Button bossBattleButton;
    
    @FXML
    private Button highScoresButton;
    
    @FXML
    private Button backButton;
    
    private FXGameController controller;
    private boolean scoresOn = false;
    private AudioManager audioManager;
    
    public FXPlayMenuView(FXGameController controller) {
        this.controller = controller;
        this.audioManager = AudioManager.getInstance();
        loadFXML();
        initializeUI();
    }
    
    /**
     * Load the FXML file
     */
    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PlayMenu.fxml"));
        loader.setController(this);
        try {
            root = loader.load();
        } catch (IOException e) {
            System.err.println("Failed to load PlayMenu.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeUI() {
        // Set button actions
        storyButton.setOnAction(e -> {
            if (audioManager != null) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
            controller.startStoryMode();
        });
        survivalButton.setOnAction(e -> {
            if (audioManager != null) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
            controller.startSurvivalMode();
        });
        twoPlayerButton.setOnAction(e -> {
            if (audioManager != null) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
            controller.startTwoPlayerMode();
        });
        bossBattleButton.setOnAction(e -> {
            if (audioManager != null) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
            controller.startBossBattleMode();
        });
        highScoresButton.setOnAction(e -> {
            if (audioManager != null) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
            showHighScores();
        });
        backButton.setOnAction(e -> {
            if (audioManager != null) {
                audioManager.playSoundEffect(SoundEffect.CLICK);
            }
            controller.showMainMenu();
        });
    }
    
    private void showHighScores() {
        if (scoresOn) {
            // Currently showing scores, switch back to game modes
            highScoresPanel.setVisible(false);
            highScoresPanel.setManaged(false);
            gameModesPanel.setVisible(true);
            gameModesPanel.setManaged(true);
        } else {
            // Currently showing game modes, switch to high scores
            populateHighScores();
            gameModesPanel.setVisible(false);
            gameModesPanel.setManaged(false);
            highScoresPanel.setVisible(true);
            highScoresPanel.setManaged(true);
        }
        scoresOn = !scoresOn;
    }
    
    /**
     * Populate the high scores grid with data
     * Display format: Score, Nickname, Gender, Date, Manifesto
     */
    private void populateHighScores() {
        // Get high scores directly from Score singleton
        List<HighScoreEntry> entries = null;
        try {
            Score highScoreManager = Score.getInstance("HighScores.json");
            entries = highScoreManager.getHighScoreEntries();
        } catch (Exception e) {
            System.out.println("Error loading high scores: " + e.getMessage());
        }
        
        // If no scores available, create empty list
        if (entries == null || entries.isEmpty()) {
            entries = new java.util.ArrayList<>();
            // Don't add default entries - just show empty
        }
        
        // Clear existing grid content
        scoresGrid.getChildren().clear();
        
        // Display all entries in a single column (showing top 10)
        for (int i = 0; i < Math.min(10, entries.size()); i++) {
            HighScoreEntry entry = entries.get(i);
            
            // Format: Rank.) Score, Nickname, Gender, Date, Manifesto
            String displayText = String.format("%2d.) %-6d | %-15s | %-6s | %s | %s", 
                i + 1, 
                entry.getScore(), 
                truncate(entry.getNickname(), 15),
                entry.getGender(),
                entry.getDate(),
                truncate(entry.getManifesto(), 30));
            
            Label label = new Label(displayText);
            label.setTextFill(Color.WHITE);
            label.setFont(Font.font("Monospaced", 11));
            label.setWrapText(false);
            label.setMaxWidth(800);
            
            // Add tooltip with full information for truncated text
            label.setTooltip(createTooltip(entry));
            
            // Add to grid (single column layout)
            scoresGrid.add(label, 0, i);
        }
    }
    
    /**
     * Truncate string to specified length, adding "..." if needed
     * 
     * @param text The text to truncate
     * @param maxLength Maximum length
     * @return Truncated text
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Create a tooltip with detailed information about a high score entry
     * 
     * @param entry The high score entry
     * @return Tooltip with detailed information
     */
    private javafx.scene.control.Tooltip createTooltip(HighScoreEntry entry) {
        String tooltipText = String.format(
            "Player: %s\nGender: %s\nScore: %d\nDate: %s\nManifesto: %s",
            entry.getNickname(),
            entry.getGender(),
            entry.getScore(),
            entry.getDate(),
            entry.getManifesto()
        );
        javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(tooltipText);
        tooltip.setStyle("-fx-font-size: 12px; -fx-background-color: #2a2a3e; -fx-text-fill: white;");
        return tooltip;
    }
    
    public BorderPane getRoot() {
        return root;
    }
}
