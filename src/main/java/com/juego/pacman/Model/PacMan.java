//Clase que representa al jugador osease Pac-Man
package com.juego.pacman.Model;

import javafx.scene.image.Image;
import com.juego.pacman.Model.Ghosts.Ghost;
import java.util.List;

public class PacMan {

    private double x = 13 * GameMap.TILE_SIZE;
    private double y = 23 * GameMap.TILE_SIZE;

    private final double baseSpeed = 1;
    private List<Ghost> ghosts;

    // separar tamaño visual y colisión
    private final double renderSize = GameMap.TILE_SIZE;
    private final double hitboxSize = GameMap.TILE_SIZE * 0.75;

    // constantes del túnel
    private static final int TUNNEL_TOP_ROW    = 13;
    private static final int TUNNEL_BOTTOM_ROW = 15;

    private int dx = 0;
    private int dy = 0;

    private double angle = 0;
    private int score = 0;
    private int scoreMultiplier = 1;

    // power pellet
    private boolean atePowerPellet = false;

    // vidas
    private int lives = 3;

    // muerte
    private boolean dying    = false;
    private boolean gameOver = false;
    private long dyingStart  = 0;

    private final long dyingDuration    = 2_000_000_000L;
    private int deathFrame              = 0;
    private long lastDeathFrame         = 0;
    private final long deathFrameDelay  = 120_000_000L;

    // poder: velocidad extra
    private boolean speedBoostActive = false;
    private long speedBoostEnd       = 0;

    // poder: puntos dobles
    private boolean doublePointsActive = false;
    private long doublePointsEnd       = 0;

    // poder: escudo (absorbe 1 golpe)
    private boolean shieldActive = false;

    // congelar movimiento (transición de nivel)
    private boolean frozen = false;

    // Animacion
    private final Image[] frames;
    private final Image[] deathFrames;

    private int currentFrame  = 0;
    private long lastFrameTime = 0;

    private final long frameDelay = 150_000_000L;

    public PacMan() {

        frames = new Image[]{
                new Image(getClass().getResource("/assets/pac-man/pac-man1.png").toExternalForm()),
                new Image(getClass().getResource("/assets/pac-man/pac-man2.png").toExternalForm())
        };

        deathFrames = new Image[]{
                new Image(getClass().getResource("/assets/pac-man/dead1.png").toExternalForm()),
                new Image(getClass().getResource("/assets/pac-man/dead2.png").toExternalForm()),
                new Image(getClass().getResource("/assets/pac-man/dead3.png").toExternalForm()),
                new Image(getClass().getResource("/assets/pac-man/dead4.png").toExternalForm()),
                new Image(getClass().getResource("/assets/pac-man/dead5.png").toExternalForm()),
                new Image(getClass().getResource("/assets/pac-man/dead6.png").toExternalForm()),
                new Image(getClass().getResource("/assets/pac-man/dead7.png").toExternalForm()),
                new Image(getClass().getResource("/assets/pac-man/dead8.png").toExternalForm())
        };
    }

    public void update(long now) {

        // comprobar timers de poderes
        if (speedBoostActive && now > speedBoostEnd) {

            speedBoostActive = false;
        }

        if (doublePointsActive && now > doublePointsEnd) {

            doublePointsActive = false;
            scoreMultiplier    = 1;
        }

        // muerte
        if (dying) {

            animateDeath(now);

            if (now - dyingStart >= dyingDuration) {

                respawn();
            }

            return;
        }

        // congelado durante transición de nivel
        if (frozen) return;

        // velocidad actual (base o boost)
        double currentSpeed = speedBoostActive ? baseSpeed * 1.6 : baseSpeed;

        double nextX = x + dx * currentSpeed;
        double nextY = y + dy * currentSpeed;

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

            if (x < -renderSize) {

                x = maxWidth - (GameMap.TILE_SIZE * 3);
            }

            if (x > maxWidth) {

                x = GameMap.TILE_SIZE * 2;
            }
        }

        // comer pellets
        int pelletCol = (int)((x + renderSize / 2) / GameMap.TILE_SIZE);
        int pelletRow = (int)((y + renderSize / 2) / GameMap.TILE_SIZE);

        // evitar indices inválidos
        if (pelletRow >= 0 && pelletRow < GameMap.getRows() &&
                pelletCol >= 0 && pelletCol < GameMap.getCols()) {

            if (GameMap.getTile(pelletRow, pelletCol) == 3) {

                atePowerPellet = true;
            }

            int pts = GameMap.eatPellet(pelletRow, pelletCol);
            addScore(pts);
        }

        // animacion
        if (now - lastFrameTime > frameDelay) {

            currentFrame = (currentFrame + 1) % frames.length;

            lastFrameTime = now;
        }
    }

    // animacion muerte
    private void animateDeath(long now) {

        if (now - lastDeathFrame > deathFrameDelay) {

            if (deathFrame < deathFrames.length - 1) {

                deathFrame++;
            }

            lastDeathFrame = now;
        }
    }

    // respawn tras morir
    private void respawn() {

        if (lives <= 0) {

            // sin vidas: game over
            gameOver = true;
            return;
        }

        x = 13 * GameMap.TILE_SIZE;
        y = 23 * GameMap.TILE_SIZE;

        dx = 0;
        dy = 0;

        dying = false;
    }

    // perder vida
    public void die(long now) {

        if (dying) return;

        // si tiene escudo, absorbe el golpe
        if (shieldActive) {

            shieldActive = false;
            return;
        }

        lives--;

        dying      = true;
        dyingStart = now;
        deathFrame = 0;
    }

    // resetea posición para nuevo nivel (mantiene score y vidas)
    public void resetPosition() {

        x = 13 * GameMap.TILE_SIZE;
        y = 23 * GameMap.TILE_SIZE;

        dx = 0;
        dy = 0;

        angle      = 0;
        dying      = false;
        deathFrame = 0;
        gameOver   = false;

        // limpiar poderes activos
        speedBoostActive   = false;
        doublePointsActive = false;
        shieldActive       = false;
        scoreMultiplier    = 1;
    }

    //Movimiento limitado
    private boolean isWall(double x, double y) {

        // usar hitbox centrado dentro del sprite
        double offset = (renderSize - hitboxSize) / 2;

        int leftCol   = (int)((x + offset) / GameMap.TILE_SIZE);
        int rightCol  = (int)((x + offset + hitboxSize - 0.1) / GameMap.TILE_SIZE);
        int topRow    = (int)((y + offset) / GameMap.TILE_SIZE);
        int bottomRow = (int)((y + offset + hitboxSize - 0.1) / GameMap.TILE_SIZE);

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

        return GameMap.getTile(topRow,    leftCol)  == 1 ||
                GameMap.getTile(topRow,    rightCol) == 1 ||
                GameMap.getTile(bottomRow, leftCol)  == 1 ||
                GameMap.getTile(bottomRow, rightCol) == 1;
    }

    // verifica si Pac-Man está en la zona del túnel
    private boolean inTunnel() {

        int row = (int)(y / GameMap.TILE_SIZE);

        return row >= TUNNEL_TOP_ROW && row <= TUNNEL_BOTTOM_ROW;
    }

    //rotaciones
    public void setDirection(int dx, int dy) {

        this.dx = dx;
        this.dy = dy;

        if (dx == 1)       angle = 0;
        else if (dx == -1) angle = 180;
        else if (dy == -1) angle = 270;
        else if (dy == 1)  angle = 90;
    }

    // ======== PODERES ========

    public void activateSpeedBoost(long now) {

        speedBoostActive = true;
        speedBoostEnd    = now + 8_000_000_000L;
    }

    public void activateDoublePoints(long now) {

        doublePointsActive = true;
        doublePointsEnd    = now + 10_000_000_000L;
        scoreMultiplier    = 2;
    }

    public void activateShield() {

        shieldActive = true;
    }

    public void addLife() {

        lives++;
    }

    public void addScore(int pts) {

        score += pts * scoreMultiplier;
    }

    public void setFrozen(boolean frozen) {

        this.frozen = frozen;
    }

    // ======== GETTERS ========

    public double getX() { return x; }
    public double getY() { return y; }
    public double getAngle() { return angle; }
    public int getDx() { return dx; }
    public int getDy() { return dy; }

    public Image getCurrentFrame() {

        if (dying) return deathFrames[deathFrame];

        return frames[currentFrame];
    }

    public int getScore()  { return score; }
    public int getLives()  { return lives; }
    public boolean isDying()     { return dying; }
    public boolean isGameOver()  { return gameOver; }
    public boolean hasShield()   { return shieldActive; }
    public boolean isSpeedBoostActive()   { return speedBoostActive; }
    public boolean isDoublePointsActive() { return doublePointsActive; }

    public boolean hasEatenPowerPellet() { return atePowerPellet; }

    public void resetPowerPellet() { atePowerPellet = false; }

    // getter para tamaño visual (para dibujar bien)
    public double getRenderSize() { return renderSize; }
    public void setGhosts(List<Ghost> ghosts) {

        this.ghosts = ghosts;
    }

    public List<Ghost> getGhosts() {

        return ghosts;
    }
}