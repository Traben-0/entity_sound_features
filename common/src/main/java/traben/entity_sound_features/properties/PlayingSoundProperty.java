package traben.entity_sound_features.properties;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;
import java.util.Set;

public class PlayingSoundProperty extends StringArrayOrRegexProperty {

    protected PlayingSoundProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(RandomProperty.readPropertiesOrThrow(properties, propertyNum, "playingSound", "playingSounds"));
    }
    public static PlayingSoundProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new PlayingSoundProperty(properties, propertyNum);
        } catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    public boolean testEntityInternal(final ETFEntity entity) {
        var engine = Minecraft.getInstance().getSoundManager().soundEngine;
        if (!engine.loaded) return false;
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
        for (String sound : sounds) {
            if (MATCHER.testString( sound)) return true;
        }
        return false;
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
