package traben.entity_sound_features.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_sound_features.ESF;
import traben.entity_sound_features.ESFSoundContext;
import traben.entity_texture_features.utils.ETFEntity;


@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void emf$reloadStart(final EntityType<?> entityType, final Level level, final CallbackInfo ci) {
        if (ESF.config().getConfig().preCheckAllEntities
                && level.isClientSide()
                && ((Entity) (Object) this) instanceof ETFEntity etf) {
            ESFSoundContext.preTestEntity(etf);
        }
    }

}
