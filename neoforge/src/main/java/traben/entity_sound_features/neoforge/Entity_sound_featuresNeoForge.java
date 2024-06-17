package traben.entity_sound_features.neoforge;


import net.neoforged.api.distmarker.Dist;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
#if MC >= MC_20_6
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
#else
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.neoforge.client.ConfigScreenHandler;
#endif
import traben.entity_sound_features.ESF;

@Mod(ESF.MOD_ID)
public class Entity_sound_featuresNeoForge {
    public Entity_sound_featuresNeoForge() {

        if (FMLEnvironment.dist == Dist.CLIENT) {

            try {
                ModLoadingContext.get().registerExtensionPoint(
                                                #if MC >= MC_20_6
                        IConfigScreenFactory.class,
                        ()-> ESF::getConfigScreen);
                        #else
                        ConfigScreenHandler.ConfigScreenFactory.class,
                        ()-> new ConfigScreenHandler.ConfigScreenFactory(ESF::getConfigScreen));
                        #endif

            } catch (NoClassDefFoundError e) {
                ESF.logError("Mod config screen broken, download latest forge version");
            }

            ESF.init();
        } else {

            throw new UnsupportedOperationException("Attempting to load a clientside only mod [ESF] on the server, refusing");
        }
    }
}