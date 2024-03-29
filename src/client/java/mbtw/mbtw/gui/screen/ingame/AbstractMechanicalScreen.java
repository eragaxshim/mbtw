package mbtw.mbtw.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import mbtw.mbtw.screen.AbstractProcessorScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AbstractMechanicalScreen<T extends AbstractProcessorScreenHandler> extends HandledScreen<T> {
    private final Identifier background;

    public AbstractMechanicalScreen(T handler, PlayerInventory inventory, Text title, Identifier background) {
        super(handler, inventory, title);
        this.background = background;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, this.background);
        int i = this.x;
        int j = this.y;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        drawProcessingArrow(matrices, i, j);
        drawPowered(matrices, i, j);
    }

    protected void drawProcessingArrow(MatrixStack matrices, int i, int j) {
        int k = this.handler.getCookProgress();
        this.drawTexture(matrices, i + 79, j + 34, 176, 14, k + 1, 16);
    }

    protected void drawPowered(MatrixStack matrices, int i, int j) {
        if (this.handler.isPowered()) {
            this.drawTexture(matrices, i + 57, j + 34 + 12, 176, 0, 14, 14);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
