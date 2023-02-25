package org.valkyrienskies.tournament

import org.valkyrienskies.core.config.VSConfigClass
import org.valkyrienskies.tournament.api.LoaderType

object tournamentMod {
    const val MOD_ID = "vs_tournament"

    @JvmStatic
    fun preInit(loader: LoaderType) {
    }

    @JvmStatic
    fun init(loader: LoaderType) {
        tournamentBlocks.register()
        tournamentBlockEntities.register()
        tournamentItems.register(loader)
        tournamentScreens.register()
        tournamentWeights.register()
        VSConfigClass.registerConfig("vs_tournament", tournamentConfig::class.java)
    }

    @JvmStatic
    fun initClient() {
        tournamentClientScreens.register()
    }
}
