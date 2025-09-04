package traben.entity_sound_features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import traben.entity_model_features.EMFAnimationApi;
import traben.entity_sound_features.methods.ESFIsPlayingSoundMethodFactory;
import traben.entity_sound_features.methods.ESFPlaySoundMethodFactory;
import traben.entity_sound_features.properties.PlayingSoundProperty;
import traben.entity_sound_features.properties.SoundRuleIndexProperty;
import traben.entity_sound_features.properties.SoundSuffixProperty;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.features.property_reading.properties.RandomProperties;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.tconfig.TConfigHandler;

public class ESF {

    public static @NotNull ResourceLocation res(String fullPath) { return ETFUtils2.res(fullPath); }
    public static @NotNull ResourceLocation res(String namespace, String path) { return ETFUtils2.res(namespace, path); }

    public static final String MOD_ID = "entity_sound_features";

    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static TConfigHandler<ESFConfig> configHandler = null;

    public static TConfigHandler<ESFConfig> config() {
        if (configHandler == null) {
            configHandler = new TConfigHandler<>(ESFConfig::new, MOD_ID, "ESF");
            ETF.registerConfigHandler(configHandler);
        }
        return configHandler;
    }

    public static void init() {
        LOGGER.info("[ESF (Entity Sound Features)] initialized.");
        config();

        ETFApi.registerCustomRandomPropertyFactory(MOD_ID,
                RandomProperties.RandomPropertyFactory.of("soundRule",
                        "entity_sound_features.rule_property",
                        SoundRuleIndexProperty::getPropertyOrNull),
                RandomProperties.RandomPropertyFactory.of("soundSuffix",
                        "entity_sound_features.suffix_property",
                        SoundSuffixProperty::getPropertyOrNull),
                RandomProperties.RandomPropertyFactory.of("playingSound",
                        "entity_sound_features.config.playingsound.property",
                        PlayingSoundProperty::getPropertyOrNull)
                );

        EMFAnimationApi.registerCustomFunctionFactory(
                MOD_ID,"playsound",
                "entity_sound_features.config.playsound.method",
                ESFPlaySoundMethodFactory::new);
        EMFAnimationApi.registerCustomFunctionFactory(
                MOD_ID,"playingsound",
                "entity_sound_features.config.playingsound.method",
                ESFIsPlayingSoundMethodFactory::new);

    }



    // mod menu config screen factory
    public static Screen getConfigScreen(Screen parent) {
        return getConfigScreen(null, parent);
    }

    // forge config screen factory
    public static Screen getConfigScreen(Minecraft ignored, Screen parent) {
        return ETF.getConfigScreen(parent);
    }

    public static void log(String message) {
        LOGGER.info("[ESF] {}", message);
    }
    public static void logError(String message) {
        LOGGER.error("[ESF] {}", message);
    }
    public static void logWarn(String message) {
        LOGGER.warn("[ESF] {}", message);
    }


}
