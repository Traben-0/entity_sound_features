package traben.entity_sound_features.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import traben.entity_sound_features.ESF;

public class ESFModMenuEntry implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        try {
            return ESF::getConfigScreen;
        } catch (Exception e) {
            return screen -> null;
        }
    }


}
