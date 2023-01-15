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

class GrabGun : Item(
    Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)
), ShipForcesInducer {

    var CurrentPlayer : Player? = null
    var thisShipID : ShipId? = null
    var grabbing : Boolean = false
    var Weight : Double = 1.0

    var SettingRot : Quaterniondc? = null
    var CurrentPlayerPitch : Double = 0.0
    var CurrentPlayerYaw : Double = 0.0

    var SettingPos : Vector3d? = null
    var thisAttachPoint : Vector3d? = null

    var thisAttachConstraintID : ConstraintId? = null
    var thisRotationConstraintID : ConstraintId? = null
    var thisPosDampingConstraintID : ConstraintId? = null
    var thisRotDampingConstraintID : ConstraintId? = null

    override fun useOn(context: UseOnContext): InteractionResult {
        if(grabbing) {
            grabbing = false
        } else {
            CurrentPlayer = context.player
            val level = context.level
            val hitLoc = context.clickLocation
            val blockPos = context.clickedPos
            val ship = context.level.getShipObjectManagingPos(blockPos)

            OnDropConstraints(level)

            if (level !is ServerLevel || ship == null) {
                return InteractionResult.PASS
            }

            val shipId = ship.id
            thisShipID = shipId

            SettingPos = ship.transform.shipToWorld.transformPosition(hitLoc.toJOML())
            thisAttachPoint = hitLoc.toJOML()

            SettingRot = ship.transform.shipToWorldRotation
            CurrentPlayerPitch = CurrentPlayer!!.xRot.toDouble()
            CurrentPlayerYaw = CurrentPlayer!!.yRot.toDouble()

            grabbing = true

        }
        return super.useOn(context)
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (isSelected && thisShipID != null){
            val tempShip = level.shipObjectWorld.loadedShips.getById(thisShipID!!)

            if(tempShip != null) {
                val MinVec = Vector3d(
                    tempShip!!.shipAABB!!.minX().toDouble(),
                    tempShip!!.shipAABB!!.minY().toDouble(),
                    tempShip!!.shipAABB!!.minZ().toDouble()
                )
                val MaxVec = Vector3d(
                    tempShip!!.shipAABB!!.maxX().toDouble(),
                    tempShip!!.shipAABB!!.maxY().toDouble(),
                    tempShip!!.shipAABB!!.maxZ().toDouble()
                )
                val totalScale = MinVec.sub(MaxVec).length() + 0.75

                println(totalScale)
                OnTickConstraints(totalScale, level)
            }
        } else {
            OnDropConstraints(level)
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected)
    }

    fun OnDropConstraints(level: Level) {
        grabbing = false
        if (level is ServerLevel && thisShipID != null && thisRotationConstraintID != null && thisAttachConstraintID != null) {
            level.shipObjectWorld.removeConstraint(thisRotationConstraintID!!)
            level.shipObjectWorld.removeConstraint(thisAttachConstraintID!!)
            level.shipObjectWorld.removeConstraint(thisPosDampingConstraintID!!)
            level.shipObjectWorld.removeConstraint(thisRotDampingConstraintID!!)
        }
    }

    fun OnTickConstraints(Distance: Double, level: Level) {
        if(grabbing) {
            if (level is ServerLevel && CurrentPlayer != null && thisShipID != null) {

                val tempShip = level.shipObjectWorld.loadedShips.getById(thisShipID!!)
                val otherShipId = level.shipObjectWorld.dimensionToGroundBodyIdImmutable[level.dimensionId]!!

                // Update Rot Values
                val newCurrentPlayerPitch = CurrentPlayer!!.xRot.toDouble()
                val newCurrentPlayerYaw = CurrentPlayer!!.yRot.toDouble()

                val ogPlayerRot = playerRotToQuaternion(CurrentPlayerPitch,CurrentPlayerYaw)
                val newPlayerRot = playerRotToQuaternion(newCurrentPlayerPitch, newCurrentPlayerYaw)
                val deltaPlayerRot = newPlayerRot.mul(ogPlayerRot.conjugate(), Quaterniond())
                val newRot = deltaPlayerRot.mul(SettingRot, Quaterniond()).normalize()

                // Update Pos Values
                SettingPos = CurrentPlayer!!.position().toJOML().add(0.0, CurrentPlayer!!.eyeHeight.toDouble(), 0.0) .add(CurrentPlayer!!.lookAngle.toJOML().normalize().mul(Distance))
                val posOffset = Vector3d(thisAttachPoint!!).sub(tempShip!!.transform.positionInShip)
                val posGlobalOffset = tempShip.transform.shipToWorld.transformDirection(posOffset, Vector3d())

                // Weight Managment
                if(Weight != null && Weight != 0.0){
                    Weight = 1.0
                }

                val AttachmentCompliance = 1e-1 * Weight
                val AttachmentMaxForce = 1e3 * Weight
                val AttachmentFixedDistance = 0.0
                val AttachmentConstraint = VSAttachmentConstraint(
                    thisShipID!!, otherShipId, AttachmentCompliance, Vector3d(thisAttachPoint!!).sub(posOffset), Vector3d(SettingPos!!).sub(posGlobalOffset),
                    AttachmentMaxForce, AttachmentFixedDistance
                )

                val RotationCompliance = 1e-1 *  Weight
                val RotationMaxForce = 1e4 * Weight
                val RotationConstraint = VSFixedOrientationConstraint(
                    thisShipID!!, otherShipId, RotationCompliance, Quaterniond(), newRot!!,
                    RotationMaxForce
                )

                val PosDampingCompliance = 1e-2 * Weight
                val PosDampingMaxForce = 1e5 * Weight
                val PosDampingEff = 1000.0 * Weight
                val PosDampingConstraint = VSPosDampingConstraint(
                    thisShipID!!, otherShipId, PosDampingCompliance, Vector3d(thisAttachPoint!!).sub(posOffset), Vector3d(SettingPos!!).sub(posGlobalOffset),
                    PosDampingMaxForce, PosDampingEff
                )

                val RotDampingCompliance = 1e-2 * Weight
                val RotDampingMaxForce = 1e5 * Weight
                val RotDampingEff = 1000.0 * Weight
                val RotDampingConstraint = VSRotDampingConstraint(
                    thisShipID!!, otherShipId, RotDampingCompliance, Quaterniond(), newRot!!,
                    RotDampingMaxForce, RotDampingEff, VSRotDampingAxes.ALL_AXES
                )

                //Drop and re grab the Constraints
                OnDropConstraints(level)
                grabbing = true

                val RotationConstraintId = level.shipObjectWorld.createNewConstraint(RotationConstraint)
                val AttachConstraintId = level.shipObjectWorld.createNewConstraint(AttachmentConstraint)
                val PosDampingConstraintId = level.shipObjectWorld.createNewConstraint(PosDampingConstraint)
                val RotDampingConstraintId = level.shipObjectWorld.createNewConstraint(RotDampingConstraint)
                thisAttachConstraintID = RotationConstraintId
                thisRotationConstraintID = AttachConstraintId
                thisPosDampingConstraintID = PosDampingConstraintId
                thisRotDampingConstraintID = RotDampingConstraintId

            }
        }else if (level is ServerLevel && thisShipID != null) {
            OnDropConstraints(level)
        }
    }

    fun playerRotToQuaternion(pitch:Double, yaw:Double) : Quaterniond {
        return Quaterniond().rotateY(Math.toRadians(-yaw)).rotateX(Math.toRadians(pitch))
    }

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl
        Weight = physShip.inertia.shipMass
    }
}