package traben.entity_sound_features;

import traben.entity_texture_features.utils.ETFEntity;

public class ESFSoundContext {

    public static ETFEntity entitySource = null;

//    public static SoundSource soundSource = null;

    public static boolean hasEntity() {
        return entitySource != null;
    }

}
