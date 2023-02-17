package mbtw.mbtw.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class NbtUtil {
    public static Item itemFromNbt(NbtCompound nbt, String key) {
        String itemString = nbt.getString(key);
        Optional<Item> item;
        if (itemString.isEmpty() || (item = Registries.ITEM.getOrEmpty(new Identifier(itemString))).isEmpty()) {
            return Items.AIR;
        }
        return item.get();
    }

    public static void writeItemToNbt(NbtCompound nbt, String key, Item item) {
        nbt.putString(key, Registries.ITEM.getId(item).toString());
    }

    public static Block blockFromNbt(NbtCompound nbt, String key) {
        String blockString = nbt.getString(key);
        Optional<Block> block;
        if (blockString.isEmpty() || (block = Registries.BLOCK.getOrEmpty(new Identifier(blockString))).isEmpty()) {
            return Blocks.AIR;
        }
        return block.get();
    }

    public static void writeBlockToNbt(NbtCompound nbt, String key, Block block) {
        nbt.putString(key, Registries.BLOCK.getId(block).toString());
    }
}
