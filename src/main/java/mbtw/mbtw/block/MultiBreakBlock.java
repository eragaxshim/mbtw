package mbtw.mbtw.block;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.item.ChiselItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
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

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        int i = state.get(BREAK_LEVEL);

        int brokenDelta = 0;

        if (player.getMainHandStack().getItem() instanceof MiningToolItem)
        {
            MiningToolItem handItem = (MiningToolItem) player.getMainHandStack().getItem();
            int miningEffect = getMiningEffect(handItem);
            if (handItem instanceof PickaxeItem)
            {
                brokenDelta = miningEffect * 3 + 1;
            }
            else if (handItem instanceof ChiselItem)
            {
                brokenDelta = miningEffect + 1;
            }

            if (miningEffect <= 1 && breakingPoint - i < brokenDelta)
            {
                brokenDelta = breakingPoint - i;
            }
            // 3 = full block in one go; 2 = 2 times, no block; 1 = slow stones, less ore, 0 = cannot get last broken
        }

        int newBroken = i + brokenDelta;

        if (newBroken <= breakingPoint){
            world.setBlockState(pos, state.with(BREAK_LEVEL, newBroken), 2);
        }
        else
        {
            world.setBlockState(pos, state.with(BROKEN, true), 2);
        }
        if (newBroken >= breakingPoint && i == 0)
        {
            Block.dropStack(world, pos, new ItemStack(blockDrop));
        }
        else {
            int count = Math.max(Math.min(breakingPoint - i - 1, Math.max(brokenDelta - 1, 1)), 0);
            Block.dropStack(world, pos, new ItemStack(itemDrop, count));
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