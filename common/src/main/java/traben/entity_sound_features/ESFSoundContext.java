package traben.entity_sound_features;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.ArrayList;
import java.util.List;

public class ESFSoundContext {

    private static final List<ESFVariantSupplier> variantSuppliers = new ArrayList<>();
    private static final ObjectOpenHashSet<ResourceLocation> announcedSounds = new ObjectOpenHashSet<>();
    private static final ObjectOpenHashSet<ResourceLocation> knownESFSounds = new ObjectOpenHashSet<>();
    public static void addKnownESFSound(ResourceLocation sound){
        knownESFSounds.add(sound);
    }
    public static boolean shouldCaptureEntity(ResourceLocation sound){
        if(ESF.config().getConfig().announceCompatibleSounds.all()) return true;
        return knownESFSounds.contains(sound);
    }
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


    public static void searchForEntity(Iterable<Entity> entities, double x, double y, double z) {

        switch (ESF.config().getConfig().entitySearchMode) {
            case EXACT -> {
                for (Entity entity : entities) {
                    if (entity.getBoundingBox().contains(x, y, z)) {
                        ETFEntity etfEntity = (ETFEntity) entity;
                        if (ESF.config().getConfig().isEntityAllowedToModifySounds(etfEntity)) {
                            ESFSoundContext.entitySource = etfEntity;
                            break;
                        }
                    }
                }
            }
            case BLOCK -> {
                var start = new Vec3(x-0.5, y-0.5, z-0.5);
                var end = new Vec3(x+0.5, y+0.5, z+0.5);
                for (Entity entity : entities) {
                    if (entity.getBoundingBox().intersects(start,end)) {
                        ETFEntity etfEntity = (ETFEntity) entity;
                        if (ESF.config().getConfig().isEntityAllowedToModifySounds(etfEntity)) {
                            ESFSoundContext.entitySource = etfEntity;
                            break;
                        }
                    }
                }
            }
            case NEAREST -> {
                double distance = Double.MAX_VALUE;
                ETFEntity nearest = null;
                for (Entity entity : entities) {
                    ETFEntity etfEntity = (ETFEntity) entity;
                    if (ESF.config().getConfig().isEntityAllowedToModifySounds(etfEntity)) {
                        double d = entity.distanceToSqr(x, y, z);
                        if (d < distance) {
                            distance = d;
                            nearest = etfEntity;
                        }
                    }
                }
                ESFSoundContext.entitySource = nearest;
            }
            case CLIENT -> ESFSoundContext.entitySource = (ETFEntity) Minecraft.getInstance().player;
        }
    }

    public static void searchForBlockEntity(Level level, double x, double y, double z) {
        if (level == null || ESF.config().getConfig().entitySearchMode == ESFConfig.EntitySearchMode.CLIENT) return;

        var state = level.getBlockState(new BlockPos((int) x, (int) y, (int) z));
        if (state.hasBlockEntity()) {
            var blockEntity = level.getBlockEntity(new BlockPos((int) x, (int) y, (int) z));
            if (blockEntity instanceof ETFEntity etfEntity) {
                if (ESF.config().getConfig().isEntityAllowedToModifySounds(etfEntity)) {
                    ESFSoundContext.entitySource = etfEntity;
                }
            }
        }
    }

}
