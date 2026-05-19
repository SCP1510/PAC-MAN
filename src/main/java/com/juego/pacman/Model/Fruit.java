package com.juego.pacman.Model;

import com.juego.pacman.Model.Ghosts.Ghost;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Random;

public class Fruit {

    // Tipos de fruta
    public enum PowerType {
        CHERRY,    // 100 pts + velocidad extra
        FRESA,     // 300 pts + escudo
        ORANGE,    // 500 pts + ralentiza fantasmas
        APPLE,     // 700 pts + puntos dobles
        GUAYABA    // 1000 pts + vida extra
    }

    // Puntos de cada fruta (en orden del enum)
    private static final int[] FRUIT_POINTS = {100, 300, 500, 700, 1000};

    // Duración visible: 10 segundos
    private static final long VISIBLE_DURATION = 10_000_000_000L;

    // Radio de recolección
    private static final double COLLECT_RADIUS = GameMap.TILE_SIZE * 1.3;

    // Posiciones de spawn válidas: {col, row} → pixel = col*TS, row*TS
    // Verificadas contra el mapa (ninguna es muro ni puerta)
    public static final double[][] SPAWN_POSITIONS = {
            {1  * GameMap.TILE_SIZE,  1 * GameMap.TILE_SIZE},   // top-left
            {26 * GameMap.TILE_SIZE,  1 * GameMap.TILE_SIZE},   // top-right
            {1  * GameMap.TILE_SIZE,  5 * GameMap.TILE_SIZE},   // fila 5 izq
            {13 * GameMap.TILE_SIZE,  5 * GameMap.TILE_SIZE},   // fila 5 centro
            {26 * GameMap.TILE_SIZE,  5 * GameMap.TILE_SIZE},   // fila 5 der
            {6  * GameMap.TILE_SIZE,  8 * GameMap.TILE_SIZE},   // fila 8 izq
            {21 * GameMap.TILE_SIZE,  8 * GameMap.TILE_SIZE},   // fila 8 der
            {1  * GameMap.TILE_SIZE, 20 * GameMap.TILE_SIZE},   // fila 20 izq
            {9  * GameMap.TILE_SIZE, 20 * GameMap.TILE_SIZE},   // fila 20 centro-izq
            {18 * GameMap.TILE_SIZE, 20 * GameMap.TILE_SIZE},   // fila 20 centro-der
            {26 * GameMap.TILE_SIZE, 20 * GameMap.TILE_SIZE},   // fila 20 der
            {6  * GameMap.TILE_SIZE, 23 * GameMap.TILE_SIZE},   // fila 23 izq
            {21 * GameMap.TILE_SIZE, 23 * GameMap.TILE_SIZE},   // fila 23 der
            {1  * GameMap.TILE_SIZE, 29 * GameMap.TILE_SIZE},   // bottom izq
            {13 * GameMap.TILE_SIZE, 29 * GameMap.TILE_SIZE},   // bottom centro
            {26 * GameMap.TILE_SIZE, 29 * GameMap.TILE_SIZE},   // bottom der
    };

    private static final Random rng = new Random();

    // sprites precargados
    private static final Image[] sprites = {
            new Image(Fruit.class.getResource("/assets/fruit/cherry.png").toExternalForm()),
            new Image(Fruit.class.getResource("/assets/fruit/fresa.png").toExternalForm()),
            new Image(Fruit.class.getResource("/assets/fruit/orange.png").toExternalForm()),
            new Image(Fruit.class.getResource("/assets/fruit/apple.png").toExternalForm()),
            new Image(Fruit.class.getResource("/assets/fruit/guayaba.png").toExternalForm())
    };

    private double    x, y;
    private PowerType type;
    private boolean   active;
    private long      spawnTime;

    // Activar fruta en posición aleatoria
    public void spawnRandom(PowerType type, long now) {

        double[] pos = SPAWN_POSITIONS[rng.nextInt(SPAWN_POSITIONS.length)];

        this.x         = pos[0];
        this.y         = pos[1];
        this.type      = type;
        this.spawnTime = now;
        this.active    = true;
    }

    // Activar fruta en posición específica
    public void spawn(PowerType type, double x, double y, long now) {

        this.x         = x;
        this.y         = y;
        this.type      = type;
        this.spawnTime = now;
        this.active    = true;
    }

    // Aplicar efecto al comer: PUNTOS + PODER
    public int collect(PacMan pacman, List<Ghost> ghosts, long now) {

        active = false;

        // Puntos de la fruta
        int points = FRUIT_POINTS[type.ordinal()];

        // Poder adicional
        switch (type) {

            case CHERRY  -> pacman.activateSpeedBoost(now);

            case FRESA   -> pacman.activateShield();

            case ORANGE  -> {
                for (Ghost g : ghosts) {
                    if (!g.isDead()) g.activateSlow(now);
                }
            }

            case APPLE   -> pacman.activateDoublePoints(now);

            case GUAYABA -> pacman.addLife();
        }

        return points;
    }

    public boolean isEatenBy(double pacX, double pacY) {

        if (!active) return false;

        double cx  = x + GameMap.TILE_SIZE / 2.0;
        double cy  = y + GameMap.TILE_SIZE / 2.0;
        double pcx = pacX + GameMap.TILE_SIZE / 2.0;
        double pcy = pacY + GameMap.TILE_SIZE / 2.0;

        return Math.hypot(cx - pcx, cy - pcy) < COLLECT_RADIUS;
    }

    public boolean isExpired(long now) {

        return active && (now - spawnTime) > VISIBLE_DURATION;
    }

    public void deactivate() { active = false; }

    public boolean isActive() { return active; }

    public Image getSprite() {

        if (type == null) return sprites[0];

        return sprites[type.ordinal()];
    }

    // Descripción del poder para el HUD
    public String getPowerLabel() {

        if (type == null) return "";

        return switch (type) {
            case CHERRY  -> "⚡SPEED";
            case FRESA   -> "🛡SHIELD";
            case ORANGE  -> "🐌SLOW";
            case APPLE   -> "2×PTS";
            case GUAYABA -> "+LIFE";
        };
    }

    public static int getPoints(PowerType type) {

        return FRUIT_POINTS[type.ordinal()];
    }

    public double    getX()    { return x; }
    public double    getY()    { return y; }
    public PowerType getType() { return type; }
}