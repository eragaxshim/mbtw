package mbtw.mbtw.util.math;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

import java.util.Set;

/**
 * MechanicalVec only works along a single axis. We can have no diagonal direction. Therefore, only one component can
 * be nonzero and it must be a scalar multiple of a unit vector.
 */
public class MechanicalVec {

    private final Direction direction;
    private final int magnitude;

    public MechanicalVec(Vec3i vec3i) {
        if (notUniDirectional(vec3i)) {
            throw new IllegalArgumentException("Exactly one component must be nonzero!");
        }
        int val = 0;
        Direction direction = Direction.EAST;
        if (vec3i.getX() != 0) {
            val = vec3i.getX();
        } else if (vec3i.getY() != 0) {
            val = vec3i.getX();
            direction = Direction.UP;
        } else if (vec3i.getZ() != 0) {
            val = vec3i.getX();
            direction = Direction.SOUTH;
        }

        if (val < 0) {
            direction = direction.getOpposite();
            val = val * -1;
        }
        this.magnitude = val;
        this.direction = direction;
    }

    public MechanicalVec(Direction direction, int magnitude) {
        this.direction = direction;
        this.magnitude = magnitude;
    }

    public MechanicalVec(Direction direction) {
        this.direction = direction;
        this.magnitude = 1;
    }

    public static boolean notUniDirectional(Vec3i vec3i) {
        return vec3i.equals(Vec3i.ZERO) || vec3i.getX()*vec3i.getY() + vec3i.getY()*vec3i.getZ() + vec3i.getZ()*vec3i.getX() != 0;
    }

    public boolean sameDirection(Direction direction) {
        return this.direction == direction;
    }

    public boolean sameDirectionAsVec(MechanicalVec vec) {
        return vec.sameDirection(this.direction);
    }

    public boolean sameDirectionAsVec(Vec3i vec) {
        return vecSameDirection(vec, this.direction);
    }

    public boolean oneOfDirections(Direction ... directions) {
        for (Direction direction : directions) {
            if (direction == this.direction) {
                return true;
            }
        }
        return false;
    }

    public boolean oneOfDirecions(Set<Direction> directions) {
        return directions.contains(this.direction);
    }

    public static boolean vecSameDirection(Vec3i vec3i, Direction direction) {
        if (notUniDirectional(vec3i)) {
            return false;
        }

        return (new MechanicalVec(vec3i)).sameDirection(direction);
    }

    public int getMagnitude() {
        return magnitude;
    }

    public Direction getDirection() {
        return direction;
    }
}
