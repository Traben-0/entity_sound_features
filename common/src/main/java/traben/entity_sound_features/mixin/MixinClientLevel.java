package traben.entity_sound_features.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_sound_features.ESFSoundContext;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(ClientLevel.class)
public abstract class MixinClientLevel {


    @Shadow
    protected abstract LevelEntityGetter<Entity> getEntities();

    @Inject(method = "playSound", at = @At(value = "HEAD"))
    private void esf$discoverEntity(final double x, final double y, final double z, final SoundEvent soundEvent,
                                    final SoundSource soundSource, final float g, final float h, final boolean bl,
                                    final long l, final CallbackInfo ci) {

        for (Entity entity : getEntities().getAll()) {
            if (entity.getBoundingBox().contains(x, y, z)) {
                //ESFClient.log("Sound called for: " + soundEvent.getLocation() + " for entity: " + entity.getType());
                ESFSoundContext.entitySource = (ETFEntity) entity;
                break;
            }
        }
    }

    @Inject(method = "playSound", at = @At(value = "TAIL"))
    private void esf$clearEntity(final CallbackInfo ci) {
//        ESFSoundContext.soundSource = null;
        ESFSoundContext.entitySource = null;
    }
}
