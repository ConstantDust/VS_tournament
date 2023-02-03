package org.valkyrienskies.tournament.item

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.apigame.constraints.*
import org.valkyrienskies.mod.common.allShips
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.shipWorldNullable
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.ship.tournamentShipControl
import org.valkyrienskies.tournament.tournamentConfig
import org.valkyrienskies.tournament.tournamentItems

class ShipDeleteGun : Item(
        Properties().stacksTo(1).tab(tournamentItems.getTab())
){

    override fun useOn(context: UseOnContext): InteractionResult {
        val force = tournamentConfig.SERVER.pulseGunForce

        val player = context.player
        val blockPosition = context.clickedPos

        if(blockPosition == null || context.level.isClientSide || player == null) {
            return InteractionResult.PASS
        }

        val level = context.level
        if(level !is ServerLevel){
            return InteractionResult.PASS
        }

        val ship = level.getShipObjectManagingPos(blockPosition)
        if(ship == null) {
            return InteractionResult.PASS
        }

        //TODO: proper ship destroy method when triode implements it in vs2

        return super.useOn(context)
    }
}