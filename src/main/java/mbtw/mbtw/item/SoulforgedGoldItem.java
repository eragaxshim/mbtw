package mbtw.mbtw.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SoulforgedGoldItem extends Item {
    public SoulforgedGoldItem(Settings settings) {
        super(settings);
    }

    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
