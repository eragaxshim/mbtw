package mbtw.mbtw.client.gui.screen.ingame;

import mbtw.mbtw.screen.BrickOvenScreenHandler;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.recipebook.BlastFurnaceRecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.FurnaceRecipeBookScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BrickOvenScreen extends AbstractFurnaceScreen<BrickOvenScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/blast_furnace.png");

    public BrickOvenScreen(BrickOvenScreenHandler container, PlayerInventory inventory, Text title) {
        super(container, new FurnaceRecipeBookScreen(), inventory, title, TEXTURE);
    }
}
