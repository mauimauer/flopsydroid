package at.maui.flopsydroid.game;

import java.util.Random;

/**
 * Created by maui on 08.07.2014.
 */
public class Utils {

    public static int random(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
}
