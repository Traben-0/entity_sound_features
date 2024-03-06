package traben.entity_sound_features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ESFClient {
    public static final String MOD_ID = "entity_sound_features";

    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("[ESF (Entity Sound Features)] initialized.");


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
