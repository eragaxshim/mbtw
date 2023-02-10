package mbtw.mbtw.util.math;

import net.minecraft.util.StringIdentifiable;

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
