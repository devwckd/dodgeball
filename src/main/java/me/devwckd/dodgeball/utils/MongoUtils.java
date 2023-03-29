package me.devwckd.dodgeball.utils;

import org.bukkit.util.Vector;

public final class MongoUtils {

    private MongoUtils() {
    }

    public static String cuboidToString(Cuboid cuboid) {
        return vectorToString(cuboid.getLowerPoint()) + ";" + vectorToString(cuboid.getUpperPoint());
    }

    public static String vectorToString(Vector vector) {
        return vector.getX() + ";" + vector.getY() + ";" + vector.getZ();
    }

    public static Vector stringToVector(String string) {
        final String[] split = string.split(";");
        final double x = Double.parseDouble(split[0]);
        final double y = Double.parseDouble(split[1]);
        final double z = Double.parseDouble(split[2]);
        return new Vector(x, y, z);
    }

    public static Cuboid stringToCuboid(String string) {
        final String[] split = string.split(";");
        final double x1 = Double.parseDouble(split[0]);
        final double y1 = Double.parseDouble(split[1]);
        final double z1 = Double.parseDouble(split[2]);
        final double x2 = Double.parseDouble(split[3]);
        final double y2 = Double.parseDouble(split[4]);
        final double z2 = Double.parseDouble(split[5]);

        final Vector lowerVector = new Vector(x1, y1, z1);
        final Vector upperVector = new Vector(x2, y2, z2);
        return new Cuboid(lowerVector, upperVector);
    }
}
