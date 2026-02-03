/**
 * TRON Game - JavaFX-based light cycle racing game
 * 
 * <h2>Application Overview</h2>
 * This package contains the main application entry point and core game architecture.
 * The TRON game is a digital recreation of the classic light cycle racing game where
 * players control vehicles that leave solid trails behind them. The objective is to 
 * force opponents to crash into trails while avoiding collisions yourself.
 * 
 * <h2>Architecture</h2>
 * The application follows the Model-View-Controller (MVC) architectural pattern:
 * <ul>
 *   <li><b>Model</b> ({@link com.tron.model}) - Game logic, state management, and data structures</li>
 *   <li><b>View</b> ({@link com.tron.view.fx}) - JavaFX-based user interface components</li>
 *   <li><b>Controller</b> ({@link com.tron.controller.fx}) - Input handling and coordination</li>
 * </ul>
 * 
 * <h2>Design Patterns Used</h2>
 * <ul>
 *   <li><b>Singleton Pattern</b> - Game controller, audio manager, score manager</li>
 *   <li><b>Observer Pattern</b> - Player state notifications, game state updates</li>
 *   <li><b>Strategy Pattern</b> - AI vs Human player behavior</li>
 *   <li><b>Decorator Pattern</b> - Behavior enhancement for AI strategies</li>
 *   <li><b>Factory Pattern</b> - Game mode creation, map configuration</li>
 * </ul>
 * 
 * <h2>Game Modes</h2>
 * <ul>
 *   <li><b>Two Player</b> - Local multiplayer on the same keyboard</li>
 *   <li><b>Story Mode</b> - Single player with progressive AI difficulty levels</li>
 *   <li><b>Survival Mode</b> - Endless gameplay with high score tracking</li>
 *   <li><b>Boss Battle</b> - Fight against a special boss enemy</li>
 * </ul>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Multiple map types with different boundary behaviors</li>
 *   <li>Power-ups system (boost charges, jump ability)</li>
 *   <li>Configurable AI difficulty levels</li>
 *   <li>Audio system with background music and sound effects</li>
 *   <li>High score persistence with player profiles</li>
 *   <li>Customizable color schemes and settings</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 3.0
 * @since 1.0
 */
package com.tron;
