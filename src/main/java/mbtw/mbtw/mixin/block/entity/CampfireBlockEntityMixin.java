package mbtw.mbtw.mixin.block.entity;

import mbtw.mbtw.block.entity.VariableCampfireBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntity.class)
public abstract class CampfireBlockEntityMixin extends BlockEntity implements CampfireBlockEntityAccessor {
    @Mutable
    @Shadow @Final private DefaultedList<ItemStack> itemsBeingCooked;

    @Mutable
    @Shadow @Final private int[] cookingTimes;

    @Mutable
    @Shadow @Final private int[] cookingTotalTimes;

    @Shadow protected abstract void updateListeners();

    private boolean[] finishedItems;

    public CampfireBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    protected void changeMaxItems(CallbackInfo ci)
    {
        this.itemsBeingCooked = DefaultedList.ofSize(2, ItemStack.EMPTY);
        this.cookingTimes = new int[2];
        this.cookingTotalTimes = new int[2];
        this.finishedItems = new boolean[2];
    }

    @Inject(method = "clientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;getHorizontal()I"), cancellable = true)
    private static void changeSmoke(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo ci)
    {
        if (campfire instanceof VariableCampfireBlockEntity && world != null)
        {
            Direction direction = state.get(CampfireBlock.FACING);

            DefaultedList<ItemStack> itemsBeingCooked = ((CampfireBlockEntityAccessor)campfire).getItemsBeingCooked();
            for(int k = 0; k < itemsBeingCooked.size(); ++k) {
                if (!((ItemStack)itemsBeingCooked.get(k)).isEmpty() && world.getRandom().nextFloat() < 0.2F) {
                    int d = k == 0 ? 1 : -1;
                    double x = (double)pos.getX() + 0.5D + (double)((float)direction.getOffsetX() * 0.3125F * d);
                    double y = (double)pos.getY() + 0.5D;
                    double z = (double)pos.getZ() + 0.5D + (double)((float)direction.getOffsetZ() * 0.3125F) * d;

                    for(int l = 0; l < 4; ++l) {
                        world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 5.0E-4D, 0.0D);
                    }
                }
            }
            ci.cancel();
        }
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    protected void addToTag(NbtCompound nbt, CallbackInfo ci)
    {
        byte[] byteArray = new byte[this.finishedItems.length];
        for (int i = 0; i < this.finishedItems.length; i++)
        {
            byteArray[i] = (byte) (this.finishedItems[i] ? 1 : 0);
        }
        nbt.putByteArray("FinishedItems", byteArray);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    protected void addFromTag(NbtCompound nbt, CallbackInfo ci)
    {
        byte[] byteArray = nbt.getByteArray("FinishedItems");
        for (int i = 0; i < this.finishedItems.length; i++)
        {
            if (byteArray.length > i)
            {
                this.finishedItems[i] = byteArray[i] == 1;
            }
        }
    }

    public ItemStack getFinishedStack()
    {
        for(int i = this.itemsBeingCooked.size() - 1; i >= 0 ; i--) {
            if (this.finishedItems[i] && !this.itemsBeingCooked.get(i).isEmpty())
            {
                this.finishedItems[i] = false;
                ItemStack finishedStack = this.itemsBeingCooked.get(i);
                this.itemsBeingCooked.set(i, ItemStack.EMPTY);
                this.updateListeners();
                return finishedStack;
            }

        }
        return null;
    }

    public boolean[] getFinishedItems() {
        return finishedItems;
    }
}
