package com.juego.pacman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main_pacman extends Application {
    @Override
    public void start(Stage stage) {

        Game game = new Game();

        Scene scene = new Scene(game.getRoot());

        stage.setScene(scene);
        stage.setTitle("Pac-Man");
        stage.sizeToScene();
        stage.setResizable(false);

        stage.show();

        game.start(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}