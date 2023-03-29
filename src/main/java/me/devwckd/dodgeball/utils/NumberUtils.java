package me.devwckd.dodgeball.utils;

public final class NumberUtils {

    private NumberUtils() { }

    public static double percentage(final long n1, final long n2) {
        if (n2 == 0) {
            return n1 < 1 ? 0D : 1D;
        }
        if (n1 == 0) {
            return 0D;
        }
        return (((double) n1) / ((double) n2));
    }

}
