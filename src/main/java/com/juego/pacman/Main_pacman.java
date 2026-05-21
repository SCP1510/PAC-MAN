//Clase que lanza el codigo
package com.juego.pacman;

import com.juego.pacman.Model.GameMap;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main_pacman extends Application {

    // tamañp de ventana
    private static final int SCALE = 2;

    // referencias estáticas para cambio de escena
    private static Stage primaryStage;
    private static Game  currentGame;

    @Override
    public void start(Stage stage) {

        primaryStage = stage;

        stage.setTitle("Pac-Man");
        stage.setResizable(false);

        showStartScreen();

        stage.show();
    }

    // pantalla de inicio

    public static void showStartScreen() {

        double width  = GameMap.getCols() * GameMap.TILE_SIZE * SCALE;
        double height = GameMap.getRows() * GameMap.TILE_SIZE * SCALE;

        StartScreen screen = new StartScreen(width, height, Main_pacman::showGame);

        primaryStage.setScene(screen.getScene());
    }

    // iniciar o detener el juego

    public static void showGame() {

        // detener juego anterior
        if (currentGame != null) currentGame.stop();

        // resetear mapa al estado original
        GameMap.resetMap();

        double width  = GameMap.getCols() * GameMap.TILE_SIZE * SCALE;
        double height = GameMap.getRows() * GameMap.TILE_SIZE * SCALE;

        currentGame = new Game(
                Main_pacman::showWinScreen,
                Main_pacman::showLoseScreen
        );

        Scene scene = new Scene(currentGame.getRoot(), width, height);

        primaryStage.setScene(scene);

        currentGame.start(scene);
    }

    // win screen

    // ======== pantalla de victoria ========

    public static void showWinScreen() {

        int score = currentGame != null ? currentGame.getScore() : 0;

        double width  =
                GameMap.getCols()
                        * GameMap.TILE_SIZE
                        * SCALE;

        double height =
                GameMap.getRows()
                        * GameMap.TILE_SIZE
                        * SCALE;

        WinScreen screen = new WinScreen(
                width,
                height,
                score,
                Main_pacman::showGame,
                Main_pacman::showStartScreen
        );

        primaryStage.setScene(screen.getScene());
    }

    // lose screen

    public static void showLoseScreen() {

        int score = currentGame != null ? currentGame.getScore() : 0;

        double width  = GameMap.getCols() * GameMap.TILE_SIZE * SCALE;
        double height = GameMap.getRows() * GameMap.TILE_SIZE * SCALE;

        LoseScreen screen = new LoseScreen(
                width,
                height,
                score,
                Main_pacman::showGame,
                Main_pacman::showStartScreen
        );

        primaryStage.setScene(screen.getScene());
    }

    public static void main(String[] args) {
        launch();
    }
}