package org.valkyrienskies.tournament.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.valkyrienskies.core.impl.config.VSConfigClass;
import org.valkyrienskies.tournament.tournamentConfig;
import org.valkyrienskies.tournament.tournamentMod;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;

@Mod(tournamentMod.MOD_ID)
public class tournamentModForge {
    boolean happendClientSetup = false;
    static IEventBus MOD_BUS;

    public tournamentModForge() {
        // Submit our event bus to let architectury register our content on the right time
        MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_BUS.addListener(this::clientSetup);

        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory((Minecraft client, Screen parent) ->
                        VSClothConfig.createConfigScreenFor(parent,
                                VSConfigClass.Companion.getRegisteredConfig(tournamentConfig.class)))
        );

        MOD_BUS.addListener(this::onModelRegistry);
        MOD_BUS.addListener(this::clientSetup);
        MOD_BUS.addListener(this::entityRenderers);

        tournamentMod.init();
    }

    void clientSetup(final FMLClientSetupEvent event) {
        if (happendClientSetup) return;
        happendClientSetup = true;

        tournamentMod.initClient();

//        WheelModels.INSTANCE.setModelGetter(woodType -> ForgeModelBakery.instance().getBakedTopLevelModels()
//                .getOrDefault(
//                        new ResourceLocation(tournamentMod.MOD_ID, "block/" + woodType.getResourceName() + "_ship_helm_wheel"),
//                        Minecraft.getInstance().getModelManager().getMissingModel()
//                ));
    }

    void entityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
//        event.registerBlockEntityRenderer(
//                tournamentBlockEntities.INSTANCE.getSHIP_HELM().get(),
//                ShipHelmBlockEntityRenderer::new
//        );
    }

    void onModelRegistry(final ModelRegistryEvent event) {
//        for (WoodType woodType : WoodType.values()) {
//            ForgeModelBakery.addSpecialModel(new ResourceLocation(tournamentMod.MOD_ID, "block/" + woodType.getResourceName() + "_ship_helm_wheel"));
//        }
    }
}
