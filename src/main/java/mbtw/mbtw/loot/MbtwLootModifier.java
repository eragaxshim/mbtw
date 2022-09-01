package mbtw.mbtw.loot;

import mbtw.mbtw.Mbtw;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.util.Identifier;

public class MbtwLootModifier {
    private static final Identifier CREEPER_LOOT_TABLE_ID = new Identifier("minecraft", "entities/creeper");

    MbtwLootModifier() {}

    public static void modifyLootTables()
    {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin() && CREEPER_LOOT_TABLE_ID.equals(id)) {
                NbtCompound ct = new NbtCompound();
                ct.putBoolean("Defused", false);
                EntityPredicate.Builder predicateBuilder = EntityPredicate.Builder.create()
                        .type(EntityType.CREEPER)
                        .nbt(new NbtPredicate(ct));

                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(Mbtw.CREEPER_OYSTER))
                        .conditionally(RandomChanceLootCondition.builder((float) 0.2))
                        .conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, predicateBuilder));

                tableBuilder.pool(poolBuilder);
            }
        });
    }
}
