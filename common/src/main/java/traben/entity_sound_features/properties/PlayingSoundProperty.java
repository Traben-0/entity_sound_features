package traben.entity_sound_features.properties;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_sound_features.ESF;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;
import java.util.Set;

public class PlayingSoundProperty extends StringArrayOrRegexProperty {

    private final boolean doPrint;
    protected PlayingSoundProperty(String values, boolean printing) throws RandomProperty.RandomPropertyException {
        super(values);
        doPrint = printing;
    }
    public static PlayingSoundProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            var val =RandomProperty.readPropertiesOrThrow(properties, propertyNum, "playingSound", "playingSounds");
            boolean printing = val.startsWith("print:");
            return new PlayingSoundProperty(printing ? val.replaceFirst("print:" , "") : val, printing);
        } catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    public boolean testEntityInternal(final ETFEntity entity) {
        Set<String> sounds = getPlayingSounds();
        if (doPrint) {
            if (sounds == null) {
                ESF.log("Sounds property print: No sounds found, this could also be from an error.");
            } else {
                ESF.log("Sounds property print: Sounds found -> " + sounds);
            }
        }
        if (sounds == null) return false;
        for (String sound : sounds) {
            if (MATCHER.testString( sound)) return true;
        }
        return false;
    }

    @Nullable
    public static Set<String> getPlayingSounds() {
        var engine = Minecraft.getInstance().getSoundManager().soundEngine;
        if (!engine.loaded) return null;

        Set<String> sounds = new ObjectOpenHashSet<>();
        for(var sound : engine.soundDeleteTime.entrySet()) {
            if (sound.getValue() > engine.tickCount) {
                continue;
            }
            sounds.add(sound.getKey().getLocation().toString().replaceFirst("minecraft:", ""));
        }
        for(var sound : engine.instanceToChannel.keySet()) {
            sounds.add(sound.getLocation().toString().replaceFirst("minecraft:", ""));
        }
        return sounds.isEmpty() ? null : sounds;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"playingSound", "playingSounds"};
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return false;
    }

    @Override
    protected @Nullable String getValueFromEntity(final ETFEntity etfEntity) {
        return null;
    }
}
