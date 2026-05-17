package com.juego.pacman.Model.Ghosts;

import com.juego.pacman.Model.GameMap;
import javafx.scene.image.Image;

import java.util.Random;

public class Ghost {

    protected double x;
    protected double y;

    protected double speed = 0.6;

    protected int dx = 1;
    protected int dy = 0;

    protected double renderSize = GameMap.TILE_SIZE;

    protected boolean frightened = false;
    protected boolean dead = false;

    protected long frightenedStart = 0;

    // sprites normales
    protected Image[] rightFrames;
    protected Image[] leftFrames;
    protected Image[] upFrames;
    protected Image[] downFrames;

    // shared frightened
    private static Image loadShared(String path) {

        var url = Ghost.class.getResource(path);

        if (url == null) {

            System.out.println("NO SE ENCONTRO: " + path);

            return null;
        }

        System.out.println("CARGADO: " + path);

        return new Image(url.toExternalForm());
    }

    private static final Image[] frightenedFrames = {
            loadShared("/assets/ghost/shared/fright1.png"),
            loadShared("/assets/ghost/shared/fright2.png")
    };

    private static final Image deadLeft =
            loadShared("/assets/ghost/shared/deadl.png");

    private static final Image deadRight =
            loadShared("/assets/ghost/shared/deadr.png");

    private static final Image deadUp =
            loadShared("/assets/ghost/shared/deadu.png");

    private static final Image deadDown =
            loadShared("/assets/ghost/shared/deadd.png");

    protected int currentFrame = 0;

    protected long lastFrameTime = 0;

    protected final long frameDelay = 180_000_000;

    private final Random random = new Random();

    public Ghost(double startX, double startY) {

        this.x = startX;
        this.y = startY;
    }

    public void update(long now) {

        if (frightened) {

            if (now - frightenedStart > 8_000_000_000L) {
                frightened = false;
            }
        }

        double nextX = x + dx * speed;
        double nextY = y + dy * speed;

        if (!isWall(nextX, nextY)) {

            x = nextX;
            y = nextY;

        } else {

            chooseDirection();
        }

        if (now - lastFrameTime > frameDelay) {

            currentFrame = (currentFrame + 1) % 2;

            lastFrameTime = now;
        }
    }

    private void chooseDirection() {

        int[][] dirs = {
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1}
        };

        int[] dir = dirs[random.nextInt(dirs.length)];

        dx = dir[0];
        dy = dir[1];
    }

    private boolean isWall(double x, double y) {

        int col = (int)((x + renderSize / 2) / GameMap.TILE_SIZE);
        int row = (int)((y + renderSize / 2) / GameMap.TILE_SIZE);

        if (row < 0 || row >= GameMap.getRows() ||
                col < 0 || col >= GameMap.getCols()) {

            return true;
        }

        return GameMap.getTile(row, col) == 1;
    }

    public Image getCurrentFrame() {

        // modo vulnerable
        if (frightened && !dead) {

            return frightenedFrames[currentFrame];
        }

        // solo ojos
        if (dead) {

            if (dx == 1) return deadRight;
            if (dx == -1) return deadLeft;
            if (dy == -1) return deadUp;

            return deadDown;
        }

        // movimiento normal
        if (dx == 1) {
            return rightFrames[currentFrame];
        }

        if (dx == -1) {
            return leftFrames[currentFrame];
        }

        if (dy == -1) {
            return upFrames[currentFrame];
        }

        return downFrames[currentFrame];
    }

    public void frighten(long now) {

        frightened = true;
        frightenedStart = now;
    }

    public void setDead(boolean dead) {

        this.dead = dead;
    }

    public boolean isDead() {

        return dead;
    }

    public boolean isFrightened() {

        return frightened;
    }

    public double getX() {

        return x;
    }

    public double getY() {

        return y;
    }

    public double getRenderSize() {

        return renderSize;
    }
}