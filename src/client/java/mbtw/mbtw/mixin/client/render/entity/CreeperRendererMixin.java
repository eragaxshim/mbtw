package mbtw.mbtw.mixin.client.render.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.entity.CreeperMixinAccess;
import net.minecraft.client.render.entity.CreeperEntityRenderer;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreeperEntityRenderer.class)
public class CreeperRendererMixin {
    private static final Identifier TEXTURE_DEFUSED = new Identifier(Mbtw.MOD_ID, "textures/entity/creeper/creeper_defused.png");

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    protected void changeTexture(CreeperEntity creeperEntity, CallbackInfoReturnable<Identifier> cir)
    {
        if (((CreeperMixinAccess)creeperEntity).getDefused())
        {
            cir.setReturnValue(TEXTURE_DEFUSED);
        }
    }
}
