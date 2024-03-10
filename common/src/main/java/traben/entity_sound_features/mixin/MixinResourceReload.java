package traben.entity_sound_features.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_sound_features.ESFSoundContext;

@Mixin(Minecraft.class)
public abstract class MixinResourceReload {

    @Inject(method = "reloadResourcePacks()Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
    private void emf$reloadStart(CallbackInfoReturnable<Float> cir) {
        ESFSoundContext.resetContext();
    }

}
