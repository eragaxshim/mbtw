package mbtw.mbtw.state.property;

import mbtw.mbtw.util.math.Relative;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.Direction;

public class MbtwProperties {
    public static final int MAX_MECHANICAL_POWER = 8;

    public static final IntProperty FIRE_SIZE = IntProperty.of("fire_size", 0, 4);
    public static final BooleanProperty EMBERS = BooleanProperty.of("embers");
    public static final BooleanProperty BROKEN = BooleanProperty.of("broken");
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public static final BooleanProperty ATTACHED = BooleanProperty.of("attached");

    public static final BooleanProperty AXIS_DIRECTION = BooleanProperty.of("axis_direction");
    public static final BooleanProperty BEARING_LOAD = BooleanProperty.of("bearing_load");
    public static final IntProperty MECHANICAL_SOURCE = IntProperty.of("mechanical_source", 0, MAX_MECHANICAL_POWER);
    public static final IntProperty MECHANICAL_SINK = IntProperty.of("mechanical_sink", 0, MAX_MECHANICAL_POWER);

    public static final IntProperty SOULS = IntProperty.of("souls", 0, 7);

    public static final EnumProperty<Direction> POWERED_UP_DOWN = EnumProperty.of("powered_face", Direction.class, Direction.UP, Direction.DOWN);
    public static final EnumProperty<Direction> UP_DIRECTION = EnumProperty.of("up_relative", Direction.class, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
}
