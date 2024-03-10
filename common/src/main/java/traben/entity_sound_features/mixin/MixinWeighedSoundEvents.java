package traben.entity_sound_features.mixin;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_sound_features.ESF;
import traben.entity_sound_features.ESFConfig;
import traben.entity_sound_features.ESFSoundContext;
import traben.entity_sound_features.ESFVariantSupplier;

@Mixin(WeighedSoundEvents.class)
public abstract class MixinWeighedSoundEvents {


    @Unique
    private ESFVariantSupplier esf$variator = null;


    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void esf$init(final ResourceLocation resourceLocation, final String string, final CallbackInfo ci) {
        esf$variator = ESFVariantSupplier.getOrNull(resourceLocation);
        if(esf$variator != null) ESFSoundContext.registerVariantSupplier(esf$variator);
    }


    @Inject(method = "getSound(Lnet/minecraft/util/RandomSource;)Lnet/minecraft/client/resources/sounds/Sound;", at = @At(value = "RETURN"), cancellable = true)
    private void esf$soundModify(final RandomSource randomSource, final CallbackInfoReturnable<Sound> cir) {
        if (esf$variator != null) {

            boolean announce = ESF.config().getConfig().announceCompatibleSounds != ESFConfig.AnnounceMode.NONE;
            if (announce) ESFSoundContext.announceSound(cir.getReturnValue().getLocation().toString(),true);

            Sound sound = esf$variator.getSoundVariantOrNull();
            if (sound != null) {
                if(announce) ESF.log("Sound modified to: " + sound.getLocation());
                cir.setReturnValue(sound);
            }
        }else if (ESF.config().getConfig().announceCompatibleSounds != ESFConfig.AnnounceMode.NONE) {
            ESFSoundContext.announceSound(cir.getReturnValue().getLocation().toString(),false);
        }
    }


}
