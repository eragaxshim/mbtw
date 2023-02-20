package mbtw.mbtw.block;

import net.minecraft.block.Block;
import net.minecraft.util.shape.VoxelShape;

public class ShapeHelper {
    public static VoxelShape createCuboidShapeFromArray(Double[] coords)
    {
        return Block.createCuboidShape(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
    }

    public static Double[] rotateCuboidCoords(Double[] coords, int rotation)
    {
        return new Double[]{coords[rotation % 3], coords[(rotation+1) % 3], coords[(rotation+2) % 3],
                coords[rotation%3 + 3], coords[(rotation+1)%3 + 3], coords[(rotation+2)%3 + 3]};
    }

    public static Double[] translateCuboidCoords(Double[] coords, double x, double y, double z) {
        return new Double[]{coords[0]+x, coords[1]+y, coords[2]+z, coords[3]+x, coords[4]+y, coords[5]+z};
    }
}
