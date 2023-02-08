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
        this.newState = oldState;
        this.requiredProperties = new HashSet<>();
    }

    /**
     * Updates property newState with value from otherState and adds property to requiredProperties
     * @param property
     * @param otherState
     * @param onlyIfDifferent Only updates if the new value is different
     */
    public void updateState(Property<?> property, BlockState otherState, boolean onlyIfDifferent) {
        this.newState = mergeProperty(newState, otherState, property, property.getType(), true, onlyIfDifferent);
    }

    public void updateState(Property<?> property, BlockState otherState) {
        updateState(property, otherState, false);
    }

    public <T extends Comparable<T>> void updateState(Property<T> property, T value) {
        updateState(property, value, false);
    }

    public <T extends Comparable<T>> void updateState(Property<T> property, T value, boolean onlyIfDifferent) {
        if (!onlyIfDifferent || newState.get(property) != value) {
            newState = newState.with(property, value);
            addProperty(property);
        }
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
            mergedState = mergeProperty(mergedState, this.newState, property, property.getType(), false, false);
        }
        return mergedState;
    }

    public <T extends Comparable<T>> BlockState mergeProperty(BlockState firstState, BlockState otherState, Property<?> property, Class<T> expectedType, boolean addRequired, boolean onlyIfDifferent) {
        if (property.getType() == expectedType) {
            Property<T> propertyT = (Property<T>) property;
            T otherValue = otherState.get(propertyT);
            boolean merge = !onlyIfDifferent || firstState.get(propertyT) != otherValue;
            if (merge && addRequired) {
                addProperty(propertyT);
            }

            if (merge) {
                return firstState.with(propertyT, otherValue);
            } else {
                return firstState;
            }
        } else {
            return null;
        }
    }
}
