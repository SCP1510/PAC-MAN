package com.juego.pacman.Model.Ghosts;

import javafx.scene.image.Image;

public class Pinky extends Ghost {

    public Pinky(double x, double y) {

        super(x, y);

        rightFrames = new Image[]{
                new Image(getClass().getResource(
                        "/assets/ghost/pinky/right1.png"
                ).toExternalForm()),

                new Image(getClass().getResource(
                        "/assets/ghost/pinky/right2.png"
                ).toExternalForm())
        };

        leftFrames = new Image[]{
                new Image(getClass().getResource(
                        "/assets/ghost/pinky/left1.png"
                ).toExternalForm()),

                new Image(getClass().getResource(
                        "/assets/ghost/pinky/left2.png"
                ).toExternalForm())
        };

        upFrames = new Image[]{
                new Image(getClass().getResource(
                        "/assets/ghost/pinky/up1.png"
                ).toExternalForm()),

                new Image(getClass().getResource(
                        "/assets/ghost/pinky/up2.png"
                ).toExternalForm())
        };

        downFrames = new Image[]{
                new Image(getClass().getResource(
                        "/assets/ghost/pinky/down1.png"
                ).toExternalForm()),

                new Image(getClass().getResource(
                        "/assets/ghost/pinky/down2.png"
                ).toExternalForm())
        };
    }
}