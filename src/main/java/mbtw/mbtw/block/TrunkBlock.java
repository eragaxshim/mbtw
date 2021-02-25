package mbtw.mbtw.block;

import mbtw.mbtw.item.ChiselItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TrunkBlock extends InterceptBreakBlock{
    private final InnerTrunkBlock innerBlock;
    private static final Text TITLE = new TranslatableText("container.crafting");
    public static final BooleanProperty WORKBENCH = BooleanProperty.of("workbench");

    public TrunkBlock(Settings settings, Block innerBlock) {
        super(settings);
        this.innerBlock = (InnerTrunkBlock) innerBlock;
        setDefaultState(getStateManager().getDefaultState().with(InterceptBreakBlock.BROKEN, false).with(WORKBENCH, false));
    }

    public BlockState processBreakAttempt(World world, BlockPos pos, BlockState state, ItemStack handStack)
    {
        if (handStack.getItem() instanceof ChiselItem && ((ChiselItem) handStack.getItem()).getMaterial().getMiningLevel() > 1) {
            return state.with(WORKBENCH, true);
        }
        else if (handStack.getItem() instanceof AxeItem && ((AxeItem) handStack.getItem()).getMaterial().getMiningLevel() > 3) {
            return state.with(InterceptBreakBlock.BROKEN, true);
        }

        return innerBlock.getDefaultState().with(InnerTrunkBlock.BREAK_LEVEL, 1);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(WORKBENCH))
        {
            if (world.isClient) {
                return ActionResult.SUCCESS;
            } else {
                player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
                player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
                return ActionResult.CONSUME;
            }
        }
        else {
            return ActionResult.PASS;
        }
    }

    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) -> {
            return new CraftingScreenHandler(i, playerInventory, ScreenHandlerContext.create(world, pos));
        }, TITLE);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(WORKBENCH);
        stateManager.add(InterceptBreakBlock.BROKEN);
    }
}
