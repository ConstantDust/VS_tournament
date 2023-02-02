package org.valkyrienskies.tournament.api

import net.minecraft.world.level.block.state.properties.IntegerProperty

object TournamentBlockstateProperties {
    val TIER: IntegerProperty = IntegerProperty.create("tier",1,5)
    val SHIPID: IntegerProperty = IntegerProperty.create("shipid", -1, Int.MAX_VALUE)
}