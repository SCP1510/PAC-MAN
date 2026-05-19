package com.juego.pacman.Model.Ghosts;

import com.juego.pacman.Model.PacMan;
import javafx.scene.image.Image;

public class Blinky extends Ghost {

    public Blinky(double x, double y, PacMan pacman) {

        super(x, y, pacman, 0);

        // Blinky agresivo: sale inmediatamente y es rápido
        speed       = 0.9;
        normalSpeed = 0.9;

        rightFrames = new Image[]{
                new Image(getClass().getResource("/assets/ghost/blinky/right1.png").toExternalForm()),
                new Image(getClass().getResource("/assets/ghost/blinky/right2.png").toExternalForm())
        };

        leftFrames = new Image[]{
                new Image(getClass().getResource("/assets/ghost/blinky/left1.png").toExternalForm()),
                new Image(getClass().getResource("/assets/ghost/blinky/left2.png").toExternalForm())
        };

        upFrames = new Image[]{
                new Image(getClass().getResource("/assets/ghost/blinky/up1.png").toExternalForm()),
                new Image(getClass().getResource("/assets/ghost/blinky/up2.png").toExternalForm())
        };

        downFrames = new Image[]{
                new Image(getClass().getResource("/assets/ghost/blinky/down1.png").toExternalForm()),
                new Image(getClass().getResource("/assets/ghost/blinky/down2.png").toExternalForm())
        };
    }
}