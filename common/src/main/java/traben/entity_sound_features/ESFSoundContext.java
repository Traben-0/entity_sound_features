package traben.entity_sound_features;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.ArrayList;
import java.util.List;

public class ESFSoundContext {

    private static final List<ESFVariantSupplier> variantSuppliers = new ArrayList<>();
    private static final ObjectOpenHashSet<ResourceLocation> announcedSounds = new ObjectOpenHashSet<>();
    public static ETFEntity entitySource = null;

    public static void registerVariantSupplier(ESFVariantSupplier supplier) {
        if (ESF.config().getConfig().preCheckAllEntities)
            variantSuppliers.add(supplier);
    }

    public static void preTestEntity(ETFEntity entity) {
        for (ESFVariantSupplier variantSupplier : variantSuppliers) {
            variantSupplier.preTestEntity(entity);
        }
    }

    public static void announceSound(ResourceLocation sound, boolean wasESF) {
        if (entitySource == null || sound == null) return;

        switch (ESF.config().getConfig().announceCompatibleSounds) {
            case ESF -> {
                if (wasESF) announceWithEntity(sound, true);
            }
            case ALL -> announceWithEntity(sound, wasESF);
            case ALL_ONCE -> {
                if (!announcedSounds.contains(sound)) {
                    announceWithEntity(sound, wasESF);
                    announcedSounds.add(sound);
                }
            }
        }
    }

    private static void announceWithEntity(ResourceLocation sound, boolean wasESF) {
        String pre;
        if (wasESF) pre = ("Modifiable sound event with ESF properties: " + sound);
        else pre = ("Modifiable sound event: " + sound);
        ESF.log(pre + ", played by: " + entitySource.etf$getType());

        if (!wasESF)
            ESF.log("This sound event can be modified by ESF with a properties file at: assets/" + sound.getNamespace() + "/esf/" + sound.getPath().replaceAll("\\.", "/") + ".properties");
    }

    public static void resetContext() {
        ESF.log("Resetting sound context");
        variantSuppliers.clear();
        announcedSounds.clear();
    }


}
