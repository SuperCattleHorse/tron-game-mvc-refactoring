package com.tron.model.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.tron.model.input.GameInput;
import com.tron.model.observer.GameStateObserver;
import com.tron.model.observer.Subject;
import com.tron.model.util.PlayerColor;

/**
 * TronGameModel - Core game model class implementing Subject in Observer Pattern
 * 
 * This class represents the central game model (Subject) that notifies observers
 * (Views, Controllers) of game state changes. This implementation demonstrates
 * the Observer Pattern combined with MVC architecture.
 * 
 * Design Pattern: Observer Pattern (Behavioral) + MVC Architecture
 * - Acts as Subject that maintains list of GameStateObserver instances
 * - Notifies all observers when game state changes
 * - Decouples game logic from presentation layer
 * - Enables multiple views to observe same model
 * 
 * Custom Observer Implementation:
 * This is a custom Observer Pattern implementation. We do NOT use Java's built-in
 * Observable class, PropertyChangeSupport, or JavaFX InvalidationListener as those
 * are pre-made implementations. Benefits of custom implementation:
 * - Type-safe observer management through {@code Subject<GameStateObserver>} interface
 * - Explicit notification methods for different event types
 * - Better control over when and how observers are notified
 * - Clear separation between model logic and observer notification
 * - Easier testing and debugging
 * 
 * Responsibilities:
 * - Manage game state and all player objects
 * - Execute game logic (movement, collision detection, scoring, etc.)
 * - Provide data access interface for View rendering
 * - Notify observers of state changes through Observer Pattern
 * 
 * MVC Principles:
 * - Model layer: No dependencies on any GUI libraries (AWT/Swing/JavaFX)
 * - Does not directly handle user input (Controller's responsibility)
 * - Only manages data and business logic
 * - Communicates with View through observer notifications
 * 
 * Observer Pattern Benefits:
 * - Loose coupling: Model doesn't know concrete observer types
 * - Open/Closed Principle: Can add new observers without modifying model
 * - Multiple observers: Multiple views can observe same model
 * - Automatic updates: Views update automatically when model changes
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 3.0 (Observer Pattern Complete)
 */
public class TronGameModel implements Subject<GameStateObserver> {
    
    // Game state
    protected PlayerHuman player;
    protected Player[] players;
    protected int currentScore;
    protected boolean isRunning;
    protected boolean paused = false;
    
    // Game configuration
    protected final int mapWidth;
    protected final int mapHeight;
    protected final int velocity;
    protected final int playerCount;
    
    // Color palette for players
    protected static final PlayerColor[] PLAYER_COLORS = {
        PlayerColor.CYAN, PlayerColor.PINK, PlayerColor.WHITE, PlayerColor.YELLOW,
        PlayerColor.BLUE, PlayerColor.ORANGE, PlayerColor.RED, PlayerColor.GREEN
    };
    
    protected Random rand = new Random();
    
    // Observer pattern for MVC communication
    // Using CopyOnWriteArrayList for thread-safe iteration during notification
    private final List<GameStateObserver> observers = new ArrayList<>();
    
    /**
     * Constructor initializes the game model
     * 
     * @param mapWidth Width of the game area
     * @param mapHeight Height of the game area
     * @param velocity Player movement speed
     * @param playerCount Number of players (max 8)
     */
    public TronGameModel(int mapWidth, int mapHeight, int velocity, int playerCount) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.velocity = velocity;
        this.playerCount = Math.min(playerCount, 8);
        this.players = new Player[this.playerCount];
        this.currentScore = 0;
        this.isRunning = false;
    }
    
    // ============ Observer Pattern Methods (Subject<GameStateObserver> Implementation) ============
    
    /**
     * Attaches an observer to receive game state updates.
     * 
     * Implements Subject interface. Once attached, the observer will receive
     * notifications whenever the game state changes. Duplicate observers are
     * not added to prevent redundant notifications.
     * 
     * @param observer The GameStateObserver to attach. Null observers are ignored.
     */
    @Override
    public void attach(GameStateObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Detaches an observer from receiving game state updates.
     * 
     * Implements Subject interface. After detachment, the observer will no
     * longer receive notifications. If the observer was not attached, this
     * method has no effect.
     * 
     * @param observer The GameStateObserver to detach
     */
    @Override
    public void detach(GameStateObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Notifies all attached observers of general game state change.
     * 
     * Implements Subject interface. This is the general notification method
     * called after any state change that requires view updates. Calls
     * onGameStateChanged() on each registered observer.
     * 
     * This method is typically called:
     * - After each game tick (player movement, collision detection)
     * - After game configuration changes
     * - After any model update that affects rendering
     */
    @Override
    public void notifyObservers() {
        for (GameStateObserver observer : observers) {
            observer.onGameStateChanged();
        }
    }
    
    /**
     * Legacy method for backward compatibility.
     * Delegates to attach() method.
     * 
     * @deprecated Use attach() instead for consistency with Subject interface
     */
    @Deprecated
    public void addObserver(GameStateObserver observer) {
        attach(observer);
    }
    
    /**
     * Legacy method for backward compatibility.
     * Delegates to detach() method.
     * 
     * @deprecated Use detach() instead for consistency with Subject interface
     */
    @Deprecated
    public void removeObserver(GameStateObserver observer) {
        detach(observer);
    }
    
    /**
     * Legacy method for backward compatibility.
     * Delegates to notifyObservers() method.
     * 
     * @deprecated Use notifyObservers() instead for consistency with Subject interface
     */
    @Deprecated
    protected void notifyGameStateChanged() {
        notifyObservers();
    }
    
    /**
     * Notify observers of score change
     */
    protected void notifyScoreChanged(int playerIndex, int newScore) {
        for (GameStateObserver observer : observers) {
            observer.onScoreChanged(playerIndex, newScore);
        }
    }
    
    /**
     * Notify observers of boost change
     */
    protected void notifyBoostChanged(int playerIndex, int boostCount) {
        for (GameStateObserver observer : observers) {
            observer.onBoostChanged(playerIndex, boostCount);
        }
    }
    
    /**
     * Notify observers when player crashes
     */
    protected void notifyPlayerCrashed(int playerIndex) {
        for (GameStateObserver observer : observers) {
            observer.onPlayerCrashed(playerIndex);
        }
    }
    
    /**
     * Notify observers when game resets
     */
    protected void notifyGameReset() {
        for (GameStateObserver observer : observers) {
            observer.onGameReset();
        }
    }
    
    // ============ Game Logic Methods ============
    
    /**
     * Update game state by one time step
     * This method should be called periodically by the game timer
     */
    public void tick() {
        if (!isRunning) return;
        if (paused) return; // Skip updates when paused
        
        // Increment score each tick (like Swing version)
        currentScore++;
        notifyScoreChanged(0, currentScore);
        
        // Move all players
        for (Player p : players) {
            if (p != null) {
                p.setBounds(mapWidth, mapHeight);
                p.move();
            }
        }
        
        // Check collisions
        for (Player p1 : players) {
            if (p1 != null) {
                for (Player p2 : players) {
                    if (p2 != null) {
                        p1.crash(p1.intersects(p2));
                    }
                }
            }
        }
        
        // Check if human player is still alive
        if (player != null && !player.getAlive()) {
            isRunning = false;
            notifyPlayerCrashed(0);
        }
        
        // Notify observers of state change
        notifyGameStateChanged();
    }
    
    /**
     * Reset the game to initial state
     * Creates all players (human + AI)
     */
    public void reset() {
        // Create human player
        int[] start = getRandomStart();
        player = new PlayerHuman(start[0], start[1], start[2], start[3], PLAYER_COLORS[0]);
        players[0] = player;
        
        // Create AI players for remaining slots
        for (int i = 1; i < players.length; i++) {
            start = getRandomStart();
            players[i] = new com.tron.model.game.PlayerAI(
                start[0], start[1], start[2], start[3], PLAYER_COLORS[i % PLAYER_COLORS.length]
            );
        }
        
        // Give all players reference to all other players (for collision detection)
        for (Player p : players) {
            if (p != null) {
                p.addPlayers(players);
            }
        }
        
        currentScore = 0;
        isRunning = true;
        resetPauseState();
        
        notifyGameReset();
    }
    
    /**
     * Generate random starting position and velocity for a player
     * Ensures the player initially moves toward the center
     * 
     * @return int array: [x, y, velocityX, velocityY]
     */
    protected int[] getRandomStart() {
        int[] start = new int[4];
        int x = 50 + rand.nextInt(400);
        int y = 50 + rand.nextInt(400);
        int velX = 0;
        int velY = 0;
        
        // Move toward center
        if (rand.nextInt(2) == 0) {
            velX = (x < 250) ? velocity : -velocity;
        } else {
            velY = (y < 250) ? velocity : -velocity;
        }
        
        start[0] = x;
        start[1] = y;
        start[2] = velX;
        start[3] = velY;
        return start;
    }
    
    // ============ Player Control Methods (called by Controller) ============
    
    /**
     * Unified input handler - receives GameInput commands from Controller
     * This method contains all business logic for input validation
     * 
     * MVC Principle: Model decides whether input is valid based on game state
     * Controller should NOT check player state - delegate all logic to Model
     * 
     * Design Change: Now uses GUI-independent GameInput enum instead of KeyEvent
     * This allows Model to be completely independent of AWT/Swing/JavaFX
     * 
     * @param input The GameInput command from controller
     */
    public void handleInput(GameInput input) {
        // Process Player 1 input
        if (player != null && player.getAlive()) {
            switch (input) {
                case MOVE_LEFT:
                    movePlayerLeft();
                    return;
                case MOVE_RIGHT:
                    movePlayerRight();
                    return;
                case MOVE_UP:
                    movePlayerUp();
                    return;
                case MOVE_DOWN:
                    movePlayerDown();
                    return;
                case JUMP:
                    playerJump();
                    return;
                case BOOST:
                    playerBoost();
                    return;
                default:
                    // Fall through to check Player 2 input
                    break;
            }
        }
        
        // Process Player 2 input (for two-player mode)
        if (players != null && players.length >= 2 && players[1] != null && players[1].getAlive()) {
            if (players[1] instanceof PlayerHuman) {
                PlayerHuman player2 = (PlayerHuman) players[1];
                switch (input) {
                    case P2_MOVE_LEFT:
                        player2.setXVelocity(-velocity);
                        player2.setYVelocity(0);
                        break;
                    case P2_MOVE_RIGHT:
                        player2.setXVelocity(velocity);
                        player2.setYVelocity(0);
                        break;
                    case P2_MOVE_UP:
                        player2.setYVelocity(-velocity);
                        player2.setXVelocity(0);
                        break;
                    case P2_MOVE_DOWN:
                        player2.setYVelocity(velocity);
                        player2.setXVelocity(0);
                        break;
                    case P2_JUMP:
                        player2.jump();
                        break;
                    case P2_BOOST:
                        player2.startBoost();
                        break;
                    default:
                        // Ignore unknown commands
                        break;
                }
            }
        }
    }
    
    /**
     * Legacy method for backward compatibility with old controllers
     * Converts AWT KeyEvent keyCode to GameInput and delegates
     * 
     * @deprecated Use handleInput(GameInput) instead
     * @param keyCode The key code from KeyEvent
     */
    @Deprecated
    public void handleKeyPress(int keyCode) {
        GameInput input = GameInput.fromAWTKeyCode(keyCode);
        handleInput(input);
    }
    
    /**
     * Set player velocity to move left
     * Internal method - called by handleKeyPress after validation
     */
    private void movePlayerLeft() {
        if (player != null && player.getAlive()) {
            player.setXVelocity(-velocity);
            player.setYVelocity(0);
        }
    }
    
    /**
     * Set player velocity to move right
     * Internal method - called by handleKeyPress after validation
     */
    private void movePlayerRight() {
        if (player != null && player.getAlive()) {
            player.setXVelocity(velocity);
            player.setYVelocity(0);
        }
    }
    
    /**
     * Set player velocity to move up
     * Internal method - called by handleKeyPress after validation
     */
    private void movePlayerUp() {
        if (player != null && player.getAlive()) {
            player.setYVelocity(-velocity);
            player.setXVelocity(0);
        }
    }
    
    /**
     * Set player velocity to move down
     * Internal method - called by handleKeyPress after validation
     */
    private void movePlayerDown() {
        if (player != null && player.getAlive()) {
            player.setYVelocity(velocity);
            player.setXVelocity(0);
        }
    }
    
    /**
     * Activate player jump
     * Internal method - called by handleKeyPress after validation
     */
    private void playerJump() {
        if (player != null && player.getAlive()) {
            player.jump();
            notifyBoostChanged(0, player.getBoostsLeft());
        }
    }
    
    /**
     * Activate player boost
     * Internal method - called by handleKeyPress after validation
     */
    private void playerBoost() {
        if (player != null && player.getAlive()) {
            player.startBoost();
            notifyBoostChanged(0, player.getBoostsLeft());
        }
    }
    
    // ============ Data Access Methods (for View) ============
    
    /**
     * Get all players
     */
    public Player[] getPlayers() {
        return players;
    }
    
    /**
     * Get the human player
     */
    public PlayerHuman getPlayer() {
        return player;
    }
    
    /**
     * Get current score
     */
    public int getCurrentScore() {
        return currentScore;
    }
    
    /**
     * Set current score
     */
    public void setCurrentScore(int score) {
        this.currentScore = score;
        notifyScoreChanged(0, currentScore);
    }
    
    /**
     * Check if game is running
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Start the game
     */
    public void start() {
        isRunning = true;
    }
    
    /**
     * Stop the game
     */
    public void stop() {
        isRunning = false;
    }
    
    /**
     * Get map width
     */
    public int getMapWidth() {
        return mapWidth;
    }
    
    /**
     * Get map height
     */
    public int getMapHeight() {
        return mapHeight;
    }
    
    /**
     * Get velocity
     */
    public int getVelocity() {
        return velocity;
    }
    
    // ============ Pause System Methods ============
    
    /**
     * Pause the game
     * Game loop will continue running but tick() will skip all updates
     */
    public void pause() {
        if (isRunning && !paused) {
            paused = true;
            notifyGameStateChanged();
        }
    }
    
    /**
     * Resume the game from pause
     */
    public void resume() {
        if (isRunning && paused) {
            paused = false;
            notifyGameStateChanged();
        }
    }
    
    /**
     * Check if game is paused
     * 
     * @return true if game is paused
     */
    public boolean isPaused() {
        return paused;
    }
    
    /**
     * Reset pause state when game resets
     */
    private void resetPauseState() {
        paused = false;
    }
}
