/**
 * Core game entities and logic including players, game modes, and behavior strategies.
 * 
 * <h2>Package Overview</h2>
 * This package contains the fundamental game objects and gameplay mechanics for the TRON game.
 * It implements multiple design patterns to provide flexible, extensible game behavior.
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li><b>{@link com.tron.model.game.GameObject}</b> - Abstract base class for all game entities</li>
 *   <li><b>{@link com.tron.model.game.Player}</b> - Abstract player implementation with Observer pattern</li>
 *   <li><b>{@link com.tron.model.game.PlayerHuman}</b> - Human-controlled player</li>
 *   <li><b>{@link com.tron.model.game.PlayerAI}</b> - AI-controlled player</li>
 *   <li><b>{@link com.tron.model.game.TronGameModel}</b> - Base game model with core mechanics</li>
 *   <li><b>{@link com.tron.model.game.StoryGameModel}</b> - Story mode with progressive difficulty</li>
 *   <li><b>{@link com.tron.model.game.SurvivalGameModel}</b> - Survival mode with high scores</li>
 *   <li><b>{@link com.tron.model.game.TwoPlayerGameModel}</b> - Local multiplayer mode</li>
 * </ul>
 * 
 * <h2>Design Patterns</h2>
 * <ul>
 *   <li><b>Strategy Pattern</b> - {@link com.tron.model.game.PlayerBehaviorStrategy} for AI/Human behavior</li>
 *   <li><b>Decorator Pattern</b> - Behavior enhancement through decorators</li>
 *   <li><b>Observer Pattern</b> - Player state change notifications</li>
 *   <li><b>Factory Pattern</b> - Game model creation in factory subpackage</li>
 *   <li><b>Template Method</b> - Abstract methods in Player and GameObject</li>
 * </ul>
 * 
 * <h2>Behavior Strategies</h2>
 * <ul>
 *   <li>{@link com.tron.model.game.HumanBehaviorStrategy} - Human player input handling</li>
 *   <li>{@link com.tron.model.game.AIBehaviorStrategy} - Basic AI decision making</li>
 *   <li>{@link com.tron.model.game.HardAIBehaviorStrategy} - Advanced AI with prediction</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 3.0
 */
package com.tron.model.game;
