package me.chimkenu.mangax.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    public static boolean probability(double x) {
        return Math.random() < x;
    }

    public static double gaussian(double x, double median, double variance) {
        return Math.exp(-1 * Math.pow((x - median) / variance, 2));
    }

    public static double sigmoid(double x, double median, double variance) {
        return 1 / (1 + Math.exp(-1 * (x - median) / variance));
    }

    public static int randomFrom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static boolean isAround(double x, double target, double range) {
        return Math.abs(target - x) <= range;
    }
}
