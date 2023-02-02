package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import org.joml.*
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ServerShipUser
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.core.impl.api.Ticked
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.core.impl.pipelines.SegmentUtils
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.tournament.tournamentConfig
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.*

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
class tournamentShipControl : ShipForcesInducer, ServerShipUser, Ticked {
    @JsonIgnore
    override var ship: ServerShip? = null

    private var extraForce = 0.0
    private var physConsumption = 0f

    private val Balloons = mutableListOf<Pair<Vector3i, Double>>()
    private val Spinners = mutableListOf<Pair<Vector3ic, Vector3dc>>()
    private val Thrusters = mutableListOf<Triple<Vector3ic, Vector3dc, Int>>()
    private val Pulses = CopyOnWriteArrayList<Pair<Vector3d, Vector3d>>()

    var consumed = 0f
        private set

    override fun applyForces(physShip: PhysShip) {
        if (ship == null) return
        physShip as PhysShipImpl

        val mass = physShip.inertia.shipMass
        val moiTensor = physShip.inertia.momentOfInertiaTensor
        val segment = physShip.segments.segments[0]?.segmentDisplacement!!
        val omega = SegmentUtils.getOmega(physShip.poseVel, segment, Vector3d())
        val vel = SegmentUtils.getVelocity(physShip.poseVel, segment, Vector3d())

        Balloons.forEach {
            val (pos, pow) = it

            val tPos = Vector3d(pos).add( 0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)

            val tHeight = physShip.transform.positionInWorld.y()
            var tPValue = tournamentConfig.SERVER.BaseHeight - (tHeight * tHeight) / 1000.0

            if (physShip.poseVel.vel.y() > 10.0)    {
                tPValue *= 0.25
            }
            if(tPValue <= 0){
                tPValue = 0.0
            }

            physShip.applyInvariantForceToPos(Vector3d(0.0,(pow + 1.0) * tournamentConfig.SERVER.BalloonPower * tPValue,0.0), tPos)

        }

        Spinners.forEach {
            val (pos, torque) = it

            val torqueGlobal = physShip.transform.shipToWorldRotation.transform(torque, Vector3d())

            physShip.applyInvariantTorque(torqueGlobal.mul(tournamentConfig.SERVER.SpinnerSpeed ))

        }

        Thrusters.forEach {
            val (pos, force, tier) = it

            val tForce = physShip.transform.shipToWorld.transformDirection(force, Vector3d()) //.shipToWorld.transformDirection(force, Vector3d())
            val tPos = Vector3d(pos).add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)

            if (force.isFinite && physShip.poseVel.vel.length() < 50) {
                physShip.applyInvariantForceToPos(tForce.mul(tournamentConfig.SERVER.ThrusterSpeed * tier, Vector3d()), tPos)
            }
        }

        //Pulse Gun
        Pulses.forEach {
            val (pos, force) = it
            val tPos = Vector3d(pos).add( 0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)
            val tForce = physShip.transform.worldToShip.transformDirection(force, Vector3d())

            physShip.applyRotDependentForceToPos(tForce, tPos)
        }

        Pulses.clear()
    }
    var power = 0.0

    var balloons = 0 // Amount of balloons
        set(v) {
            field = v; deleteIfEmpty()
        }

    override fun tick() {
        extraForce = power
        power = 0.0
        consumed = physConsumption *0.1f
        physConsumption = 0.0f
//        balloonTick()
    }

    private fun deleteIfEmpty() {
        if (balloons == 0) {
            ship?.saveAttachment<tournamentShipControl>(null)
        }
    }


    fun addBalloon(pos: BlockPos, pow: Double) {
        Balloons.add(pos.toJOML() to pow)
    }

    fun removeBalloon(pos: BlockPos, pow: Double) {
        Balloons.remove(pos.toJOML() to pow)
    }

    fun addThruster(pos: BlockPos, tier: Int, force: Vector3dc) {
        Thrusters.add(Triple(pos.toJOML(), force, tier))
    }
    fun removeThruster(pos: BlockPos, tier: Int,force: Vector3dc) {
        Thrusters.remove(Triple(pos.toJOML(), force, tier))
    }

    fun addSpinner(pos: Vector3ic, torque: Vector3dc) {
        Spinners.add(pos to torque)
    }
    fun removeSpinner(pos: Vector3ic, torque: Vector3dc) {
        Spinners.remove(pos to torque)
    }

    fun addPulse(pos: Vector3d, force: Vector3d) {
        Pulses.add(pos to force)
    }

    fun forceStopThruster(pos: BlockPos) {
        Thrusters.removeAll { it.first == pos }
    }



    companion object {
        fun getOrCreate(ship: ServerShip): tournamentShipControl {
            return ship.getAttachment<tournamentShipControl>()
                ?: tournamentShipControl().also { ship.saveAttachment(it) }
        }

        private val forcePerBalloon get() = tournamentConfig.SERVER.BalloonPower * -GRAVITY

        private const val GRAVITY = -10.0
    }
}
