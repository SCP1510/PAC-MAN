package com.juego.pacman.Model.Ghosts;

import com.juego.pacman.Model.GameMap;
import com.juego.pacman.Model.PacMan;
import javafx.scene.image.Image;

public class Pinky extends Ghost {

    public Pinky(double x, double y, PacMan pacman) {

        super(x, y, pacman, 2_000_000_000L);

        speed = 2;

        rightFrames = new Image[]{
                new Image(getClass().getResource(
                        "/assets/ghost/pinky/right1.png")
                        .toExternalForm()),
                new Image(getClass().getResource(
                        "/assets/ghost/pinky/right2.png")
                        .toExternalForm())
        };

        leftFrames = new Image[]{
                new Image(getClass().getResource(
                        "/assets/ghost/pinky/left1.png")
                        .toExternalForm()),
                new Image(getClass().getResource(
                        "/assets/ghost/pinky/left2.png")
                        .toExternalForm())
        };

        upFrames = new Image[]{
                new Image(getClass().getResource(
                        "/assets/ghost/pinky/up1.png")
                        .toExternalForm()),
                new Image(getClass().getResource(
                        "/assets/ghost/pinky/up2.png")
                        .toExternalForm())
        };

        downFrames = new Image[]{
                new Image(getClass().getResource(
                        "/assets/ghost/pinky/down1.png")
                        .toExternalForm()),
                new Image(getClass().getResource(
                        "/assets/ghost/pinky/down2.png")
                        .toExternalForm())
        };
    }

    @Override
    protected void chooseDirection() {

        double targetX =
                pacman.getX() + dx * 4 * GameMap.TILE_SIZE;

        double targetY =
                pacman.getY() + dy * 4 * GameMap.TILE_SIZE;

        double diffX = targetX - x;
        double diffY = targetY - y;

        if (Math.abs(diffX) > Math.abs(diffY)) {

            nextDx = diffX > 0 ? 1 : -1;
            nextDy = 0;

        } else {

            nextDy = diffY > 0 ? 1 : -1;
            nextDx = 0;
        }
    }
}