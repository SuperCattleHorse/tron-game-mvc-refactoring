/**
 * JavaFX-based view layer for user interface and game rendering.
 * 
 * <h2>Package Overview</h2>
 * This package implements the View component of the MVC architecture using JavaFX.
 * It renders the game state, displays menus, and handles user interface elements.
 * 
 * <h2>Subpackages</h2>
 * <ul>
 *   <li><b>{@link com.tron.view.fx.menu}</b> - Menu screens (main menu, options, play selection)</li>
 * </ul>
 * 
 * <h2>Key Classes</h2>
 * <b>Game Views:</b>
 * <ul>
 *   <li>{@link com.tron.view.fx.FXTronGameView} - Canvas-based game renderer</li>
 *   <li>{@link com.tron.view.fx.FXStoryGameView} - Story mode view wrapper</li>
 *   <li>{@link com.tron.view.fx.FXSurvivalGameView} - Survival mode view wrapper</li>
 *   <li>{@link com.tron.view.fx.FXTwoPlayerGameView} - Two-player mode view wrapper</li>
 *   <li>{@link com.tron.view.fx.FXBossBattleGameView} - Boss battle view wrapper</li>
 *   <li>{@link com.tron.view.fx.FXBossBattleView} - Boss-specific rendering</li>
 * </ul>
 * 
 * <b>Dialogs:</b>
 * <ul>
 *   <li>{@link com.tron.view.fx.FXPauseDialog} - Pause menu overlay</li>
 *   <li>{@link com.tron.view.fx.FXPlayerInfoDialog} - High score entry form</li>
 * </ul>
 * 
 * <h2>Design Principles</h2>
 * <ul>
 *   <li><b>Observer Pattern</b> - Views observe Model state changes</li>
 *   <li><b>Separation of Concerns</b> - Views only handle rendering, no game logic</li>
 *   <li><b>Canvas Rendering</b> - High-performance 2D graphics</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
package com.tron.view.fx;
