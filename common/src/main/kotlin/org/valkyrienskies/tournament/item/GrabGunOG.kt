package org.valkyrienskies.tournament.item

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
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.constraints.VSFixedOrientationConstraint
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.physics_api.ConstraintId

class GrabGunOG : Item(
    Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)
) {
    var CurrentPlayer : Player? = null
    var grabbing : Boolean = false

    var SettingPos : Vector3d? = null
    var SettingRot : Quaterniondc? = null
    var CurrentPlayerPitch : Double = 0.0
    var CurrentPlayerYaw : Double = 0.0
    var thisShipID : ShipId? = null
    var thisAttachConstraintID : ConstraintId? = null
    var thisRotationConstraintID : ConstraintId? = null
    var thisAttachPoint : Vector3d? = null

    override fun useOn(context: UseOnContext): InteractionResult {
        if(grabbing) {
            grabbing = false
        } else {
            CurrentPlayer = context.player
            val level = context.level
            val hitLoc = context.clickLocation
            val blockPos = context.clickedPos
            val ship = context.level.getShipObjectManagingPos(blockPos)

            if (thisRotationConstraintID != null && thisAttachConstraintID != null) {
                OnDropConstraints(level)
            }

            if (level !is ServerLevel) {
                return InteractionResult.PASS
            }
            if (ship == null) {
                return InteractionResult.PASS
            }
            val shipId = ship.id
            val otherShipId = level.shipObjectWorld.dimensionToGroundBodyIdImmutable[level.dimensionId]!!

            thisShipID = shipId
            SettingPos = ship.transform.shipToWorld.transformPosition(hitLoc.toJOML())
            SettingRot = ship.transform.shipToWorldRotation
            thisAttachPoint = hitLoc.toJOML()

            CurrentPlayerPitch = CurrentPlayer!!.xRot.toDouble()
            CurrentPlayerYaw = CurrentPlayer!!.yRot.toDouble()


            val attachmentCompliance = 1e-8
            val attachmentMaxForce = 1e8
            val attachmentFixedDistance = 0.0
            val attachmentConstraint = VSAttachmentConstraint(
                shipId, otherShipId, attachmentCompliance, thisAttachPoint!!, SettingPos!!,
                attachmentMaxForce, attachmentFixedDistance
            )


            val RotationCompliance = 1e-10
            val RotationMaxForce = 1e10
            val RotationConstraint = VSFixedOrientationConstraint(
                shipId, otherShipId, RotationCompliance, Quaterniond(), SettingRot!!,
                RotationMaxForce
            )

            grabbing = true

            val RotationConstraintId = level.shipObjectWorld.createNewConstraint(RotationConstraint)
            val attachConstraintId = level.shipObjectWorld.createNewConstraint(attachmentConstraint)

            thisAttachConstraintID = attachConstraintId
            thisRotationConstraintID = RotationConstraintId

            println(shipId)
            println(blockPos.toJOMLD().add(0.5, 0.5, 0.5))
            println(hitLoc.toJOML())
            println()
        }
        return super.useOn(context)
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (isSelected){
            OnTickConstraints(15.0, level)

        } else if (thisRotationConstraintID != null && thisAttachConstraintID != null/* If Constraints Valid */) {
            OnDropConstraints(level)
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected)
    }

    fun OnDropConstraints(level: Level) {
        if (level is ServerLevel && thisShipID != null) {
            level.shipObjectWorld.removeConstraint(thisRotationConstraintID!!)
            level.shipObjectWorld.removeConstraint(thisAttachConstraintID!!)
            grabbing = false
        }
    }

    fun OnTickConstraints(Distance: Double, level: Level) {
        if(grabbing) {
            if (level is ServerLevel && CurrentPlayer != null && thisShipID != null) {

                SettingPos = CurrentPlayer!!.position().toJOML().add(0.0, CurrentPlayer!!.eyeHeight.toDouble(), 0.0) .add(CurrentPlayer!!.lookAngle.toJOML().normalize().mul(Distance))

                val newCurrentPlayerPitch = CurrentPlayer!!.xRot.toDouble()
                val newCurrentPlayerYaw = CurrentPlayer!!.yRot.toDouble()

                val ogPlayerRot = playerRotToQuaternion(CurrentPlayerPitch,CurrentPlayerYaw)
                val newPlayerRot = playerRotToQuaternion(newCurrentPlayerPitch, newCurrentPlayerYaw)

                val deltaPlayerRot = newPlayerRot.mul(ogPlayerRot.conjugate(), Quaterniond())

                val newRot = deltaPlayerRot.mul(SettingRot, Quaterniond()).normalize()

                val tempShip = level.shipObjectWorld.loadedShips.getById(thisShipID!!)
                val posOffset = Vector3d(thisAttachPoint!!).sub(tempShip!!.transform.positionInShip)
                val posGlobalOffset = tempShip.transform.shipToWorld.transformDirection(posOffset, Vector3d())


                val otherShipId = level.shipObjectWorld.dimensionToGroundBodyIdImmutable[level.dimensionId]!!

                val newAttachmentCompliance = 1e-8
                val newAttachmentMaxForce = 1e8
                val newAttachmentFixedDistance = 0.0
                val newAttachmentConstraint = VSAttachmentConstraint(
                    thisShipID!!, otherShipId, newAttachmentCompliance, Vector3d(thisAttachPoint!!).sub(posOffset), Vector3d(SettingPos!!).sub(posGlobalOffset),
                    newAttachmentMaxForce, newAttachmentFixedDistance
                )

                val newRotationCompliance = 1e-8
                val newRotationMaxForce = 1e8

                val newRotationConstraint = VSFixedOrientationConstraint(
                    thisShipID!!, otherShipId, newRotationCompliance, Quaterniond(), newRot!!,
                    newRotationMaxForce
                )

                OnDropConstraints(level)
                grabbing = true
                val newerRotationConstraintId = level.shipObjectWorld.createNewConstraint(newRotationConstraint)
                val newerAttachConstraintId = level.shipObjectWorld.createNewConstraint(newAttachmentConstraint)

                thisAttachConstraintID = newerAttachConstraintId
                thisRotationConstraintID = newerRotationConstraintId

            }
        }else {
            if (level is ServerLevel && thisShipID != null) {
                OnDropConstraints(level)
            }
        }
    }

    fun playerRotToQuaternion(pitch:Double, yaw:Double) : Quaterniond {
        return Quaterniond().rotateY(Math.toRadians(-yaw)).rotateX(Math.toRadians(pitch))
    }
}