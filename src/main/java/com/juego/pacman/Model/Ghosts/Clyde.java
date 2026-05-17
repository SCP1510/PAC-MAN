package com.juego.pacman.Model.Ghosts;

import javafx.scene.image.Image;

public class Clyde extends Ghost {

    public Clyde(double x, double y) {

        super(x, y);

        rightFrames = new Image[]{
                new Image(getClass().getResource(
                        "/assets/ghost/clyde/right1.png"
                ).toExternalForm()),

                new Image(getClass().getResource(
                        "/assets/ghost/clyde/right2.png"
                ).toExternalForm())
        };

        leftFrames = new Image[]{
                new Image(getClass().getResource(
                        "/assets/ghost/clyde/left1.png"
                ).toExternalForm()),

                new Image(getClass().getResource(
                        "/assets/ghost/clyde/left2.png"
                ).toExternalForm())
        };

        upFrames = new Image[]{
                new Image(getClass().getResource(
                        "/assets/ghost/clyde/up1.png"
                ).toExternalForm()),

                new Image(getClass().getResource(
                        "/assets/ghost/clyde/up2.png"
                ).toExternalForm())
        };

        downFrames = new Image[]{
                new Image(getClass().getResource(
                        "/assets/ghost/clyde/down1.png"
                ).toExternalForm()),

                new Image(getClass().getResource(
                        "/assets/ghost/clyde/down2.png"
                ).toExternalForm())
        };
    }
}