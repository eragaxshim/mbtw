package mbtw.mbtw.block;

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

    public MechanicalUpdate withState(BlockState newState) {
        this.newState = newState;
        if (newState == oldState && newState.getBlock().getDefaultState() == newState) {
            requiredProperties.clear();
        }
        return this;
    }

    public void addProperty(Property<?> property) {
        requiredProperties.add(property);
    }

    public boolean isCompatibleWith(MechanicalUpdate otherUpdate) {
        for (Property<?> property : requiredProperties) {
            if (newState.get(property) != otherUpdate.newState.get(property) && otherUpdate.requiredProperties.contains(property)) {
                return false;
            }
        }
        return true;
    }

    public BlockState getNewState() {
        return this.newState;
    }

    public BlockState mergeState(MechanicalUpdate otherUpdate) {
        BlockState mergedState = otherUpdate.newState;
        for (Property<?> property : requiredProperties) {
            mergedState = mergeProperty(mergedState, this.newState, property, property.getType());
        }
        return mergedState;
    }

    public static <T extends Comparable<T>> BlockState mergeProperty(BlockState firstState, BlockState otherState, Property<?> property, Class<T> expectedType) {
        if (property.getType() == expectedType) {
            Property<T> propertyT = (Property<T>) property;
            return firstState.with(propertyT, otherState.get(propertyT));
        } else {
            return null;
        }
    }
}
