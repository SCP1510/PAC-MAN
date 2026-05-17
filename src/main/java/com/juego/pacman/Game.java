//Inicializa TODOS los componentes del juego
package com.juego.pacman;

import com.juego.pacman.Logic.GameLoop;
import com.juego.pacman.Model.GameMap;
import com.juego.pacman.Model.PacMan;

import com.juego.pacman.Model.Ghosts.Blinky;
import com.juego.pacman.Model.Ghosts.Clyde;
import com.juego.pacman.Model.Ghosts.Inky;
import com.juego.pacman.Model.Ghosts.Pinky;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Game {

    private final Group root;

    private final Canvas canvas;

    private final GraphicsContext gc;

    private final PacMan pacman;

    private final Blinky blinky;
    private final Pinky pinky;
    private final Inky inky;
    private final Clyde clyde;

    // escala
    private final int SCALE = 2;

    public Game() {//inicia el ciclo del juego

        int width = GameMap.getCols() * GameMap.TILE_SIZE * SCALE;
        int height = GameMap.getRows() * GameMap.TILE_SIZE * SCALE;

        root = new Group();

        canvas = new Canvas(width, height);

        gc = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);

        pacman = new PacMan();

        // fantasmas
        blinky = new Blinky(
                13 * GameMap.TILE_SIZE,
                11 * GameMap.TILE_SIZE
        );

        pinky = new Pinky(
                12 * GameMap.TILE_SIZE,
                14 * GameMap.TILE_SIZE
        );

        inky = new Inky(
                14 * GameMap.TILE_SIZE,
                14 * GameMap.TILE_SIZE
        );

        clyde = new Clyde(
                15 * GameMap.TILE_SIZE,
                14 * GameMap.TILE_SIZE
        );
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
        GameLoop loop = new GameLoop(
                gc,
                pacman,
                blinky,
                pinky,
                inky,
                clyde
        );

        loop.start();
    }

    public Group getRoot() {
        return root;
    }
}