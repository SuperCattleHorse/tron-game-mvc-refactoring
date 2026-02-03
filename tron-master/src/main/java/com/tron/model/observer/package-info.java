/**
 * Custom Observer Pattern implementation for game state and player event notifications.
 * 
 * <h2>Package Overview</h2>
 * This package provides a custom implementation of the Observer Pattern (not using Java's
 * built-in Observable/Observer classes). This custom implementation offers:
 * <ul>
 *   <li>Type-safe observer management through generic interfaces</li>
 *   <li>Explicit notification methods for different event types</li>
 *   <li>Clear separation between different observable subjects</li>
 *   <li>Better testability with mock observers</li>
 * </ul>
 * 
 * <h2>Observer Interfaces</h2>
 * <ul>
 *   <li><b>{@link com.tron.model.observer.Subject}</b> - Generic Subject interface for any observable</li>
 *   <li><b>{@link com.tron.model.observer.PlayerObserver}</b> - Observes player state changes</li>
 *   <li><b>{@link com.tron.model.observer.GameStateObserver}</b> - Observes game state transitions</li>
 *   <li><b>{@link com.tron.model.observer.ScoreObserver}</b> - Observes score updates</li>
 *   <li><b>{@link com.tron.model.observer.InputObserver}</b> - Observes keyboard input events</li>
 * </ul>
 * 
 * <h2>Observable Subjects</h2>
 * <ul>
 *   <li><b>{@link com.tron.model.observer.InputSubject}</b> - Singleton for global input distribution</li>
 *   <li><b>{@link com.tron.model.game.Player}</b> - Observable player entity</li>
 *   <li><b>{@link com.tron.model.game.TronGameModel}</b> - Observable game model</li>
 *   <li><b>{@link com.tron.model.score.Score}</b> - Observable score manager</li>
 * </ul>
 * 
 * <h2>Event Types</h2>
 * <b>Player Events:</b>
 * <ul>
 *   <li>Player state changed (position, velocity)</li>
 *   <li>Player died (collision detected)</li>
 *   <li>Player collision (object hit)</li>
 *   <li>Direction changed (input received)</li>
 *   <li>Boost activated (power-up used)</li>
 * </ul>
 * 
 * <b>Game State Events:</b>
 * <ul>
 *   <li>Game state changed (running, paused, game over)</li>
 *   <li>Game reset (new game started)</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Register observer
 * Player player = new PlayerHuman(...);
 * player.attach(new PlayerObserver() {
 *     public void onPlayerDied(Player p) {
 *         // Handle player death
 *     }
 * });
 * 
 * // Observer is automatically notified
 * player.crash(Intersection.UP);
 * }</pre>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 2.0
 */
package com.tron.model.observer;
