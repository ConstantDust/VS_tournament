package org.valkyrienskies.eureka.forge;

import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.valkyrienskies.dependency_downloader.ValkyrienDependencyDownloader;

import java.nio.file.Paths;

public class AutoDependenciesForge {
    public static void runUpdater() {
        System.setProperty("java.awt.headless", "false");
        ValkyrienDependencyDownloader.start(
            FMLPaths.MODSDIR.get(),
            FMLLoader.getLoadingModList().getModFileById("vs_eureka").getFile().getFilePath(),
            FMLEnvironment.dist.isDedicatedServer()
        );
    }
}
