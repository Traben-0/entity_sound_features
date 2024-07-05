package traben.entity_sound_features.methods;


import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.math.*;
import traben.entity_sound_features.ESF;

import java.util.List;

import static traben.entity_model_features.models.animation.math.MathExpressionParser.NULL_EXPRESSION;

public class ESFPlaySoundMethodFactory extends MathMethod {

    private long lastSoundTick = 0;

    public ESFPlaySoundMethodFactory(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());
        boolean log = ESF.config().getConfig().logSoundSetup;
        if (log) ESF.log("creating sound EMF math function definition (no guaranteed failure log messages unless you enable EMF math logging):\n > " + args);
        try {
            //sound event
            String soundId = args.get(0);
            if (soundId == null || soundId.isBlank())
                throw new EMFMathException("Sound event ID invalid");
            var res = ESF.res(soundId);
            var event = Minecraft.getInstance().getSoundManager().getSoundEvent(res);
            if (event == null)
                throw new EMFMathException("Sound event not found: " + soundId);
            //event is valid sound event from here

            //required args
            MathComponent trueForSound = MathExpressionParser.getOptimizedExpression(args.get(1), false, calculationInstance);
            MathComponent delay = MathExpressionParser.getOptimizedExpression(args.get(2), false, calculationInstance);
            if (delay == NULL_EXPRESSION || trueForSound == NULL_EXPRESSION)
                throw new EMFMathException("Invalid arguments");
            //required args valid

            //optional args
            MathComponent volume;
            MathComponent pitch;
            MathComponent range;
            if (args.size() == 6) {
                volume = MathExpressionParser.getOptimizedExpression(args.get(3), false, calculationInstance);
                pitch = MathExpressionParser.getOptimizedExpression(args.get(4), false, calculationInstance);
                range = MathExpressionParser.getOptimizedExpression(args.get(5), false, calculationInstance);
                if (volume == NULL_EXPRESSION || pitch == NULL_EXPRESSION)
                    throw new EMFMathException("Invalid optional arguments");
            } else {
                volume = () -> 1f;
                pitch = () -> 1f;
                range = () -> 16f;
            }
            //all args valid

            setSupplierAndOptimize(() -> {
                //check entity
                var emfEntity = EMFAnimationEntityContext.getEMFEntity();
                if (emfEntity == null || emfEntity.etf$getWorld() == null) return MathValue.FALSE;

                //check delay
                float delayVal = delay.getResult();
                var currentTick = emfEntity.etf$getWorld().getGameTime();
                if (lastSoundTick + delayVal > currentTick) return MathValue.FALSE;

                //play sound
                lastSoundTick = currentTick;
                try {
                    emfEntity.etf$getWorld().playSound(Minecraft.getInstance().player, emfEntity.etf$getBlockPos(),
                            SoundEvent.createFixedRangeEvent(res, Math.clamp(range.getResult(),0,128)),
                            emfEntity instanceof Entity entity ? entity.getSoundSource() : SoundSource.BLOCKS,
                             Math.clamp(volume.getResult(),0,1), Math.clamp(pitch.getResult(),0,1));
                    return MathValue.TRUE;
                } catch (Exception e) {
                    return MathValue.FALSE;
                }
            });
            if (log) ESF.log("sound EMF math function definition created successfully for " + res);

        } catch (Exception e) {
            //wrap any exception in EMFMathException adding function name
            if (log) ESF.log("Error creating sound EMF math function definition: " + e.getMessage());
            throw new EMFMathException("Error in playsound() function: " + e.getMessage());
        }
    }

    @Override
    protected boolean canOptimizeForConstantArgs() {
        return false;
    }

    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount == 3 || argCount == 6;
    }
}
