package com.tron.audio;

import java.util.HashMap;
import java.util.Map;

import com.tron.config.AudioSettings;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * AudioManager - Centralized audio management system
 * 
 * Responsibilities:
 * - Load and manage all game audio resources
 * - Play sound effects and background music
 * - Handle audio lifecycle (pause, resume, stop)
 * - Prevent memory leaks with proper cleanup
 * 
 * Design Pattern: Singleton Pattern
 * - Ensures single audio manager instance across application
 * - Centralized control of all audio playback
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 */
public class AudioManager {
    
    private static AudioManager instance;
    
    /**
     * Sound effect types
     */
    public enum SoundEffect {
        CLICK,      // Button clicks
        LOSE,       // Game over / death
        WIN,        // Level complete / victory
        PAUSE,      // Pause game
        UNPAUSE,    // Resume game
        PICKUP      // Collect power-up
    }
    
    // Audio resources
    private Map<SoundEffect, Media> soundEffects;
    private MediaPlayer bgmPlayer;
    private Media bgmMedia;
    
    // Settings reference
    private AudioSettings audioSettings;
    
    // State tracking (deprecated - use AudioSettings instead)
    private boolean sfxEnabled = true;
    private boolean musicEnabled = true;
    private double sfxVolume = 0.7;
    private double musicVolume = 0.5;
    private boolean isGameActive = false; // Track if game is currently running
    
    /**
     * Private constructor (Singleton)
     */
    private AudioManager() {
        soundEffects = new HashMap<>();
        audioSettings = AudioSettings.getInstance();
        loadAudioResources();
    }
    
    /**
     * Get singleton instance
     * @return the singleton AudioManager instance
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    /**
     * Load all audio resources
     */
    private void loadAudioResources() {
        try {
            // Load sound effects
            soundEffects.put(SoundEffect.CLICK, 
                new Media(getClass().getResource("/sound/click.mp3").toString()));
            soundEffects.put(SoundEffect.LOSE, 
                new Media(getClass().getResource("/sound/lose.mp3").toString()));
            soundEffects.put(SoundEffect.WIN, 
                new Media(getClass().getResource("/sound/win.mp3").toString()));
            soundEffects.put(SoundEffect.PAUSE, 
                new Media(getClass().getResource("/sound/pause.mp3").toString()));
            soundEffects.put(SoundEffect.UNPAUSE, 
                new Media(getClass().getResource("/sound/unpause.mp3").toString()));
            soundEffects.put(SoundEffect.PICKUP, 
                new Media(getClass().getResource("/sound/pickup.mp3").toString()));
            
            // Load BGM
            bgmMedia = new Media(getClass().getResource("/sound/bgm.mp3").toString());
            
        } catch (Exception e) {
            System.err.println("Failed to load audio resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Play sound effect
     * Creates new MediaPlayer for each play (allows overlapping)
     * @param effect the sound effect to play
     */
    public void playSoundEffect(SoundEffect effect) {
        // Check AudioSettings first, then fall back to legacy flag
        if (!audioSettings.isSoundEffectsEnabled() || !sfxEnabled || !soundEffects.containsKey(effect)) {
            return;
        }
        
        try {
            MediaPlayer player = new MediaPlayer(soundEffects.get(effect));
            player.setVolume(sfxVolume);
            player.setOnEndOfMedia(() -> player.dispose()); // Cleanup after playing
            player.play();
        } catch (Exception e) {
            System.err.println("Failed to play sound effect: " + effect);
        }
    }
    
    /**
     * Start background music (looping)
     */
    public void startBGM() {
        // Check AudioSettings first, then fall back to legacy flag
        if (!audioSettings.isBgmEnabled() || !musicEnabled || bgmMedia == null) {
            return;
        }
        
        try {
            if (bgmPlayer != null) {
                bgmPlayer.stop();
                bgmPlayer.dispose();
            }
            
            bgmPlayer = new MediaPlayer(bgmMedia);
            bgmPlayer.setVolume(musicVolume);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop forever
            bgmPlayer.play();
            isGameActive = true;
            
        } catch (Exception e) {
            System.err.println("Failed to start BGM: " + e.getMessage());
        }
    }
    
    /**
     * Pause background music
     */
    public void pauseBGM() {
        if (bgmPlayer != null) {
            bgmPlayer.pause();
        }
    }
    
    /**
     * Resume background music
     */
    public void resumeBGM() {
        if (bgmPlayer != null && audioSettings.isBgmEnabled() && musicEnabled) {
            bgmPlayer.play();
        }
    }
    
    /**
     * Stop background music
     */
    public void stopBGM() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
        }
        isGameActive = false;
    }
    
    /**
     * Enable/disable sound effects
     * @param enabled true to enable, false to disable
     */
    public void setSFXEnabled(boolean enabled) {
        this.sfxEnabled = enabled;
    }
    
    /**
     * Enable/disable music
     * @param enabled true to enable, false to disable
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled && bgmPlayer != null) {
            bgmPlayer.pause();
        } else if (enabled && bgmPlayer != null) {
            bgmPlayer.play();
        }
    }
    
    /**
     * Enable/disable BGM from settings (runtime toggle)
     * @param enabled true to enable, false to disable
     */
    public void setBgmEnabledFromSettings(boolean enabled) {
        if (!enabled && bgmPlayer != null) {
            bgmPlayer.stop();
            isGameActive = false;
        } else if (enabled && isGameActive) {
            // Only restart if game was active
            startBGM();
        }
    }
    
    /**
     * Enable/disable sound effects from settings (runtime toggle)
     * @param enabled true to enable, false to disable
     */
    public void setSoundEffectsEnabledFromSettings(boolean enabled) {
        // Settings are checked in playSoundEffect method
        // This method exists for future extensibility
    }
    
    /**
     * Set sound effects volume (0.0 to 1.0)
     * @param volume the volume level
     */
    public void setSFXVolume(double volume) {
        this.sfxVolume = Math.max(0.0, Math.min(1.0, volume));
    }
    
    /**
     * Set music volume (0.0 to 1.0)
     * @param volume the volume level
     */
    public void setMusicVolume(double volume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, volume));
        if (bgmPlayer != null) {
            bgmPlayer.setVolume(musicVolume);
        }
    }
    
    /**
     * Cleanup all audio resources
     */
    public void dispose() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer.dispose();
        }
        soundEffects.clear();
    }
}
