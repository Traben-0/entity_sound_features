package traben.entity_sound_features.forge;

import traben.entity_sound_features.ESFClient;
import net.minecraftforge.fml.common.Mod;

@Mod(ESFClient.MOD_ID)
public class Entity_sound_featuresForge {
    public Entity_sound_featuresForge() {
        ESFClient.init();
    }
}