//Maneja el ciclo principal del juego
package com.juego.pacman.Logic;

import com.juego.pacman.Model.GameMap;
import com.juego.pacman.Model.PacMan;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class GameLoop extends AnimationTimer {

    //se necesita ejecutar en cada frame
    private GraphicsContext gc;
    private PacMan pacman;

    private Image mapImage;

    // tamañp de ventana
    private final int SCALE = 2;

    public GameLoop(GraphicsContext gc, PacMan pacman) {
        this.gc = gc;
        this.pacman = pacman;

        // mapa
        var url = getClass().getResource("/assets/map/map-0.png");

        if (url == null) {
            throw new RuntimeException("No se encontró map-0.png en resources/assets/map/");
        }

        mapImage = new Image(url.toExternalForm());
    }

    @Override
    public void handle(long now) {
        update(now);
        render();
    }

    private void update(long now) {//actualiza la logica
        pacman.update(now);
    }

    private void render() {//dibuja los elementos

        double width = GameMap.getCols() * GameMap.TILE_SIZE;
        double height = GameMap.getRows() * GameMap.TILE_SIZE;

        // limpiar pantalla
        gc.clearRect(0, 0, width * SCALE, height * SCALE);

        // dibujar el mapa
        gc.drawImage(
                mapImage,
                0,
                0,
                width * SCALE,
                height * SCALE
        );

        // pellets
        drawPellets();

        // pacman
        drawPacman();

        // score
        gc.fillText(
                "Score: " + pacman.getScore(),
                20,
                20
        );
    }

    private void drawPacman() {

        double size = pacman.getRenderSize() * SCALE;

        gc.save();

        // posición escalada
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

    private void drawPellets() {

        for (int row = 0; row < GameMap.getRows(); row++) {

            for (int col = 0; col < GameMap.getCols(); col++) {

                int tile = GameMap.getTile(row, col);

                double x = col * GameMap.TILE_SIZE * SCALE;
                double y = row * GameMap.TILE_SIZE * SCALE;

                // pellet normal
                if (tile == 2) {

                    gc.fillOval(
                            x + SCALE * 3,
                            y + SCALE * 3,
                            SCALE * 2,
                            SCALE * 2
                    );
                }

                // power pellet
                if (tile == 3) {

                    gc.fillOval(
                            x + SCALE * 2,
                            y + SCALE * 2,
                            SCALE * 4,
                            SCALE * 4
                    );
                }
            }
        }
    }
}