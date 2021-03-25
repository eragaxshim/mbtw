package mbtw.mbtw.mixin.block;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.VariableCampfireBlock;
import mbtw.mbtw.block.entity.CampfireBlockEntityMixinAccessor;
import mbtw.mbtw.block.entity.VariableCampfireBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CampfireBlockEntity.class)
public abstract class CampfireBlockEntityMixin extends BlockEntity implements CampfireBlockEntityMixinAccessor {
    @Mutable
    @Shadow @Final private DefaultedList<ItemStack> itemsBeingCooked;

    @Mutable
    @Shadow @Final private int[] cookingTimes;

    @Mutable
    @Shadow @Final private int[] cookingTotalTimes;

    @Shadow protected abstract void updateListeners();

    private boolean[] finishedItems;

    public CampfireBlockEntityMixin(BlockEntityType<?> type) {
        super(type);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    protected void changeMaxItems(CallbackInfo ci)
    {
        this.itemsBeingCooked = DefaultedList.ofSize(2, ItemStack.EMPTY);
        this.cookingTimes = new int[2];
        this.cookingTotalTimes = new int[2];
        this.finishedItems = new boolean[2];
    }

    @Inject(method = "spawnSmokeParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;getHorizontal()I"), cancellable = true)
    protected void changeSmoke(CallbackInfo ci)
    {
        if ((Object) this instanceof VariableCampfireBlockEntity && this.world != null)
        {
            Direction direction = this.getCachedState().get(CampfireBlock.FACING);

            for(int k = 0; k < this.itemsBeingCooked.size(); ++k) {
                if (!((ItemStack)this.itemsBeingCooked.get(k)).isEmpty() && this.world.getRandom().nextFloat() < 0.2F) {
                    int d = k == 0 ? 1 : -1;
                    double x = (double)this.pos.getX() + 0.5D + (double)((float)direction.getOffsetX() * 0.3125F * d);
                    double y = (double)this.pos.getY() + 0.5D;
                    double z = (double)this.pos.getZ() + 0.5D + (double)((float)direction.getOffsetZ() * 0.3125F) * d;

                    for(int l = 0; l < 4; ++l) {
                        this.world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 5.0E-4D, 0.0D);
                    }
                }
            }
            ci.cancel();
        }
    }

    @Inject(method = "updateItemsBeingCooked", at = @At("HEAD"), cancellable = true)
    protected void changeUpdateItems(CallbackInfo ci)
    {
        BlockState state = this.getCachedState();

        if ((Object) this instanceof VariableCampfireBlockEntity && state.getBlock() instanceof VariableCampfireBlock)
        {
            for(int i = 0; i < this.itemsBeingCooked.size(); ++i) {
                ItemStack itemStack = (ItemStack)this.itemsBeingCooked.get(i);
                if (!itemStack.isEmpty()) {
                    int fireSize = state.get(VariableCampfireBlock.FIRE_SIZE);
                    this.cookingTimes[i] += 1 - (fireSize == 1 ? this.world.getRandom().nextInt(1) : 0) + (fireSize == 4 ? 1 : 0);


                    if (this.finishedItems[i] && this.cookingTimes[i] > 2 * this.cookingTotalTimes[i])
                    {
                        BlockPos blockPos = this.getPos();
                        ItemScatterer.spawn(this.world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), Mbtw.ASH_PILE.getDefaultStack());
                        this.finishedItems[i] = false;
                        this.itemsBeingCooked.set(i, ItemStack.EMPTY);
                    }
                    else if (!this.finishedItems[i] && this.cookingTimes[i] >= this.cookingTotalTimes[i]) {
                        Inventory inventory = new SimpleInventory(itemStack);
                        ItemStack craftedStack = (ItemStack)this.world.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, inventory, this.world).map((campfireCookingRecipe) -> campfireCookingRecipe.craft(inventory)).orElse(itemStack);
                        if (craftedStack != itemStack)
                        {
                            this.finishedItems[i] = true;
                            this.itemsBeingCooked.set(i, craftedStack);
                        }
                        else {
                            BlockPos blockPos = this.getPos();
                            ItemScatterer.spawn(this.world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), craftedStack);
                            this.itemsBeingCooked.set(i, ItemStack.EMPTY);
                        }
                    }
                    this.updateListeners();
                }
            }
            ci.cancel();
        }
    }

    @Inject(method = "toTag", at = @At("TAIL"))
    protected void addToTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir)
    {
        byte[] byteArray = new byte[this.finishedItems.length];
        for (int i = 0; i < this.finishedItems.length; i++)
        {
            byteArray[i] = (byte) (this.finishedItems[i] ? 1 : 0);
        }
        tag.putByteArray("FinishedItems", byteArray);
    }

    @Inject(method = "fromTag", at = @At("TAIL"))
    protected void addFromTag(BlockState state, CompoundTag tag, CallbackInfo ci)
    {
        byte[] byteArray = tag.getByteArray("FinishedItems");
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
}
