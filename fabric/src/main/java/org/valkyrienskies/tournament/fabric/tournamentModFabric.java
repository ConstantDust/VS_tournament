package org.valkyrienskies.tournament.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.valkyrienskies.core.config.VSConfigClass;
import org.valkyrienskies.tournament.*;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;
import org.valkyrienskies.tournament.api.LoaderType;

import java.util.List;

public class tournamentModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before tournament
        // new ValkyrienSkiesModFabric().onInitialize();

        tournamentMod.preInit(LoaderType.FABRIC);

        List<ItemStack> il = tournamentItems.INSTANCE.getItems();

        tournamentItems.INSTANCE.setTAB(FabricItemGroupBuilder.create(new ResourceLocation("vs_tournament", "tournament_tab"))
                .icon(() -> new ItemStack(tournamentBlocks.INSTANCE.getSHIPIFIER().get()))
                .appendItems(list -> list.addAll(il))
                .build());

        tournamentMod.init(LoaderType.FABRIC);

    }

    @Environment(EnvType.CLIENT)
    public static class Client implements ClientModInitializer {
        @Override
        public void onInitializeClient() {
            tournamentMod.initClient();
        }
    }

    public static class ModMenu implements ModMenuApi {
        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return (parent) -> VSClothConfig.createConfigScreenFor(
                    parent,
                    VSConfigClass.Companion.getRegisteredConfig(tournamentConfig.class)
            );
        }
    }
}
