package mbtw.mbtw.block;

import mbtw.mbtw.item.ChiselItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StratifiedOreBlock extends InterceptBreakBlock {
    public final MultiBreakBlock sourceBlock;
    public final Item itemDrop;
    public final Item chunkDrop;
    public final Item pileDrop;

    public StratifiedOreBlock(Settings settings, MultiBreakBlock sourceBlock, Item itemDrop) {
        super(settings);
        this.sourceBlock = sourceBlock;
        this.itemDrop = itemDrop;
        this.chunkDrop = null;
        this.pileDrop = null;
    }
    public StratifiedOreBlock(Settings settings, MultiBreakBlock sourceBlock, Item chunkDrop, Item pileDrop) {
        super(settings);
        this.sourceBlock = sourceBlock;
        this.itemDrop = null;
        this.chunkDrop = chunkDrop;
        this.pileDrop = pileDrop;
    }

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        if (player.getMainHandStack().getItem() instanceof MiningToolItem)
        {
            MiningToolItem handItem = (MiningToolItem) player.getMainHandStack().getItem();
            int miningEffect = sourceBlock.getMiningEffect(handItem);

            if (handItem instanceof ChiselItem)
            {
                if (miningEffect >= 2)
                {
                    if (itemDrop != null)
                    {
                        Block.dropStack(world, pos, new ItemStack(itemDrop));
                    }
                    else {
                        Block.dropStack(world, pos, new ItemStack(pileDrop));
                    }
                }
            }
            else if (handItem instanceof PickaxeItem)
            {
                if (miningEffect >= 1)
                {
                    if (itemDrop != null)
                    {
                        Block.dropStack(world, pos, new ItemStack(itemDrop));
                    }
                    else if (miningEffect >= 2 ){
                        Block.dropStack(world, pos, new ItemStack(chunkDrop));
                    }
                    else {
                        Block.dropStack(world, pos, new ItemStack(pileDrop));
                    }
                }
            }


        }
        replace(state, sourceBlock.getDefaultState(), world, pos, 2);
        BlockState newBlockState = world.getBlockState(pos);
        newBlockState.getBlock().onBreak(world, pos, newBlockState, player);
    }
}
