package mbtw.mbtw.block;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.item.ChiselItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MultiBreakBlock extends InterceptBreakBlock {
    public static final IntProperty BREAK_LEVEL = IntProperty.of("break_level", 0, 9);
    public final int stratification;
    public final int breakingPoint;
    public final Block blockDrop;
    public final Item itemDrop;

    public MultiBreakBlock(FabricBlockSettings settings, int breakingPoint, int stratification, Block blockDrop, Item itemDrop) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(BREAK_LEVEL, 0));
        setDefaultState(getStateManager().getDefaultState().with(BROKEN, false));
        this.breakingPoint = breakingPoint;
        this.stratification = stratification;
        this.blockDrop = blockDrop;
        this.itemDrop = itemDrop;
    }

    public BlockState processBreakAttempt(World world, BlockPos pos, BlockState state, Item handItem)
    {
        int b = state.get(BREAK_LEVEL);

        int brokenDelta = 0;

        int newBroken = b;
        if (handItem instanceof MiningToolItem)
        {
            MiningToolItem toolItem = (MiningToolItem) handItem;
            int miningEffect = getMiningEffect(toolItem);
            if (toolItem instanceof PickaxeItem)
            {
                brokenDelta = miningEffect * 3 + 1;
            }
            else if (toolItem instanceof ChiselItem)
            {
                brokenDelta = miningEffect + 1;
            }

            if (miningEffect <= 1 && breakingPoint - b < brokenDelta)
            {
                brokenDelta = breakingPoint - b;
            }
            // 3 = full block in one go; 2 = 2 times, no block; 1 = slow stones, less ore, 0 = cannot get last broken

            newBroken = b + brokenDelta;
            if (newBroken >= breakingPoint && b == 0)
            {
                Block.dropStack(world, pos, new ItemStack(blockDrop));
            }
            else {
                int count = Math.max(Math.min(breakingPoint - b - 1, Math.max(brokenDelta - 1, 1)), 0);
                Block.dropStack(world, pos, new ItemStack(itemDrop, count));
            }
        }

        if (newBroken <= breakingPoint){
            return state.with(BREAK_LEVEL, newBroken);
        }
        else
        {
            return state.with(BROKEN, true);
        }
    }



    public int getMiningEffect(MiningToolItem handItem)
    {
        return Math.max(handItem.getMaterial().getMiningLevel() + 1 - stratification, 0);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(BREAK_LEVEL);
        stateManager.add(BROKEN);
    }
}