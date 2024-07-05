package traben.entity_sound_features.methods;


import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.*;
import traben.entity_sound_features.ESF;

import java.util.List;
import java.util.Map;

public class ESFIsPlayingSoundMethodFactory extends MathMethod {


    public ESFIsPlayingSoundMethodFactory(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());
        boolean log = ESF.config().getConfig().logSoundSetup;
        if (log) ESF.log("creating sound EMF math function definition (no guaranteed failure log messages unless you enable EMF math logging):\n > " + args);
        try {
            //sound event
            String soundId = args.get(0);
            if (soundId == null || soundId.isBlank())
                throw new EMFMathException("Sound event ID invalid");
            var test = ESF.res(soundId);

            //all args valid


            setSupplierAndOptimize(() -> {
                var engine = Minecraft.getInstance().getSoundManager().soundEngine;
                if (!engine.loaded) return MathValue.FALSE;

                boolean found = false;
                for (Map.Entry<SoundInstance, Integer> sound : engine.soundDeleteTime.entrySet()) {
                    if (sound.getValue() > engine.tickCount) {
                        continue;
                    }
                    var res = sound.getKey().getLocation();
                    if (res.equals(test)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    for (SoundInstance sound : engine.instanceToChannel.keySet()) {
                        var res = sound.getLocation();
                        if (res.equals(test)) {
                            found = true;
                            break;
                        }

                    }
                }
                return MathValue.fromBoolean(found);
            });
            if (log) ESF.log("sound EMF math function definition created successfully for " + test);

        } catch (Exception e) {
            //wrap any exception in EMFMathException adding function name
            if (log) ESF.log("Error creating sound EMF math function definition: " + e.getMessage());
            throw new EMFMathException("Error in playingsound() function: " + e.getMessage());
        }
    }

    @Override
    protected boolean canOptimizeForConstantArgs() {
        return false;
    }

    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount == 1;
    }
}
