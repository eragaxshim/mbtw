package mbtw.mbtw.block;

import net.minecraft.util.Identifier;

public interface HopperConversionStore {
    int incrementConversionProgress(Identifier conversionType);

    void resetConversionProgress(Identifier conversionType);
}
