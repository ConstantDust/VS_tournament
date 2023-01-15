package org.valkyrienskies.tournament.item

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import org.joml.Vector3d
import org.valkyrienskies.core.apigame.constraints.*
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.ship.PulseForces

class PulseGun : Item(
    Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)
){

    val Force : Double = 1000.0
    var pulseForce : Vector3d? = null

    override fun useOn(context: UseOnContext): InteractionResult {

        val player = context.player
        val blockPosition = context.clickedPos
        val blockLocation = context.clickLocation.toJOML()

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

        pulseForce = player!!.lookAngle.toJOML().normalize().mul(Force * ship.inertiaData.mass)

        PulseForces.getOrCreate(ship!!).addPulse(blockLocation, pulseForce!!)

        return super.useOn(context)
    }
}