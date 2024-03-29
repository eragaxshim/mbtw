package mbtw.mbtw.mixin.entity.mob;


import mbtw.mbtw.Mbtw;
import mbtw.mbtw.entity.CreeperMixinAccess;
import mbtw.mbtw.mixin.entity.MobEntityMixin;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreeperEntity.class)
public abstract class CreeperMixin extends MobEntityMixin implements CreeperMixinAccess, Shearable {
    @Shadow @Final private static TrackedData<Boolean> IGNITED;

    @Shadow public abstract void setFuseSpeed(int fuseSpeed);

    private static final TrackedData<Boolean> DEFUSED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public boolean isShearable()
    {
        return this.isAlive() && !this.getDefused();
    }

    public void sheared(SoundCategory shearedSoundCategory) {
        this.world.playSoundFromEntity(null, (CreeperEntity) (Object) this, SoundEvents.ENTITY_SHEEP_SHEAR, shearedSoundCategory, 1.0F, 1.0F);
        this.defuse();
        ItemEntity itemEntity = this.dropItem(Mbtw.CREEPER_OYSTER, 1);
        if (itemEntity != null) {
            itemEntity.setVelocity(itemEntity.getVelocity().add((this.random.nextFloat() - this.random.nextFloat()) * 0.1F, this.random.nextFloat() * 0.05F, (this.random.nextFloat() - this.random.nextFloat()) * 0.1F));
        }
    }

    public void defuse()
    {
        this.dataTracker.set(IGNITED, false);
        this.dataTracker.set(DEFUSED, true);
        this.setFuseSpeed(-1);
    }

    @Override
    protected void changeInteraction(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() == Items.SHEARS) {
            if (!this.world.isClient && this.isShearable()) {
                this.sheared(SoundCategory.PLAYERS);
                itemStack.damage(1, player, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

                cir.setReturnValue(ActionResult.SUCCESS);
            } else {
                cir.setReturnValue(ActionResult.CONSUME);
            }
        }
        else {
            super.changeInteraction(player, hand, cir);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeExtraData(NbtCompound tag, CallbackInfo ci) {
        tag.putBoolean("Defused", this.getDefused());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readExtraData(NbtCompound tag, CallbackInfo ci) {
        this.dataTracker.set(DEFUSED, tag.getBoolean("Defused"));
    }

    public boolean getDefused() {
        return this.dataTracker.get(DEFUSED);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void initDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(DEFUSED, false);
    }

}
