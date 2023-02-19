package mbtw.mbtw.block.lookup;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface BlockTypeApiLookup<A> {
    static <A> BlockTypeApiLookup<A> get(Identifier lookupId, Class<A> apiClass) {
        return BlockTypeApiLookupImpl.get(lookupId, apiClass);
    }

    @Nullable
    A find(Block block);

    void register(BlockTypeApiProvider<A> provider, Block block);

    interface BlockTypeApiProvider<A> {
        @Nullable
        A find(Block block);
    }
}
