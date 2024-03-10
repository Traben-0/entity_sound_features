package traben.entity_sound_features.fabric;

import net.fabricmc.api.ClientModInitializer;
import traben.entity_sound_features.ESF;

public class Entity_sound_featuresFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ESF.init();
    }
}