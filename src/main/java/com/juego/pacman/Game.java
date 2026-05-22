package com.juego.pacman;

import com.juego.pacman.Logic.GameLoop;
import com.juego.pacman.Model.GameMap;
import com.juego.pacman.Model.Ghosts.Blinky;
import com.juego.pacman.Model.Ghosts.Clyde;
import com.juego.pacman.Model.Ghosts.Inky;
import com.juego.pacman.Model.Ghosts.Pinky;
import com.juego.pacman.Model.PacMan;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

public class Game {

    private final Group root;
    private final Canvas canvas;
    private final GraphicsContext gc;

    private final PacMan pacman;

    private final Blinky blinky;
    private final Pinky pinky;
    private final Inky inky;
    private final Clyde clyde;

    private final GameLoop loop;
    final boolean[] firstMove = { false };

    // nivel actual
    private int currentLevel = 1;

    // callbacks externos
    private final Runnable externalOnWin;
    private final Runnable externalOnLose;

    private final int SCALE = 2;

    // posiciones inicio de cada fantasma (dentro de la casa)
    private static final double BLINKY_X = 13 * GameMap.TILE_SIZE;
    private static final double BLINKY_Y = 14 * GameMap.TILE_SIZE;

    private static final double PINKY_X = 12 * GameMap.TILE_SIZE;
    private static final double PINKY_Y = 14 * GameMap.TILE_SIZE;

    private static final double INKY_X = 14 * GameMap.TILE_SIZE;
    private static final double INKY_Y = 14 * GameMap.TILE_SIZE;

    private static final double CLYDE_X = 15 * GameMap.TILE_SIZE;
    private static final double CLYDE_Y = 14 * GameMap.TILE_SIZE;

    public Game(Runnable onWin, Runnable onLose) {
        this.externalOnWin  = onWin;
        this.externalOnLose = onLose;

        root = new Group();

        canvas = new Canvas(
                GameMap.getCols() * GameMap.TILE_SIZE * SCALE,
                GameMap.getRows() * GameMap.TILE_SIZE * SCALE
        );

        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        pacman = new PacMan();

        // casa de fantasmas
        blinky = new Blinky(BLINKY_X, BLINKY_Y, pacman);
        pinky  = new Pinky (PINKY_X,  PINKY_Y,  pacman);
        inky   = new Inky  (INKY_X,   INKY_Y,   pacman);
        clyde  = new Clyde (CLYDE_X,  CLYDE_Y,  pacman);

        loop = new GameLoop(
                gc,
                pacman,
                blinky,
                pinky,
                inky,
                clyde,
                this::handleLevelComplete,
                this::handleGameOver
        );
    }

    // todos los pellets comidos
    private void handleLevelComplete() {
        if (currentLevel < 3) {
            // siguiente nivel
            currentLevel++;

            GameMap.resetMap();
            pacman.resetPosition();

            blinky.reset(BLINKY_X, BLINKY_Y, 0L);
            pinky.reset (PINKY_X,  PINKY_Y,  2_000_000_000L);
            inky.reset  (INKY_X,   INKY_Y,   4_000_000_000L);
            clyde.reset (CLYDE_X,  CLYDE_Y,  6_000_000_000L);

            loop.startLevel(currentLevel, System.nanoTime());

        } else {
            // nivel 3 completado
            loop.stop();
            externalOnWin.run();
        }
    }

    private void handleGameOver() {
        loop.stop();
        externalOnLose.run();
    }

    public void start(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode key = event.getCode();

            boolean movementKey = key == KeyCode.W || key == KeyCode.A
                    || key == KeyCode.S || key == KeyCode.D
                    || key == KeyCode.UP || key == KeyCode.DOWN
                    || key == KeyCode.LEFT || key == KeyCode.RIGHT;

            // primer movimiento real
            if (movementKey && !firstMove[0]) {
                firstMove[0] = true;
                SoundManager.stopStart();
            }

            if (key == KeyCode.W || key == KeyCode.UP)    pacman.setDirection(0, -1);
            if (key == KeyCode.S || key == KeyCode.DOWN)  pacman.setDirection(0, 1);
            if (key == KeyCode.A || key == KeyCode.LEFT)  pacman.setDirection(-1, 0);
            if (key == KeyCode.D || key == KeyCode.RIGHT) pacman.setDirection(1, 0);
        });

        loop.start();
    }

    public void stop() {
        loop.stop();
    }

    public int getScore() {
        return pacman.getScore();
    }

    public Group getRoot() {
        return root;
    }
}