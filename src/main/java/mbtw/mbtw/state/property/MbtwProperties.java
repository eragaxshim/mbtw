package mbtw.mbtw.state.property;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;

public class MbtwProperties {
    public static final IntProperty FIRE_SIZE = IntProperty.of("fire_size", 0, 4);
    public static final BooleanProperty EMBERS = BooleanProperty.of("embers");
    public static final BooleanProperty BROKEN = BooleanProperty.of("broken");
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
}
