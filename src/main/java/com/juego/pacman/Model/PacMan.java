package com.juego.pacman.Model;

import javafx.scene.image.Image;

public class PacMan {

    private double x = 13 * GameMap.TILE_SIZE;
    private double y = 23 * GameMap.TILE_SIZE;

    private final double speed = 2;
    private final double size = GameMap.TILE_SIZE;

    private int dx = 0;
    private int dy = 0;

    private double angle = 0;

    // Animacion
    private Image[] frames;
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

        if (!isWall(nextX, y)) {
            x = nextX;
        }

        if (!isWall(x, nextY)) {
            y = nextY;
        }

        // túnel
        int row = (int)(y / GameMap.TILE_SIZE);
        if (row == 14) { // fila del tunel

            double maxWidth = GameMap.getCols() * GameMap.TILE_SIZE;

            if (x < -size) {
                x = maxWidth;
            }

            if (x > maxWidth) {
                x = -size;
            }
        }
        // animacion
        if (now - lastFrameTime > frameDelay) {
            currentFrame = (currentFrame + 1) % frames.length;
            lastFrameTime = now;
        }
    }
    //Movimiento limitado
    private boolean isWall(double x, double y) {

        int leftCol = (int)(x / GameMap.TILE_SIZE);
        int rightCol = (int)((x + size) / GameMap.TILE_SIZE);
        int topRow = (int)(y / GameMap.TILE_SIZE);
        int bottomRow = (int)((y + size) / GameMap.TILE_SIZE);

        if (topRow < 0 || bottomRow >= GameMap.getRows() ||
                leftCol < 0 || rightCol >= GameMap.getCols()) {
            return true;
        }

        return GameMap.getTile(topRow, leftCol) == 1 ||
                GameMap.getTile(topRow, rightCol) == 1 ||
                GameMap.getTile(bottomRow, leftCol) == 1 ||
                GameMap.getTile(bottomRow, rightCol) == 1;
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

    public double getX() { return x; }
    public double getY() { return y; }
    public double getAngle() { return angle; }
    public Image getCurrentFrame() { return frames[currentFrame]; }
}