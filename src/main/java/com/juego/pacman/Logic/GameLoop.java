//Maneja el ciclo principal del juego
package com.juego.pacman.Logic;

import com.juego.pacman.Model.Fruit;
import com.juego.pacman.Model.GameMap;
import com.juego.pacman.Model.PacMan;

import com.juego.pacman.Model.Ghosts.Blinky;
import com.juego.pacman.Model.Ghosts.Clyde;
import com.juego.pacman.Model.Ghosts.Ghost;
import com.juego.pacman.Model.Ghosts.Inky;
import com.juego.pacman.Model.Ghosts.Pinky;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameLoop extends AnimationTimer {

    //se necesita ejecutar en cada frame
    private final GraphicsContext gc;

    private final PacMan pacman;

    private final Blinky blinky;
    private final Pinky  pinky;
    private final Inky   inky;
    private final Clyde  clyde;

    private final List<Ghost> ghosts;

    private final Image mapImage;
    private final Image pelletImage;
    private final Image powerPelletImage;

    // tamaño de ventana
    private final int SCALE = 2;

    // callbacks
    private final Runnable onLevelComplete;
    private final Runnable onLose;

    // estado
    private int     currentLevel   = 1;
    private boolean pelletsCleared = false;
    private boolean loseTriggered  = false;

    // transición de nivel
    private boolean levelTransition = false;
    private long    transitionStart = 0;
    private static final long TRANSITION_DURATION = 3_000_000_000L;

    // combo de fantasmas comidos en un mismo fright
    private int ghostCombo = 0;

    // fruta activa
    private final Fruit activeFruit = new Fruit();

    private int  fruitIndex       = 0;
    private long level2StartTime  = 0;

    private final Random rng = new Random();

    // tipos de fruta nivel 2 (orden de aparición)
    private static final Fruit.PowerType[] LEVEL2_FRUITS = {
            Fruit.PowerType.CHERRY,
            Fruit.PowerType.FRESA,
            Fruit.PowerType.ORANGE,
            Fruit.PowerType.APPLE,
            Fruit.PowerType.GUAYABA
    };

    // segundos desde inicio del nivel 2 en los que aparece cada fruta
    private static final long[] FRUIT_SPAWN_OFFSETS = {
            5_000_000_000L,
            20_000_000_000L,
            35_000_000_000L,
            50_000_000_000L,
            65_000_000_000L
    };

    public GameLoop(
            GraphicsContext gc,
            PacMan pacman,
            Blinky blinky,
            Pinky  pinky,
            Inky   inky,
            Clyde  clyde,
            Runnable onLevelComplete,
            Runnable onLose
    ) {

        this.gc = gc;

        this.pacman = pacman;

        this.blinky = blinky;
        this.pinky  = pinky;
        this.inky   = inky;
        this.clyde  = clyde;

        this.onLevelComplete = onLevelComplete;
        this.onLose          = onLose;

        ghosts = Arrays.asList(blinky, pinky, inky, clyde);

        // mapa
        var mapUrl = getClass().getResource("/assets/map/map-0.png");
        if (mapUrl == null) throw new RuntimeException("No se encontró map-0.png");
        mapImage = new Image(mapUrl.toExternalForm());

        // pellet normal
        var pelletUrl = getClass().getResource("/assets/pellets/pellet.png");
        if (pelletUrl == null) throw new RuntimeException("No se encontró pellet.png");
        pelletImage = new Image(pelletUrl.toExternalForm());

        // power pellet
        var powerUrl = getClass().getResource("/assets/pellets/pelletpowerup.png");
        if (powerUrl == null) throw new RuntimeException("No se encontró pelletpowerup.png");
        powerPelletImage = new Image(powerUrl.toExternalForm());
    }

    @Override
    public void handle(long now) {

        // transición entre niveles: solo mostrar mensaje
        if (levelTransition) {

            renderTransition();

            if (now - transitionStart >= TRANSITION_DURATION) {

                levelTransition = false;
                pacman.setFrozen(false);
            }

            return;
        }

        update(now);

        render(now);
    }

    private void update(long now) {

        pacman.update(now);

        // game over (sin vidas, animación terminada)
        if (!loseTriggered && pacman.isGameOver()) {

            loseTriggered = true;
            onLose.run();

            return;
        }

        if (pacman.isGameOver()) return;

        // actualizar fantasmas
        blinky.update(now);
        pinky.update(now);
        inky.update(now);
        clyde.update(now);

        // power pellet comido
        if (pacman.hasEatenPowerPellet()) {

            frightenGhosts(now);
            pacman.resetPowerPellet();
        }

        // colisiones
        checkGhostCollisions(now);

        // frutas nivel 2
        if (currentLevel == 2) {

            handleFruits(now);
        }

        // victoria: todos los pellets comidos
        if (!pelletsCleared && !GameMap.hasRemainingPellets()) {

            pelletsCleared = true;
            onLevelComplete.run();
        }
    }

    // ======== FANTASMAS ASUSTADOS ========

    private void frightenGhosts(long now) {

        // reset combo al inicio de cada fright
        ghostCombo = 0;

        for (Ghost ghost : ghosts) {

            if (!ghost.isDead()) ghost.frighten(now);
        }
    }

    // ======== COLISIONES PAC-MAN / FANTASMAS ========

    private void checkGhostCollisions(long now) {

        if (pacman.isDying()) return;

        for (Ghost ghost : ghosts) {

            double distance = Math.hypot(
                    pacman.getX() - ghost.getX(),
                    pacman.getY() - ghost.getY()
            );

            if (distance < GameMap.TILE_SIZE * 0.9) {

                if (ghost.isFrightened()) {

                    // Comer fantasma: puntuación acumulada 200→400→800→1600
                    ghostCombo++;

                    int eatScore = (int)(200 * Math.pow(2, ghostCombo - 1));

                    pacman.addScore(eatScore);

                    ghost.eaten();

                } else if (!ghost.isDead()) {

                    pacman.die(now);

                    // Respawn de fantasmas si quedan vidas
                    if (pacman.getLives() > 0) {

                        resetGhostsAfterDeath();
                    }
                }
            }
        }
    }

    private void resetGhostsAfterDeath() {

        blinky.reset();
        pinky.reset();
        inky.reset();
        clyde.reset();
    }

    // ======== FRUTAS NIVEL 2 ========

    private void handleFruits(long now) {

        // comprobar fruta activa
        if (activeFruit.isActive()) {

            if (activeFruit.isExpired(now)) {

                activeFruit.deactivate();

            } else if (activeFruit.isEatenBy(pacman.getX(), pacman.getY())) {

                // collect devuelve los puntos; addScore los aplica con multiplicador
                int pts = activeFruit.collect(pacman, ghosts, now);
                pacman.addScore(pts);
            }
        }

        // aparecer siguiente fruta en posición aleatoria del mapa
        if (!activeFruit.isActive() && fruitIndex < LEVEL2_FRUITS.length) {

            long elapsed = now - level2StartTime;

            if (elapsed >= FRUIT_SPAWN_OFFSETS[fruitIndex]) {

                activeFruit.spawnRandom(LEVEL2_FRUITS[fruitIndex], now);

                fruitIndex++;
            }
        }
    }

    // ======== INICIAR NIVEL 2 ========

    public void startLevel2(long now) {

        currentLevel    = 2;
        pelletsCleared  = false;
        fruitIndex      = 0;
        ghostCombo      = 0;
        level2StartTime = now;

        // fantasmas más rápidos
        blinky.increaseNormalSpeed(1.2);
        pinky.increaseNormalSpeed(1.2);
        inky.increaseNormalSpeed(1.2);
        clyde.increaseNormalSpeed(1.2);

        // mensaje de transición
        levelTransition = true;
        transitionStart = now;
        pacman.setFrozen(true);
    }

    // ======== RENDER ========

    private void render(long now) {

        double width  = GameMap.getCols() * GameMap.TILE_SIZE;
        double height = GameMap.getRows() * GameMap.TILE_SIZE;

        gc.clearRect(0, 0, width * SCALE, height * SCALE);

        gc.drawImage(mapImage, 0, 0, width * SCALE, height * SCALE);

        drawPellets();

        // fruta nivel 2
        if (currentLevel == 2 && activeFruit.isActive()) {

            drawFruit();
        }

        drawPacman();

        drawGhost(blinky);
        drawGhost(pinky);
        drawGhost(inky);
        drawGhost(clyde);

        drawHUD();
    }

    private void renderTransition() {

        double width  = GameMap.getCols() * GameMap.TILE_SIZE;
        double height = GameMap.getRows() * GameMap.TILE_SIZE;

        gc.clearRect(0, 0, width * SCALE, height * SCALE);
        gc.drawImage(mapImage, 0, 0, width * SCALE, height * SCALE);

        gc.setFill(Color.color(0, 0, 0, 0.78));
        gc.fillRect(0, 0, width * SCALE, height * SCALE);

        double cx = width  * SCALE / 2.0;
        double cy = height * SCALE / 2.0;

        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 22));
        gc.setFill(Color.YELLOW);
        gc.fillText("NIVEL 2", cx - 50, cy - 15);

        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 12));
        gc.setFill(Color.WHITE);
        gc.fillText("¡PREPÁRATE!", cx - 44, cy + 8);

        gc.setFont(Font.font("Monospace", 9));
        gc.setFill(Color.CYAN);
        gc.fillText("Recoge frutas para poderes y puntos extra", cx - 110, cy + 28);
    }

    private void drawPacman() {

        double size = pacman.getRenderSize() * SCALE;

        gc.save();

        gc.translate(
                (pacman.getX() * SCALE) + size / 2,
                (pacman.getY() * SCALE) + size / 2
        );

        gc.rotate(pacman.getAngle());

        gc.drawImage(
                pacman.getCurrentFrame(),
                -size / 2,
                -size / 2,
                size,
                size
        );

        gc.restore();
    }

    private void drawGhost(Ghost ghost) {

        double size = ghost.getRenderSize() * SCALE;

        gc.drawImage(
                ghost.getCurrentFrame(),
                ghost.getX() * SCALE,
                ghost.getY() * SCALE,
                size,
                size
        );
    }

    private void drawFruit() {

        double size = GameMap.TILE_SIZE * SCALE;

        gc.drawImage(
                activeFruit.getSprite(),
                activeFruit.getX() * SCALE,
                activeFruit.getY() * SCALE,
                size,
                size
        );
    }

    private void drawPellets() {

        for (int row = 0; row < GameMap.getRows(); row++) {

            for (int col = 0; col < GameMap.getCols(); col++) {

                int tile = GameMap.getTile(row, col);

                double x = col * GameMap.TILE_SIZE * SCALE;
                double y = row * GameMap.TILE_SIZE * SCALE;

                // pellet normal → 10 pts
                if (tile == 2) {

                    gc.drawImage(
                            pelletImage,
                            x + (GameMap.TILE_SIZE * SCALE * 0.35),
                            y + (GameMap.TILE_SIZE * SCALE * 0.35),
                            GameMap.TILE_SIZE * SCALE * 0.3,
                            GameMap.TILE_SIZE * SCALE * 0.3
                    );
                }

                // power pellet → 50 pts + fantasmas azules
                if (tile == 3) {

                    gc.drawImage(
                            powerPelletImage,
                            x + (GameMap.TILE_SIZE * SCALE * 0.2),
                            y + (GameMap.TILE_SIZE * SCALE * 0.2),
                            GameMap.TILE_SIZE * SCALE * 0.6,
                            GameMap.TILE_SIZE * SCALE * 0.6
                    );
                }
            }
        }
    }

    private void drawHUD() {

        double width  = GameMap.getCols() * GameMap.TILE_SIZE * SCALE;
        double height = GameMap.getRows() * GameMap.TILE_SIZE * SCALE;

        // Score principal
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 9));
        gc.setFill(Color.WHITE);
        gc.fillText("SCORE: " + pacman.getScore(), 6, 10);

        // Nivel
        gc.setFill(Color.YELLOW);
        gc.fillText("LVL " + currentLevel, width / 2 - 14, 10);

        // Vidas
        gc.setFill(Color.WHITE);
        gc.fillText("LIVES: " + pacman.getLives(), width - 62, 10);

        // Indicadores de poder activo (zona inferior)
        double powerY = height - 6;

        if (pacman.isSpeedBoostActive()) {

            gc.setFill(Color.YELLOW);
            gc.fillText("⚡SPEED", 4, powerY);
        }

        if (pacman.hasShield()) {

            gc.setFill(Color.CYAN);
            gc.fillText("🛡SHIELD", 52, powerY);
        }

        if (pacman.isDoublePointsActive()) {

            gc.setFill(Color.ORANGE);
            gc.fillText("2xPTS", 108, powerY);
        }

        // Indicador fright
        boolean anyFright = ghosts.stream().anyMatch(Ghost::isFrightened);

        if (anyFright) {

            gc.setFont(Font.font("Monospace", FontWeight.BOLD, 9));
            gc.setFill(Color.LIGHTBLUE);
            gc.fillText("FRIGHT!", width - 52, powerY);
        }
    }
}