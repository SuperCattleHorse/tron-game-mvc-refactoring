/**
 * Data Transfer Objects (DTOs) for passing rendering data from Model to View.
 * 
 * <h2>Package Overview</h2>
 * This package contains immutable data classes that transfer information from the
 * Model layer to the View layer without exposing internal model structure. This
 * follows the Data Transfer Object (DTO) pattern.
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li><b>{@link com.tron.model.data.DrawData}</b> - Complete rendering data for game objects</li>
 * </ul>
 * 
 * <h2>Design Pattern: Data Transfer Object (DTO)</h2>
 * <ul>
 *   <li><b>Immutable</b> - All fields are final, objects cannot be modified</li>
 *   <li><b>Framework-Independent</b> - Contains no UI framework dependencies</li>
 *   <li><b>View-Optimized</b> - Structured for efficient rendering</li>
 *   <li><b>Thread-Safe</b> - Immutability ensures thread safety</li>
 * </ul>
 * 
 * <h2>Benefits</h2>
 * <ul>
 *   <li>Model doesn't expose internal state to View</li>
 *   <li>View only receives data needed for rendering</li>
 *   <li>Easy to test without UI framework</li>
 *   <li>Clear separation between Model and View</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
package com.tron.model.data;
