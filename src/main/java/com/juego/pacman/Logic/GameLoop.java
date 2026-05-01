package com.juego.pacman.Logic;

import com.juego.pacman.Model.GameMap;
import com.juego.pacman.Model.PacMan;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class GameLoop extends AnimationTimer {

    private GraphicsContext gc;
    private PacMan pacman;

    private Image mapImage;

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

    private void update(long now) {
        pacman.update(now);
    }

    private void render() {

        double width = GameMap.getCols() * GameMap.TILE_SIZE;
        double height = GameMap.getRows() * GameMap.TILE_SIZE;

        gc.clearRect(0, 0, width, height);

        // dibujar el mapa
        gc.drawImage(
                mapImage,
                0, 0,
                width,
                height
        );
        //dibuja el pac man
        drawPacman();
    }

    private void drawPacman() {

        double size = GameMap.TILE_SIZE;

        gc.save();

        gc.translate(pacman.getX() + size / 2, pacman.getY() + size / 2);
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
}