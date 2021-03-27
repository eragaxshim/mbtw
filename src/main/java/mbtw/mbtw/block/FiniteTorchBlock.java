package mbtw.mbtw.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;

import java.util.function.ToIntFunction;

public class FiniteTorchBlock extends TorchBlock {
    public static final IntProperty TORCH_FIRE = IntProperty.of("torch_fire", 0, 3);

    public FiniteTorchBlock(Settings settings, ParticleEffect particle) {
        super(settings, particle);
        this.setDefaultState(this.getDefaultState().with(TORCH_FIRE, 1));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(TORCH_FIRE);
    }

    public static ToIntFunction<BlockState> createLightLevelFromTorchFire() {
        return (blockState) -> {
            switch (blockState.get(TORCH_FIRE))
            {
                case 2:
                    return 8;
                case 3:
                    return 14;
                default:
                    return 0;
            }
        };
    }
}
