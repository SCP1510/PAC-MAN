package com.juego.pacman;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;

public class SoundManager {

    private static MediaPlayer gameplayPlayer;
    private static MediaPlayer startPlayer;

    private static MediaPlayer wakaPlayer;
    private static MediaPlayer powerPlayer;
    private static MediaPlayer eatGhostPlayer;
    private static MediaPlayer returnPlayer;
    private static MediaPlayer deathPlayer;
    private static MediaPlayer fruitPlayer;

    private static boolean returnPlaying = false;
    private static long    lastWakaTime  = 0;
    private static boolean startLooping  = false;

    static {
        gameplayPlayer  = load("/assets/sounds/17. Game Play.mp3");
        startPlayer     = load("/assets/sounds/02. Start Music.mp3");
        wakaPlayer      = load("/assets/sounds/PacmanWakaWaka04.wav");
        powerPlayer     = load("/assets/sounds/12. Ghost - Turn to Blue.mp3");
        eatGhostPlayer  = load("/assets/sounds/13. PAC-MAN - Eating The Ghost.mp3");
        returnPlayer    = load("/assets/sounds/14. Ghost - Return to Home.mp3");
        deathPlayer     = load("/assets/sounds/15. Fail.mp3");
        fruitPlayer     = load("/assets/sounds/11. PAC-MAN - Eating The Fruit.mp3");

        if (gameplayPlayer != null) {
            gameplayPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            gameplayPlayer.setVolume(0.18);
        }

        if (startPlayer    != null) startPlayer.setVolume(0.22);
        if (wakaPlayer     != null) wakaPlayer.setVolume(0.08);
        if (powerPlayer    != null) powerPlayer.setVolume(0.16);
        if (eatGhostPlayer != null) eatGhostPlayer.setVolume(0.18);

        if (returnPlayer != null) {
            returnPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            returnPlayer.setVolume(0.10);
        }

        if (deathPlayer != null) deathPlayer.setVolume(0.22);
        if (fruitPlayer != null) fruitPlayer.setVolume(0.14);
    }

    private static MediaPlayer load(String path) {
        try {
            URL url = SoundManager.class.getResource(path);
            if (url == null) return null;
            Media media = new Media(url.toURI().toString());
            return new MediaPlayer(media);
        } catch (Exception e) {
            System.err.println("No se pudo cargar: " + path);
            return null;
        }
    }

    private static void playOnce(MediaPlayer player) {
        if (player == null) return;
        player.stop();
        player.seek(Duration.ZERO);
        player.play();
    }

    public static void playStart() {
        stopGameplay();
        if (startPlayer == null) return;
        if (startLooping) return;

        startLooping = true;
        startPlayer.setOnEndOfMedia(() -> {
            if (startLooping) {
                startPlayer.seek(Duration.ZERO);
                startPlayer.play();
            }
        });
        startPlayer.seek(Duration.ZERO);
        startPlayer.play();
    }

    public static void stopStart() {
        startLooping = false;
        if (startPlayer != null) startPlayer.stop();
    }

    public static void playGameplay() {
        stopStart();
        if (gameplayPlayer == null) return;

        if (gameplayPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }

        gameplayPlayer.play();
    }

    public static void stopGameplay() {
        if (gameplayPlayer != null) gameplayPlayer.stop();
    }

    public static void playWaka() {
        if (wakaPlayer == null) return;

        wakaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        if (wakaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }

        wakaPlayer.play();
    }

    public static void stopWaka() {
        if (wakaPlayer != null) {
            wakaPlayer.stop();
            wakaPlayer.seek(Duration.ZERO);
        }
    }

    public static void playPowerPellet() { playOnce(powerPlayer); }
    public static void playGhostEaten()  { playOnce(eatGhostPlayer); }

    public static void startReturn() {
        if (returnPlayer == null || returnPlaying) return;
        returnPlayer.play();
        returnPlaying = true;
    }

    public static void stopReturn() {
        if (returnPlayer != null) returnPlayer.stop();
        returnPlaying = false;
    }

    public static void playDeath() {
        stopGameplay();
        stopReturn();
        stopWaka();
        playOnce(deathPlayer);
    }

    public static void playFruitEaten() { playOnce(fruitPlayer); }

    public static void stopAll() {
        stopGameplay();
        stopStart();
        stopReturn();
        stopWaka();

        if (powerPlayer    != null) powerPlayer.stop();
        if (eatGhostPlayer != null) eatGhostPlayer.stop();
        if (deathPlayer    != null) deathPlayer.stop();
        if (fruitPlayer    != null) fruitPlayer.stop();
    }
}