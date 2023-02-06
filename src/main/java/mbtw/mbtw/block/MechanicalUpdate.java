package mbtw.mbtw.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;

import java.util.HashSet;
import java.util.Set;

public class MechanicalUpdate {
    private final BlockState oldState;
    private BlockState newState;
    private final Set<Property<?>> requiredProperties;

    public MechanicalUpdate(BlockState oldState) {
        this.oldState = oldState;
        this.newState = null;
        this.requiredProperties = new HashSet<>();
    }

    public void setNewState(BlockState newState) {
        this.newState = newState;
    }

    public void addProperty(Property<?> property) {
        this.requiredProperties.add(property);
    }
}
