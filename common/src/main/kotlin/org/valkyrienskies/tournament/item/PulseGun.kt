package org.valkyrienskies.tournament.item

import net.fabricmc.loader.impl.lib.sat4j.core.Vec
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.*
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.physics_api.ConstraintId

class PulseGun : Item(
    Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)
), ShipForcesInducer {

    val Force : Double = 1e8
    var pulseForce : Vector3d? = null

    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player
        val blockPosition = context.clickedPos

        if(blockPosition == null || context.level.isClientSide ) {
            return InteractionResult.PASS
        }

        val ship = context.level.getShipObjectManagingPos(blockPosition)


        if(player != null) { pulseForce = player!!.lookAngle.toJOML().normalize().mul( Force ) }

        return super.useOn(context)
    }
    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl
        if(pulseForce != null) {
            physShip.applyInvariantForce(pulseForce!!)
            pulseForce = null
        }
    }
}