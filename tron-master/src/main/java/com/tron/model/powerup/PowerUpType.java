package com.tron.model.powerup;

/**
 * PowerUpType - Enum defining different types of power-ups
 * 
 * Design Pattern: Strategy Pattern (behavioral)
 * - Each type represents a different effect strategy
 * - Extensible for future power-up types
 * 
 * Current Types:
 * - BOOST: Grants additional boost charges to player
 * - BOSS_DAMAGE: Deals damage to boss (reserved for future boss battle mode)
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public enum PowerUpType {
    /**
     * BOOST - Increases player's boost count by 1
     * Effect: Player gains one additional boost charge
     * Used in: Story mode regular levels
     */
    BOOST,
    
    /**
     * BOSS_DAMAGE - Deals damage to boss enemy
     * Effect: Reduces boss HP when collected
     * Used in: Future boss battle levels (reserved)
     */
    BOSS_DAMAGE
}
