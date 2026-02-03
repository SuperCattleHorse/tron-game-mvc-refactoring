/**
 * Utility classes for geometry, colors, maps, and framework-independent data types.
 * 
 * <h2>Package Overview</h2>
 * This package provides reusable utility classes that are used throughout the game model.
 * All classes are framework-independent, containing no references to JavaFX, AWT, or Swing.
 * 
 * <h2>Geometric Utilities</h2>
 * <ul>
 *   <li><b>{@link com.tron.model.util.Shape}</b> - Interface for geometric shapes</li>
 *   <li><b>{@link com.tron.model.util.Line}</b> - Represents a line segment (trail segment)</li>
 *   <li><b>{@link com.tron.model.util.Intersection}</b> - Collision detection results</li>
 * </ul>
 * 
 * <h2>Color Management</h2>
 * <ul>
 *   <li><b>{@link com.tron.model.util.PlayerColor}</b> - Framework-independent color enum with RGB values</li>
 * </ul>
 * 
 * <h2>Map System</h2>
 * <ul>
 *   <li><b>{@link com.tron.model.util.MapType}</b> - Available map types (Classic, Snake, Grid, Arena)</li>
 *   <li><b>{@link com.tron.model.util.MapConfig}</b> - Map configuration with obstacles and boundaries</li>
 *   <li><b>{@link com.tron.model.util.MapObstacle}</b> - Static rectangular obstacles</li>
 * </ul>
 * 
 * <h2>Design Principles</h2>
 * <ul>
 *   <li><b>Framework Independence</b> - Can work with any UI framework</li>
 *   <li><b>Immutability</b> - Most utility classes are immutable value objects</li>
 *   <li><b>Type Safety</b> - Enums for compile-time type checking</li>
 *   <li><b>Factory Pattern</b> - MapConfig uses factory method for creation</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
package com.tron.model.util;
