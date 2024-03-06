package traben.entity_sound_features;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFApi;

import java.util.Objects;
import java.util.Optional;

public class ESFVariantSupplier {

    protected Int2ObjectArrayMap<Sound> variantSounds;
    protected ETFApi.ETFVariantSuffixProvider variator;
    protected ResourceLocation location;

    protected ESFVariantSupplier(ResourceLocation location, ETFApi.ETFVariantSuffixProvider variator, Int2ObjectArrayMap<Sound> variantSounds) {
        if (variantSounds.isEmpty())
            throw new IllegalArgumentException("ESFVariantSupplier: Variant sounds cannot be empty");

        this.variantSounds = Objects.requireNonNull(variantSounds);
        this.variator = Objects.requireNonNull(variator);
        this.location = Objects.requireNonNull(location);
    }

    @Nullable
    public static ESFVariantSupplier getOrNull(final ResourceLocation soundEventResource) {

        try {
            String propertiesPath = soundEventResource.getNamespace() + ":esf/" + soundEventResource.getPath().replaceAll("\\.", "/") + ".properties";
            if (ResourceLocation.isValidResourceLocation(propertiesPath)) {
                ResourceLocation properties = new ResourceLocation(propertiesPath);
                var variator = ETFApi.getVariantSupplierOrNull(properties,
                        new ResourceLocation(propertiesPath.replaceAll("\\.properties$",".ogg")), "sounds");
                if (variator != null) {
                    ESFClient.log(propertiesPath + " variator found for: " + soundEventResource);
                    ESFClient.log("suffixes: " + variator.getAllSuffixes());
                    var suffixes = variator.getAllSuffixes();
                    suffixes.removeIf((k) -> k == 1);
                    suffixes.removeIf((k) -> k == 0);
                    if (!suffixes.isEmpty()) {
                        var variantSounds = new Int2ObjectArrayMap<Sound>();
                        String soundPrefix = propertiesPath.replaceAll("\\.properties$", "");

                        for (int suffix : suffixes) {
                            var soundLocation = new ResourceLocation(soundPrefix + suffix + ".ogg");
                            Optional<Resource> soundResource = Minecraft.getInstance().getResourceManager().getResource(soundLocation);
                            if (soundResource.isPresent()) {
                                ESFClient.log(propertiesPath + " has variant: " + soundLocation);
                                variantSounds.put(suffix, new ESFSound(soundLocation));
                            } else {
                                ESFClient.log(propertiesPath + " invalid variant: " + soundLocation);
                            }
                        }
                        if (!variantSounds.isEmpty())
                            return new ESFVariantSupplier(soundEventResource, variator, variantSounds);
                    }
                }
            } else {
                ESFClient.logWarn(propertiesPath + " was invalid sound properties id");
            }
        } catch (Exception e) {
            ESFClient.logError(e.getMessage());
        }
        return null;
    }

    public Sound getSoundVariantOrNull() {
        int vary = variator.getSuffixForETFEntity(ESFSoundContext.entitySource);
        if (vary > 0 && variantSounds.containsKey(vary)) {
            Sound sound = variantSounds.get(vary);
            ESFClient.log("sound returned: #" + vary + ", " + sound.getLocation());
            return sound;
        }
        return null;
    }

    @Override
    public String toString() {
        return location.toString() + " [variants: " + variantSounds.keySet() + "]";
    }
}
