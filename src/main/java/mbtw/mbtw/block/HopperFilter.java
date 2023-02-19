package mbtw.mbtw.block;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;

@FunctionalInterface
public interface HopperFilter {
    boolean test(ItemVariant variant);
}
