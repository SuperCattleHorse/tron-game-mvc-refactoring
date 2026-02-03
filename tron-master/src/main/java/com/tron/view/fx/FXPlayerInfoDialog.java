package com.tron.view.fx;

import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Pattern;

import com.tron.model.score.HighScoreEntry;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;

/**
 * FXPlayerInfoDialog - Beautiful dialog for collecting player information when achieving high score
 * 
 * Color Scheme: Cyan, White, Black, Indigo
 * 
 * Collects:
 * - Nickname (3-20 characters, alphanumeric and symbols only)
 * - Gender (Male, Female, Hidden)
 * - Manifesto (3-20 characters, alphanumeric and symbols only)
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 2.0 (Enhanced UI)
 */
public class FXPlayerInfoDialog extends Dialog<HighScoreEntry> {
    
    private TextField nicknameField;
    private ComboBox<String> genderComboBox;
    private TextField manifestoField;
    
    // Color scheme
    private static final String COLOR_BLACK = "#000000";
    private static final String COLOR_INDIGO = "#4B0082";
    private static final String COLOR_CYAN = "#00FFFF";
    private static final String COLOR_WHITE = "#FFFFFF";
    private static final String COLOR_DARK_INDIGO = "#2E0854";
    
    // Pattern for validating input (alphanumeric and common symbols)
    private static final Pattern VALID_PATTERN = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{}|;:',.<>?/` ~]+$");
    
    /**
     * Constructor
     * 
     * @param score The score achieved by the player
     */
    public FXPlayerInfoDialog(int score) {
        // Remove default styling
        initStyle(StageStyle.UNDECORATED);
        setTitle("High Score Achievement");
        
        // Create main container with gradient background
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30, 40, 30, 40));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle(String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, %s);" +
            "-fx-border-color: %s;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;",
            COLOR_INDIGO, COLOR_DARK_INDIGO, COLOR_CYAN
        ));
        
        // Add drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.web(COLOR_CYAN, 0.5));
        dropShadow.setRadius(20);
        dropShadow.setSpread(0.3);
        mainContainer.setEffect(dropShadow);
        
        // Title section
        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER);
        
        Text congratsText = new Text("ðŸ† CONGRATULATIONS! ðŸ†");
        congratsText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        congratsText.setFill(Color.web(COLOR_CYAN));
        congratsText.setEffect(new DropShadow(5, Color.web(COLOR_CYAN, 0.8)));
        
        Text scoreText = new Text("NEW HIGH SCORE: " + score);
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        scoreText.setFill(Color.web(COLOR_WHITE));
        
        Text instructionText = new Text("Enter your information to claim your spot!");
        instructionText.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        instructionText.setFill(Color.web(COLOR_CYAN, 0.9));
        
        titleBox.getChildren().addAll(congratsText, scoreText, instructionText);
        
        // Separator line
        Region separator = new Region();
        separator.setPrefHeight(2);
        separator.setStyle(String.format("-fx-background-color: %s;", COLOR_CYAN));
        separator.setMaxWidth(350);
        
        // Form section
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setPadding(new Insets(20, 0, 20, 0));
        
        // Nickname field
        Label nicknameLabel = createStyledLabel("NICKNAME:");
        nicknameField = createStyledTextField("Enter your nickname...");
        Label nicknameError = createErrorLabel();
        
        // Gender combo box
        Label genderLabel = createStyledLabel("GENDER:");
        genderComboBox = createStyledComboBox();
        genderComboBox.getItems().addAll("Male", "Female", "Hidden");
        genderComboBox.setValue("Hidden");
        
        // Manifesto field
        Label manifestoLabel = createStyledLabel("MANIFESTO:");
        manifestoField = createStyledTextField("Your victory message...");
        Label manifestoError = createErrorLabel();
        
        // Add components to grid
        formGrid.add(nicknameLabel, 0, 0);
        formGrid.add(nicknameField, 1, 0);
        formGrid.add(nicknameError, 1, 1);
        
        formGrid.add(genderLabel, 0, 2);
        formGrid.add(genderComboBox, 1, 2);
        
        formGrid.add(manifestoLabel, 0, 3);
        formGrid.add(manifestoField, 1, 3);
        formGrid.add(manifestoError, 1, 4);
        
        // Button section
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button submitButton = createStyledButton("âœ?SUBMIT", COLOR_CYAN, COLOR_BLACK);
        Button cancelButton = createStyledButton("âœ?CANCEL", COLOR_WHITE, COLOR_INDIGO);
        
        buttonBox.getChildren().addAll(submitButton, cancelButton);
        
        // Assemble main container
        mainContainer.getChildren().addAll(titleBox, separator, formGrid, buttonBox);
        
        // Set up dialog pane
        getDialogPane().setContent(mainContainer);
        getDialogPane().setStyle("-fx-background-color: transparent;");
        
        // Hide default buttons
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        
        // Add real-time validation
        nicknameField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateNickname(newVal, nicknameError);
            updateSubmitButton(submitButton);
        });
        
        manifestoField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateManifesto(newVal, manifestoError);
            updateSubmitButton(submitButton);
        });
        
        // Initial button state
        submitButton.setDisable(true);
        
        // Button actions
        submitButton.setOnAction(e -> {
            if (isFormValid()) {
                String nickname = nicknameField.getText().trim();
                String gender = genderComboBox.getValue();
                String manifesto = manifestoField.getText().trim();
                LocalDate today = LocalDate.now();
                
                setResult(new HighScoreEntry(score, nickname, gender, manifesto, today));
                close();
            }
        });
        
        cancelButton.setOnAction(e -> {
            setResult(null);
            close();
        });
        
        // Request focus on nickname field
        javafx.application.Platform.runLater(() -> nicknameField.requestFocus());
    }
    
    /**
     * Create a styled label
     */
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.web(COLOR_CYAN));
        label.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        return label;
    }
    
    /**
     * Create a styled text field
     */
    private TextField createStyledTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setPrefWidth(250);
        field.setStyle(String.format(
            "-fx-background-color: %s;" +
            "-fx-text-fill: %s;" +
            "-fx-prompt-text-fill: rgba(255, 255, 255, 0.5);" +
            "-fx-border-color: %s;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 8;" +
            "-fx-font-size: 13;",
            COLOR_BLACK, COLOR_WHITE, COLOR_CYAN
        ));
        
        // Add focus effect
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(field.getStyle() + 
                    String.format("-fx-border-color: %s; -fx-border-width: 3;", COLOR_CYAN));
            } else {
                field.setStyle(field.getStyle().replace("-fx-border-width: 3;", "-fx-border-width: 2;"));
            }
        });
        
        return field;
    }
    
    /**
     * Create a styled combo box
     */
    private ComboBox<String> createStyledComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPrefWidth(250);
        comboBox.setStyle(String.format(
            "-fx-background-color: %s;" +
            "-fx-text-fill: %s;" +
            "-fx-border-color: %s;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-font-size: 13;",
            COLOR_BLACK, COLOR_WHITE, COLOR_CYAN
        ));
        return comboBox;
    }
    
    /**
     * Create a styled button
     */
    private Button createStyledButton(String text, String bgColor, String textColor) {
        Button button = new Button(text);
        button.setPrefWidth(130);
        button.setStyle(String.format(
            "-fx-background-color: %s;" +
            "-fx-text-fill: %s;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14;" +
            "-fx-padding: 10 20;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;",
            bgColor, textColor
        ));
        
        // Hover effect
        button.setOnMouseEntered(e -> {
            button.setStyle(button.getStyle() + 
                String.format("-fx-effect: dropshadow(gaussian, %s, 10, 0.7, 0, 0);", bgColor));
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(button.getStyle().replace(
                String.format("-fx-effect: dropshadow(gaussian, %s, 10, 0.7, 0, 0);", bgColor), ""));
        });
        
        return button;
    }
    
    /**
     * Create an error label
     */
    private Label createErrorLabel() {
        Label label = new Label();
        label.setTextFill(Color.web("#FF6B6B"));
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        return label;
    }
    
    /**
     * Validate nickname input
     */
    private void validateNickname(String value, Label errorLabel) {
        if (value.isEmpty()) {
            errorLabel.setText("");
        } else if (!isValidInput(value)) {
            errorLabel.setText("âš?Only letters, numbers, and symbols allowed");
        } else if (value.length() < 3) {
            errorLabel.setText("âš?Too short (min 3 characters)");
        } else if (value.length() > 20) {
            errorLabel.setText("âš?Too long (max 20 characters)");
        } else {
            errorLabel.setText("âœ?Valid");
            errorLabel.setTextFill(Color.web(COLOR_CYAN));
        }
    }
    
    /**
     * Validate manifesto input
     */
    private void validateManifesto(String value, Label errorLabel) {
        if (value.isEmpty()) {
            errorLabel.setText("");
        } else if (!isValidInput(value)) {
            errorLabel.setText("âš?Only letters, numbers, and symbols allowed");
        } else if (value.length() < 3) {
            errorLabel.setText("âš?Too short (min 3 characters)");
        } else if (value.length() > 20) {
            errorLabel.setText("âš?Too long (max 20 characters)");
        } else {
            errorLabel.setText("âœ?Valid");
            errorLabel.setTextFill(Color.web(COLOR_CYAN));
        }
    }
    
    /**
     * Update submit button state
     */
    private void updateSubmitButton(Button submitButton) {
        submitButton.setDisable(!isFormValid());
    }
    
    /**
     * Validate input string against allowed characters
     * 
     * @param input The input string to validate
     * @return true if input contains only alphanumeric and symbols
     */
    private boolean isValidInput(String input) {
        return VALID_PATTERN.matcher(input).matches();
    }
    
    /**
     * Check if the form is valid
     * 
     * @return true if all fields are valid
     */
    private boolean isFormValid() {
        String nickname = nicknameField.getText().trim();
        String manifesto = manifestoField.getText().trim();
        
        boolean nicknameValid = !nickname.isEmpty() && 
                                nickname.length() >= 3 && 
                                nickname.length() <= 20 && 
                                isValidInput(nickname);
        
        boolean manifestoValid = !manifesto.isEmpty() && 
                                 manifesto.length() >= 3 && 
                                 manifesto.length() <= 20 && 
                                 isValidInput(manifesto);
        
        return nicknameValid && manifestoValid;
    }
    
    /**
     * Show the dialog and return the result
     * 
     * @return Optional containing HighScoreEntry if submitted, empty if cancelled
     */
    public Optional<HighScoreEntry> showAndGetResult() {
        return showAndWait();
    }
}
