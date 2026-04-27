package traben.entity_sound_features.methods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import traben.entity_model_features.EMFAnimationApi;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.math.asm.ASMHelper;
import traben.entity_sound_features.ESF;

import java.util.HashMap;
import java.util.Map;

import static traben.entity_sound_features.ESF.MOD_ID;

@SuppressWarnings("unused")
public abstract class ESFMethods {

    public static void register() {
        try {
            EMFAnimationApi.registerCustomFunctionFromStaticMethod(
                    MOD_ID,
                    "playingsound",
                    "entity_sound_features.config.playingsound.method",
                    ASMHelper.getStaticMethod("playingSound", ESFMethods.class),
                    null
            );
            EMFAnimationApi.registerCustomFunctionFromStaticMethod(
                    MOD_ID,
                    "isplayingsound",
                    "entity_sound_features.config.playingsound.method",
                    ASMHelper.getStaticMethod("playingSound", ESFMethods.class),
                    null
            );

            EMFAnimationApi.registerCustomFunctionFromStaticMethod(
                    MOD_ID,
                    "playsound",
                    "entity_sound_features.config.playsound.method",
                    ASMHelper.getStaticMethod("playsoundStatic", ESFMethods.class),
                    null
            );
        } catch (Exception e) {
            ESF.logError("Failed to register ESF math methods: " + e.getMessage());
        }
    }

    public static boolean playingSound(String soundId) {
        if (soundId == null || soundId.isBlank()) {
            ESF.logError("Sound event ID invalid: " + soundId);
            return false;
        }
        var test = ESF.res(soundId);

        var engine = Minecraft.getInstance().getSoundManager().soundEngine;
        if (!engine.loaded) return false;

        boolean found = false;
        for (Map.Entry<SoundInstance, Integer> sound : engine.soundDeleteTime.entrySet()) {
            if (sound.getValue() > engine.tickCount) {
                continue;
            }
            var res = sound.getKey()
                    //#if MC >= 26.1
                    //$$ .getIdentifier();
                    //#else
                    .getLocation();
                    //#endif
            if (res.equals(test)) {
                found = true;
                break;
            }
        }
        if (!found) {
            for (SoundInstance sound : engine.instanceToChannel.keySet()) {
                var res = sound
                        //#if MC >= 26.1
                        //$$ .getIdentifier();
                        //#else
                        .getLocation();
                        //#endif
                if (res.equals(test)) {
                    found = true;
                    break;
                }

            }
        }
        return found;
    }


    private static final Map<String, Long> lastSoundTicks = new HashMap<>();

    public static boolean playsoundStatic(String soundId, boolean trueForSound, float delay, float volume, float pitch, float range) {
        if (!trueForSound) return false;

        // check entity
        var emfEntity = EMFAnimationEntityContext.getEMFEntity();
        if (emfEntity == null || emfEntity.etf$getWorld() == null) return false;

        // sound event
        if (soundId == null || soundId.isBlank()) {
            ESF.logError("Sound event ID invalid: " + soundId);
            return false;
        }

        // check delay
        var currentTick = emfEntity.etf$getWorld().getGameTime();
        Long lastTick = lastSoundTicks.getOrDefault(soundId, 0L);
        if (lastTick + (delay < 1 ? 1 : delay) > currentTick) return false;
        lastSoundTicks.put(soundId, currentTick);


        var res = ESF.res(soundId);
        var event = Minecraft.getInstance().getSoundManager().getSoundEvent(res);
        if (event == null) {
            ESF.logError("Sound event not found: " + soundId);
            return false;
        }
        // event is valid sound event from here

        try {
            emfEntity.etf$getWorld().playSound(Minecraft.getInstance().player, emfEntity.etf$getBlockPos(),
                    SoundEvent.createFixedRangeEvent(res, Mth.clamp( range, 0, 128)),
                    emfEntity instanceof Entity entity ? entity.getSoundSource() : SoundSource.BLOCKS,
                    Mth.clamp( volume, 0, 1), Mth.clamp(pitch, 0.5F, 2.0F));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
