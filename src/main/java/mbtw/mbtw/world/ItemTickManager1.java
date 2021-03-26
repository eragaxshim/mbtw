package mbtw.mbtw.world;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;

import java.util.UUID;
import java.util.stream.Collectors;

public class ItemTickManager1 extends PersistentState {
    Object2LongOpenHashMap<UUID> trackedStacks = new Object2LongOpenHashMap<>();
    ServerWorld world;

    public ItemTickManager1(ServerWorld world) {
        super(nameFor(world.getDimension()));
        this.world = world;
    }

    public void addTrackedStack(ItemStack itemStack, BlockPos pos, int lifeTime)
    {
        CompoundTag stackTag = itemStack.getOrCreateTag();
        UUID stackUUID;
        if (!stackTag.contains("TickUUID"))
        {
            stackUUID = MathHelper.randomUuid(this.world.getRandom());
            stackTag.putUuid("TickUUID", stackUUID);
        }
        else {
            stackUUID = stackTag.getUuid("TickUUID");
        }
        
        this.trackedStacks.put(stackUUID, this.world.getChunk(pos).getInhabitedTime() + lifeTime);
    }
    
    public void removeTrackedStack(ItemStack itemStack)
    {
        CompoundTag stackTag = itemStack.getOrCreateTag();
        if (stackTag.contains("TickUUID"))
        {
            this.trackedStacks.removeLong(stackTag.getUuid("TickUUID"));
        }
    }

    public int getRemainingTime(ItemStack itemStack, BlockPos pos) {
        CompoundTag stackTag = itemStack.getOrCreateTag();
        if (stackTag.contains("TickUUID"))
        {
            return getRemainingTime(stackTag.getUuid("TickUUID"), this.world.getChunk(pos).getInhabitedTime());
        }
        return -1;
    }
    
    public int getRemainingTime(UUID itemUuid, long chunkTime) {
        long endTime = this.trackedStacks.getOrDefault(itemUuid, -1);

        return endTime == -1 ? -1 : (int) Math.max(endTime - chunkTime, 0);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        if (tag.contains("TrackedStacks"))
        {
            ListTag trackedList = tag.getList("TrackedStacks", 10);
            trackedList.forEach((Tag tagInList) -> {
                CompoundTag compoundTag = (CompoundTag) tagInList;
                UUID stackUuid = compoundTag.getUuid("TickUUID");
                long endChunkTime = compoundTag.getLong("EndChunkTime");
                this.trackedStacks.putIfAbsent(stackUuid, endChunkTime);
            });
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        ListTag trackedList = this.trackedStacks.object2LongEntrySet().stream()
                .map((Object2LongMap.Entry<UUID> entry) -> {
                    CompoundTag compoundTag = new CompoundTag();
                    compoundTag.putUuid("TickUUID", entry.getKey());
                    compoundTag.putLong("EndChunkTime", entry.getLongValue());
                    return compoundTag;
                })
                .collect(Collectors.toCollection(ListTag::new));
        tag.put("TrackedStacks", trackedList);
        return tag;
    }

    public static String nameFor(DimensionType dimensionType) {
        return "itemTicker" + dimensionType.getSuffix();
    }
}
