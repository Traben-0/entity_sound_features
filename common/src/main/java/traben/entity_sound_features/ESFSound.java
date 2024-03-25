package traben.entity_sound_features;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantFloat;
import org.jetbrains.annotations.NotNull;

public class ESFSound extends Sound {
    public ESFSound(final ResourceLocation soundLocation) {
        super(soundLocation.toString(), ConstantFloat.of(1.0F), ConstantFloat.of(1.0F), 1, Type.FILE, false, false, 16);
    }

    public ESFSound(final ResourceLocation soundLocation, Sound copyFrom) {
        super(soundLocation.toString(),
                copyFrom.getVolume(),
                copyFrom.getPitch(),
                copyFrom.getWeight(),
                Type.FILE,
                copyFrom.shouldStream(),
                copyFrom.shouldPreload(),
                copyFrom.getAttenuationDistance());
    }

    @Override
    public @NotNull ResourceLocation getPath() {
        return getLocation();
    }
}
