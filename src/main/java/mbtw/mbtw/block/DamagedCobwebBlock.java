package mbtw.mbtw.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CobwebBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.SweepingEnchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraft.util.math.random.Random;

public class DamagedCobwebBlock extends CobwebBlock implements BreakInterceptable {
    public static final IntProperty BREAK_LEVEL = IntProperty.of("break_level", 0, 3);

    public DamagedCobwebBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(BreakInterceptable.BROKEN, false).with(BREAK_LEVEL, 0));
    }

    @Override
    public BlockState processBreakAttempt(World world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack handStack) {
        Item handItem = handStack.getItem();
        int b = state.get(BREAK_LEVEL);
        if (b == 3 || handItem instanceof ShearsItem || handItem instanceof SwordItem || handItem instanceof AxeItem || (b == 0 && EnchantmentHelper.get(handStack).containsKey(Enchantments.SILK_TOUCH)))
        {
            return state.with(BreakInterceptable.BROKEN, true);
        }
        else {
            return state.with(BREAK_LEVEL, b+1);
        }
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        float darkness = Math.max((9 - world.getLightLevel(pos)) / 8.0F, 0);
        float chance = darkness > 0 ? 1.0F : 0.11F;
        double break_float = (chance + (darkness / 9) - random.nextFloat()) * 3.6;
        if (break_float > 0) {
            int break_amount = (int) Math.max(Math.round(break_float), 1);
            int b = state.get(BREAK_LEVEL);
            if (b + break_amount > 3)
            {
                world.removeBlock(pos, false);
            }
            else {
                world.setBlockState(pos, state.with(BREAK_LEVEL, b+break_amount), 2);
            }
        }

    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(BREAK_LEVEL);
        stateManager.add(BreakInterceptable.BROKEN);
    }
}
