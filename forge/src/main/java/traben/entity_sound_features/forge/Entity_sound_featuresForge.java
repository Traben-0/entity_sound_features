package traben.entity_sound_features.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import traben.entity_sound_features.ESF;
import net.minecraftforge.fml.common.Mod;

@Mod(ESF.MOD_ID)
public class Entity_sound_featuresForge {
    public Entity_sound_featuresForge() {

        if (FMLEnvironment.dist == Dist.CLIENT) {

            //not 100% sure what this actually does but it will trigger the catch if loading on the server side
            ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));


            try {
                ModLoadingContext.get().registerExtensionPoint(
                        ConfigScreenHandler.ConfigScreenFactory.class,
                        () -> new ConfigScreenHandler.ConfigScreenFactory(
                                #if MC >= MC_21
                                (a,b)-> ESF.getConfigScreen(a,b)
                                #else
                                ESF::getConfigScreen
                                #endif
                        ));
            } catch (NoClassDefFoundError e) {
                ESF.logError("Mod config screen broken, download latest forge version");
            }

            ESF.init();
        } else {

            throw new UnsupportedOperationException("Attempting to load a clientside only mod [EMF] on the server, refusing");
        }
    }
}