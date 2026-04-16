package traben.entity_sound_features;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.util.valueproviders.ConstantFloat;
import org.jetbrains.annotations.NotNull;

public class ESFSound extends Sound {
    public ESFSound(
            //#if MC >= 26.1
            //$$ net.minecraft.resources.Identifier soundLocation
            //#else
            net.minecraft.resources.ResourceLocation soundLocation
            //#endif
    ) {
        super(
                //#if MC >= 12100
                soundLocation,
                //#else
                //$$ soundLocation.toString(),
                //#endif
                ConstantFloat.of(1.0F), ConstantFloat.of(1.0F), 1, Type.FILE, false, false, 16);
    }

    public ESFSound(
            //#if MC >= 26.1
            //$$ net.minecraft.resources.Identifier soundLocation,
            //#else
            net.minecraft.resources.ResourceLocation soundLocation,
            //#endif
            Sound copyFrom) {
        super(
                //#if MC >= 12100
                soundLocation,
                //#else
                //$$ soundLocation.toString(),
                //#endif
                copyFrom.getVolume(),
                copyFrom.getPitch(),
                copyFrom.getWeight(),
                Type.FILE,
                copyFrom.shouldStream(),
                copyFrom.shouldPreload(),
                copyFrom.getAttenuationDistance());
    }

    //#if MC >= 26.1
    //$$ @Override public @NotNull net.minecraft.resources.Identifier getPath() {  return getLocation(); }
    //#else
    @Override public @NotNull net.minecraft.resources.ResourceLocation getPath() {
        return getLocation();
    }
    //#endif
}
