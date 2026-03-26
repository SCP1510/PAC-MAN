package com.juego.pacman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main_pacman extends Application {

    @Override
    public void start(Stage stage) {
        Game game = new Game();

        Scene scene = new Scene(game.getRoot(), 800, 800);

        stage.setTitle("Pac-Man");
        stage.setScene(scene);
        stage.show();

        game.start(scene); // importante
    }

    public static void main(String[] args) {
        launch();
    }
}