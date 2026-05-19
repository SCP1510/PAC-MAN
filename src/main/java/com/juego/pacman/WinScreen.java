package com.juego.pacman;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
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

    public WinScreen(double width, double height, int score, Runnable onPlayAgain) {

        // actualizar récord
        boolean isNewBest = HighScore.submit(score);
        int     bestScore = HighScore.get();

        // título
        Text youWin = new Text("YOU  WIN!");
        youWin.setFill(Color.YELLOW);
        youWin.setFont(Font.font("Monospace", FontWeight.BOLD, 58));

        // subtítulo
        Text sub = new Text("¡Completaste los 2 niveles!");
        sub.setFill(Color.ORANGE);
        sub.setFont(Font.font("Monospace", 12));

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

        // jugar de nuevo (parpadeo)
        Text playAgain = new Text("PRESS  R  TO  PLAY  AGAIN");
        playAgain.setFill(Color.CYAN);
        playAgain.setFont(Font.font("Monospace", FontWeight.BOLD, 16));

        // layout
        VBox root = new VBox(18, youWin, sub, scoreText, highScoreText, playAgain);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(width, height);
        root.setBackground(
                new Background(new BackgroundFill(Color.BLACK, null, null))
        );

        // animación pulsante
        ScaleTransition pulse = new ScaleTransition(Duration.millis(800), youWin);
        pulse.setFromX(1.0);
        pulse.setToX(1.1);
        pulse.setFromY(1.0);
        pulse.setToY(1.1);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(ScaleTransition.INDEFINITE);
        pulse.play();

        // parpadeo
        Timeline blink = new Timeline(
                new KeyFrame(Duration.millis(600),
                        e -> playAgain.setVisible(!playAgain.isVisible()))
        );
        blink.setCycleCount(Timeline.INDEFINITE);
        blink.play();

        scene = new Scene(root, width, height);

        scene.setOnKeyPressed(e -> {

            if (e.getCode() == KeyCode.R) {

                pulse.stop();
                blink.stop();
                onPlayAgain.run();
            }
        });
    }

    public Scene getScene() {
        return scene;
    }
}