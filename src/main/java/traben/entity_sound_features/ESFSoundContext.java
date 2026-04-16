package traben.entity_sound_features;

import net.minecraft.client.Minecraft;
//#if MC >= 12102
import net.minecraft.client.renderer.entity.state.EntityRenderState;
//#endif
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFLruCache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ESFSoundContext {

    private static final List<ESFVariantSupplier> variantSuppliers = new ArrayList<>();
    //#if MC >= 26.1
    //$$ private static final Set<net.minecraft.resources.Identifier> announcedSounds = new HashSet<>();
    //$$ private static final Set<net.minecraft.resources.Identifier> knownESFSounds = new HashSet<>();
    //$$ public static void addKnownESFSound(net.minecraft.resources.Identifier sound){
    //$$     knownESFSounds.add(sound);
    //$$ }
    //$$ public static boolean shouldCaptureEntity(net.minecraft.resources.Identifier sound){
    //$$     if(ESF.config().getConfig().announceCompatibleSounds.all()) return true;
    //$$     return knownESFSounds.contains(sound);
    //$$ }
    //#else
    private static final Set<net.minecraft.resources.ResourceLocation> announcedSounds = new HashSet<>();
    private static final Set<net.minecraft.resources.ResourceLocation> knownESFSounds = new HashSet<>();
    public static void addKnownESFSound(net.minecraft.resources.ResourceLocation sound){
        knownESFSounds.add(sound);
    }
    public static boolean shouldCaptureEntity(net.minecraft.resources.ResourceLocation sound){
        if(ESF.config().getConfig().announceCompatibleSounds.all()) return true;
        return knownESFSounds.contains(sound);
    }
    //#endif
    public static @Nullable ETFEntityRenderState entitySource = null;

    public static void setSource(ETFEntity entity) {
        entitySource = ETFEntityRenderState.forEntity(entity);
        //#if MC >= 12102
        entitySource.setVanillaState(new EntityRenderState()); // just to be safe
        //#endif
    }

//    public static void registerVariantSupplier(ESFVariantSupplier supplier) {
//        if (ESF.config().getConfig().preCheckAllEntities)
//            variantSuppliers.add(supplier);
//    }

    public static void preTestEntity(ETFEntityRenderState entity) {
        for (ESFVariantSupplier variantSupplier : variantSuppliers) {
            variantSupplier.preTestEntity(entity);
        }
    }

    public static void announceSound(
            //#if MC >= 26.1
            //$$ net.minecraft.resources.Identifier sound,
            //#else
            net.minecraft.resources.ResourceLocation sound,
            //#endif
                                     boolean wasESF) {
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

    private static void announceWithEntity(
            //#if MC >= 26.1
            //$$ net.minecraft.resources.Identifier sound,
            //#else
            net.minecraft.resources.ResourceLocation sound,
            //#endif
                                           boolean wasESF) {
        String pre;
        if (wasESF) pre = ("Modifiable sound event with ESF properties: " + sound);
        else pre = ("Modifiable sound event: " + sound);
        if (entitySource != null) ESF.log(pre + ", played by: " + entitySource.entityType());

        if (!wasESF)
            ESF.log("This sound event can be modified by ESF with a properties file at: assets/" + sound.getNamespace() + "/esf/" + sound.getPath().replaceAll("\\.", "/") + ".properties");
    }

    public static void resetContext() {
        ESF.log("Resetting sound context");
        variantSuppliers.clear();
        announcedSounds.clear();
        lastRuleMet.clear();
        lastSuffix.clear();
    }

    public static final ETFLruCache.UUIDInteger lastRuleMet = new ETFLruCache.UUIDInteger();
    public static final ETFLruCache.UUIDInteger lastSuffix = new ETFLruCache.UUIDInteger();


    public static void searchForEntity(Iterable<Entity> entities, double x, double y, double z) {

        switch (ESF.config().getConfig().entitySearchMode) {
            case EXACT -> {
                for (Entity entity : entities) {
                    if (entity.getBoundingBox().contains(x, y, z)) {
                        ETFEntity etfEntity = (ETFEntity) entity;
                        if (ESF.config().getConfig().isEntityAllowedToModifySounds(etfEntity)) {
                            setSource(etfEntity);
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
                            setSource(etfEntity);
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
                setSource(nearest);
            }
            case CLIENT -> setSource((ETFEntity) Minecraft.getInstance().player);
        }
    }

    public static void searchForBlockEntity(Level level, double x, double y, double z) {
        if (level == null || ESF.config().getConfig().entitySearchMode == ESFConfig.EntitySearchMode.CLIENT) return;

        var state = level.getBlockState(new BlockPos((int) x, (int) y, (int) z));
        if (state.hasBlockEntity()) {
            var blockEntity = level.getBlockEntity(new BlockPos((int) x, (int) y, (int) z));
            if (blockEntity instanceof ETFEntity etfEntity) {
                if (ESF.config().getConfig().isEntityAllowedToModifySounds(etfEntity)) {
                    setSource(etfEntity);
                }
            }
        }
    }

}
