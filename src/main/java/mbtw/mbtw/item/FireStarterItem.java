package mbtw.mbtw.item;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.Igniteable;
import mbtw.mbtw.tag.MbtwTagsMaps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class FireStarterItem extends ConsumeDamageItem{
    private final float startEfficiency;

    public FireStarterItem(Settings settings, int useTime, ItemStack targetItem, float startEfficiency) {
        super(settings, useTime, targetItem, false);
        this.startEfficiency = startEfficiency;
    }

    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
        BlockPos pos = raycastIgnoreAir(world, user, 4.5D, 1.0F, false);
        //System.out.println(world.getBlockState(new BlockPos(hitResult.getPos())));
        //System.out.println(blockState);
        if (pos != null && stack.getItem() instanceof FireStarterItem)
        {
            BlockState targetBlock = world.getBlockState(pos);
            if (targetBlock.getBlock() instanceof Igniteable)
            {
                ((Igniteable) targetBlock.getBlock()).attemptFireStart(world, user, stack, targetBlock, pos);
            }
            else if (targetBlock.getBlock().isIn(MbtwTagsMaps.IGNITEABLES))
            {

            }
            super.usageTick(world, user, stack, remainingUseTicks);
        }
    }

    public BlockPos raycastIgnoreAir(World world, LivingEntity user, double maxDistance, float tickDelta, boolean includeFluids) {
        Vec3d vec3d = user.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = user.getRotationVec(tickDelta);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
        BlockPos pos = null;
        while (vec3d3.distanceTo(vec3d) > 0.5D) {
            BlockHitResult result = world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.COLLIDER, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, user));
            if (!(result.getType() == HitResult.Type.MISS)) {
                pos = result.getBlockPos();
                if (!world.getBlockState(pos).isOf(Blocks.AIR)) {
                    break;
                }
            }

            vec3d = vec3d.add(vec3d2.x * 0.5D, vec3d2.y * 0.5D, vec3d2.z * 0.5D);
        }
        return pos;
    }

    public float getStartEfficiency()
    {
        return this.startEfficiency;
    }
}
