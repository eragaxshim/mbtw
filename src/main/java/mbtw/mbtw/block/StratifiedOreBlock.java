package mbtw.mbtw.block;

import mbtw.mbtw.item.ChiselItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

public class StratifiedOreBlock extends InterceptBreakBlock {
    public final StratifiedStoneBlock sourceBlock;
    public final Item itemDrop;
    public final Item chunkDrop;
    public final Item pileDrop;

    public StratifiedOreBlock(Settings settings, StratifiedStoneBlock sourceBlock, Item itemDrop) {
        super(settings);
        this.sourceBlock = sourceBlock;
        this.itemDrop = itemDrop;
        this.chunkDrop = null;
        this.pileDrop = null;
    }
    public StratifiedOreBlock(Settings settings, StratifiedStoneBlock sourceBlock, Item chunkDrop, Item pileDrop) {
        super(settings);
        this.sourceBlock = sourceBlock;
        this.itemDrop = null;
        this.chunkDrop = chunkDrop;
        this.pileDrop = pileDrop;
    }

    public BlockState processBreakAttempt(World world, BlockPos pos, BlockState state, ItemStack handStack)
    {
        Item handItem = handStack.getItem();
        if (handItem instanceof MiningToolItem)
        {
            MiningToolItem toolItem = (MiningToolItem) handItem;
            int miningEffect = sourceBlock.getMiningEffect(toolItem);

            if (toolItem instanceof ChiselItem)
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
            else if (toolItem instanceof PickaxeItem)
            {
                Map<Enchantment, Integer> ei = EnchantmentHelper.get(handStack);
                if (ei.containsKey(Enchantments.SILK_TOUCH))
                {
                    return state.with(BROKEN, true);
                }

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
        return sourceBlock.processBreakAttempt(world, pos, sourceBlock.getDefaultState(), handStack);
    }
}
