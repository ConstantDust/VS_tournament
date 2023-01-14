package org.valkyrienskies.tournament

import org.valkyrienskies.core.impl.config.VSConfigClass


object tournamentMod {
    const val MOD_ID = "vs_tournament"

    @JvmStatic
    fun init() {
        tournamentBlocks.register()
        tournamentBlockEntities.register()
        tournamentItems.register()
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
