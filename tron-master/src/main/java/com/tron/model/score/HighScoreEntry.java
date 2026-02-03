package com.tron.model.score;

import java.time.LocalDate;

/**
 * HighScoreEntry - Data class for high score records
 * 
 * Stores complete information about a high score achievement including:
 * - Player score
 * - Player nickname
 * - Player gender
 * - Achievement manifesto
 * - Achievement date
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class HighScoreEntry implements Comparable<HighScoreEntry> {
    
    private int score;
    private String nickname;
    private String gender;  // "Male", "Female", "Hidden"
    private String manifesto;
    private LocalDate date;
    
    /**
     * Constructor for creating a new high score entry
     * 
     * @param score The score value
     * @param nickname Player's nickname (3-20 characters, alphanumeric and symbols only)
     * @param gender Player's gender ("Male", "Female", "Hidden")
     * @param manifesto Player's manifesto (3-20 characters, alphanumeric and symbols only)
     * @param date Date of achievement
     */
    public HighScoreEntry(int score, String nickname, String gender, String manifesto, LocalDate date) {
        this.score = score;
        this.nickname = nickname;
        this.gender = gender;
        this.manifesto = manifesto;
        this.date = date;
    }
    
    /**
     * Constructor for legacy scores (migration from old format)
     * Creates entry with default values for unknown player data
     * 
     * @param score The score value
     */
    public HighScoreEntry(int score) {
        this(score, "unknown", "Hidden", "No manifesto", LocalDate.of(1970, 1, 1));
    }
    
    /**
     * Gets the score value of this entry.
     * 
     * @return the score achieved by the player
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Gets the nickname of the player who achieved this score.
     * 
     * @return the player's nickname (3-20 characters)
     */
    public String getNickname() {
        return nickname;
    }
    
    /**
     * Gets the gender of the player.
     * 
     * @return the player's gender ("Male", "Female", or "Hidden")
     */
    public String getGender() {
        return gender;
    }
    
    /**
     * Gets the player's manifesto message.
     * 
     * @return the manifesto text (3-20 characters)
     */
    public String getManifesto() {
        return manifesto;
    }
    
    /**
     * Gets the date when this score was achieved.
     * 
     * @return the achievement date as LocalDate
     */
    public LocalDate getDate() {
        return date;
    }
    
    /**
     * Sets the score value for this entry.
     * 
     * @param score the new score value
     */
    public void setScore(int score) {
        this.score = score;
    }
    
    /**
     * Sets the nickname of the player.
     * 
     * @param nickname the new nickname (should be 3-20 characters, alphanumeric and symbols)
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    /**
     * Sets the gender of the player.
     * 
     * @param gender the new gender ("Male", "Female", or "Hidden")
     */
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    /**
     * Sets the player's manifesto message.
     * 
     * @param manifesto the new manifesto text (should be 3-20 characters, alphanumeric and symbols)
     */
    public void setManifesto(String manifesto) {
        this.manifesto = manifesto;
    }
    
    /**
     * Sets the achievement date for this entry.
     * 
     * @param date the new achievement date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    /**
     * Compare entries by score (descending order)
     * Higher scores come first
     */
    @Override
    public int compareTo(HighScoreEntry other) {
        return Integer.compare(other.score, this.score);
    }
    
    @Override
    public String toString() {
        return String.format("%d - %s (%s) - %s - %s", 
            score, nickname, gender, date, manifesto);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HighScoreEntry other = (HighScoreEntry) obj;
        return score == other.score && 
               nickname.equals(other.nickname) &&
               date.equals(other.date);
    }
    
    @Override
    public int hashCode() {
        return score * 31 + nickname.hashCode() + date.hashCode();
    }
}
