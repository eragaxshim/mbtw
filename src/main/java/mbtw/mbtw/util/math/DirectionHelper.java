package mbtw.mbtw.util.math;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public class DirectionHelper {
    public enum Relative {

        IDENTITY(0, 5),
        LEFT(1, 2),
        RIGHT(2, 1),
        UP(3, 4),
        DOWN(4, 3),
        OPPOSITE(5, 0);

        private final int i;
        private final int opposite_i;

        Relative(int i, int opposite_i) {
            this.i = i;
            this.opposite_i = opposite_i;
        }

        public int getBitField() {
            return switch (i) {
                case 0 -> 0;
                case 1 -> 1;
                case 2 -> 2;
                case 3 -> 4;
                case 4 -> 8;
                case 5 -> 16;
                default -> throw new IllegalStateException("Unexpected value: " + i);
            };
        }

        Relative getOpposite() {
            return byIndex(this.opposite_i);
        }

        public int getIndex() {
            return i;
        }

        public static Relative byIndex(int i) {
            return switch (i) {
                case 0 -> IDENTITY;
                case 1 -> LEFT;
                case 2 -> RIGHT;
                case 3 -> UP;
                case 4 -> DOWN;
                case 5 -> OPPOSITE;
                default -> throw new IllegalArgumentException("Unexpected index: " + i);
            };
        }
    }

    public static Relative getRelative(Direction baseDirection, Direction otherDirection) {
        if (baseDirection == otherDirection) {
            return Relative.IDENTITY;
        } else if (baseDirection == otherDirection.getOpposite()) {
            return Relative.OPPOSITE;
        }

        Direction.Axis axis = baseDirection.getAxis();
        Direction.Axis otherAxis = otherDirection.getAxis();
        boolean isPositive = baseDirection.getDirection() == Direction.AxisDirection.POSITIVE;
        boolean otherPositive = otherDirection.getDirection() == Direction.AxisDirection.POSITIVE;
        // We know they cannot be same axis, so base is X, Z
        boolean doOpposite = false;
        Relative relative;

        switch (axis) {
            case X -> {
                // y up
                // z left if x positive, otherwise z right
                if (otherAxis == Direction.Axis.Y) {
                    relative = Relative.UP;
                    doOpposite = !otherPositive;
                } else {
                    relative = Relative.RIGHT;
                    doOpposite = isPositive == otherPositive;
                }
            }
            case Z -> {
                // y up
                // x right if z positive, otherwise x left
                if (otherAxis == Direction.Axis.Y) {
                    relative = Relative.UP;
                    doOpposite = !otherPositive;
                } else {
                    relative = Relative.RIGHT;
                    doOpposite = isPositive != otherPositive;
                }
            }
            case Y -> {
                // x up
                // z right if y positive, otherwise z left
                if (otherAxis == Direction.Axis.X) {
                    relative = Relative.UP;
                    doOpposite = !otherPositive;
                } else {
                    relative = Relative.RIGHT;
                    doOpposite = isPositive != otherPositive;
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + axis);
        }

        if (doOpposite) {
            return relative.getOpposite();
        } else {
            return relative;
        }
    }

    public static Direction relativeTo(Direction direction, Relative relative) {
        if (relative == Relative.IDENTITY) {
            return direction;
        } else if (relative == Relative.OPPOSITE) {
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
