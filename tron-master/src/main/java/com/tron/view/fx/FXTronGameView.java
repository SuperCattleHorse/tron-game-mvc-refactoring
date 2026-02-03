package com.tron.view.fx;

import java.util.List;

import com.tron.model.boss.Boss;
import com.tron.model.data.DrawData;
import com.tron.model.game.Player;
import com.tron.model.game.StoryGameModel;
import com.tron.model.game.SurvivalGameModel;
import com.tron.model.game.TronGameModel;
import com.tron.model.observer.GameStateObserver;
import com.tron.model.powerup.PowerUp;
import com.tron.model.powerup.PowerUpManager;
import com.tron.model.util.MapConfig;
import com.tron.model.util.MapObstacle;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * FXTronGameView - JavaFX Game View (Canvas-based) with Power-Up Support
 * 
 * This is the JavaFX version of TronGameView, using Canvas for rendering
 * players, trails, and power-ups. Maintains Observer Pattern architecture
 * to receive Model updates.
 * 
 * Design Pattern: Observer Pattern + MVC Architecture
 * - Implements GameStateObserver to receive Model notifications
 * - Automatically redraws when Model state changes
 * - Uses JavaFX Canvas and GraphicsContext for rendering
 * 
 * Responsibilities:
 * - Render game screen (players, trails, background, power-ups)
 * - Listen to Model state changes and auto-refresh
 * - Provide JavaFX Canvas component
 * 
 * MVC Principles:
 * - Only responsible for rendering, no business logic
 * - Does not directly modify Model state
 * - Receives Model updates through observer pattern
 * 
 * Rendering Architecture:
 * - Uses JavaFX Canvas (similar to HTML5 Canvas)
 * - GraphicsContext for 2D drawing operations
 * - Immediate mode rendering (redraw on each state change)
 * 
 * Power-Up Rendering:
 * - Draws white filled 5-point stars for power-ups
 * - Only rendered in Story mode
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 2.0 (With Power-Up Support)
 */
public class FXTronGameView extends Canvas implements GameStateObserver {
    
    protected TronGameModel model;
    protected GraphicsContext gc;
    protected Color backgroundColor = Color.BLACK;
    protected Image powerUpImage;
    protected Image bossImage;
    
    // Health bar dimensions for Boss level
    private static final int HEALTH_BAR_WIDTH = 200;
    private static final int HEALTH_BAR_HEIGHT = 20;
    private static final int HEALTH_BAR_MARGIN_TOP = 30;
    
    /**
     * Constructor with model reference
     * 
     * Registers this view as an observer of the model to receive state change
     * notifications automatically.
     * 
     * @param model The game model to observe and render
     */
    public FXTronGameView(TronGameModel model) {
        super(model.getMapWidth(), model.getMapHeight());
        this.model = model;
        this.gc = getGraphicsContext2D();
        this.model.attach(this);  // Register as observer using Subject interface
        
        // Load power-up image
        try {
            powerUpImage = new Image(getClass().getResourceAsStream("/powerup_star.png"));
        } catch (Exception e) {
            System.err.println("Warning: Failed to load power-up image: " + e.getMessage());
        }
        
        // Load Boss image for Story mode Boss level
        try {
            bossImage = new Image(getClass().getResourceAsStream("/boss.gif"));
        } catch (Exception e) {
            System.err.println("Warning: Failed to load Boss image: " + e.getMessage());
        }
        
        // Set canvas to be focusable for keyboard input
        setFocusTraversable(true);
        
        // Initial draw
        draw();
    }
    
    // ============ Rendering Methods ============
    
    /**
     * Adjust canvas size dynamically based on level type
     */
    private void adjustCanvasSize() {
        boolean isBossLevel = false;
        if (model instanceof StoryGameModel) {
            isBossLevel = ((StoryGameModel) model).isBossLevel();
        }
        
        if (isBossLevel) {
            // Boss Battle requires 900x600 canvas
            if (getWidth() != 900 || getHeight() != 600) {
                setWidth(900);
                setHeight(600);
            }
        } else {
            // Normal levels use 500x500 canvas
            if (getWidth() != 500 || getHeight() != 500) {
                setWidth(500);
                setHeight(500);
            }
        }
    }
    
    /**
     * Main drawing method - called when state changes
     * This replaces Swing's paintComponent()
     */
    protected void draw() {
        // Null safety check
        if (gc == null) {
            return;
        }
        
        // Adjust canvas size if needed
        adjustCanvasSize();
        
        // Clear canvas with background color
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, getWidth(), getHeight());
        
        // Check if this is Story mode Boss level
        boolean isBossLevel = false;
        if (model instanceof StoryGameModel) {
            isBossLevel = ((StoryGameModel) model).isBossLevel();
        }
        
        if (isBossLevel) {
            // Draw Boss Battle layout
            drawBossLevel();
        } else {
            // Draw normal level layout
            drawNormalLevel();
        }
    }
    
    /**
     * Draw normal level (no Boss)
     */
    private void drawNormalLevel() {
        // Draw map border
        drawBorder();
        
        // Draw obstacles (if in Survival mode with map obstacles)
        drawObstacles();
        
        // Draw power-ups (if in Story mode)
        drawPowerUps();
        
        // Draw all players
        Player[] players = model.getPlayers();
        if (players != null) {
            for (Player p : players) {
                if (p != null) {
                    DrawData data = p.getDrawData();
                    drawPlayer(data);
                }
            }
        }
    }
    
    /**
     * Draw Boss Battle level (Story mode level 8)
     * Exactly matches FXBossBattleView rendering logic
     */
    private void drawBossLevel() {
        StoryGameModel storyModel = (StoryGameModel) model;
        int playerAreaWidth = storyModel.getPlayerAreaWidth();
        int bossAreaWidth = (int)getWidth() - playerAreaWidth;
        
        // Clear entire canvas with black background first
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight());
        
        // Fill only player area (left side) with custom background color
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, playerAreaWidth, getHeight());
        
        // Draw Boost count above player area
        drawBoostCount(playerAreaWidth);
        
        // Draw player area (left half) with white border
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3.0);
        gc.strokeRect(0, 0, playerAreaWidth, getHeight());
        
        // Draw Boss display area (right half) - use drawBossArea method
        drawBossAreaComplete(playerAreaWidth, bossAreaWidth, storyModel.getBoss());
        
        // Draw player
        Player player = model.getPlayer();
        if (player != null) {
            DrawData data = player.getDrawData();
            drawPlayer(data);
        }
        
        // Draw Boss power-ups (LAST)
        drawBossPowerUps();
    }
    
    /**
     * Draw Boss area (right half) with image and health bar
     * Exactly matches FXBossBattleView.drawBossArea()
     */
    private void drawBossAreaComplete(int playerAreaWidth, int bossAreaWidth, Boss boss) {
        // Draw health bar at top
        drawBossHealthBar(playerAreaWidth, bossAreaWidth, boss);
        
        // Draw Boss name below health bar
        drawBossName(playerAreaWidth, bossAreaWidth);
        
        // Draw Boss image if loaded
        if (bossImage != null && !bossImage.isError()) {
            // Calculate Boss image position (centered in right half, below health bar and name)
            double bossImageX = playerAreaWidth + (bossAreaWidth - bossImage.getWidth()) / 2;
            double bossImageY = (getHeight() - bossImage.getHeight()) / 2 + 40; // Offset down to make room
            
            // Draw Boss image (GIF animation will play automatically)
            gc.drawImage(bossImage, bossImageX, bossImageY);
        } else {
            // Draw placeholder if image failed to load
            gc.setFill(Color.RED);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            gc.fillText("BOSS", playerAreaWidth + bossAreaWidth / 2 - 30, getHeight() / 2);
        }
    }
    
    /**
     * Draw Boost count above player area
     */
    private void drawBoostCount(int playerAreaWidth) {
        Player player = model.getPlayer();
        if (player == null) return;
        
        int boostCount = player.getBoostsLeft();
        String boostText = "Boost: " + boostCount;
        
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // Position above player area (centered horizontally)
        double textX = playerAreaWidth / 2 - 60; // Approximate center
        double textY = 25;
        
        gc.fillText(boostText, textX, textY);
    }
    
    /**
     * Draw Boss area with image and health bar
     */
    private void drawBossArea(int playerAreaWidth, Boss boss) {
        int bossAreaWidth = (int)getWidth() - playerAreaWidth;
        
        // Draw health bar at top
        drawBossHealthBar(playerAreaWidth, bossAreaWidth, boss);
        
        // Draw Boss name below health bar
        drawBossName(playerAreaWidth, bossAreaWidth);
        
        // Draw Boss image (centered in right half, offset down to avoid overlap)
        if (bossImage != null) {
            double bossImageX = playerAreaWidth + (bossAreaWidth - bossImage.getWidth()) / 2;
            double bossImageY = (getHeight() - bossImage.getHeight()) / 2 + 40; // Offset down
            gc.drawImage(bossImage, bossImageX, bossImageY);
        }
    }
    
    /**
     * Draw Boss name "Boss: Cat Monster" in red bold text
     */
    private void drawBossName(int playerAreaWidth, int bossAreaWidth) {
        String bossName = "Boss: Cat Monster";
        
        gc.setFill(Color.RED);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        
        // Position below health bar, centered in Boss area
        double textWidth = bossName.length() * 16; // Approximate text width
        double textX = playerAreaWidth + (bossAreaWidth - textWidth) / 2;
        double textY = HEALTH_BAR_MARGIN_TOP + HEALTH_BAR_HEIGHT + 60; // Below HP text
        
        gc.fillText(bossName, textX, textY);
    }
    
    /**
     * Draw Boss health bar and HP text
     */
    private void drawBossHealthBar(int playerAreaWidth, int bossAreaWidth, Boss boss) {
        // Health bar position (top of right half, centered)
        double healthBarX = playerAreaWidth + (bossAreaWidth - HEALTH_BAR_WIDTH) / 2;
        double healthBarY = HEALTH_BAR_MARGIN_TOP;
        
        // Draw health bar background
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2.0);
        gc.strokeRect(healthBarX, healthBarY, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        
        // Draw health bar fill (white, decreases from right to left)
        double healthPercentage = boss.getHealthPercentage();
        double healthBarFillWidth = HEALTH_BAR_WIDTH * healthPercentage;
        
        gc.setFill(Color.WHITE);
        gc.fillRect(healthBarX, healthBarY, healthBarFillWidth, HEALTH_BAR_HEIGHT);
        
        // Draw HP text below health bar
        String hpText = boss.getCurrentHealth() + "/" + boss.getMaxHealth();
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Center text below health bar
        double textX = healthBarX + (HEALTH_BAR_WIDTH - hpText.length() * 10) / 2;
        double textY = healthBarY + HEALTH_BAR_HEIGHT + 25;
        
        gc.fillText(hpText, textX, textY);
    }
    
    protected void drawBorder() {
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2.0);
        gc.strokeRect(0, 0, getWidth(), getHeight());
    }
    
    /**
     * Draw map obstacles (for Survival mode with obstacle maps)
     */
    protected void drawObstacles() {
        if (model instanceof SurvivalGameModel) {
            SurvivalGameModel survivalModel = (SurvivalGameModel) model;
            MapConfig mapConfig = survivalModel.getMapConfig();
            
            if (mapConfig != null && !mapConfig.getObstacles().isEmpty()) {
                gc.setFill(Color.WHITE);
                gc.setStroke(Color.CYAN);
                gc.setLineWidth(2);
                
                for (MapObstacle obstacle : mapConfig.getObstacles()) {
                    gc.fillRect(obstacle.getX(), obstacle.getY(), 
                               obstacle.getWidth(), obstacle.getHeight());
                    gc.strokeRect(obstacle.getX(), obstacle.getY(), 
                                 obstacle.getWidth(), obstacle.getHeight());
                }
            }
        }
    }
    
    /**
     * Draw a single player using DrawData
     * Converts model data to JavaFX rendering calls
     * 
     * @param data Player's draw data
     */
    protected void drawPlayer(DrawData data) {
        // Convert PlayerColor to JavaFX Color
        Color playerColor = data.getColor().toFXColor();
        gc.setStroke(playerColor);
        gc.setFill(playerColor);
        
        // Draw player's path (trail)
        // Note: Shape drawing needs conversion from AWT shapes to JavaFX
        for (com.tron.model.util.Shape shape : data.getPath()) {
            if (shape instanceof com.tron.model.util.Line) {
                com.tron.model.util.Line line = (com.tron.model.util.Line) shape;
                drawLine(line);
            }
        }
        
        // Draw player's current position
        if (data.isAlive()) {
            gc.fillRect(data.getX(), data.getY(), data.getWidth(), data.getHeight());
        }
    }
    
    /**
     * Draw a line using JavaFX GraphicsContext
     * 
     * @param line The Line object to draw
     */
    protected void drawLine(com.tron.model.util.Line line) {
        gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
    }
    
    /**
     * Draw all active power-ups (Story mode only)
     * Renders power-ups using PNG image
     */
    protected void drawPowerUps() {
        // Only draw power-ups in Story mode
        if (!(model instanceof StoryGameModel)) {
            return;
        }
        
        StoryGameModel storyModel = (StoryGameModel) model;
        PowerUpManager powerUpManager = storyModel.getPowerUpManager();
        
        if (powerUpManager == null || powerUpImage == null) {
            return;
        }
        
        List<PowerUp> powerUps = powerUpManager.getActivePowerUps();
        for (PowerUp powerUp : powerUps) {
            if (powerUp.isActive()) {
                // Draw image centered on power-up position
                // Image is 10x10, so offset by 5 pixels to center it
                gc.drawImage(powerUpImage, powerUp.getX() - 5, powerUp.getY() - 5);
            }
        }
    }
    
    /**
     * Draw Boss level power-ups
     */
    protected void drawBossPowerUps() {
        if (!(model instanceof StoryGameModel)) {
            return;
        }
        
        StoryGameModel storyModel = (StoryGameModel) model;
        if (!storyModel.isBossLevel() || powerUpImage == null) {
            return;
        }
        
        List<PowerUp> powerUps = storyModel.getBossPowerUpManager().getActivePowerUps();
        for (PowerUp powerUp : powerUps) {
            if (powerUp.isActive()) {
                gc.drawImage(powerUpImage, powerUp.getX() - 5, powerUp.getY() - 5);
            }
        }
    }

    
    // ============ Observer Pattern Implementation ============
    
    /**
     * Called when game state changes - redraw the view
     */
    @Override
    public void onGameStateChanged() {
        draw();
    }
    
    /**
     * Called when score changes
     */
    @Override
    public void onScoreChanged(int playerIndex, int newScore) {
        // Subclasses can override to update score display
    }
    
    /**
     * Called when boost count changes
     */
    @Override
    public void onBoostChanged(int playerIndex, int boostCount) {
        // Subclasses can override to update boost display
    }
    
    /**
     * Called when a player crashes
     */
    @Override
    public void onPlayerCrashed(int playerIndex) {
        // Subclasses can override to show game over screen
        draw();
    }
    
    /**
     * Called when game resets
     */
    @Override
    public void onGameReset() {
        draw();
    }
    
    // ============ Utility Methods ============
    
    /**
     * Set background color
     * 
     * @param color JavaFX Color for background
     */
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        draw();
    }
    
    /**
     * Get the associated model
     * 
     * @return The game model
     */
    public TronGameModel getModel() {
        return model;
    }
    
    /**
     * Request focus for this canvas
     * Required for keyboard input handling
     */
    public void requestFocus() {
        super.requestFocus();
    }
}
