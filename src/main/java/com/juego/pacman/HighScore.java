package com.juego.pacman;

import java.util.prefs.Preferences;

public class HighScore {

    private static final Preferences PREFS =
            Preferences.userNodeForPackage(HighScore.class);

    private static final String KEY = "pacman_highscore";

    // Obtener el puntaje más alto guardado
    public static int get() {

        return PREFS.getInt(KEY, 0);
    }

    // Enviar score; devuelve true si es nuevo récord
    public static boolean submit(int score) {

        if (score > get()) {

            PREFS.putInt(KEY, score);

            return true;
        }

        return false;
    }

    // Resetear (para pruebas)
    public static void reset() {

        PREFS.putInt(KEY, 0);
    }
}