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

public class LoseScreen {

    private final Scene scene;

    public LoseScreen(
            double width,
            double height,
            int score,
            Runnable onRetry,
            Runnable onMenu
    ) {

        // actualizar récord y saber si es nuevo
        boolean isNewBest = HighScore.submit(score);
        int     bestScore = HighScore.get();

        // título
        Text gameOver = new Text("GAME  OVER");
        gameOver.setFill(Color.RED);
        gameOver.setFont(Font.font("Monospace", FontWeight.BOLD, 52));

        // score actual
        Text scoreText = new Text("SCORE:  " + score);
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font("Monospace", FontWeight.BOLD, 22));

        // high score
        Text highScoreText;

        if (isNewBest) {

            highScoreText = new Text("★  NEW  BEST  SCORE!  ★");
            highScoreText.setFill(Color.GOLD);
            highScoreText.setFont(Font.font("Monospace", FontWeight.BOLD, 14));

        } else {

            highScoreText = new Text("BEST  SCORE:  " + bestScore);
            highScoreText.setFill(Color.color(0.7, 0.7, 0.7));
            highScoreText.setFont(Font.font("Monospace", 13));
        }

        // retry (parpadeo)
        Text retry = new Text("PRESS  R  TO  RETRY");
        retry.setFill(Color.YELLOW);
        retry.setFont(Font.font("Monospace", FontWeight.BOLD, 16));

        // menú
        Text menu = new Text("PRESS  M  FOR  MENU");
        menu.setFill(Color.color(0.6, 0.6, 0.6));
        menu.setFont(Font.font("Monospace", 12));

        // layout
        VBox root = new VBox(20, gameOver, scoreText, highScoreText, retry, menu);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(width, height);
        root.setBackground(
                new Background(new BackgroundFill(Color.BLACK, null, null))
        );

        // parpadeo
        Timeline blink = new Timeline(
                new KeyFrame(Duration.millis(600),
                        e -> retry.setVisible(!retry.isVisible()))
        );
        blink.setCycleCount(Timeline.INDEFINITE);
        blink.play();

        scene = new Scene(root, width, height);

        scene.setOnKeyPressed(e -> {

            if (e.getCode() == KeyCode.R) {

                blink.stop();
                onRetry.run();
            }

            if (e.getCode() == KeyCode.M) {

                blink.stop();
                onMenu.run();
            }
        });
    }

    public Scene getScene() {
        return scene;
    }
}