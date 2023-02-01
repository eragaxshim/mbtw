package mbtw.mbtw.mixin.block.entity;

import mbtw.mbtw.block.entity.CampfireBlockEntityMixinAccessor;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CampfireBlockEntity.class)
public interface CampfireBlockEntityAccessor extends CampfireBlockEntityMixinAccessor {
    @Accessor("itemsBeingCooked")
    DefaultedList<ItemStack> getItemsBeingCooked();

    @Accessor("cookingTimes")
    int[] getCookingTimes();

    @Accessor("cookingTotalTimes")
    int[] getCookingTotalTimes();
}
