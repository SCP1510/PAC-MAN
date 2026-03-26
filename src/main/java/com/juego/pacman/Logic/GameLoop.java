//Maneja el ciclo principal del juego
package com.juego.pacman.Logic;

import com.juego.pacman.Model.PacMan;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

public class GameLoop extends AnimationTimer {
    //se necesita ejecutafr en cada frame
    private GraphicsContext gc;
    private PacMan pacman;

    public GameLoop(GraphicsContext gc, PacMan pacman) {
        this.gc = gc;
        this.pacman = pacman;
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
        gc.clearRect(0, 0, 800, 800);

        gc.save();

        gc.translate(pacman.getX() + 16, pacman.getY() + 16);
        gc.rotate(pacman.getAngle());

        gc.drawImage(
                pacman.getCurrentFrame(),
                -16, -16,
                32, 32
        );

        gc.restore();
    }
}