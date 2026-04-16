package traben.entity_sound_features.mixin.mixins;

import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.FileToIdConverter;
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
    //#if MC >= 26.1
    //$$ @Shadow public Map<net.minecraft.resources.Identifier, Resource> soundCache;
    //#else
    @Shadow public Map<net.minecraft.resources.ResourceLocation, Resource> soundCache;
    //#endif

    @Inject(method = "listResources", at = @At(value = "TAIL"))
    private void esf$addESFDirectorySounds(final ResourceManager resourceManager, final CallbackInfo ci) {
        this.soundCache.putAll(new FileToIdConverter("esf", ".ogg").listMatchingResources(resourceManager));
    }


}
