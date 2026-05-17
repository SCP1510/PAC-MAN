//Maneja el ciclo principal del juego
package com.juego.pacman.Logic;

import com.juego.pacman.Model.GameMap;
import com.juego.pacman.Model.PacMan;

import com.juego.pacman.Model.Ghosts.Blinky;
import com.juego.pacman.Model.Ghosts.Clyde;
import com.juego.pacman.Model.Ghosts.Inky;
import com.juego.pacman.Model.Ghosts.Pinky;
import com.juego.pacman.Model.Ghosts.Ghost;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class GameLoop extends AnimationTimer {

    //se necesita ejecutar en cada frame
    private final GraphicsContext gc;

    private final PacMan pacman;

    private final Blinky blinky;
    private final Pinky pinky;
    private final Inky inky;
    private final Clyde clyde;

    private final Image mapImage;

    private final Image pelletImage;
    private final Image powerPelletImage;

    // tamaño de ventana
    private final int SCALE = 2;

    public GameLoop(
            GraphicsContext gc,
            PacMan pacman,
            Blinky blinky,
            Pinky pinky,
            Inky inky,
            Clyde clyde
    ) {

        this.gc = gc;

        this.pacman = pacman;

        this.blinky = blinky;
        this.pinky = pinky;
        this.inky = inky;
        this.clyde = clyde;

        // mapa
        var mapUrl = getClass().getResource("/assets/map/map-0.png");

        if (mapUrl == null) {
            throw new RuntimeException("No se encontró map-0.png");
        }

        mapImage = new Image(mapUrl.toExternalForm());

        // pellet normal
        var pelletUrl = getClass().getResource("/assets/pellets/pellet.png");

        if (pelletUrl == null) {
            throw new RuntimeException("No se encontró pellet.png");
        }

        pelletImage = new Image(pelletUrl.toExternalForm());

        // power pellet
        var powerUrl = getClass().getResource("/assets/pellets/pelletpowerup.png");

        if (powerUrl == null) {
            throw new RuntimeException("No se encontró pelletpowerup.png");
        }

        powerPelletImage = new Image(powerUrl.toExternalForm());
    }

    @Override
    public void handle(long now) {

        update(now);

        render();
    }

    private void update(long now) {//actualiza la logica

        pacman.update(now);

        blinky.update(now);
        pinky.update(now);
        inky.update(now);
        clyde.update(now);
    }

    private void render() {//dibuja los elementos

        double width = GameMap.getCols() * GameMap.TILE_SIZE;

        double height = GameMap.getRows() * GameMap.TILE_SIZE;

        // limpiar pantalla
        gc.clearRect(0, 0, width * SCALE, height * SCALE);

        // dibujar mapa
        gc.drawImage(
                mapImage,
                0,
                0,
                width * SCALE,
                height * SCALE
        );

        drawPellets();

        drawPacman();

        drawGhost(blinky);
        drawGhost(pinky);
        drawGhost(inky);
        drawGhost(clyde);

        // color score
        gc.setFill(Color.WHITE);

        gc.fillText(
                "Score: " + pacman.getScore(),
                20,
                20
        );
    }

    private void drawPacman() {

        double size = pacman.getRenderSize() * SCALE;

        gc.save();

        gc.translate(
                (pacman.getX() * SCALE) + size / 2,
                (pacman.getY() * SCALE) + size / 2
        );

        gc.rotate(pacman.getAngle());

        gc.drawImage(
                pacman.getCurrentFrame(),
                -size / 2,
                -size / 2,
                size,
                size
        );

        gc.restore();
    }

    private void drawGhost(Ghost ghost) {

        double size = ghost.getRenderSize() * SCALE;

        gc.drawImage(
                ghost.getCurrentFrame(),
                ghost.getX() * SCALE,
                ghost.getY() * SCALE,
                size,
                size
        );
    }

    private void drawPellets() {

        for (int row = 0; row < GameMap.getRows(); row++) {

            for (int col = 0; col < GameMap.getCols(); col++) {

                int tile = GameMap.getTile(row, col);

                double x = col * GameMap.TILE_SIZE * SCALE;

                double y = row * GameMap.TILE_SIZE * SCALE;

                // pellet normal
                if (tile == 2) {

                    gc.drawImage(
                            pelletImage,
                            x + (GameMap.TILE_SIZE * SCALE * 0.35),
                            y + (GameMap.TILE_SIZE * SCALE * 0.35),
                            GameMap.TILE_SIZE * SCALE * 0.3,
                            GameMap.TILE_SIZE * SCALE * 0.3
                    );
                }

                // power pellet
                if (tile == 3) {

                    gc.drawImage(
                            powerPelletImage,
                            x + (GameMap.TILE_SIZE * SCALE * 0.2),
                            y + (GameMap.TILE_SIZE * SCALE * 0.2),
                            GameMap.TILE_SIZE * SCALE * 0.6,
                            GameMap.TILE_SIZE * SCALE * 0.6
                    );
                }
            }
        }
    }
}