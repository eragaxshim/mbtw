package mbtw.mbtw.gui.screen.ingame;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.screen.MillstoneScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MillstoneScreen extends AbstractMechanicalScreen<MillstoneScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(Mbtw.MOD_ID, "textures/gui/container/generic_mechanical.png");

    public MillstoneScreen(MillstoneScreenHandler container, PlayerInventory inventory, Text title) {
        super(container, inventory, title, TEXTURE);
    }
}
