package org.valkyrienskies.tournament

import org.valkyrienskies.core.impl.config.VSConfigClass
import org.valkyrienskies.tournament.api.LoaderType

object tournamentMod {
    const val MOD_ID = "vs_tournament"

    @JvmStatic
    fun init(loader: LoaderType) {
        tournamentBlocks.register()
        tournamentBlockEntities.register()
        tournamentItems.register(loader)
        tournamentScreens.register()
        tournamentEntities.register()
        tournamentWeights.register()
        VSConfigClass.registerConfig("vs_tournament", tournamentConfig::class.java)
    }

    @JvmStatic
    fun initClient() {
        tournamentClientScreens.register()
    }
}
