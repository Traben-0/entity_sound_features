package traben.entity_sound_features.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_sound_features.ESF;
import traben.entity_sound_features.ESFSoundContext;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class MixinClientLevel extends Level {


    @Shadow protected abstract @NotNull LevelEntityGetter<Entity> getEntities();

    protected MixinClientLevel(final WritableLevelData writableLevelData, final ResourceKey<Level> resourceKey, final RegistryAccess registryAccess, final Holder<DimensionType> holder, final Supplier<ProfilerFiller> supplier, final boolean bl, final boolean bl2, final long l, final int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
    }


    @Inject(method = "playSound", at = @At(value = "HEAD"))
    private void esf$discoverEntity(final double x, final double y, final double z, final SoundEvent soundEvent,
                                    final SoundSource soundSource, final float g, final float h, final boolean bl,
                                    final long l, final CallbackInfo ci) {

        ESFSoundContext.entitySource = null;
        //todo consider sound source limiting???
        if (ESFSoundContext.shouldCaptureEntity(soundEvent.getLocation())) {
            ESFSoundContext.searchForEntity(getEntities().getAll(), x, y, z);
            if(ESFSoundContext.entitySource == null)
                ESFSoundContext.searchForBlockEntity(this, x, y, z);
        }
        if (ESF.config().getConfig().announceCompatibleSounds.all()) {
            ESF.log("found entity at " + ESFSoundContext.entitySource.etf$getBlockPos().toShortString() + ", for sound at " + x + ", " + y + ", " + z);
        }
    }

    @Inject(method = "playSound", at = @At(value = "TAIL"))
    private void esf$clearEntity(final CallbackInfo ci) {
        ESFSoundContext.entitySource = null;
    }
}
