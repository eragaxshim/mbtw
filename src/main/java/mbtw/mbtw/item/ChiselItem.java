package mbtw.mbtw.item;

import com.google.common.collect.ImmutableSet;
import mbtw.mbtw.Mbtw;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;
import java.util.function.Consumer;
import java.lang.Math;

public class ChiselItem extends MiningToolItem {
    private static final Set<Block> EFFECTIVE_BLOCKS;
    private final float durabilityModifier;

    public ChiselItem(int standardUses, float attackDamage, float attackSpeed, ToolMaterial material, Settings settings) {
        super(attackDamage, attackSpeed, material, EFFECTIVE_BLOCKS, settings);
        this.durabilityModifier = (float) standardUses / (float) material.getDurability();
    }

    public ChiselItem(float durabilityModifier, float attackDamage, float attackSpeed, ToolMaterial material, Settings settings) {
        super(attackDamage, attackSpeed, material, EFFECTIVE_BLOCKS, settings);
        this.durabilityModifier = durabilityModifier;
    }

    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        int damageAmount = (int) Math.ceil(2.0f / durabilityModifier);
        stack.damage(damageAmount, attacker, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
            int damageAmount = (int) Math.ceil(1.0f / durabilityModifier);
            stack.damage(damageAmount, miner, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }

        return true;
    }

    static {
        EFFECTIVE_BLOCKS = ImmutableSet.of(Mbtw.MBTW_STONE, Mbtw.MBTW_LOOSE_COBBLESTONE, Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.SANDSTONE, Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.STONE_SLAB, Blocks.COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.GRANITE_SLAB, Blocks.ANDESITE_SLAB, Blocks.DIORITE_SLAB);
    }
}
