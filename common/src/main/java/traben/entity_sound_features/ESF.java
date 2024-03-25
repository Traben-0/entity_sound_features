package traben.entity_sound_features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.features.property_reading.properties.RandomProperties;
import traben.tconfig.TConfigHandler;

public class ESF {
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
                        SoundSuffixProperty::getPropertyOrNull)
                );

    }

    //mod menu config screen factory
    public static Screen getConfigScreen(Screen parent) {
        return getConfigScreen(null, parent);
    }

    //forge config screen factory
    public static Screen getConfigScreen(Minecraft ignored, Screen parent) {
        return ETF.getConfigScreen(parent);
    }

    //method to log info
    public static void log(String message) {
        LOGGER.info("[ESF] " + message);
    }

    //method to log error
    public static void logError(String message) {
        LOGGER.error("[ESF] " + message);
    }

    //method to log warn
    public static void logWarn(String message) {
        LOGGER.warn("[ESF] " + message);
    }


}
