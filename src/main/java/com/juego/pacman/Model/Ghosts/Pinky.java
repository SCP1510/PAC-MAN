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
        // 1. MODO FRIGHTENED: Si está asustada, usamos la lógica aleatoria de la clase padre
        if (frightened) {
            super.chooseDirection();
            return;
        }

        // 2. Obtener solo los caminos que NO son paredes
        java.util.List<int[]> possibleDirs = getPossibleDirections();
        if (possibleDirs.isEmpty()) return;

        // 3. Evitar que dé media vuelta (reversa)
        java.util.List<int[]> filtered = new java.util.ArrayList<>();
        for (int[] dir : possibleDirs) {
            if (!(dir[0] == -dx && dir[1] == -dy)) {
                filtered.add(dir);
            }
        }
        if (!filtered.isEmpty()) {
            possibleDirs = filtered;
        }

        // 4. Calcular el objetivo de Pinky (4 casillas adelante de Pacman)
        // Usamos el dx y dy de PACMAN para saber hacia dónde mira él
        double targetX = pacman.getX() + pacman.getDx() * 4 * GameMap.TILE_SIZE;
        double targetY = pacman.getY() + pacman.getDy() * 4 * GameMap.TILE_SIZE;

        double bestDistance = Double.MAX_VALUE;
        int bestDx = dx;
        int bestDy = dy;

        // 5. De las opciones REALES y sin paredes, elegimos la que nos acerque más al objetivo
        for (int[] dir : possibleDirs) {
            double futureX = x + dir[0] * GameMap.TILE_SIZE;
            double futureY = y + dir[1] * GameMap.TILE_SIZE;

            double distance = Math.hypot(targetX - futureX, targetY - futureY);

            if (distance < bestDistance) {
                bestDistance = distance;
                bestDx = dir[0];
                bestDy = dir[1];
            }
        }

        // 6. Asignar los siguientes movimientos seguros que encontramos
        nextDx = bestDx;
        nextDy = bestDy;
    }
}