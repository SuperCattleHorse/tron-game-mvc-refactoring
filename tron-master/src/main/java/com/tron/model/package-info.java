/**
 * Model layer containing all game logic, data structures, and business rules.
 * 
 * <h2>Package Overview</h2>
 * This package implements the Model component of the MVC architecture. It is completely
 * independent of the View layer and contains no references to JavaFX, Swing, or any
 * UI framework. This separation enables:
 * <ul>
 *   <li>Easy testing without UI dependencies</li>
 *   <li>Potential to support multiple UI frameworks</li>
 *   <li>Clear separation of concerns</li>
 *   <li>Reusable game logic</li>
 * </ul>
 * 
 * <h2>Subpackages</h2>
 * <ul>
 *   <li>{@link com.tron.model.game} - Core game entities (Player, GameObject, game modes)</li>
 *   <li>{@link com.tron.model.observer} - Observer pattern implementation for state notifications</li>
 *   <li>{@link com.tron.model.score} - High score management and persistence</li>
 *   <li>{@link com.tron.model.powerup} - Power-up system implementation</li>
 *   <li>{@link com.tron.model.boss} - Boss battle specific logic</li>
 *   <li>{@link com.tron.model.util} - Utility classes (colors, shapes, maps)</li>
 *   <li>{@link com.tron.model.data} - Data transfer objects for view rendering</li>
 *   <li>{@link com.tron.model.input} - Input event definitions</li>
 * </ul>
 * 
 * <h2>Design Principles</h2>
 * <ul>
 *   <li><b>Framework Independence</b> - No UI framework dependencies</li>
 *   <li><b>Observer Pattern</b> - Decoupled event notification system</li>
 *   <li><b>Strategy Pattern</b> - Flexible AI and player behavior</li>
 *   <li><b>Immutable DTOs</b> - Safe data transfer to view layer</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 3.0
 */
package com.tron.model;
