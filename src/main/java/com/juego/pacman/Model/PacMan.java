package com.juego.pacman.Model;

import javafx.scene.image.Image;

public class PacMan {

    private double x = 100;
    private double y = 100;
    private double speed = 2;

    private int dx = 0;
    private int dy = 0;

    private double angle = 0;

    // Animación
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
        // Movimiento
        x += dx * speed;
        y += dy * speed;

        // Animación
        if (now - lastFrameTime > frameDelay) {
            currentFrame = (currentFrame + 1) % frames.length;
            lastFrameTime = now;
        }
    }

    public void setDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;

        // Rotación según dirección
        if (dx == 1) angle = 0;
        else if (dx == -1) angle = 180;
        else if (dy == -1) angle = 270;
        else if (dy == 1) angle = 90;
    }

    // 🔽 GETTERS GameLoop

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
}