package mbtw.mbtw.gui.screen.ingame;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.screen.CrucibleScreenHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CrucibleScreen extends AbstractMechanicalScreen<CrucibleScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(Mbtw.MOD_ID, "textures/gui/container/crucible.png");

    public CrucibleScreen(CrucibleScreenHandler container, PlayerInventory inventory, Text title) {
        super(container, inventory, title, TEXTURE);
    }

    protected void drawProcessingArrow(MatrixStack matrices, int i, int j) {
        int k = this.handler.getCookProgress();
        this.drawTexture(matrices, i + 89, j + 34, 176, 14, k + 1, 16);
    }

    protected void drawPowered(MatrixStack matrices, int i, int j) {
        if (this.handler.isPowered()) {
            this.drawTexture(matrices, i + 95, j + 54, 176, 0, 14, 14);
        }
    }
}
