package mbtw.mbtw.item;

import com.google.common.collect.ImmutableSet;
import mbtw.mbtw.Mbtw;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
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

    public boolean isEffectiveOn(BlockState state) {
        return (state.getMaterial() == Material.WOOD || state.getMaterial() == Material.STONE);
    }

    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return state.getMaterial() == Material.WOOD || state.getMaterial() == Material.STONE ? Math.max(this.miningSpeed / 5.0F, 1.0F) : 1.0F;
    }

    static {
        EFFECTIVE_BLOCKS = ImmutableSet.of(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.POWERED_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.NETHER_GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.GRANITE, Blocks.POLISHED_GRANITE, Blocks.DIORITE, Blocks.POLISHED_DIORITE, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE, Blocks.STONE_SLAB, Blocks.SMOOTH_STONE_SLAB, Blocks.SANDSTONE_SLAB, Blocks.PETRIFIED_OAK_SLAB, Blocks.COBBLESTONE_SLAB, Blocks.BRICK_SLAB, Blocks.STONE_BRICK_SLAB, Blocks.NETHER_BRICK_SLAB, Blocks.QUARTZ_SLAB, Blocks.RED_SANDSTONE_SLAB, Blocks.PURPUR_SLAB, Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_RED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_STONE, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE, Blocks.POLISHED_GRANITE_SLAB, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_DIORITE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.END_STONE_BRICK_SLAB, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.GRANITE_SLAB, Blocks.ANDESITE_SLAB, Blocks.RED_NETHER_BRICK_SLAB, Blocks.POLISHED_ANDESITE_SLAB, Blocks.DIORITE_SLAB, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.PISTON_HEAD);
    }
}
