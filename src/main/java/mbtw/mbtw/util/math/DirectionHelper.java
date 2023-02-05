package mbtw.mbtw.util.math;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public class DirectionHelper {
    public enum Relative {
        LEFT,
        RIGHT,
        OPPOSITE,
        UP,
        DOWN
    }

    public static Direction relativeTo(Direction direction, Relative relative) {
        if (relative == Relative.OPPOSITE) {
            return direction.getOpposite();
        }

        // Process:
        // Determine axis
        // If we are x or z, then we want positive y up no matter what
        // If we are y, then we want x up no matter what
        // Down is simply opposite this
        // Now point the direction towards you and fix the up axis
        // Right is then the direction to the right

        Direction.Axis axis = direction.getAxis();

        Direction up;
        Direction right;
        switch (axis) {
            case X -> {
                up = Direction.from(Direction.Axis.Y, Direction.AxisDirection.POSITIVE);
                right = Direction.from(Direction.Axis.Z, Direction.AxisDirection.NEGATIVE);
            }
            case Y -> {
                up = Direction.from(Direction.Axis.X, Direction.AxisDirection.POSITIVE);
                right = Direction.from(Direction.Axis.Z, Direction.AxisDirection.POSITIVE);
            }
            case Z -> {
                up = Direction.from(Direction.Axis.Y, Direction.AxisDirection.POSITIVE);
                right = Direction.from(Direction.Axis.X, Direction.AxisDirection.POSITIVE);
            }
            default -> throw new IllegalStateException("Unexpected value: " + axis);
        }

        if (direction.getDirection() == Direction.AxisDirection.NEGATIVE) {
            right = right.getOpposite();
        }

        return switch (relative) {
            case LEFT -> right.getOpposite();
            case RIGHT -> right;
            case UP -> up;
            case DOWN -> up.getOpposite();
            default -> throw new IllegalStateException("Unexpected value: " + relative);
        };
    }
}
