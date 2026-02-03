/**
 * High score management, persistence, and player achievement tracking.
 * 
 * <h2>Package Overview</h2>
 * This package handles all aspects of score tracking, high score persistence,
 * and player profile management. It implements the Singleton pattern for
 * centralized score management.
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li><b>{@link com.tron.model.score.Score}</b> - Singleton score manager with JSON persistence</li>
 *   <li><b>{@link com.tron.model.score.HighScoreEntry}</b> - Individual high score record</li>
 *   <li><b>{@link com.tron.model.score.LocalDateAdapter}</b> - Gson adapter for date serialization</li>
 * </ul>
 * 
 * <h2>Features</h2>
 * <ul>
 *   <li><b>Persistent Storage</b> - JSON-based high score file</li>
 *   <li><b>Player Profiles</b> - Nickname, gender, manifesto for each entry</li>
 *   <li><b>Legacy Support</b> - Migration from old text-based format</li>
 *   <li><b>Observer Pattern</b> - Score change notifications</li>
 *   <li><b>Top 10 Tracking</b> - Maintains best 10 scores</li>
 * </ul>
 * 
 * <h2>Data Structure</h2>
 * Each high score entry contains:
 * <ul>
 *   <li>Score value</li>
 *   <li>Player nickname (3-20 characters)</li>
 *   <li>Player gender (Male, Female, Hidden)</li>
 *   <li>Achievement manifesto (player message)</li>
 *   <li>Date of achievement</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 2.0
 */
package com.tron.model.score;
