/**
 * JavaFX-based controller layer for input handling and scene management.
 * 
 * <h2>Package Overview</h2>
 * This package implements the Controller component of the MVC architecture using JavaFX.
 * It handles user input, coordinates between Model and View, and manages navigation.
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li><b>{@link com.tron.controller.fx.FXGameController}</b> - Singleton main controller</li>
 *   <li><b>{@link com.tron.controller.fx.FXGameInputController}</b> - Keyboard input handler</li>
 * </ul>
 * 
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li><b>Scene Management</b> - Switch between menu and game scenes</li>
 *   <li><b>Input Handling</b> - Process keyboard events and distribute to model</li>
 *   <li><b>View Coordination</b> - Initialize and manage view lifecycle</li>
 *   <li><b>Navigation</b> - Handle transitions between game modes</li>
 * </ul>
 * 
 * <h2>Design Pattern</h2>
 * <ul>
 *   <li><b>Singleton Pattern</b> - Single game controller instance</li>
 *   <li><b>MVC Architecture</b> - Mediates between Model and View</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
package com.tron.controller.fx;
