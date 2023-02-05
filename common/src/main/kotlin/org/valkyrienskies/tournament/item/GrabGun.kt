// aded back from 15a9250 for test stuff

package org.valkyrienskies.tournament.item

import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.joml.*
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.apigame.constraints.*
import org.valkyrienskies.core.impl.api.LoadedServerShipInternal
import org.valkyrienskies.mod.common.*
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.api.CWItem
import org.valkyrienskies.tournament.mixinduck.MixinPlayerDuck
import org.valkyrienskies.tournament.tournamentItems
import java.lang.Math


class GrabGun : CWItem(
        Properties().stacksTo(1).tab(tournamentItems.getTab())
) {

    open fun getState(player: Player?): GravitronState {
        val p: MixinPlayerDuck? = player as MixinPlayerDuck?
        var s: GravitronState? = p?.cw_getGravitronState()
        if (s == null) {
            s = GravitronState()
            p?.cw_setGravitronState(s)
        }
        return s
    }

    override fun useOn(context: UseOnContext): InteractionResult? {
        if (context.level is ServerLevel && context.player != null) {
            val s: GravitronState = getState(context.player)
            if (s.shipID == null && !context.player!!.cooldowns.isOnCooldown(this) && !s.grabbing) {
                s.grabbing = true
                context.player!!.cooldowns.addCooldown(this, 20)
                s.grabCD = 20
                tryGrabShip(s, context.level as ServerLevel, context)
            }
        }
        return super.useOn(context)
    }

    // || ITEM FUNCTIONS || //
    override fun use(level: Level?, player: Player?, usedHand: InteractionHand?): InteractionResultHolder<ItemStack?>? {
        val s: GravitronState = getState(player)
        if (s.shipID != null && s.grabCD == 0 && s.grabbing) {
            s.shouldDrop = true
        }
        return super.use(level, player, usedHand)
    }

    override fun inventoryTick(stack: ItemStack?, level: Level?, entity: Entity?, slotId: Int, isSelected: Boolean) {
        if (entity !is Player || level !is ServerLevel) {
            return
        }
        val s: GravitronState = getState(entity)
        if (isSelected && !s.shouldDrop) {
            updateShip(s, level, entity)
        } else {
            dropShip(s, level)
        }
        if (s.grabCD > 0) {
            s.grabCD--
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected)
    }

    // called first to put the ship into the players grasp
    fun tryGrabShip(s: GravitronState?, level: ServerLevel, context: UseOnContext) {
        if (context.player == null) {
            return
        }
        val ship = context.level.getShipManagingPos(context.clickedPos)
        val grabPosInShip = context.clickLocation.toJOML()
        val grabPosInWorld = Vector3d(grabPosInShip)
        if (level.isBlockInShipyard(context.clickedPos) && ship == null) {
            return
        }
        if (ship == null) {
            return  // todo: try to assemble a ship when grabbing
            //            DenseBlockPosSet toAssemble = new DenseBlockPosSet();
//            toAssemble.add(context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ());
//            ship = ShipAssemblyKt.createNewShipWithBlocks(context.getClickedPos(), toAssemble, level);
//            ship.getWorldToShip().transformPosition(grabPosInShip);
        } else {
            ship.shipToWorld.transformPosition(grabPosInWorld)
        }
        s?.let { grabShip(it, context.player!!, ship, grabPosInShip) }
    }

    // || SHIP FUNCTIONS || //

    // || SHIP FUNCTIONS || //
    fun grabShip(s: GravitronState, p: Player, ship: Ship, grabPosInShip: Vector3dc?) {
        s.shipID = ship.id
        s.HeldBlockPos = ship.transform.shipToWorld.transformPosition(Vector3d(grabPosInShip))
        s.PlayerGrabbedRotation = Vector2d(p.xRot.toDouble(), p.yRot.toDouble())
        s.ShipGrabbedPos = Vector3d(grabPosInShip)
        s.ShipGrabbedRot = ship.transform.shipToWorldRotation
    }

    // sets down the ship
    fun dropShip(s: GravitronState, level: ServerLevel?) {
        s.grabbing = false
        if (level != null && !level.isClientSide) {
            delConstraint(level, s.positionConstraintID)
            delConstraint(level, s.positionDampeningConstraintID)
            delConstraint(level, s.rotationConstraintID)
            delConstraint(level, s.rotationDampeningConstraintID)
            s.shipID = null
            s.positionConstraintID = null
            s.rotationConstraintID = null
            s.positionDampeningConstraintID = null
            s.rotationDampeningConstraintID = null
            s.shouldDrop = false
        }
    }

    // ONLY IN DEBUG SHOULD THIS BE USED
    fun printRemovedConstraints(vararg constraints: Int?) {
        for (constraint in constraints) {
            if (constraint != null) {
                println("Removed $constraint")
            }
        }
    }

    fun updateShip(s: GravitronState, level: ServerLevel, entity: Entity) {
        if (s.grabbing) {
            if (s.shipID != null) {
                val shipUnloaded: Ship? = level.shipObjectWorld.allShips.getById(s.shipID!!)
                val ship = level.shipObjectWorld.loadedShips.getById(s.shipID!!)
                val worldShipID = level.shipObjectWorld.dimensionToGroundBodyIdImmutable[level.dimensionId]
                if (ship != null && (ship as LoadedServerShipInternal).areVoxelsFullyLoaded()) {
                    val mass = ship.inertiaData.mass

                    // Update Rot Values
                    val playerCurrentRotation = Vector2d(entity.xRot.toDouble(), entity.yRot.toDouble())
                    val origPlayerRot: Quaterniondc =
                        playerRotToQuaternion(s.PlayerGrabbedRotation!!.x, s.PlayerGrabbedRotation!!.y)!!.normalize()
                    val newPlayerRot: Quaterniondc =
                        playerRotToQuaternion(playerCurrentRotation.x, playerCurrentRotation.y)!!.normalize()
                    val deltaPlayerRot: Quaterniondc =
                        newPlayerRot.mul(origPlayerRot.conjugate(Quaterniond()), Quaterniond())
                    val rotation = deltaPlayerRot.mul(s.ShipGrabbedRot, Quaterniond()).normalize()

                    // Update Pos Values
                    s.HeldBlockPos = entity.position().toJOML().add(0.0, entity.eyeHeight.toDouble(), 0.0)
                        .add(entity.lookAngle.toJOML().normalize().mul(getShipSize(ship)))
                    val Location: Vector3d = Vector3d(s.ShipGrabbedPos)
                    val Position: Vector3d = Vector3d(s.HeldBlockPos)
                    val AttachmentCompliance = 1e-6 / mass
                    val AttachmentMaxForce = 1e10
                    val RotationMaxForce = 1e10
                    val constraint = VSAttachmentOrientationConstraint(
                        s.shipID!!, worldShipID!!, AttachmentCompliance, Location, Position,
                        AttachmentMaxForce, rotation, Quaterniond(), RotationMaxForce
                    )
                    val PosDampingCompliance = 0.0
                    val PosDampingMaxForce = 0.0
                    val PosDampingEff = 100.0
                    val (shipId0, shipId1, compliance, localPos0, localPos1, maxForce, posDamping) = VSPosDampingConstraint(
                        s.shipID!!, worldShipID, PosDampingCompliance, Location, Position,
                        PosDampingMaxForce, PosDampingEff
                    )
                    val RotDampingCompliance = 0.0
                    val RotDampingMaxForce = 0.0
                    val RotDampingEff = 100.0
                    val (shipId01, shipId11, compliance1, localRot0, localRot1, maxTorque, rotDamping, rotDampingAxes) = VSRotDampingConstraint(
                        s.shipID!!, worldShipID, RotDampingCompliance, rotation, Quaterniond(),
                        RotDampingMaxForce, RotDampingEff, VSRotDampingAxes.ALL_AXES
                    )

                    //Drop and re grab the Constraints

//                    System.out.println(Location);
//                    System.out.println(Position);
//                    System.out.println();
                    delConstraint(level, s.positionConstraintID)
                    // delConstraint(level, s.positionDampeningConstraintID);
                    // delConstraint(level, s.rotationConstraintID);
                    // delConstraint(level, s.rotationDampeningConstraintID);
                    s.positionConstraintID = level.shipObjectWorld.createNewConstraint(constraint)
                    // s.rotationConstraintID = VSGameUtilsKt.getShipObjectWorld(level).createNewConstraint(RotationConstraint);
                    // s.positionDampeningConstraintID = VSGameUtilsKt.getShipObjectWorld(level).createNewConstraint(PosDampingConstraint);
                    // s.rotationDampeningConstraintID = VSGameUtilsKt.getShipObjectWorld(level).createNewConstraint(RotDampingConstraint);
                } else if (shipUnloaded == null) {
                    dropShip(s, level)
                }
            }
        }
    }

    fun delConstraint(level: ServerLevel, ID: Int?) {
        if (ID != null) {
            level.shipObjectWorld.removeConstraint(ID)
        }
    }

    // || MATH FUNCTIONS || //
    fun getShipSize(thisship: Ship?): Double {
        return if (thisship != null) {
            val MinVector = Vector3d(
                thisship.shipAABB!!.minX().toDouble(),
                thisship.shipAABB!!.minY().toDouble(),
                thisship.shipAABB!!.minZ().toDouble()
            )
            val MaxVector = Vector3d(
                thisship.shipAABB!!.maxX().toDouble(),
                thisship.shipAABB!!.maxY().toDouble(),
                thisship.shipAABB!!.maxZ().toDouble()
            )
            MinVector.sub(MaxVector).length() + 0.75
        } else 0.0
    }

    fun playerRotToQuaternion(pitch: Double, yaw: Double): Quaterniond? {
        return Quaterniond().rotateY(Math.toRadians(-yaw)).rotateX(Math.toRadians(pitch))
    }

    override fun getUseAnimation(stack: ItemStack?): UseAnim? {
        return UseAnim.BOW
    }

    fun getArmPose(stack: ItemStack?, player: AbstractClientPlayer, hand: InteractionHand?): HumanoidModel.ArmPose? {
        return if (!player.swinging) {
            HumanoidModel.ArmPose.CROSSBOW_HOLD
        } else null
    }

    override fun canAttackBlock(state: BlockState?, world: Level?, pos: BlockPos?, player: Player?): Boolean {
        return false
    }

    class GravitronState {
        var grabbing = false
        var shouldDrop = false
        var HeldBlockPos: Vector3d? = null
        var PlayerGrabbedRotation: Vector2d? = null // Pitch , Yaw
        var ShipGrabbedPos: Vector3d? = null
        var ShipGrabbedRot: Quaterniondc? = null
        var shipID: Long? = null
        var positionConstraintID: Int? = null
        var rotationConstraintID: Int? = null
        var positionDampeningConstraintID: Int? = null
        var rotationDampeningConstraintID: Int? = null
        var grabCD = 0
    }
}