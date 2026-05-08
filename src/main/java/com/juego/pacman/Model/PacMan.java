//Clase que representa al jugador osease Pac-Man
package com.juego.pacman.Model;

import javafx.scene.image.Image;

public class PacMan {

    private double x = 13 * GameMap.TILE_SIZE;
    private double y = 23 * GameMap.TILE_SIZE;

    private final double speed = 0.7;

    // separar tamaño visual y colisión
    private final double renderSize = GameMap.TILE_SIZE;
    private final double hitboxSize = GameMap.TILE_SIZE * 0.5;

    // constantes del túnel
    private static final int TUNNEL_TOP_ROW = 13;
    private static final int TUNNEL_BOTTOM_ROW = 15;

    private int dx = 0;
    private int dy = 0;

    private double angle = 0;
    private int score = 0;

    // Animacion
    private final Image[] frames;

    private int currentFrame = 0;
    private long lastFrameTime = 0;

    private final long frameDelay = 150_000_000;

    public PacMan() {

        frames = new Image[]{
                new Image(getClass().getResource("/assets/pac-man/pac-man1.png").toExternalForm()),
                new Image(getClass().getResource("/assets/pac-man/pac-man2.png").toExternalForm())
        };
    }

    public void update(long now) {

        double nextX = x + dx * speed;
        double nextY = y + dy * speed;

        // movimiento horizontal
        if (!isWall(nextX, y)) {
            x = nextX;
        }

        // movimiento vertical
        if (!isWall(x, nextY)) {
            y = nextY;
        }

        // túnel
        if (inTunnel()) {

            double maxWidth = GameMap.getCols() * GameMap.TILE_SIZE;

            // izquierda → derecha
            if (x < -renderSize) {

                // aparecer un poco adentro
                x = maxWidth - (GameMap.TILE_SIZE * 3);
            }

            // derecha → izquierda
            if (x > maxWidth) {

                // aparecer un poco adentro
                x = GameMap.TILE_SIZE * 2;
            }
        }

        // comer pellets
        int pelletCol = (int)((x + renderSize / 2) / GameMap.TILE_SIZE);
        int pelletRow = (int)((y + renderSize / 2) / GameMap.TILE_SIZE);

        // evitar indices inválidos
        if (pelletRow >= 0 && pelletRow < GameMap.getRows() &&
                pelletCol >= 0 && pelletCol < GameMap.getCols()) {

            score += GameMap.eatPellet(pelletRow, pelletCol);
        }

        // animacion
        if (now - lastFrameTime > frameDelay) {

            currentFrame = (currentFrame + 1) % frames.length;

            lastFrameTime = now;
        }
    }

    //Movimiento limitado
    private boolean isWall(double x, double y) {

        // usar hitbox centrado dentro del sprite
        double offset = (renderSize - hitboxSize) / 2;

        int leftCol = (int)((x + offset) / GameMap.TILE_SIZE);
        int rightCol = (int)((x + offset + hitboxSize) / GameMap.TILE_SIZE);

        int topRow = (int)((y + offset) / GameMap.TILE_SIZE);
        int bottomRow = (int)((y + offset + hitboxSize) / GameMap.TILE_SIZE);

        // límites verticales normales
        if (topRow < 0 || bottomRow >= GameMap.getRows()) {
            return true;
        }

        // permitir salir horizontalmente en túnel
        if (!inTunnel()) {

            if (leftCol < 0 || rightCol >= GameMap.getCols()) {
                return true;
            }
        }

        // si está fuera horizontalmente en túnel NO revisar tiles
        if (leftCol < 0 || rightCol >= GameMap.getCols()) {
            return false;
        }

        return GameMap.getTile(topRow, leftCol) == 1 ||
                GameMap.getTile(topRow, rightCol) == 1 ||
                GameMap.getTile(bottomRow, leftCol) == 1 ||
                GameMap.getTile(bottomRow, rightCol) == 1;
    }

    // verifica si Pac-Man está en la zona del túnel
    private boolean inTunnel() {

        int row = (int)(y / GameMap.TILE_SIZE);

        return row >= TUNNEL_TOP_ROW &&
                row <= TUNNEL_BOTTOM_ROW;
    }

    //rotaciones
    public void setDirection(int dx, int dy) {

        this.dx = dx;
        this.dy = dy;

        if (dx == 1) angle = 0;
        else if (dx == -1) angle = 180;
        else if (dy == -1) angle = 270;
        else if (dy == 1) angle = 90;
    }

    // GETTERS GameLoop
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }

    public Image getCurrentFrame() {
        return frames[currentFrame];
    }

    public int getScore() {
        return score;
    }

    // getter para tamaño visual (para dibujar bien)
    public double getRenderSize() {
        return renderSize;
    }
}