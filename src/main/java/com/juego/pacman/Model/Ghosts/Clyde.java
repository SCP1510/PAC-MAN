package com.juego.pacman.Model.Ghosts;

import com.juego.pacman.Model.GameMap;
import com.juego.pacman.Model.PacMan;
import javafx.scene.image.Image;

public class Clyde extends Ghost {

    public Clyde(double x, double y, PacMan pacman) {
        super(x, y, pacman, 6_000_000_000L);

        // clyde: cobarde / random
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
        // si esta asustado, usa logica aleatoria base
        if (frightened) {
            super.chooseDirection();
            return;
        }

        // caminos sin paredes
        java.util.List<int[]> possibleDirs = getPossibleDirections();
        if (possibleDirs.isEmpty()) return;

        // filtrar reversa
        java.util.List<int[]> filtered = new java.util.ArrayList<>();
        for (int[] dir : possibleDirs) {
            if (!(dir[0] == -dx && dir[1] == -dy)) filtered.add(dir);
        }
        if (!filtered.isEmpty()) possibleDirs = filtered;

        // distancia actual a pacman en pixeles
        double distance = Math.hypot(pacman.getX() - x, pacman.getY() - y);

        int bestDx = dx;
        int bestDy = dy;

        // si esta cerca huye, si no persigue
        if (distance < 6 * GameMap.TILE_SIZE) {
            // modo huida: maximizar distancia
            double maxDistance = -1;
            for (int[] dir : possibleDirs) {
                double futureX = x + dir[0] * GameMap.TILE_SIZE;
                double futureY = y + dir[1] * GameMap.TILE_SIZE;
                double dist = Math.hypot(pacman.getX() - futureX, pacman.getY() - futureY);
                if (dist > maxDistance) {
                    maxDistance = dist;
                    bestDx = dir[0];
                    bestDy = dir[1];
                }
            }
        } else {
            // modo persiguiendo (logica base)
            double minDistance = Double.MAX_VALUE;
            for (int[] dir : possibleDirs) {
                double futureX = x + dir[0] * GameMap.TILE_SIZE;
                double futureY = y + dir[1] * GameMap.TILE_SIZE;
                double dist = Math.hypot(pacman.getX() - futureX, pacman.getY() - futureY);
                if (dist < minDistance) {
                    minDistance = dist;
                    bestDx = dir[0];
                    bestDy = dir[1];
                }
            }
        }

        nextDx = bestDx;
        nextDy = bestDy;
    }
}