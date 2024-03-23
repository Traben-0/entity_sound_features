package traben.entity_sound_features;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.EntityIntLRU;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.IntSupplier;

public class ESFVariantSupplier {

    private static final Random random = new Random();
    protected Int2ObjectArrayMap<Sound> variantSounds;
    protected ETFApi.ETFVariantSuffixProvider variator;
    protected ResourceLocation location;

    protected ESFVariantSupplier(ResourceLocation location, ETFApi.ETFVariantSuffixProvider variator, Int2ObjectArrayMap<Sound> variantSounds) {
        if (variantSounds.isEmpty())
            throw new IllegalArgumentException("ESFVariantSupplier: Variant sounds cannot be empty");

        this.variantSounds = Objects.requireNonNull(variantSounds);
        this.variator = Objects.requireNonNull(variator);
        this.location = Objects.requireNonNull(location);
        int max = variator.getAllSuffixes().size();
        this.variator.setRandomSupplier(getRandomSupplier(max));
//        if(variator instanceof PropertiesRandomProvider propeties){
//            propeties.setOnMeetsRuleHook((entity,rule)->{
//                System.out.println("sound meets rule: "+(rule==null? "null":rule.RULE_NUMBER));
//            });
//        }
        ESFSoundContext.addKnownESFSound(location);
    }

    private ETFApi.ETFVariantSuffixProvider.EntityRandomSeedFunction getRandomSupplier(int max){
        return switch (ESF.config().getConfig().randomSuffixBehaviour){
            case RANDOM -> (entity) -> random.nextInt(max);
            case CONSISTENT -> (entity) -> entity.etf$getUuid().hashCode();
            case FIRST -> (entity)->0;
            case SEQUENTIAL -> {
                IntSupplier counter = new IntSupplier() {
                    private int c = -1;
                    @Override
                    public int getAsInt() {
                        c++;
                        return c;
                    }
                };
                yield (entity)->counter.getAsInt();
            }
            case SEQUENTIAL_ENTITY -> new ETFApi.ETFVariantSuffixProvider.EntityRandomSeedFunction(){
                final EntityIntLRU counter = new EntityIntLRU(max);
                @Override
                public int toInt(ETFEntity entity) {
                    int c = counter.getInt(entity.etf$getUuid());
                    c++;
                    counter.put(entity.etf$getUuid(), c);
                    return c;
                }
            };
        };
    }

    @Nullable
    public static ESFVariantSupplier getOrNull(final ResourceLocation soundEventResource) {
        boolean log = ESF.config().getConfig().logSoundSetup;
        try {
            String propertiesPath = soundEventResource.getNamespace() + ":esf/" + soundEventResource.getPath().replaceAll("\\.", "/") + ".properties";
            if (ResourceLocation.isValidResourceLocation(propertiesPath)) {
                ResourceLocation properties = new ResourceLocation(propertiesPath);
                var variator = ETFApi.getVariantSupplierOrNull(properties,
                        new ResourceLocation(propertiesPath.replaceAll("\\.properties$", ".ogg")), "sounds", "sound");
                if (variator != null) {
                    if (log) ESF.log(propertiesPath + " ESF sound properties found for: " + soundEventResource);
                    if (log) ESF.log("suffixes: " + variator.getAllSuffixes());
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
                                if (log) ESF.log(propertiesPath + " added variant: " + soundLocation);
                                variantSounds.put(suffix, new ESFSound(soundLocation));
                            } else {
                                if (log) ESF.log(propertiesPath + " invalid variant: " + soundLocation);
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

    public void preTestEntity(ETFEntity entity) {
        variator.getSuffixForETFEntity(entity);
    }

    public Sound getSoundVariantOrNull() {
        int vary = variator.getSuffixForETFEntity(ESFSoundContext.entitySource);
        if (vary > 0 && variantSounds.containsKey(vary)) {
            return variantSounds.get(vary);
        }
        return null;
    }

    @Override
    public String toString() {
        return location.toString() + " [variants: " + variantSounds.keySet() + "]";
    }
}
