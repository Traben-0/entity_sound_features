package traben.entity_sound_features;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import traben.entity_features.config.EFConfig;
import traben.entity_features.config.gui.options.EFOptionBoolean;
import traben.entity_features.config.gui.options.EFOptionCategory;
import traben.entity_features.config.gui.options.EFOptionEnum;

public class ESFConfig extends EFConfig {

    public boolean preCheckAllEntities = true;
    public boolean logSoundSetup = false;

    public AnnounceMode announceCompatibleSounds = AnnounceMode.NONE;


    @Override
    public EFOptionCategory getGUIOptions() {
        return new EFOptionCategory.Empty().add(
                new EFOptionCategory("config.entity_features.sounds_main").add(
                        new EFOptionBoolean("entity_sound_features.config.pre_check", "entity_sound_features.config.pre_check.tooltip",
                                () -> preCheckAllEntities, value -> preCheckAllEntities = value, true),
                        new EFOptionBoolean("entity_sound_features.config.log_setup", "entity_sound_features.config.log_setup.tooltip",
                                () -> logSoundSetup, value -> logSoundSetup = value, false),
                        new EFOptionEnum<>("entity_sound_features.config.announce", "entity_sound_features.config.announce.tooltip",
                                () -> announceCompatibleSounds, value -> announceCompatibleSounds = value, AnnounceMode.NONE)
                )
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

        AnnounceMode(@Translatable String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return Component.translatable(key).getString();
        }
    }
}
