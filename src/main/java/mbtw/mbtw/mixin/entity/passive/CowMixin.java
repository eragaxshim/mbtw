package mbtw.mbtw.mixin.entity.passive;

import mbtw.mbtw.entity.QuadrupedMixinAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CowEntity.class)
public abstract class CowMixin extends AnimalMixin implements QuadrupedMixinAccess {
    private int eatGrassTimer;
    private EatGrassGoal eatGrassGoal;

    @Inject(method = "initGoals", at = @At("HEAD"))
    protected void initGrassGoal(CallbackInfo ci) {
        this.eatGrassGoal = new EatGrassGoal(((CowEntity) (Object) this));
        this.goalSelector.add(6, this.eatGrassGoal);
    }

    @Override
    protected void changeMobTick(CallbackInfo ci) {
        this.eatGrassTimer = this.eatGrassGoal.getTimer();
    }

    @Override
    protected void changeTickMovement(CallbackInfo ci) {
        if (this.world.isClient) {
            this.eatGrassTimer = Math.max(0, this.eatGrassTimer - 1);
        }
    }

    @Override
    protected void changeEatinGrass(CallbackInfo ci) {
        super.changeEatinGrass(ci);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public float getNeckAngle(float delta) {
        if (this.eatGrassTimer <= 0) {
            return 0.0F;
        } else if (this.eatGrassTimer >= 4 && this.eatGrassTimer <= 36) {
            return 1.0F;
        } else {
            return this.eatGrassTimer < 4 ? ((float)this.eatGrassTimer - delta) / 4.0F : -((float)(this.eatGrassTimer - 40) - delta) / 4.0F;
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public float getHeadAngle(float delta) {
        if (this.eatGrassTimer > 4 && this.eatGrassTimer <= 36) {
            float f = ((float)(this.eatGrassTimer - 4) - delta) / 32.0F;
            return 0.62831855F + 0.21991149F * MathHelper.sin(f * 28.7F);
        } else {
            return this.eatGrassTimer > 0 ? 0.62831855F : this.pitch * 0.017453292F;
        }
    }
}
