package mbtw.mbtw.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Optional;

public class BlockSchedule {
    private final BlockPos blockPos;
    private final String blockName;
    private final HashSet<Property.Value<?>> requiredProperties;

    public BlockSchedule(BlockPos blockPos, Block block) {
        this.blockPos = blockPos;
        this.blockName = Registry.BLOCK.getId(block).toString();
        this.requiredProperties = new HashSet<>();
    }

    private BlockSchedule(BlockPos blockPos, Block block, HashSet<Property.Value<?>> requiredProperties) {
        this.blockPos = blockPos;
        this.blockName = Registry.BLOCK.getId(block).toString();
        this.requiredProperties = requiredProperties;
    }
    
    public NbtCompound createTag()
    {
        NbtCompound tag = new NbtCompound();
        tag.put("BlockPos", NbtHelper.fromBlockPos(this.blockPos));
        tag.putString("BlockName", this.blockName);

        if (!requiredProperties.isEmpty())
        {
            NbtCompound propertiesTag = new NbtCompound();
            for (Property.Value<?> propertyValue : requiredProperties)
            {
                propertiesTag.putString(propertyValue.property().getName(), nameValue(propertyValue));
            }
            tag.put("RequiredProperties", propertiesTag);
        }
        return tag;
    }

    public void runSchedule(ServerWorld world)
    {
        BlockState nowState = world.getBlockState(this.blockPos);
        if (this.compliesToProperties(nowState))
        {
            ((BlockSchedulable) nowState.getBlock()).runScheduled(world, nowState, this.blockPos);
        }
    }

    public boolean compliesToProperties(BlockState nowState)
    {
        if (Registry.BLOCK.getId(nowState.getBlock()).toString().equals(this.blockName) && nowState.getBlock() instanceof BlockSchedulable)
        {
            boolean complies = true;
            for (Property.Value<?> propertyValue : this.requiredProperties)
            {
                complies = compliesToProperty(nowState, propertyValue);
            }
            return complies;
        }
        return false;
    }

    public BlockPos getBlockPos()
    {
        return this.blockPos;
    }

    private static <T extends Comparable<T>> boolean compliesToProperty(BlockState state, Property.Value<T> propertyValue)
    {
        return state.get(state.getBlock().getStateManager().getProperty(propertyValue.property().getName())).equals(propertyValue.value());
    }

    public static BlockSchedule scheduleFromTag(NbtCompound tag) {
        BlockPos pos = NbtHelper.toBlockPos(tag.getCompound("BlockPos"));
        Block block = Registry.BLOCK.get(new Identifier(tag.getString("BlockName")));
        HashSet<Property.Value<?>> requiredProperties = new HashSet<>();

        if (tag.contains("RequiredProperties", 10)) {
            NbtCompound propertiesTag = tag.getCompound("RequiredProperties");
            for (String key : propertiesTag.getKeys())
            {
                Property<?> property = block.getStateManager().getProperty(key);
                Property.Value<?> propertyValue;
                if (property != null && (propertyValue = parseValue(property, propertiesTag.getString(key))) != null)
                {
                    requiredProperties.add(propertyValue);
                }

            }
        }
        return new BlockSchedule(pos, block, requiredProperties);
    }

    private static <T extends Comparable<T>> Property.Value<T> parseValue(Property<T> property, String valueString) {
        Optional<T> optional = property.parse(valueString);
        return optional.map(property::createValue).orElse(null);
    }

    private static <T extends Comparable<T>> String nameValue(Property.Value<T> propertyValue) {
        return propertyValue.property().name(propertyValue.value());
    }
    
    public static class Builder {
        private final BlockPos blockPos;
        private final Block block;
        private final HashSet<Property.Value<?>> requiredProperties;
        
        public Builder(BlockPos blockPos, Block block) { 
            this.blockPos = blockPos;
            this.block = block;
            this.requiredProperties = new HashSet<>();
        }
        
        public Builder addProperty(Property.Value<?> property)
        {
            this.requiredProperties.add(property);
            return this;
        }

        public <T extends Comparable<T>> Builder addProperty(Property<T> property, T value)
        {
            return addProperty(property.createValue(value));
        }
        
        public BlockSchedule build()
        {
            return new BlockSchedule(this.blockPos, this.block, this.requiredProperties);
        }
    }
}
