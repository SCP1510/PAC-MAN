//Inicializa TODOS los componentes del juego
package com.juego.pacman;

import com.juego.pacman.Model.PacMan;
import com.juego.pacman.Logic.GameLoop;
import com.juego.pacman.Model.GameMap;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Game {

    private Group root;
    private Canvas canvas;
    private GraphicsContext gc;

    private PacMan pacman;

    // tamañp de ventana
    private final int SCALE = 2;

    public Game() {//inicia el ciclo del juego

        int width = GameMap.getCols() * GameMap.TILE_SIZE * SCALE;
        int height = GameMap.getRows() * GameMap.TILE_SIZE * SCALE;

        root = new Group();
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);

        pacman = new PacMan();
    }

    public void start(Scene scene) {//inicia el GameLoop

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W:
                case UP:
                    pacman.setDirection(0, -1);
                    break;
                case S:
                case DOWN:
                    pacman.setDirection(0, 1);
                    break;
                case A:
                case LEFT:
                    pacman.setDirection(-1, 0);
                    break;
                case D:
                case RIGHT:
                    pacman.setDirection(1, 0);
                    break;
            }
        });

        // LOOP
        GameLoop loop = new GameLoop(gc, pacman);
        loop.start();
    }

    public Group getRoot() {
        return root;
    }
}