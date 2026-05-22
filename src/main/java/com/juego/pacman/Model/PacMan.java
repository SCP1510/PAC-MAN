//Clase que representa al jugador osease Pac-Man
package com.juego.pacman.Model;

import javafx.scene.image.Image;
import com.juego.pacman.Model.Ghosts.Ghost;

import java.util.List;

public class PacMan {

    private double x = 13 * GameMap.TILE_SIZE;
    private double y = 23 * GameMap.TILE_SIZE;

    //velocidad base
    private double baseSpeed = 1.5;

    private List<Ghost> ghosts;

    //tamaño visual y colisión
    private final double renderSize = GameMap.TILE_SIZE;
    private final double hitboxSize = GameMap.TILE_SIZE * 0.75;

    //constantes túnel
    private static final int TUNNEL_TOP_ROW = 13;
    private static final int TUNNEL_BOTTOM_ROW = 15;

    //dirección actual
    private int dx = 0;
    private int dy = 0;

    //dirección en cola
    private int queuedDx = 0;
    private int queuedDy = 0;

    //asistencia movimiento
    private static final double ASSIST_RANGE =
            GameMap.TILE_SIZE * 0.55;

    private double angle = 0;

    private int score = 0;

    private int scoreMultiplier = 1;

    //power pellet
    private boolean atePowerPellet = false;

    //sonido pellet
    private boolean justAtePellet = false;

    //vidas
    private int lives = 3;

    //muerte
    private boolean dying = false;
    private boolean gameOver = false;

    private long dyingStart = 0;

    private final long dyingDuration =
            2_000_000_000L;

    private int deathFrame = 0;

    private long lastDeathFrame = 0;

    private final long deathFrameDelay =
            120_000_000L;

    //poder velocidad
    private boolean speedBoostActive = false;

    private long speedBoostEnd = 0;

    //puntos dobles
    private boolean doublePointsActive = false;

    private long doublePointsEnd = 0;

    //escudo
    private boolean shieldActive = false;

    //freeze transición
    private boolean frozen = false;

    //animación
    private final Image[] frames;

    private final Image[] deathFrames;

    private int currentFrame = 0;

    private long lastFrameTime = 0;

    private final long frameDelay =
            150_000_000L;

    public PacMan() {

        frames = new Image[]{

                new Image(
                        getClass().getResource(
                                "/assets/pac-man/pac-man1.png"
                        ).toExternalForm()
                ),

                new Image(
                        getClass().getResource(
                                "/assets/pac-man/pac-man2.png"
                        ).toExternalForm()
                )
        };

        deathFrames = new Image[]{

                new Image(
                        getClass().getResource(
                                "/assets/pac-man/dead1.png"
                        ).toExternalForm()
                ),

                new Image(
                        getClass().getResource(
                                "/assets/pac-man/dead2.png"
                        ).toExternalForm()
                ),

                new Image(
                        getClass().getResource(
                                "/assets/pac-man/dead3.png"
                        ).toExternalForm()
                ),

                new Image(
                        getClass().getResource(
                                "/assets/pac-man/dead4.png"
                        ).toExternalForm()
                ),

                new Image(
                        getClass().getResource(
                                "/assets/pac-man/dead5.png"
                        ).toExternalForm()
                ),

                new Image(
                        getClass().getResource(
                                "/assets/pac-man/dead6.png"
                        ).toExternalForm()
                ),

                new Image(
                        getClass().getResource(
                                "/assets/pac-man/dead7.png"
                        ).toExternalForm()
                ),

                new Image(
                        getClass().getResource(
                                "/assets/pac-man/dead8.png"
                        ).toExternalForm()
                )
        };
    }

    public void update(long now) {

        //reset sonido pellet
        justAtePellet = false;

        //timers poderes
        if (
                speedBoostActive
                        &&
                        now > speedBoostEnd
        ) {

            speedBoostActive = false;
        }

        if (
                doublePointsActive
                        &&
                        now > doublePointsEnd
        ) {

            doublePointsActive = false;

            scoreMultiplier = 1;
        }

        //muerte
        if (dying) {

            animateDeath(now);

            if (
                    now - dyingStart
                            >= dyingDuration
            ) {

                respawn();
            }

            return;
        }

        //freeze
        if (frozen) return;

        double currentSpeed =
                speedBoostActive
                        ? baseSpeed * 1.6
                        : baseSpeed;

        //dirección en cola
        tryApplyQueuedDirection(currentSpeed);

        double nextX =
                x + dx * currentSpeed;

        double nextY =
                y + dy * currentSpeed;

        //horizontal
        if (!isWall(nextX, y)) {

            x = nextX;
        }

        //vertical
        if (!isWall(x, nextY)) {

            y = nextY;
        }

        //túnel
        if (inTunnel()) {

            double maxWidth =
                    GameMap.getCols()
                            * GameMap.TILE_SIZE;

            if (x < -renderSize) {

                x =
                        maxWidth
                                - (GameMap.TILE_SIZE * 3);
            }

            if (x > maxWidth) {

                x = GameMap.TILE_SIZE * 2;
            }
        }

        //pellets
        int pelletCol =
                (int)(
                        (x + renderSize / 2)
                                /
                                GameMap.TILE_SIZE
                );

        int pelletRow =
                (int)(
                        (y + renderSize / 2)
                                /
                                GameMap.TILE_SIZE
                );

        if (
                pelletRow >= 0
                        &&
                        pelletRow < GameMap.getRows()
                        &&
                        pelletCol >= 0
                        &&
                        pelletCol < GameMap.getCols()
        ) {

            if (
                    GameMap.getTile(
                            pelletRow,
                            pelletCol
                    ) == 3
            ) {

                atePowerPellet = true;
            }

            int pts =
                    GameMap.eatPellet(
                            pelletRow,
                            pelletCol
                    );

            if (pts > 0) {

                // SOLO reproducir waka si está vivo
                if (!dying && !frozen && !gameOver) {

                    justAtePellet = true;
                }

                addScore(pts);
            }
        }

        //animación normal
        if (
                now - lastFrameTime
                        > frameDelay
        ) {

            currentFrame =
                    (currentFrame + 1)
                            % frames.length;

            lastFrameTime = now;
        }
    }

    //asistencia movimiento
    private void tryApplyQueuedDirection(
            double currentSpeed
    ) {

        if (
                queuedDx == 0
                        &&
                        queuedDy == 0
        ) {
            return;
        }

        double snapX =
                Math.round(
                        x / GameMap.TILE_SIZE
                ) * GameMap.TILE_SIZE;

        double snapY =
                Math.round(
                        y / GameMap.TILE_SIZE
                ) * GameMap.TILE_SIZE;

        boolean nearX =
                Math.abs(x - snapX)
                        <= ASSIST_RANGE;

        boolean nearY =
                Math.abs(y - snapY)
                        <= ASSIST_RANGE;

        if (nearX && nearY) {

            double testX =
                    snapX
                            + queuedDx * currentSpeed;

            double testY =
                    snapY
                            + queuedDy * currentSpeed;

            if (!isWall(testX, testY)) {

                x = snapX;
                y = snapY;

                dx = queuedDx;
                dy = queuedDy;

                queuedDx = 0;
                queuedDy = 0;
            }
        }
    }

    //animación muerte
    private void animateDeath(long now) {

        if (
                now - lastDeathFrame
                        > deathFrameDelay
        ) {

            if (
                    deathFrame
                            <
                            deathFrames.length - 1
            ) {

                deathFrame++;
            }

            lastDeathFrame = now;
        }
    }

    //respawn
    private void respawn() {

        if (lives <= 0) {

            gameOver = true;

            return;
        }

        x = 13 * GameMap.TILE_SIZE;
        y = 23 * GameMap.TILE_SIZE;

        dx = 0;
        dy = 0;

        queuedDx = 0;
        queuedDy = 0;

        dying = false;

        // detener waka
        justAtePellet = false;
    }

    //morir
    public void die(long now) {

        if (dying) return;

        //escudo
        if (shieldActive) {

            shieldActive = false;

            return;
        }

        lives--;

        // detener waka
        justAtePellet = false;

        dying = true;

        dyingStart = now;

        deathFrame = 0;
    }

    //reset nivel
    public void resetPosition() {

        x = 13 * GameMap.TILE_SIZE;
        y = 23 * GameMap.TILE_SIZE;

        dx = 0;
        dy = 0;

        queuedDx = 0;
        queuedDy = 0;

        angle = 0;

        dying = false;

        deathFrame = 0;

        gameOver = false;

        speedBoostActive = false;

        doublePointsActive = false;

        shieldActive = false;

        scoreMultiplier = 1;

        justAtePellet = false;
    }

    //colisiones paredes
    private boolean isWall(
            double x,
            double y
    ) {

        double offset =
                (renderSize - hitboxSize)
                        / 2;

        int leftCol =
                (int)(
                        (x + offset)
                                /
                                GameMap.TILE_SIZE
                );

        int rightCol =
                (int)(
                        (
                                x
                                        + offset
                                        + hitboxSize
                                        - 0.1
                        )
                                /
                                GameMap.TILE_SIZE
                );

        int topRow =
                (int)(
                        (y + offset)
                                /
                                GameMap.TILE_SIZE
                );

        int bottomRow =
                (int)(
                        (
                                y
                                        + offset
                                        + hitboxSize
                                        - 0.1
                        )
                                /
                                GameMap.TILE_SIZE
                );

        if (
                topRow < 0
                        ||
                        bottomRow >= GameMap.getRows()
        ) {

            return true;
        }

        if (!inTunnel()) {

            if (
                    leftCol < 0
                            ||
                            rightCol >= GameMap.getCols()
            ) {

                return true;
            }
        }

        if (
                leftCol < 0
                        ||
                        rightCol >= GameMap.getCols()
        ) {

            return false;
        }

        return
                GameMap.getTile(
                        topRow,
                        leftCol
                ) == 1
                        ||

                        GameMap.getTile(
                                topRow,
                                rightCol
                        ) == 1
                        ||

                        GameMap.getTile(
                                bottomRow,
                                leftCol
                        ) == 1
                        ||

                        GameMap.getTile(
                                bottomRow,
                                rightCol
                        ) == 1;
    }

    //túnel
    private boolean inTunnel() {

        int row =
                (int)(
                        y / GameMap.TILE_SIZE
                );

        return
                row >= TUNNEL_TOP_ROW
                        &&
                        row <= TUNNEL_BOTTOM_ROW;
    }

    //dirección
    public void setDirection(
            int newDx,
            int newDy
    ) {

        if (newDx == 1) {

            angle = 0;

        } else if (newDx == -1) {

            angle = 180;

        } else if (newDy == -1) {

            angle = 270;

        } else if (newDy == 1) {

            angle = 90;
        }

        queuedDx = newDx;
        queuedDy = newDy;
    }

    //poderes
    public void activateSpeedBoost(long now) {

        speedBoostActive = true;

        speedBoostEnd =
                now + 8_000_000_000L;
    }

    public void activateDoublePoints(long now) {

        doublePointsActive = true;

        doublePointsEnd =
                now + 10_000_000_000L;

        scoreMultiplier = 2;
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

    //subir velocidad
    public void increaseSpeed(double factor) {

        baseSpeed =
                Math.min(
                        baseSpeed * factor,
                        2.2
                );
    }

    //getters
    public double getX() {

        return x;
    }

    public double getY() {

        return y;
    }

    public double getAngle() {

        return angle;
    }

    public int getDx() {

        return dx;
    }

    public int getDy() {

        return dy;
    }

    public Image getCurrentFrame() {

        if (dying) {

            return deathFrames[deathFrame];
        }

        return frames[currentFrame];
    }

    public int getScore() {

        return score;
    }

    public int getLives() {

        return lives;
    }

    public boolean isDying() {

        return dying;
    }

    public boolean isGameOver() {

        return gameOver;
    }

    public boolean hasShield() {

        return shieldActive;
    }

    public boolean isSpeedBoostActive() {

        return speedBoostActive;
    }

    public boolean isDoublePointsActive() {

        return doublePointsActive;
    }

    public boolean hasEatenPowerPellet() {

        return atePowerPellet;
    }

    public boolean justAtePellet() {

        return justAtePellet;
    }

    public void resetPowerPellet() {

        atePowerPellet = false;
    }

    public double getRenderSize() {

        return renderSize;
    }

    public void setGhosts(List<Ghost> ghosts) {

        this.ghosts = ghosts;
    }

    public List<Ghost> getGhosts() {

        return ghosts;
    }
}