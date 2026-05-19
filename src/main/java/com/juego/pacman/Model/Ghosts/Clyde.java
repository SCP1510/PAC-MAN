package com.juego.pacman.Model.Ghosts;

import com.juego.pacman.Model.GameMap;
import com.juego.pacman.Model.PacMan;
import javafx.scene.image.Image;

public class Clyde extends Ghost {

    public Clyde(double x, double y, PacMan pacman) {

        super(x, y, pacman, 6_000_000_000L);

        // Clyde cobarde / random
        speed       = 0.8;
        normalSpeed = 0.8;

        rightFrames = new Image[]{
                new Image(getClass().getResource("/assets/ghost/clyde/right1.png").toExternalForm()),
                new Image(getClass().getResource("/assets/ghost/clyde/right2.png").toExternalForm())
        };

        leftFrames = new Image[]{
                new Image(getClass().getResource("/assets/ghost/clyde/left1.png").toExternalForm()),
                new Image(getClass().getResource("/assets/ghost/clyde/left2.png").toExternalForm())
        };

        upFrames = new Image[]{
                new Image(getClass().getResource("/assets/ghost/clyde/up1.png").toExternalForm()),
                new Image(getClass().getResource("/assets/ghost/clyde/up2.png").toExternalForm())
        };

        downFrames = new Image[]{
                new Image(getClass().getResource("/assets/ghost/clyde/down1.png").toExternalForm()),
                new Image(getClass().getResource("/assets/ghost/clyde/down2.png").toExternalForm())
        };
    }

    @Override
    protected void chooseDirection() {

        if (frightened) {

            super.chooseDirection();

            return;
        }

        double distance = Math.hypot(pacman.getX() - x, pacman.getY() - y);

        // si está cerca, huye; si está lejos, persigue
        if (distance < 6 * GameMap.TILE_SIZE) {

            double bestDist = -1;
            int bestDx = dx, bestDy = dy;

            for (int[] dir : getPossibleDirections()) {

                if (dir[0] == -dx && dir[1] == -dy) continue;

                double futureX = x + dir[0] * GameMap.TILE_SIZE;
                double futureY = y + dir[1] * GameMap.TILE_SIZE;

                double dist = Math.hypot(pacman.getX() - futureX, pacman.getY() - futureY);

                if (dist > bestDist) {

                    bestDist = dist;
                    bestDx   = dir[0];
                    bestDy   = dir[1];
                }
            }

            nextDx = bestDx;
            nextDy = bestDy;

        } else {

            super.chooseDirection();
        }
    }
}