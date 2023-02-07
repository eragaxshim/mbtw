package mbtw.mbtw.gui.screen.ingame;

import mbtw.mbtw.screen.MillstoneScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MillstoneScreen extends AbstractMechanicalScreen<MillstoneScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/blast_furnace.png");

    public MillstoneScreen(MillstoneScreenHandler container, PlayerInventory inventory, Text title) {
        super(container, inventory, title, TEXTURE);
    }
}
