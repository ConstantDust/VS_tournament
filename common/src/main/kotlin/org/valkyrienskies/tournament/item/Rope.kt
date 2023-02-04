// aded back from 15a9250 for test stuff

package org.valkyrienskies.tournament.item

import net.minecraft.core.BlockPos
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
import org.valkyrienskies.physics_api.ConstraintId
import org.valkyrienskies.tournament.tournamentItems


class Rope : Item(
        Properties().stacksTo(1).tab(tournamentItems.TAB)
) {

    var CurrentPlayer : Player? = null
    var IsMain : Boolean = false
    var clickedPosition: BlockPos? = null
    var clickedShipId: ShipId? = null


    override fun useOn(context: UseOnContext): InteractionResult {

        val level = context.level
        val blockPos = context.clickedPos
        println("Block: $blockPos")
        val ship = context.level.getShipObjectManagingPos(blockPos)

        if (level is ServerLevel || ship != null) {
            val shipId = ship?.id
            clickedShipId = shipId



        }else if(level is ServerLevel) {
            clickedPosition = blockPos
        }


        println(clickedPosition)
        println(clickedShipId)
        println("------------")
        return super.useOn(context)
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        super.inventoryTick(stack, level, entity, slotId, isSelected)
    }
}