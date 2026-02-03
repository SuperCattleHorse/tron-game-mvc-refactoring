/**
 * Audio system for background music and sound effects.
 * 
 * <h2>Package Overview</h2>
 * This package provides centralized audio management using JavaFX Media API.
 * It implements the Singleton pattern for global audio control.
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li><b>{@link com.tron.audio.AudioManager}</b> - Singleton audio manager</li>
 * </ul>
 * 
 * <h2>Features</h2>
 * <ul>
 *   <li><b>Background Music (BGM)</b> - Looping game soundtrack</li>
 *   <li><b>Sound Effects</b> - Click, win, lose, pause, pickup sounds</li>
 *   <li><b>Volume Control</b> - Separate controls for BGM and SFX</li>
 *   <li><b>Enable/Disable</b> - Toggle audio on/off at runtime</li>
 *   <li><b>Resource Management</b> - Proper cleanup to prevent memory leaks</li>
 * </ul>
 * 
 * <h2>Sound Effects</h2>
 * <ul>
 *   <li>CLICK - UI button clicks</li>
 *   <li>LOSE - Player death or game over</li>
 *   <li>WIN - Level complete or victory</li>
 *   <li>PAUSE - Game paused</li>
 *   <li>UNPAUSE - Game resumed</li>
 *   <li>PICKUP - Power-up collected</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
package com.tron.audio;
