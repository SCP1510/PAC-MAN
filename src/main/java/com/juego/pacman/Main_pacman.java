//Clase que lanza el codigo
package com.juego.pacman;

import com.juego.pacman.Model.GameMap;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main_pacman extends Application {

    // tamañp de ventana
    private final int SCALE = 2;

    @Override
    public void start(Stage stage) {

        Game game = new Game();

        // asegura que el screen sea del tamaño adecuado (ESCALADO)
        double width = GameMap.getCols() * GameMap.TILE_SIZE * SCALE;
        double height = GameMap.getRows() * GameMap.TILE_SIZE * SCALE;

        Scene scene = new Scene(game.getRoot(), width, height);

        stage.setScene(scene);
        stage.setTitle("Pac-Man");

        // evita redimensionar
        stage.setResizable(false);

        stage.show();

        game.start(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}