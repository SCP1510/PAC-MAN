package com.juego.pacman;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class WinScreen {

    private final Scene scene;

    public WinScreen(double width, double height, int score, Runnable onNextLevel, Runnable onMenu) {
        // detener sonidos molestos
        SoundManager.stopWaka();
        SoundManager.stopReturn();

        // musica fondo
        SoundManager.playGameplay();

        Text win = new Text("YOU  WIN!");
        win.setFill(Color.YELLOW);
        win.setFont(Font.font("Monospace", FontWeight.BOLD, 52));

        Text scoreText = new Text("SCORE:  " + score);
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font("Monospace", FontWeight.BOLD, 22));

        Text continueText = new Text("PRESS  N  FOR  NEXT  LEVEL");
        continueText.setFill(Color.LIME);
        continueText.setFont(Font.font("Monospace", FontWeight.BOLD, 16));

        Text menuText = new Text("PRESS  M  FOR  MENU");
        menuText.setFill(Color.color(0.6, 0.6, 0.6));
        menuText.setFont(Font.font("Monospace", 12));

        VBox root = new VBox(20, win, scoreText, continueText, menuText);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(width, height);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        Timeline blink = new Timeline(
                new KeyFrame(Duration.millis(600), e -> continueText.setVisible(!continueText.isVisible()))
        );
        blink.setCycleCount(Timeline.INDEFINITE);
        blink.play();

        scene = new Scene(root, width, height);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.N) {
                blink.stop();
                SoundManager.stopGameplay();
                onNextLevel.run();
            }

            if (e.getCode() == KeyCode.M) {
                blink.stop();
                SoundManager.stopGameplay();
                onMenu.run();
            }
        });
    }

    public Scene getScene() {
        return scene;
    }
}