package mbtw.mbtw.block.lookup;

import net.fabricmc.fabric.api.lookup.v1.custom.ApiLookupMap;
import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class BlockTypeApiLookupImpl<A> implements BlockTypeApiLookup<A> {
    private static final ApiLookupMap<BlockTypeApiLookup<?>> LOOKUPS = ApiLookupMap.create(
            (id, apiClass, context) -> new BlockTypeApiLookupImpl<>(id, apiClass)
    );

    @SuppressWarnings("unchecked")
    public static <A> BlockTypeApiLookup<A> get(Identifier lookupId, Class<A> apiClass) {
        return (BlockTypeApiLookup<A>) LOOKUPS.getLookup(lookupId, apiClass, Void.class);
    }

    private BlockTypeApiLookupImpl(Identifier id, Class<?> apiClass) {

    }

    private final ApiProviderMap<Block, BlockTypeApiProvider<A>> providerMap = ApiProviderMap.create();

    @Nullable
    public A find(Block block) {
        BlockTypeApiProvider<A> provider = providerMap.get(block);
        if (provider == null) {
            return null;
        } else {
            return provider.find(block);
        }
    }

    public void register(BlockTypeApiProvider<A> provider, Block block) {
        Objects.requireNonNull(provider, "BlockTypeApiProvider may not be null.");
        Objects.requireNonNull(block, "Item may not be null");
        if (providerMap.putIfAbsent(block, provider) != null) {
            LoggerFactory.getLogger("MBTW").warn("Encountered duplicate API provider registration for block " + Registries.BLOCK.getId(block) + ".");
        }
    }
}
