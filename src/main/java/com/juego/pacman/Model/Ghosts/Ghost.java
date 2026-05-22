package com.juego.pacman.Model.Ghosts;

import com.juego.pacman.Model.GameMap;
import com.juego.pacman.Model.PacMan;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Ghost {

    protected double x;
    protected double y;

    private double startX;
    private double startY;
    private long startDelay;

    protected double speed = 1;
    protected double normalSpeed = 1;
    protected double deadSpeed = 1.5;

    protected int dx = 0;
    protected int dy = -1;

    protected int nextDx = 0;
    protected int nextDy = -1;

    // Sistema de objetivos por Azulejo (Tile-Targeting)
    protected double targetX;
    protected double targetY;
    protected boolean hasTarget = false;

    // Hitbox y renderizado
    protected double hitboxSize = GameMap.TILE_SIZE * 0.7;
    protected double renderSize = GameMap.TILE_SIZE;

    protected PacMan pacman;

    protected boolean frightened = false;
    protected boolean dead = false;
    protected boolean released = false;
    protected boolean slow = false;

    protected long slowEnd = 0;
    protected long releaseTime;
    protected long frightenedStart = 0;

    protected int currentFrame = 0;
    protected long lastFrameTime = 0;
    protected final long frameDelay = 220_000_000L; // Ajuste smooth de animación

    protected final Random random = new Random();

    protected Image[] rightFrames;
    protected Image[] leftFrames;
    protected Image[] upFrames;
    protected Image[] downFrames;

    // Coordenadas de la casa de los fantasmas
    private final int homeCol = 14;
    private final int homeRow = 14;

    // Ruta BFS para cuando mueren (Ojos)
    private List<int[]> deadPath = new ArrayList<>();
    private int deadPathIndex = 0;

    // Sprites compartidos (Fright y Ojos)
    private static final Image[] frightenedFrames = {
            new Image(Ghost.class.getResource("/assets/ghost/shared/fright1.png").toExternalForm()),
            new Image(Ghost.class.getResource("/assets/ghost/shared/fright2.png").toExternalForm())
    };

    private static final Image deadRight = new Image(Ghost.class.getResource("/assets/ghost/shared/deadr.png").toExternalForm());
    private static final Image deadLeft  = new Image(Ghost.class.getResource("/assets/ghost/shared/deadl.png").toExternalForm());
    private static final Image deadUp    = new Image(Ghost.class.getResource("/assets/ghost/shared/deadu.png").toExternalForm());
    private static final Image deadDown  = new Image(Ghost.class.getResource("/assets/ghost/shared/deadd.png").toExternalForm());

    public Ghost(double startX, double startY, PacMan pacman, long delay) {
        this.x = startX;
        this.y = startY;
        this.startX = startX;
        this.startY = startY;
        this.startDelay = delay;
        this.pacman = pacman;

        releaseTime = System.nanoTime() + delay;
    }

    public void update(long now) {
        // Lógica para salir de la casa
        if (!released) {
            if (now >= releaseTime) {
                if (y > 11 * GameMap.TILE_SIZE) {
                    y -= speed;
                } else {
                    released = true;
                    int col = (int) Math.round(x / GameMap.TILE_SIZE);

                    // Separarse al salir
                    dx = (col <= 13) ? -1 : 1;
                    dy = 0;

                    nextDx = dx;
                    nextDy = dy;

                    // Inicializar el primer objetivo del riel al salir de la casa
                    snapToGrid();
                    targetX = x;
                    targetY = y;
                    hasTarget = true;
                }
            }
            animate(now);
            return;
        }

        // Terminar estado asustado (Fright) tras 7 segundos
        if (frightened && now - frightenedStart > 7_000_000_000L) {
            frightened = false;
            speed = normalSpeed;
        }

        // Terminar estado ralentizado (Slow)
        if (slow && now > slowEnd) {
            slow = false;
            speed = normalSpeed;
        }

        // Si está muerto (sólo los ojos), regresa a casa mediante BFS directo
        if (dead) {
            moveToHome();
            animate(now);
            return;
        }

        // Gestión dinámica de velocidades según estados
        if (frightened) {
            speed = 0.7;
        } else if (slow) {
            speed = 0.5;
        } else {
            speed = normalSpeed;
        }

        // El movimiento gestiona internamente cuándo ejecutar chooseDirection() al pisar azulejos
        move();
        animate(now);
    }

    protected void move() {
        // Si es el primer frame de movimiento, asegurar coordenadas base como objetivo
        if (!hasTarget) {
            snapToGrid();
            targetX = x;
            targetY = y;
            hasTarget = true;
        }

        // Calcular la distancia que le falta para llegar al centro de su azulejo destino
        double distX = targetX - x;
        double distY = targetY - y;
        double distance = Math.hypot(distX, distY);

        // Si ya llegó al centro del azulejo actual (o está extremadamente cerca en base a su velocidad)
        if (distance <= speed) {
            x = targetX;
            y = targetY; // Forzar alineación perfecta en el centro del nodo

            // 1. Preguntar a la IA cuál quiere que sea el siguiente giro (Modifica nextDx y nextDy)
            chooseDirection();

            // 2. Si el giro planeado no choca contra una pared, cambiar la dirección de movimiento actual
            if (!isWall(x + nextDx * GameMap.TILE_SIZE, y + nextDy * GameMap.TILE_SIZE)) {
                dx = nextDx;
                dy = nextDy;
            }

            // 3. Salvavidas si la dirección actual se topa con un muro de frente (intersección bloqueada)
            if (isWall(x + dx * GameMap.TILE_SIZE, y + dy * GameMap.TILE_SIZE)) {
                List<int[]> validDirs = getPossibleDirections();
                validDirs.removeIf(d -> d[0] == -dx && d[1] == -dy); // Evitar dar media vuelta si hay opciones

                if (validDirs.isEmpty()) {
                    validDirs = getPossibleDirections(); // Si está atrapado, aceptar la reversa
                }

                if (!validDirs.isEmpty()) {
                    int[] chosen = validDirs.get(random.nextInt(validDirs.size()));
                    dx = chosen[0];
                    dy = chosen[1];
                    nextDx = dx;
                    nextDy = dy;
                } else {
                    dx = 0;
                    dy = 0; // Freno absoluto en caso de error estructural del mapa
                }
            }

            // 4. Registrar el nuevo azulejo objetivo hacia el cual se va a desplazar
            targetX = x + dx * GameMap.TILE_SIZE;
            targetY = y + dy * GameMap.TILE_SIZE;

        } else {
            // Si está viajando a través del pasillo del azulejo, avanza de forma recta y estable
            x += dx * speed;
            y += dy * speed;
        }
    }

    protected boolean collidesWithGhost(double nextX, double nextY) {
        // Retornar falso permanentemente desactiva los choques físicos perjudiciales.
        // Esto imita al juego de arcade clásico y evita trabas grupales.
        return false;
    }

    protected void chooseDirection() {
        List<int[]> possibleDirs = getPossibleDirections();
        if (possibleDirs.isEmpty()) return;

        // Filtrar direcciones para evitar que los fantasmas regresen sobre sus propios pasos
        List<int[]> filtered = new ArrayList<>();
        for (int[] dir : possibleDirs) {
            if (!(dir[0] == -dx && dir[1] == -dy)) {
                filtered.add(dir);
            }
        }
        if (!filtered.isEmpty()) {
            possibleDirs = filtered;
        }

        // MODO FRIGHT: Movimiento caótico y puramente aleatorio en bifurcaciones
        if (frightened) {
            int[] dir = possibleDirs.get(random.nextInt(possibleDirs.size()));
            nextDx = dir[0];
            nextDy = dir[1];
            return;
        }

        // MODO NORMAL: Búsqueda matemática de distancia directa Euclidiana hacia Pacman (Sin Jitter)
        double pacX = pacman.getX();
        double pacY = pacman.getY();

        double bestDistance = Double.MAX_VALUE;
        int bestDx = dx;
        int bestDy = dy;

        for (int[] dir : possibleDirs) {
            double futureX = x + dir[0] * GameMap.TILE_SIZE;
            double futureY = y + dir[1] * GameMap.TILE_SIZE;

            double distance = Math.hypot(pacX - futureX, pacY - futureY);

            if (distance < bestDistance) {
                bestDistance = distance;
                bestDx = dir[0];
                bestDy = dir[1];
            }
        }

        nextDx = bestDx;
        nextDy = bestDy;
    }

    protected List<int[]> getPossibleDirections() {
        List<int[]> dirs = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] dir : directions) {
            double nextX = x + dir[0] * GameMap.TILE_SIZE;
            double nextY = y + dir[1] * GameMap.TILE_SIZE;

            if (!isWall(nextX, nextY)) {
                dirs.add(dir);
            }
        }
        return dirs;
    }

    private void moveToHome() {
        if (deadPath.isEmpty()) {
            computeDeadPath();
            if (deadPath.isEmpty()) {
                resetGhost();
                return;
            }
        }

        if (deadPathIndex >= deadPath.size()) {
            resetGhost();
            return;
        }

        int[] targetTile = deadPath.get(deadPathIndex);
        double targetTileX = targetTile[1] * GameMap.TILE_SIZE;
        double targetTileY = targetTile[0] * GameMap.TILE_SIZE;

        double distX = targetTileX - x;
        double distY = targetTileY - y;
        double dist = Math.hypot(distX, distY);

        if (dist < deadSpeed + 0.5) {
            x = targetTileX;
            y = targetTileY;
            deadPathIndex++;

            if (deadPathIndex < deadPath.size()) {
                int[] next = deadPath.get(deadPathIndex);
                dx = Integer.signum(next[1] - targetTile[1]);
                dy = Integer.signum(next[0] - targetTile[0]);
            }
        } else {
            x += (distX / dist) * deadSpeed;
            y += (distY / dist) * deadSpeed;
        }
    }

    private void computeDeadPath() {
        int startCol = (int) Math.round(x / GameMap.TILE_SIZE);
        int startRow = (int) Math.round(y / GameMap.TILE_SIZE);

        startCol = Math.max(0, Math.min(GameMap.getCols() - 1, startCol));
        startRow = Math.max(0, Math.min(GameMap.getRows() - 1, startRow));

        boolean[][] visited = new boolean[GameMap.getRows()][GameMap.getCols()];
        int[][] parentRow = new int[GameMap.getRows()][GameMap.getCols()];
        int[][] parentCol = new int[GameMap.getRows()][GameMap.getCols()];

        for (int[] row : parentRow) Arrays.fill(row, -1);
        for (int[] row : parentCol) Arrays.fill(row, -1);

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        boolean found = false;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];

            if (row == homeRow && col == homeCol) {
                found = true;
                break;
            }

            for (int[] dir : dirs) {
                int nr = row + dir[0];
                int nc = col + dir[1];

                if (nr < 0 || nr >= GameMap.getRows() || nc < 0 || nc >= GameMap.getCols()) continue;
                if (visited[nr][nc]) continue;

                int tile = GameMap.getTile(nr, nc);
                if (tile == 1) continue; // Los ojos ignoran las compuertas blancas (tile 1)

                visited[nr][nc] = true;
                parentRow[nr][nc] = row;
                parentCol[nr][nc] = col;
                queue.add(new int[]{nr, nc});
            }
        }

        deadPath.clear();
        if (!found) return;

        int row = homeRow;
        int col = homeCol;

        while (!(row == startRow && col == startCol)) {
            deadPath.add(0, new int[]{row, col});
            int pr = parentRow[row][col];
            int pc = parentCol[row][col];
            row = pr;
            col = pc;
        }

        deadPath.add(0, new int[]{startRow, startCol});
        deadPathIndex = 1;
    }

    private void resetGhost() {
        x = homeCol * GameMap.TILE_SIZE;
        y = homeRow * GameMap.TILE_SIZE;

        dead = false;
        frightened = false;
        slow = false;
        released = false;

        speed = normalSpeed;

        dx = 0;
        dy = -1;
        nextDx = 0;
        nextDy = -1;

        releaseTime = System.nanoTime() + 2_000_000_000L;
        deadPath.clear();
        deadPathIndex = 0;
        hasTarget = false; // Resetear bandera del riel
    }

    protected void snapToGrid() {
        x = Math.round(x / GameMap.TILE_SIZE) * GameMap.TILE_SIZE;
        y = Math.round(y / GameMap.TILE_SIZE) * GameMap.TILE_SIZE;
    }

    protected boolean isCentered() {
        // Mantenemos el método por compatibilidad de firmas de clase,
        // pero la precisión ahora es manejada de forma nativa por el gestor de proximidad en move()
        double cx = Math.round(x / GameMap.TILE_SIZE) * GameMap.TILE_SIZE;
        double cy = Math.round(y / GameMap.TILE_SIZE) * GameMap.TILE_SIZE;
        return Math.abs(x - cx) < 0.01 && Math.abs(y - cy) < 0.01;
    }

    protected boolean isWall(double nextX, double nextY) {
        double offset = (renderSize - hitboxSize) / 2;

        int leftCol   = (int) ((nextX + offset) / GameMap.TILE_SIZE);
        int rightCol  = (int) ((nextX + offset + hitboxSize) / GameMap.TILE_SIZE);
        int topRow    = (int) ((nextY + offset) / GameMap.TILE_SIZE);
        int bottomRow = (int) ((nextY + offset + hitboxSize) / GameMap.TILE_SIZE);

        if (topRow < 0 || bottomRow >= GameMap.getRows() || leftCol < 0 || rightCol >= GameMap.getCols()) {
            return true;
        }

        int topLeft     = GameMap.getTile(topRow, leftCol);
        int topRight    = GameMap.getTile(topRow, rightCol);
        int bottomLeft  = GameMap.getTile(bottomRow, leftCol);
        int bottomRight = GameMap.getTile(bottomRow, rightCol);

        // Los ojos (dead) pueden atravesar las compuertas/puertas de la casa (tile 1)
        if (dead) {
            return topLeft == 1 || topRight == 1 || bottomLeft == 1 || bottomRight == 1;
        }

        return topLeft == 1 || topRight == 1 || bottomLeft == 1 || bottomRight == 1 ||
                topLeft == 4 || topRight == 4 || bottomLeft == 4 || bottomRight == 4;
    }

    protected void animate(long now) {
        if (dead) return;

        if (now - lastFrameTime > frameDelay) {
            currentFrame = (currentFrame + 1) % 2;
            lastFrameTime = now;
        }
    }

    public Image getCurrentFrame() {
        if (frightened && !dead) {
            return frightenedFrames[currentFrame];
        }

        if (dead) {
            if (dx == 1) return deadRight;
            if (dx == -1) return deadLeft;
            if (dy == -1) return deadUp;
            return deadDown;
        }

        if (dx == 1)  return rightFrames[currentFrame];
        if (dx == -1) return leftFrames[currentFrame];
        if (dy == -1) return upFrames[currentFrame];
        return downFrames[currentFrame];
    }

    public void frighten(long now) {
        frightened = true;
        frightenedStart = now;
    }

    public void activateSlow(long now) {
        slow = true;
        slowEnd = now + 8_000_000_000L;
    }

    public void eaten() {
        dead = true;
        frightened = false;
        slow = false;
        speed = deadSpeed;
        deadPath.clear();
        deadPathIndex = 0;
    }

    public void reset() {
        x = startX;
        y = startY;

        dead = false;
        frightened = false;
        slow = false;
        released = false;

        speed = normalSpeed;

        dx = 0;
        dy = -1;
        nextDx = 0;
        nextDy = -1;

        releaseTime = System.nanoTime() + startDelay;
        deadPath.clear();
        deadPathIndex = 0;
        hasTarget = false;
    }

    public void reset(double sx, double sy, long delay) {
        x = sx;
        y = sy;
        startX = sx;
        startY = sy;
        startDelay = delay;

        dead = false;
        frightened = false;
        slow = false;
        released = false;

        speed = normalSpeed;

        dx = 0;
        dy = -1;
        nextDx = 0;
        nextDy = -1;

        releaseTime = System.nanoTime() + delay;
        deadPath.clear();
        deadPathIndex = 0;
        hasTarget = false;
    }

    public void increaseNormalSpeed(double multiplier) {
        normalSpeed *= multiplier;
        if (normalSpeed > 2.5) {
            normalSpeed = 2.5;
        }
        speed = normalSpeed;
    }

    // Getters básicos
    public boolean isDead() { return dead; }
    public boolean isFrightened() { return frightened; }
    public boolean isReleased() { return released; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getRenderSize() { return renderSize; }
}