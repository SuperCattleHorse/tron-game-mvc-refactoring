package com.tron.view.fx;

import java.util.List;

import com.tron.model.boss.Boss;
import com.tron.model.boss.BossBattleGameModel;
import com.tron.model.data.DrawData;
import com.tron.model.game.Player;
import com.tron.model.powerup.PowerUp;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * FXBossBattleView - JavaFX View for Boss Battle Mode
 * 
 * Responsibilities:
 * - Render split-screen layout (player area left, boss display right)
 * - Draw player with white boundary box
 * - Display Boss image (Boss.gif) in right half
 * - Render Boss health bar and HP text
 * - Draw power-ups using PNG image
 * 
 * Layout Design:
 * - Left half: Player activity area with white border
 * - Right half: Boss static image display
 * - Top of right half: Health bar (white, decreases left to right)
 * - Below health bar: HP text (e.g., "6/10")
 * - Entire layout centered in window
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class FXBossBattleView extends Canvas {
    
    private BossBattleGameModel model;
    private GraphicsContext gc;
    private Color backgroundColor = Color.BLACK;  // Default background color
    
    // Images
    private Image bossImage;
    private Image powerUpImage;
    
    // Layout constants
    private final int totalWidth;
    private final int totalHeight;
    private final int playerAreaWidth;
    private final int bossAreaWidth;
    
    // Health bar dimensions
    private static final int HEALTH_BAR_WIDTH = 200;
    private static final int HEALTH_BAR_HEIGHT = 20;
    private static final int HEALTH_BAR_MARGIN_TOP = 30;
    
    /**
     * Constructor
     * 
     * @param model BossBattleGameModel instance
     */
    public FXBossBattleView(BossBattleGameModel model) {
        super(model.getMapWidth(), model.getMapHeight());
        
        this.model = model;
        this.gc = getGraphicsContext2D();
        
        this.totalWidth = model.getMapWidth();
        this.totalHeight = model.getMapHeight();
        this.playerAreaWidth = model.getPlayerAreaWidth();
        this.bossAreaWidth = totalWidth - playerAreaWidth;
        
        // Load images
        loadImages();
        
        // Make focusable for keyboard input
        setFocusTraversable(true);
        
        // Observer pattern - implement GameStateObserver
        model.attach(new com.tron.model.observer.GameStateObserver() {
            @Override
            public void onGameStateChanged() {
                draw();
            }
            
            @Override
            public void onScoreChanged(int playerIndex, int newScore) {}
            
            @Override
            public void onBoostChanged(int playerIndex, int boostCount) {}
            
            @Override
            public void onPlayerCrashed(int playerIndex) {
                draw();
            }
            
            @Override
            public void onGameReset() {
                draw();
            }
        });
        
        // Initial draw
        draw();
    }
    
    /**
     * Load Boss.gif and powerup_star.png
     */
    private void loadImages() {
        try {
            bossImage = new Image(getClass().getResourceAsStream("/boss.gif"));
        } catch (Exception e) {
            System.err.println("ERROR: Failed to load Boss.gif: " + e.getMessage());
            e.printStackTrace();
        }
        
        try {
            powerUpImage = new Image(getClass().getResourceAsStream("/powerup_star.png"));
        } catch (Exception e) {
            System.err.println("ERROR: Failed to load powerup_star.png: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Main draw method - renders entire Boss Battle scene
     */
    private void draw() {
        if (gc == null) return;
        
        // Clear entire canvas with black background first
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, totalWidth, totalHeight);
        
        // Fill only player area (left side) with custom background color
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, playerAreaWidth, totalHeight);
        
        // Draw Boost count above player area
        drawBoostCount();
        
        // Draw player area (left half) with white border
        drawPlayerArea();
        
        // Draw Boss display area (right half)
        drawBossArea();
        
        // Draw player
        drawPlayer();
        
        // Draw power-ups
        drawPowerUps();
    }
    
    /**
     * Draw Boost count above player area
     */
    private void drawBoostCount() {
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
     * Draw player activity area with white border
     */
    private void drawPlayerArea() {
        // Draw white border around player area
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3.0);
        gc.strokeRect(0, 0, playerAreaWidth, totalHeight);
    }
    
    /**
     * Draw Boss area (right half) with image and health bar
     */
    private void drawBossArea() {
        Boss boss = model.getBoss();
        
        // Draw health bar at top
        drawHealthBar(boss);
        
        // Draw Boss name below health bar
        drawBossName();
        
        // Draw Boss image if loaded
        if (bossImage != null && !bossImage.isError()) {
            // Calculate Boss image position (centered in right half, below health bar and name)
            double bossImageX = playerAreaWidth + (bossAreaWidth - bossImage.getWidth()) / 2;
            double bossImageY = (totalHeight - bossImage.getHeight()) / 2 + 40; // Offset down to make room
            
            // Draw Boss image (GIF animation will play automatically)
            gc.drawImage(bossImage, bossImageX, bossImageY);
        } else {
            // Draw placeholder if image failed to load
            gc.setFill(Color.RED);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            gc.fillText("BOSS", playerAreaWidth + bossAreaWidth / 2 - 30, totalHeight / 2);
        }
    }
    
    /**
     * Draw Boss name "Boss: Cat Monster" in red bold text
     */
    private void drawBossName() {
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
     * 
     * @param boss Boss instance
     */
    private void drawHealthBar(Boss boss) {
        // Health bar position (top of right half, horizontally centered)
        double healthBarX = playerAreaWidth + (bossAreaWidth - HEALTH_BAR_WIDTH) / 2;
        double healthBarY = HEALTH_BAR_MARGIN_TOP;
        
        // Draw health bar background (black outline)
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
        double textWidth = hpText.length() * 10; // Approximate text width
        double textX = healthBarX + (HEALTH_BAR_WIDTH - textWidth) / 2;
        double textY = healthBarY + HEALTH_BAR_HEIGHT + 25;
        
        gc.fillText(hpText, textX, textY);
    }
    
    /**
     * Draw player trails and current position
     */
    private void drawPlayer() {
        Player player = model.getPlayer();
        if (player == null) return;
        
        DrawData data = player.getDrawData();
        Color playerColor = data.getColor().toFXColor();
        
        gc.setStroke(playerColor);
        gc.setFill(playerColor);
        
        // Draw player's trail
        for (com.tron.model.util.Shape shape : data.getPath()) {
            if (shape instanceof com.tron.model.util.Line) {
                com.tron.model.util.Line line = (com.tron.model.util.Line) shape;
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
            }
        }
        
        // Draw player's current position
        if (data.isAlive()) {
            gc.fillRect(data.getX(), data.getY(), data.getWidth(), data.getHeight());
        }
    }
    
    /**
     * Draw power-ups using PNG image
     */
    private void drawPowerUps() {
        if (powerUpImage == null) return;
        
        List<PowerUp> powerUps = model.getPowerUpManager().getActivePowerUps();
        for (PowerUp powerUp : powerUps) {
            if (powerUp.isActive()) {
                // Center image on power-up position (image is 10x10)
                gc.drawImage(powerUpImage, powerUp.getX() - 5, powerUp.getY() - 5);
            }
        }
    }
    
    /**
     * Get the model
     * 
     * @return BossBattleGameModel instance
     */
    public BossBattleGameModel getModel() {
        return model;
    }
    
    /**
     * Request keyboard focus
     */
    @Override
    public void requestFocus() {
        super.requestFocus();
    }
    
    /**
     * Set background color for the canvas
     * 
     * @param color The new background color
     */
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        draw(); // Redraw with new background color
    }
}
