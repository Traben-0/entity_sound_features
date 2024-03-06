package traben.entity_sound_features.mixin;

import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(SoundManager.Preparations.class)
public abstract class MixinSoundManager$Preparations {
    @Shadow
    public Map<ResourceLocation, Resource> soundCache;

    @Inject(method = "listResources", at = @At(value = "TAIL"))
    private void esf$addESFDirectorySounds(final ResourceManager resourceManager, final CallbackInfo ci) {
        this.soundCache.putAll(new FileToIdConverter("esf", ".ogg").listMatchingResources(resourceManager));
    }


}
