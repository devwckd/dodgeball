package me.devwckd.dodgeball.utils;

import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import org.bukkit.util.Vector;

@Getter
public class Cuboid {

    private final Vector lowerPoint;
    private final Vector upperPoint;

    public Cuboid(Vector firstPoint, Vector secondPoint) {
        this.lowerPoint = new Vector(
          Math.min(firstPoint.getX(), secondPoint.getX()),
          Math.min(firstPoint.getY(), secondPoint.getY()),
          Math.min(firstPoint.getZ(), secondPoint.getZ())
        );
        this.upperPoint = new Vector(
          Math.max(firstPoint.getX(), secondPoint.getX() + 1),
          Math.max(firstPoint.getY(), secondPoint.getY() + 1),
          Math.max(firstPoint.getZ(), secondPoint.getZ() + 1)
        );
    }

    public Cuboid(Region region) {
        this(new Vector(region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), region.getMinimumPoint().getZ()),
          new Vector(region.getMaximumPoint().getX(), region.getMaximumPoint().getY(), region.getMaximumPoint().getZ()));
    }

    public boolean intersects(Vector vector) {
        if(vector.getX() > upperPoint.getX() || vector.getX() < lowerPoint.getX()) return false;
        if(vector.getY() > upperPoint.getY() || vector.getY() < lowerPoint.getY()) return false;
        if(vector.getZ() > upperPoint.getZ() || vector.getZ() < lowerPoint.getZ()) return false;

        return true;
    }
}
