package mbtw.mbtw.block;

import net.minecraft.util.Identifier;

public interface HopperResidueStore {
    int increaseResidue(Identifier residueType, int amount);

    void resetResidue(Identifier residueType);

    boolean passingResidue();
}
