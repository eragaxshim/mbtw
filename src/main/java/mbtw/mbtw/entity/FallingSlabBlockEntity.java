package mbtw.mbtw.entity;

import com.google.common.collect.Lists;
import mbtw.mbtw.block.FallingSlabBlock;
import mbtw.mbtw.mixin.entity.FallingBlockMixin;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.Tag;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class FallingSlabBlockEntity extends FallingBlockEntity {
    private BlockState sourceBlock;

    public FallingSlabBlockEntity(World world, double x, double y, double z, BlockState block, BlockState sourceBlock) {
        super(world, x, y, z, block);
        this.sourceBlock = sourceBlock;
    }
    
    private static BlockState changeBlock(BlockState block)
    {
        if (block.get(Properties.SLAB_TYPE) == SlabType.TOP) {
            return block.with(Properties.SLAB_TYPE, SlabType.BOTTOM);
        }
        else {
            return block;
        }
    }

    public void tick() {
        int tt = this.timeFalling;
        if (((FallingBlockMixin)this).getBlock().isAir()) {
            this.remove();
        } else {
            Block block = ((FallingBlockMixin)this).getBlock().getBlock();
            BlockPos blockPos2;
            if (this.timeFalling++ == 0) {
                blockPos2 = this.getBlockPos();
                if (this.world.getBlockState(blockPos2).isOf(sourceBlock.getBlock())) {
                    this.world.removeBlock(blockPos2, false);
                } else if (!this.world.isClient) {
                    this.remove();
                    return;
                }
            }

            if (!this.hasNoGravity()) {
                this.setVelocity(this.getVelocity().add(0.0D, -0.04D, 0.0D));
            }

            this.move(MovementType.SELF, this.getVelocity());
            if (!this.world.isClient) {
                blockPos2 = this.getBlockPos();
                boolean bl = block instanceof ConcretePowderBlock;
                boolean bl2 = bl && this.world.getFluidState(blockPos2).isIn(FluidTags.WATER);
                double d = this.getVelocity().lengthSquared();
                if (bl && d > 1.0D) {
                    BlockHitResult blockHitResult = this.world.raycast(new RaycastContext(new Vec3d(this.prevX, this.prevY, this.prevZ), this.getPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.SOURCE_ONLY, this));
                    if (blockHitResult.getType() != HitResult.Type.MISS && this.world.getFluidState(blockHitResult.getBlockPos()).isIn(FluidTags.WATER)) {
                        blockPos2 = blockHitResult.getBlockPos();
                        bl2 = true;
                    }
                }

                if (!this.onGround && !bl2) {
                    if (!this.world.isClient && (this.timeFalling > 100 && (blockPos2.getY() < 1 || blockPos2.getY() > 256) || this.timeFalling > 600)) {
                        if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                            this.dropItem(sourceBlock.getBlock());
                            System.out.println("dropp3");
                        }
                        this.remove();
                    }
                    System.out.println("next tick moving");
                } else {
                    BlockState blockState = this.world.getBlockState(blockPos2);
                    this.setVelocity(this.getVelocity().multiply(0.7D, -0.5D, 0.7D));
                    if (!blockState.isOf(Blocks.MOVING_PISTON)) {
                        this.remove();
                        if (!((FallingBlockMixin)this).getDestroyedOnLanding()) {
                            boolean bl3 = blockState.canReplace(new AutomaticItemPlacementContext(this.world, blockPos2, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                            boolean bl4 = FallingBlock.canFallThrough(this.world.getBlockState(blockPos2.down())) && (!bl || !bl2);
                            boolean bl5 = ((FallingBlockMixin)this).getBlock().canPlaceAt(this.world, blockPos2) && !bl4;
                            if (bl3 && bl5) {
                                if (((FallingBlockMixin)this).getBlock().contains(Properties.WATERLOGGED) && this.world.getFluidState(blockPos2).getFluid() == Fluids.WATER) {
                                    ((FallingBlockMixin)this).setBlock(((FallingBlockMixin)this).getBlock().with(Properties.WATERLOGGED, true));
                                }

                                if (this.world.setBlockState(blockPos2, sourceBlock, 3)) {
                                    if (block instanceof FallingSlabBlock) {
                                        ((FallingSlabBlock)block).onLanding(this.world, blockPos2, sourceBlock, blockState, this);
                                    }

                                    if (this.blockEntityData != null && block instanceof BlockEntityProvider) {
                                        BlockEntity blockEntity = this.world.getBlockEntity(blockPos2);
                                        if (blockEntity != null) {
                                            CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());

                                            for (String string : this.blockEntityData.getKeys()) {
                                                Tag tag = this.blockEntityData.get(string);
                                                if (!"x".equals(string) && !"y".equals(string) && !"z".equals(string)) {
                                                    compoundTag.put(string, tag.copy());
                                                }
                                            }

                                            blockEntity.fromTag(((FallingBlockMixin)this).getBlock(), compoundTag);
                                            blockEntity.markDirty();
                                        }
                                    }
                                } else if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                                    this.dropItem(sourceBlock.getBlock());
                                    System.out.println("dropp1");
                                }
                            } else if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                                this.dropItem(sourceBlock.getBlock());
                                System.out.println("dropp2");
                            }
                        } else if (block instanceof FallingSlabBlock) {
                            ((FallingSlabBlock)block).onDestroyedOnLanding(this.world, blockPos2, this);
                        }
                    }
                }
            }

            this.setVelocity(this.getVelocity().multiply(0.98D));
        }
    }

    protected void writeCustomDataToTag(CompoundTag tag) {
        tag.put("SourceBlockState", NbtHelper.fromBlockState(sourceBlock));
        super.writeCustomDataToTag(tag);
    }

    protected void readCustomDataFromTag(CompoundTag tag) {
        this.sourceBlock = NbtHelper.toBlockState(tag.getCompound("SourceBlockState"));
        super.readCustomDataFromTag(tag);
    }
}
