package traben.entity_sound_features.fabric;

import net.fabricmc.api.ClientModInitializer;
import traben.entity_sound_features.ESFClient;
import net.fabricmc.api.ModInitializer;

public class Entity_sound_featuresFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ESFClient.init();
    }
}