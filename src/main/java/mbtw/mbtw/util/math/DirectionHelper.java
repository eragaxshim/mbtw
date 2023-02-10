package mbtw.mbtw.util.math;

import net.minecraft.util.math.Direction;

public class DirectionHelper {
    public static Direction.Axis otherAxis(Direction firstDirection, Direction.Axis secondAxis) {
        return firstDirection.rotateClockwise(secondAxis).getAxis();
    }

    public static Relative getRelativeCheckY(Direction baseDirection, Direction upDirection, Direction otherDirection) {
        // If it is in north, east, west or south (horizontal), up is always up as the player cannot look upside down
        if (baseDirection.getVector().getY() == 0) {
            return getRelative(baseDirection, Direction.UP, otherDirection);
        } else {
            return getRelative(baseDirection, upDirection, otherDirection);
        }
    }

    public static Relative getRelative(Direction baseDirection, Direction upDirection, Direction otherDirection) {
        if (baseDirection.getAxis() == upDirection.getAxis()) {
            throw new IllegalArgumentException("Direction cannot be in same axis as upDirection!");
        }

        if (baseDirection == otherDirection) {
            return Relative.IDENTITY;
        } else if (baseDirection == otherDirection.getOpposite()) {
            return Relative.OPPOSITE;
        }

        if (otherDirection == upDirection) {
            return Relative.UP;
        } else if (otherDirection == upDirection.getOpposite()) {
            return Relative.DOWN;
        }

        boolean baseUpSame = baseDirection.getDirection() == upDirection.getDirection();
        boolean otherPos = otherDirection.getDirection() == Direction.AxisDirection.POSITIVE;

        return baseUpSame == otherPos ? Relative.LEFT : Relative.RIGHT;
    }

    public static Direction relativeToCheckY(Direction baseDirection, Direction upDirection, Relative relative) {
        // If it is in north, east, west or south (horizontal), up is always up as the player cannot look upside down
        if (baseDirection.getVector().getY() == 0) {
            return relativeTo(baseDirection, Direction.UP, relative);
        } else {
            return relativeTo(baseDirection, upDirection, relative);
        }
    }

    public static Direction relativeTo(Direction direction, Direction upDirection, Relative relative) {
        // Fix up as pointing in the upDirection and point direction towards you
        // This then gives only one possibility for up, down, left, right

        if (direction.getAxis() == upDirection.getAxis()) {
            throw new IllegalArgumentException("Direction cannot be in same axis as upDirection!");
        }
        if (relative == Relative.IDENTITY) {
            return direction;
        } else if (relative == Relative.OPPOSITE) {
            return direction.getOpposite();
        }

        boolean doClockWise = upDirection.getDirection() == Direction.AxisDirection.POSITIVE;
        Direction.Axis axis = upDirection.getAxis();

        switch (relative) {
            case LEFT -> {}
            case RIGHT -> doClockWise = !doClockWise;
            case UP -> axis = otherAxis(direction, axis);
            case DOWN -> {
                doClockWise = !doClockWise;
                axis = otherAxis(direction, axis);
            }
            default -> throw new IncompatibleClassChangeError();
        }
        if (doClockWise) {
            return direction.rotateClockwise(axis);
        } else {
            return direction.rotateCounterclockwise(axis);
        }
    }
}
