/**
 * Power-up system for collectible items and temporary abilities.
 * 
 * <h2>Package Overview</h2>
 * This package implements the power-up system that allows players to collect
 * special items during gameplay to gain temporary advantages.
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li><b>{@link com.tron.model.powerup.PowerUp}</b> - Individual power-up item</li>
 *   <li><b>{@link com.tron.model.powerup.PowerUpManager}</b> - Spawning and collection logic</li>
 *   <li><b>{@link com.tron.model.powerup.PowerUpType}</b> - Available power-up types</li>
 * </ul>
 * 
 * <h2>Power-Up Types</h2>
 * <ul>
 *   <li><b>BOOST</b> - Grants extra speed boost charge</li>
 *   <li><b>BOSS_DAMAGE</b> - Deals damage to boss in boss battle mode</li>
 * </ul>
 * 
 * <h2>Spawning System</h2>
 * <ul>
 *   <li>Random spawn locations on the map</li>
 *   <li>Timed spawn intervals</li>
 *   <li>Collision detection with players</li>
 *   <li>Visual rendering as white stars</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
package com.tron.model.powerup;
