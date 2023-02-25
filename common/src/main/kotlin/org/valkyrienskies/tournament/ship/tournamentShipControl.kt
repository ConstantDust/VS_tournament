package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.core.BlockPos
import org.joml.*
import org.valkyrienskies.mod.common.util.toJOML
import org.joml.Vector3d
import org.valkyrienskies.core.api.*
import org.valkyrienskies.core.game.ships.PhysShip
import org.valkyrienskies.core.pipelines.SegmentUtils
import org.valkyrienskies.core.util.y
import org.valkyrienskies.tournament.tournamentConfig
import java.util.concurrent.CopyOnWriteArrayList

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

//    private var weightedCenterOfLift:Vector3d = Vector3d()
//    private var BalloonsPower = 1.0
//    private var EFFMulti = 0.0
    private val Balloons = mutableListOf<Pair<Vector3i, Double>>()
    private val Spinners = mutableListOf<Pair<Vector3ic, Vector3dc>>()
    private val Thrusters = mutableListOf<Triple<Vector3ic, Vector3dc, Double>>()
    private val Pulses = CopyOnWriteArrayList<Pair<Vector3d, Vector3d>>()

    var consumed = 0f
        private set

    override fun applyForces(forcesApplier: ForcesApplier, physShip: PhysShip) {
        if (ship == null) return

        val mass = physShip.inertia.shipMass
        val moiTensor = physShip.inertia.momentOfInertiaTensor
        val segment = physShip.segments.segments[0]?.segmentDisplacement!!
        val vel = SegmentUtils.getVelocity(physShip.poseVel, segment, Vector3d())

        Balloons.forEach {
            val (pos, pow) = it

            val tPos = Vector3d(pos).add( 0.5, 0.5, 0.5).sub(ship!!.shipTransform.shipPositionInShipCoordinates)


            val tHeight = ship!!.shipTransform.shipPositionInWorldCoordinates.y
            var tPValue = tournamentConfig.SERVER.BaseHeight - ((tHeight * tHeight) / 1000.0)

            if (vel.y() > 10.0)    {
                tPValue = (-vel.y() * 0.25)
            }
            if(tPValue <= 0){
                tPValue = 0.0
            }

            forcesApplier.applyInvariantForceToPos(
                Vector3d(
                    0.0,
                    (pow + 1.0) * tournamentConfig.SERVER.BalloonPower * tPValue,
                    0.0),
                    tPos
            )


        }

        Spinners.forEach {
            val (pos, torque) = it

            val torqueGlobal = ship!!.shipTransform.shipCoordinatesToWorldCoordinatesRotation.transform(torque, Vector3d())

            forcesApplier.applyInvariantTorque(torqueGlobal.mul(tournamentConfig.SERVER.SpinnerSpeed ))

        }

        Thrusters.forEach {
            val (pos, force, tier) = it

            val tForce = ship!!.shipTransform.worldToShipMatrix.transformDirection(force, Vector3d()) //.shipToWorld.transformDirection(force, Vector3d())
            val tPos = Vector3d(pos).add(0.5, 0.5, 0.5).sub(ship!!.shipTransform.shipPositionInShipCoordinates)

            if (force.isFinite && physShip.poseVel.vel.length() < 50) {
                forcesApplier.applyInvariantForceToPos(tForce.mul(tournamentConfig.SERVER.ThrusterSpeed * tier, Vector3d()), tPos)
            }
        }

        //Pulse Gun
        Pulses.forEach {
            val (pos, force) = it

            val tForce = ship!!.shipTransform.worldToShipMatrix.transformDirection(force.mul(tournamentConfig.SERVER.pulseGunForce * physShip.inertia.shipMass), Vector3d())

            forcesApplier.applyRotDependentForce(tForce)
        }

        Pulses.clear()
    }
    var power = 0.0


    override fun tick() {
        extraForce = power
        power = 0.0
        consumed = physConsumption *0.1f
        physConsumption = 0.0f
//        balloonTick()
    }

    private fun deleteIfEmpty() {
        ship?.saveAttachment<tournamentShipControl>(null)
    }


    fun addBalloon(pos: BlockPos, pow: Double) {
        Balloons.add(pos.toJOML() to pow)
//        weightedCenterOfLift = weightedCenterOfLift.add(pos.toJOMLD().mul(pow))
//        BalloonsPower += pow
    }

    fun removeBalloon(pos: BlockPos, pow: Double) {
        Balloons.remove(pos.toJOML() to pow)
//        weightedCenterOfLift = weightedCenterOfLift.sub(pos.toJOMLD().mul(pow))
//        BalloonsPower -= pow
    }

    fun addThruster(pos: BlockPos, tier: Double, force: Vector3dc) {
        Thrusters.add(Triple(pos.toJOML(), force, tier))
    }
    fun removeThruster(pos: BlockPos, tier: Double,force: Vector3dc) {
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
