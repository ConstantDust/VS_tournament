package org.valkyrienskies.tournament.api

import net.minecraft.world.level.block.state.properties.IntegerProperty

object TournamentBlockstateProperties {
    val TIER: IntegerProperty = IntegerProperty.create("tier",1,5)
}