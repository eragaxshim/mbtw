package mbtw.mbtw.block;

import mbtw.mbtw.block.entity.VariableCampfireBlockEntity;
import mbtw.mbtw.item.FireStarterItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Random;
import java.util.function.ToIntFunction;

public class VariableCampfireBlock extends CampfireBlock implements Igniteable {
    public static final IntProperty FIRE_SIZE = IntProperty.of("fire_size", 1, 4);
    public static final BooleanProperty EMBERS = BooleanProperty.of("embers");
    private final int fireDamage;

    public VariableCampfireBlock(boolean emitsParticles, int fireDamage, Settings settings) {
        super(emitsParticles, fireDamage, settings);
        this.fireDamage = fireDamage;
        this.setDefaultState(this.getStateManager().getDefaultState().with(LIT, false).with(SIGNAL_FIRE, false).with(WATERLOGGED, false).with(FACING, Direction.NORTH).with(FIRE_SIZE, 1).with(EMBERS, false));
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof VariableCampfireBlockEntity) {
            VariableCampfireBlockEntity campfireBlockEntity = (VariableCampfireBlockEntity)blockEntity;
            ItemStack itemStack = player.getStackInHand(hand);
            Optional<CampfireCookingRecipe> optional = campfireBlockEntity.getRecipeFor(itemStack);
            int fuelTime;
            if (optional.isPresent()) {
                if (!world.isClient && campfireBlockEntity.addItem(player.abilities.creativeMode ? itemStack.copy() : itemStack, optional.get().getCookTime())) {
                    player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
                    return ActionResult.SUCCESS;
                }

                return ActionResult.CONSUME;
            }
            else if ((state.get(Properties.LIT) || state.get(EMBERS)) && (fuelTime = AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(itemStack.getItem(), 0)) > 0) {
                if (!state.get(Properties.LIT)) {
                    world.setBlockState(pos, state.with(EMBERS, false), 3);
                }
                else {
                    world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F + 1.0F);
                }

                if (!world.isClient) {
                    campfireBlockEntity.addFuel(player.abilities.creativeMode ? itemStack.copy() : itemStack, fuelTime);
                    player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
                    return ActionResult.SUCCESS;
                }
                return ActionResult.CONSUME;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if (state.get(LIT) && state.get(FIRE_SIZE) == 4) {
            if (random.nextInt(2) == 0) {
                world.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FIRE_SIZE);
        stateManager.add(EMBERS);
        super.appendProperties(stateManager);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!entity.isFireImmune() && state.get(LIT) && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
            entity.damage(DamageSource.IN_FIRE, (float)(this.fireDamage * state.get(FIRE_SIZE) / 2.0));
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new VariableCampfireBlockEntity();
    }

    public static ToIntFunction<BlockState> createLightLevelFromFireSize() {
        return (blockState) -> {
            if (blockState.get(Properties.LIT))
            {
                switch (blockState.get(VariableCampfireBlock.FIRE_SIZE))
                {
                    case 1:
                        return 4;
                    case 2:
                        return 8;
                    case 3:
                        return 12;
                    case 4:
                        return 15;
                }
            }
            return 0;
        };
    }

    public void ignite(World world, LivingEntity entity, ItemStack stack, BlockState state, BlockPos pos) {
        if (state.isOf(this) && !state.get(LIT) && !state.get(EMBERS))
        {
            world.setBlockState(pos, state.with(LIT, true), 11);
        }
    }
}
