package traben.entity_sound_features;

import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.MultipliedFloats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.features.property_reading.PropertiesRandomProvider;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Objects;
import java.util.Optional;

public class ESFVariantSupplier {

    private static final TypeToken<SoundEventRegistration> SOUND_EVENT_REGISTRATION_TYPE = new TypeToken<>() {
    };
   // private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Component.class, new Component.SerializerAdapter()).registerTypeAdapter(SoundEventRegistration.class, new SoundEventRegistrationSerializer()).create();
    private final RandomSource random = RandomSource.create();
    //private static final Random random = new Random();
    protected Int2ObjectArrayMap<WeighedSoundEvents> variantSounds;
    protected ETFApi.ETFVariantSuffixProvider variator;
    protected ResourceLocation location;

    protected ESFVariantSupplier(ResourceLocation location, ETFApi.ETFVariantSuffixProvider variator, Int2ObjectArrayMap<WeighedSoundEvents> variantSounds) {
        if (variantSounds.isEmpty())
            throw new IllegalArgumentException("ESFVariantSupplier: Variant sounds cannot be empty");

        this.variantSounds = Objects.requireNonNull(variantSounds);
        this.variator = Objects.requireNonNull(variator);
        this.location = Objects.requireNonNull(location);

//        this.variator.setRandomSupplier((entity) -> entity.etf$getUuid().hashCode());

        if (variator instanceof PropertiesRandomProvider propeties) {
            propeties.setOnMeetsRuleHook((entity, rule) -> {
                if (rule == null) {
                    ESFSoundContext.lastRuleMet.removeInt(entity.etf$getUuid());
                } else {
                    ESFSoundContext.lastRuleMet.put(entity.etf$getUuid(), rule.RULE_NUMBER);
                }
            });
        }
        ESFSoundContext.addKnownESFSound(location);
    }

    @Nullable
    public static ESFVariantSupplier getOrNull(final ResourceLocation soundEventResource) {
        boolean log = ESF.config().getConfig().logSoundSetup;
        try {
            String propertiesPath = soundEventResource.getNamespace() + ":esf/" + soundEventResource.getPath().replaceAll("\\.", "/") + ".properties";
            if (ResourceLocation.isValidResourceLocation(propertiesPath)) {
                ResourceLocation properties = new ResourceLocation(propertiesPath);
                var variator = ETFApi.getVariantSupplierOrNull(properties,
                        new ResourceLocation(propertiesPath.replaceAll("\\.properties$", ".json")), "sounds", "sound");
                if (variator != null) {
                    if (log) ESF.log(propertiesPath + " ESF sound properties found for: " + soundEventResource);
                    if (log) ESF.log("suffixes: " + variator.getAllSuffixes());
                    var suffixes = variator.getAllSuffixes();
                    suffixes.removeIf((k) -> k == 1);
                    suffixes.removeIf((k) -> k == 0);
                    if (!suffixes.isEmpty()) {
                        var variantSounds = new Int2ObjectArrayMap<WeighedSoundEvents>();
                        String soundPrefix = propertiesPath.replaceAll("\\.properties$", "");

                        for (int suffix : suffixes) {
                            var soundLocation = new ResourceLocation(soundPrefix + suffix + ".json");
                            Optional<Resource> soundResource = Minecraft.getInstance().getResourceManager().getResource(soundLocation);
                            if (soundResource.isPresent()) {
                                if (log) ESF.log(propertiesPath + " adding variants from json: " + soundLocation);
                                //variantSounds.put(suffix, new ESFSound(soundLocation));
                                parseSoundEventVariant(soundLocation, soundResource.get(), variantSounds, suffix);
                            } else {
                                ResourceLocation ogg = new ResourceLocation(soundLocation.getNamespace(),
                                        soundLocation.getPath().replaceAll("\\.json$", ".ogg"));
                                //try replace json file with direct .ogg
                                if (Minecraft.getInstance().getResourceManager().getResource(ogg).isPresent()) {
                                    //esf folder sound
                                    var event = new WeighedSoundEvents(null, null);
                                    event.addSound(new ESFSound(ogg));
                                    variantSounds.put(suffix, event);
                                    if (log) ESF.log(propertiesPath + " added variant: " + ogg);
                                } else if (log) ESF.log(propertiesPath + " invalid variants: " + soundLocation + " or "+ ogg);
                            }
                        }
                        if (!variantSounds.isEmpty())
                            return new ESFVariantSupplier(soundEventResource, variator, variantSounds);
                    }
                }
            } else {
                ESF.logWarn(propertiesPath + " was invalid sound properties id");
            }
        } catch (Exception e) {
            ESF.logError(e.getMessage());
        }
        return null;
    }

    private static void parseSoundEventVariant(ResourceLocation soundJson, Resource jsonResource, Int2ObjectArrayMap<WeighedSoundEvents> soundMap, int suffix) {
        try {
            SoundEventRegistration map = GsonHelper.fromJson(SoundManager.GSON, jsonResource.openAsReader(), SOUND_EVENT_REGISTRATION_TYPE);
            var weighedSoundEvents = new WeighedSoundEvents(null, map.getSubtitle());
            for (Sound sound : map.getSounds()) {
                final ResourceLocation resourceLocation2 = sound.getLocation();
                Object weighted;
                switch (sound.getType()) {
                    case FILE:
                        //try ogg in esf directory first
                        ResourceLocation ogg = new ResourceLocation(soundJson.getNamespace(),
                                "esf/" + sound.getLocation().getPath() + ".ogg");
                        if (Minecraft.getInstance().getResourceManager().getResource(ogg).isPresent()) {
                            //esf folder sound
                            weighted = new ESFSound(ogg, sound);
                            break;
                        } else if (Minecraft.getInstance().getResourceManager().getResource(sound.getPath()).isPresent()) {
                            //vanilla folder sounds
                            weighted = sound;
                            break;
                        }
                    case SOUND_EVENT:
                        weighted = new Weighted<Sound>() {
                            public int getWeight() {
                                WeighedSoundEvents weighedSoundEvents = Minecraft.getInstance().getSoundManager().getSoundEvent(resourceLocation2);
                                return weighedSoundEvents == null ? 0 : weighedSoundEvents.getWeight();
                            }

                            public @NotNull Sound getSound(RandomSource randomSource) {
                                WeighedSoundEvents weighedSoundEvents = Minecraft.getInstance().getSoundManager().getSoundEvent(resourceLocation2);
                                if (weighedSoundEvents == null) {
                                    return SoundManager.EMPTY_SOUND;
                                } else {
                                    Sound soundx = weighedSoundEvents.getSound(randomSource);
                                    return new Sound(soundx.getLocation().toString(), new MultipliedFloats(soundx.getVolume(), sound.getVolume()), new MultipliedFloats(soundx.getPitch(), sound.getPitch()), sound.getWeight(), Sound.Type.FILE, soundx.shouldStream() || sound.shouldStream(), soundx.shouldPreload(), soundx.getAttenuationDistance());
                                }
                            }

                            public void preloadIfRequired(SoundEngine soundEngine) {
                                WeighedSoundEvents weighedSoundEvents = Minecraft.getInstance().getSoundManager().getSoundEvent(resourceLocation2);
                                if (weighedSoundEvents != null) {
                                    weighedSoundEvents.preloadIfRequired(soundEngine);
                                }
                            }
                        };
                        break;
                    default:
                        throw new IllegalStateException("Unknown SoundEventRegistration type: " + sound.getType());
                }
                weighedSoundEvents.addSound((Weighted) weighted);
            }
            soundMap.put(suffix, weighedSoundEvents);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void preTestEntity(ETFEntity entity) {
        variator.getSuffixForETFEntity(entity);
    }

    public Sound getSoundVariantOrNull() {
        int vary = variator.getSuffixForETFEntity(ESFSoundContext.entitySource);

        //log entity suffix
        if (ESFSoundContext.entitySource != null) {
            if (vary > 0) {
                ESFSoundContext.lastSuffix.put(ESFSoundContext.entitySource.etf$getUuid(), vary);
            } else {
                ESFSoundContext.lastSuffix.removeInt(ESFSoundContext.entitySource.etf$getUuid());
            }
        }

        if (vary > 0 && variantSounds.containsKey(vary)) {
            return variantSounds.get(vary).getSound(random);
        }
        return null;
    }

    @Override
    public String toString() {
        return location.toString() + " [variants: " + variantSounds.keySet() + "]";
    }
}
