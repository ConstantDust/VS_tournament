package org.valkyrienskies.tournament.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.valkyrienskies.core.impl.config.VSConfigClass;
import org.valkyrienskies.tournament.*;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;
import org.valkyrienskies.tournament.api.LoaderType;

import java.util.List;
import java.util.function.Consumer;

public class tournamentModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before tournament
        new ValkyrienSkiesModFabric().onInitialize();

        tournamentItems.INSTANCE.setTAB(FabricItemGroupBuilder.create(new ResourceLocation("vs_tournament", "tournament_tab"))
                .icon(() -> new ItemStack(tournamentBlocks.INSTANCE.getSHIPIFIER().get()))
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
