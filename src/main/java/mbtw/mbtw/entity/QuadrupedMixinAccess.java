package mbtw.mbtw.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface QuadrupedMixinAccess {
    @Environment(EnvType.CLIENT)
    float getNeckAngle(float delta);

    @Environment(EnvType.CLIENT)
    float getHeadAngle(float delta);
}
