package mbtw.mbtw.item;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;

import java.util.Set;

public class ChiselItem extends MiningToolItem {
    private static final Set<Block> EFFECTIVE_BLOCKS;

    protected ChiselItem(float attackDamage, float attackSpeed, ToolMaterial material, Settings settings) {
        super(attackDamage, attackSpeed, material, EFFECTIVE_BLOCKS, settings);
    }

    static {
        EFFECTIVE_BLOCKS = ImmutableSet.of(Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.SANDSTONE, Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.STONE_SLAB, Blocks.COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.GRANITE_SLAB, Blocks.ANDESITE_SLAB, Blocks.DIORITE_SLAB);
    }
}
