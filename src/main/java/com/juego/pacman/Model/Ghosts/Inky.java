package com.juego.pacman.Model.Ghosts;

import com.juego.pacman.Model.PacMan;
import javafx.scene.image.Image;

public class Inky extends Ghost {

    public Inky(double x, double y, PacMan pacman) {

        super(x, y, pacman, 4_000_000_000L);

        // Inky impredecible
        speed       = 0.9;
        normalSpeed = 0.9;

        rightFrames = new Image[]{
                new Image(getClass().getResource("/assets/ghost/inky/right1.png").toExternalForm()),
                new Image(getClass().getResource("/assets/ghost/inky/right2.png").toExternalForm())
        };

        leftFrames = new Image[]{
                new Image(getClass().getResource("/assets/ghost/inky/left1.png").toExternalForm()),
                new Image(getClass().getResource("/assets/ghost/inky/left2.png").toExternalForm())
        };

        upFrames = new Image[]{
                new Image(getClass().getResource("/assets/ghost/inky/up1.png").toExternalForm()),
                new Image(getClass().getResource("/assets/ghost/inky/up2.png").toExternalForm())
        };

        downFrames = new Image[]{
                new Image(getClass().getResource("/assets/ghost/inky/down1.png").toExternalForm()),
                new Image(getClass().getResource("/assets/ghost/inky/down2.png").toExternalForm())
        };
    }

    @Override
    protected void chooseDirection() {

        // 35% random, 65% perseguir
        if (random.nextDouble() < 0.35) {

            var dirs = getPossibleDirections();

            dirs.removeIf(d -> d[0] == -dx && d[1] == -dy);

            if (!dirs.isEmpty()) {

                var chosen = dirs.get(random.nextInt(dirs.size()));

                nextDx = chosen[0];
                nextDy = chosen[1];

                return;
            }
        }

        super.chooseDirection();
    }
}