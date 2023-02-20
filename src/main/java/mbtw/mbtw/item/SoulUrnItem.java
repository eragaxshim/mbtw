package mbtw.mbtw.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SoulUrnItem extends Item {

    public SoulUrnItem(Settings settings) {
        super(settings);
    }

    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
