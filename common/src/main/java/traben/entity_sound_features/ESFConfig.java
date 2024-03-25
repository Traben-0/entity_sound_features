package traben.entity_sound_features;

import com.demonwav.mcdev.annotations.Translatable;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.utils.ETFEntity;
import traben.tconfig.TConfig;
import traben.tconfig.gui.entries.TConfigEntryBoolean;
import traben.tconfig.gui.entries.TConfigEntryCategory;
import traben.tconfig.gui.entries.TConfigEntryEnumButton;
import traben.tconfig.gui.entries.TConfigEntryEnumSlider;

public class ESFConfig extends TConfig {

    public boolean preCheckAllEntities = true;
    public boolean logSoundSetup = false;

    public EntitySearchMode entitySearchMode = EntitySearchMode.EXACT;

    public AnnounceMode announceCompatibleSounds = AnnounceMode.NONE;


    public ObjectOpenHashSet<String> entityDisableSounds = new ObjectOpenHashSet<>();


    public boolean isEntityAllowedToModifySounds(ETFEntity entity){
        if (entityDisableSounds.isEmpty()) return true;
        return !entityDisableSounds.contains(entity.etf$getEntityKey());
    }




    @Override
    public TConfigEntryCategory getGUIOptions() {
        return new TConfigEntryCategory.Empty().add(
                new TConfigEntryCategory("config.entity_features.sounds_main").add(
                        new TConfigEntryBoolean("entity_sound_features.config.pre_check", "entity_sound_features.config.pre_check.tooltip",
                                () -> preCheckAllEntities, value -> preCheckAllEntities = value, true),
                        new TConfigEntryBoolean("entity_sound_features.config.log_setup", "entity_sound_features.config.log_setup.tooltip",
                                () -> logSoundSetup, value -> logSoundSetup = value, false),
                        new TConfigEntryEnumButton<>("entity_sound_features.config.announce", "entity_sound_features.config.announce.tooltip",
                                () -> announceCompatibleSounds, value -> announceCompatibleSounds = value, AnnounceMode.NONE),
                       new TConfigEntryEnumSlider<>("entity_sound_features.config.entity_search", "entity_sound_features.config.entity_search.tooltip",
                                () -> entitySearchMode, value -> entitySearchMode = value, EntitySearchMode.EXACT)

                ),
                getEntitySettings()
        );
    }

    private TConfigEntryCategory getEntitySettings() {
        TConfigEntryCategory category = new TConfigEntryCategory("config.entity_features.per_entity_settings");

        try {
            BuiltInRegistries.ENTITY_TYPE.forEach((entityType) -> {
                if (entityType != EntityType.PLAYER) {
                    String translationKey = entityType.getDescriptionId();
                    TConfigEntryCategory entityCategory = new TConfigEntryCategory(translationKey);
                    addEntitySettings(entityCategory, translationKey);
                    category.add(entityCategory);
                }
            });

            BlockEntityRenderers.PROVIDERS.keySet().forEach((entityType) -> {
                String translationKey = ETFApi.getBlockEntityTypeToTranslationKey(entityType);
                TConfigEntryCategory entityCategory = (new TConfigEntryCategory(translationKey));
                addEntitySettings(entityCategory, translationKey);
                category.add(entityCategory);
            });
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return category;
    }

    private void addEntitySettings(final TConfigEntryCategory entityCategory, final String translationKey) {
        entityCategory.add(
                new TConfigEntryBoolean("entity_sound_features.config.allow_entity", "entity_sound_features.config.allow_entity.tooltip",
                        () -> !entityDisableSounds.contains(translationKey),
                        value -> {
                            if (value) {
                                entityDisableSounds.remove(translationKey);
                            } else {
                                entityDisableSounds.add(translationKey);
                            }
                        },
                        true)

                );
    }


    @Override
    public ResourceLocation getModIcon() {
        return new ResourceLocation(ESF.MOD_ID, "textures/gui/icon.png");
    }

    public enum AnnounceMode {
        NONE("") {
            @Override
            public String toString() {
                return CommonComponents.OPTION_OFF.getString();
            }
        },
        ESF("entity_sound_features.config.announce.esf"),
        ALL("entity_sound_features.config.announce.all"),
        ALL_ONCE("entity_sound_features.config.announce.all_once");
        private final String key;

        public boolean all() {
            return this == ALL || this == ALL_ONCE;
        }

        AnnounceMode(@Translatable String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return Component.translatable(key).getString();
        }
    }

    public enum EntitySearchMode {

        EXACT("entity_sound_features.config.entity_search.exact"),
        BLOCK("entity_sound_features.config.entity_search.block"),
        NEAREST("entity_sound_features.config.entity_search.nearest"),
        CLIENT("entity_sound_features.config.entity_search.client");
        private final String key;

        EntitySearchMode(@Translatable String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return Component.translatable(key).getString();
        }
    }
}
