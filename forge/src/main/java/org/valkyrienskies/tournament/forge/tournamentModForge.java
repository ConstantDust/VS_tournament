package org.valkyrienskies.tournament.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.valkyrienskies.core.config.VSConfigClass;
import org.valkyrienskies.tournament.api.LoaderType;
import org.valkyrienskies.tournament.tournamentBlocks;
import org.valkyrienskies.tournament.tournamentConfig;
import org.valkyrienskies.tournament.tournamentItems;
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

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY,
                () -> (Minecraft client, Screen parent) ->
                        VSClothConfig.createConfigScreenFor(parent,
                                VSConfigClass.Companion.getRegisteredConfig(tournamentConfig.class))
        );

        MOD_BUS.addListener(this::onModelRegistry);
        MOD_BUS.addListener(this::clientSetup);

        tournamentMod.init(LoaderType.FORGE);


        tournamentItems.INSTANCE.setTAB(new tournamentTab(CreativeModeTab.TABS.length, "vs_tournament.tournament_tab"));

    }

    void clientSetup(final FMLClientSetupEvent event) {
        if (happendClientSetup) return;
        happendClientSetup = true;

        tournamentMod.initClient();
    }

    void onModelRegistry(final ModelRegistryEvent event) {

    }
}
class tournamentTab extends CreativeModeTab {
    public tournamentTab(int i, String string) {
        super(i, string);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(tournamentBlocks.INSTANCE.getSHIPIFIER().get().asItem());
    }
}