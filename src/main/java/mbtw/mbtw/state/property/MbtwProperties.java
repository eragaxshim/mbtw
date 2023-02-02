package mbtw.mbtw.state.property;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.Direction;

public class MbtwProperties {
    public static final IntProperty FIRE_SIZE = IntProperty.of("fire_size", 0, 4);
    public static final BooleanProperty EMBERS = BooleanProperty.of("embers");
    public static final BooleanProperty BROKEN = BooleanProperty.of("broken");
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");

    public static final EnumProperty<Direction> POWERED_UP_DOWN = EnumProperty.of("powered_face", Direction.class, Direction.UP, Direction.DOWN);
}
