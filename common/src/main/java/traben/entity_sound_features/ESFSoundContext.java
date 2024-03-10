package traben.entity_sound_features;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.ArrayList;
import java.util.List;

public class ESFSoundContext {

    private static final List<ESFVariantSupplier> variantSuppliers = new ArrayList<>();


    public static void registerVariantSupplier(ESFVariantSupplier supplier) {
        if(ESF.config().getConfig().preCheckAllEntities)
            variantSuppliers.add(supplier);
    }

    public static void preTestEntity(ETFEntity entity) {
        for (ESFVariantSupplier variantSupplier : variantSuppliers) {
            variantSupplier.preTestEntity(entity);
        }
    }

    public static void announceSound(String sound, boolean wasESF){
        if (entitySource == null) return;

        switch (ESF.config().getConfig().announceCompatibleSounds){
            case ESF ->{
                if(wasESF) announceSimple(sound, true);
            }
            case ALL -> announceSimple(sound, wasESF);
            case ALL_ONCE ->{
                if(!announcedSounds.contains(sound)){
                    announceSimple(sound, wasESF);
                    announcedSounds.add(sound);
                }
            }
        }
    }

    private static void announceSimple(String sound, boolean wasESF){
        if (wasESF) ESF.log("Modifiable sound with ESF properties played: " + sound);
        else ESF.log("Modifiable sound played: " + sound);
    }

    private static final ObjectOpenHashSet<String> announcedSounds = new ObjectOpenHashSet<>();

    public static void resetContext() {
        ESF.log("Resetting sound context");
        variantSuppliers.clear();
        announcedSounds.clear();
    }

    public static ETFEntity entitySource = null;


}
