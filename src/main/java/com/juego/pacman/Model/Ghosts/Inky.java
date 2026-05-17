package com.juego.pacman.Model.Ghosts;

import javafx.scene.image.Image;

public class Inky extends Ghost {

    public Inky(double x, double y) {

        super(x, y);

        rightFrames = new Image[]{
                new Image(getClass().getResource(
                        "/assets/ghost/inky/right1.png"
                ).toExternalForm()),

                new Image(getClass().getResource(
                        "/assets/ghost/inky/right2.png"
                ).toExternalForm())
        };

        leftFrames = new Image[]{
                new Image(getClass().getResource(
                        "/assets/ghost/inky/left1.png"
                ).toExternalForm()),

                new Image(getClass().getResource(
                        "/assets/ghost/inky/left2.png"
                ).toExternalForm())
        };

        upFrames = new Image[]{
                new Image(getClass().getResource(
                        "/assets/ghost/inky/up1.png"
                ).toExternalForm()),

                new Image(getClass().getResource(
                        "/assets/ghost/inky/up2.png"
                ).toExternalForm())
        };

        downFrames = new Image[]{
                new Image(getClass().getResource(
                        "/assets/ghost/inky/down1.png"
                ).toExternalForm()),

                new Image(getClass().getResource(
                        "/assets/ghost/inky/down2.png"
                ).toExternalForm())
        };
    }
}