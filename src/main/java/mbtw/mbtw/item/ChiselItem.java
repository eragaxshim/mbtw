package mbtw.mbtw.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import mbtw.mbtw.Mbtw;
import net.minecraft.block.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;
import java.util.function.Consumer;
import java.lang.Math;

public class ChiselItem extends MiningToolItem {
    private static final Set<Material> EFFECTIVE_MATERIALS;
    private static final ImmutableMap<Block, Block> STRIPPABLE_STRIPPED_MAP;
    private final float durabilityModifier;

    public ChiselItem(int standardUses, float attackDamage, float attackSpeed, ToolMaterial material, Settings settings) {
        this((float) standardUses / (float) material.getDurability(), attackDamage, attackSpeed, material, settings);
    }

    public ChiselItem(float durabilityModifier, float attackDamage, float attackSpeed, ToolMaterial material, Settings settings) {
        super(attackDamage, attackSpeed, material, BlockTags.PICKAXE_MINEABLE, settings);
        this.durabilityModifier = durabilityModifier;
    }

    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(getDamageAmount(2.0f), attacker, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
            stack.damage(getDamageAmount(1.0f), miner, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }

        return true;
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        if (this.getMaterial().getMiningLevel() > 1)
        {
            World world = context.getWorld();
            BlockPos blockPos = context.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);
            Block block = STRIPPABLE_STRIPPED_MAP.get(blockState.getBlock());
            if (block != null) {
                PlayerEntity playerEntity = context.getPlayer();
                world.playSound(playerEntity, blockPos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!world.isClient) {
                    world.setBlockState(blockPos, block.getDefaultState().with(PillarBlock.AXIS, blockState.get(PillarBlock.AXIS)), 11);
                    if (playerEntity != null) {
                        context.getStack().damage(getDamageAmount(2.0f), playerEntity, (e) -> e.sendToolBreakStatus(context.getHand()));
                    }
                }
                return ActionResult.success(world.isClient);
            }
        }
        return ActionResult.PASS;
    }

    public boolean isSuitableFor(BlockState state) {
        return EFFECTIVE_MATERIALS.contains(state.getMaterial()) || super.isSuitableFor(state);
    }

    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        float slowModifier = this.getMaterial().getMiningLevel() < 2 ? 1.5F : 0.75F;
        slowModifier = state.getMaterial() == Material.COBWEB ? slowModifier / 2 : slowModifier;
        return isSuitableFor(state) ? Math.max(this.miningSpeed / slowModifier, 1.0F) : 1.0F;
    }

    private int getDamageAmount(float base)
    {
        return (int) Math.ceil(base / this.durabilityModifier);
    }

    static {
        EFFECTIVE_MATERIALS = ImmutableSet.of(Material.WOOD, Material.STONE, Material.NETHER_WOOD, Material.COBWEB);
        STRIPPABLE_STRIPPED_MAP = ImmutableMap.<Block, Block>builder()
            .put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD)
            .put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG)
            .put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD)
            .put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG)
            .put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD)
            .put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG)
            .put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD)
            .put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG)
            .put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD)
            .put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG)
            .put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD)
            .put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG)
            .put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM)
            .put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE)
            .put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM)
            .put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE)
                .build();
    }
}
