package traben.entity_sound_features;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import traben.entity_features.config.EFConfigHandler;
import traben.entity_features.config.gui.EFConfigScreenMain;

public class ESF {
    public static final String MOD_ID = "entity_sound_features";

    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static EFConfigHandler<ESFConfig> configHandler = null;

    public static EFConfigHandler<ESFConfig> config() {
        if (configHandler == null) {
            configHandler = new EFConfigHandler<>( ESFConfig::new, MOD_ID,"ESF");
        }
        return configHandler;
    }

    public static void init() {
        LOGGER.info("[ESF (Entity Sound Features)] initialized.");
        config();


    }

    //mod menu config screen factory
    public static Screen getConfigScreen(Screen parent) {
        return getConfigScreen(null, parent);
    }

    //forge config screen factory
    public static Screen getConfigScreen(Minecraft ignored, Screen parent) {
        return new EFConfigScreenMain(parent);
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
