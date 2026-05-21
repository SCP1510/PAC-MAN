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

public class StartScreen {

    private final Scene scene;

    public StartScreen(double width, double height, Runnable onStart) {
        SoundManager.playGameplay();
        // título
        Text title = new Text("PAC-MAN");
        title.setFill(Color.YELLOW);
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 60));

        // crédito retro
        Text credit = new Text("© 1980 NAMCO LTD.");
        credit.setFill(Color.ORANGE);
        credit.setFont(Font.font("Monospace", 12));

        // texto parpadeo
        Text pressEnter = new Text("PRESS  ENTER  TO  START");
        pressEnter.setFill(Color.WHITE);
        pressEnter.setFont(Font.font("Monospace", FontWeight.BOLD, 16));

        // instrucciones
        Text controls = new Text("WASD / FLECHAS  PARA  MOVER");
        controls.setFill(Color.CYAN);
        controls.setFont(Font.font("Monospace", 12));

        // hint
        Text lvl2hint = new Text("— Recoge frutas para obtener poderes —");
        lvl2hint.setFill(Color.color(0.6, 0.6, 0.6));
        lvl2hint.setFont(Font.font("Monospace", 10));

        // layout
        VBox root = new VBox(20, title, credit, pressEnter, controls, lvl2hint);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(width, height);
        root.setBackground(
                new Background(new BackgroundFill(Color.BLACK, null, null))
        );

        // animación de parpadeo
        Timeline blink = new Timeline(
                new KeyFrame(Duration.millis(600),
                        e -> pressEnter.setVisible(!pressEnter.isVisible()))
        );
        blink.setCycleCount(Timeline.INDEFINITE);
        blink.play();

        scene = new Scene(root, width, height);

        scene.setOnKeyPressed(e -> {

            if (e.getCode() == KeyCode.ENTER) {
                blink.stop();
                SoundManager.stopGameplay();
                SoundManager.playStart();
                onStart.run();
            }
        });
    }

    public Scene getScene() {
        return scene;
    }
}