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
import java.util.concurrent.ConcurrentHashMap
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
    private val farters = mutableListOf<Pair<Vector3i, Direction>>()

    public var pulseForces : Vector3dc? = null

    var consumed = 0f
        private set

    private data class ControlData(
        var forwardImpulse: Float = 0.0f,
        var leftImpulse: Float = 0.0f,
        var upImpulse: Float = 0.0f,
        var liftImpulse: Float = 0.0f,
    )

    override fun applyForces(physShip: PhysShip) {
        if (ship == null) return

        val forcesApplier = physShip

        physShip as PhysShipImpl

        val mass = physShip.inertia.shipMass
        val moiTensor = physShip.inertia.momentOfInertiaTensor
        val segment = physShip.segments.segments[0]?.segmentDisplacement!!
        val omega = SegmentUtils.getOmega(physShip.poseVel, segment, Vector3d())
        val vel = SegmentUtils.getVelocity(physShip.poseVel, segment, Vector3d())

//        val buoyantFactorPerFloater = min(
//            tournamentConfig.SERVER.floaterBuoyantFactorPerKg / 15 / mass,
//            tournamentConfig.SERVER.maxFloaterBuoyantFactor
//        )

        physShip.buoyantFactor = 1.0 //+ floaters * buoyantFactorPerFloater
        // Revisiting eureka control code.
        // [x] Move torque stabilization code
        // [x] Move linear stabilization code
        // [x] Revisit player controlled torque
        // [x] Revisit player controlled linear force
        // [x] Anchor freezing
        // [x] Rewrite Alignment code
        // [x] Revisit Elevation code
        // [x] Balloon limiter
        // [ ] Add Cruise code
        // [ ] Rotation based of shipsize
        // [x] Engine consumption
        // [ ] Fix elevation sensititvity

        // region Balloons
        var idealUpwardVel = Vector3d(0.0, 0.0, 0.0)
        //        val elevationSnappiness = 10.0
        val idealUpwardForce = Vector3d(
            0.0,
            idealUpwardVel.y() - vel.y() - (GRAVITY / 10.0),
            0.0
        ).mul(mass * 10.0)

        physShip as PhysShipImpl
        val shipCoordsinworld: Vector3dc = physShip.poseVel.pos

        val shipCoords = ship!!.transform.positionInShip
        var falloffamount = 0.0
        var falloff = 1.0

        val actualUpwardForce = Vector3d(0.0, (5000.0/falloff), 0.0)
        balloonpos.forEach {
            if (actualUpwardForce.isFinite) {
                forcesApplier.applyInvariantForceToPos(actualUpwardForce, Vector3d(it).sub(shipCoords))
//                println("APPLIED AT " + it.sub(shipCoords).toString())
            }
        }
        // end region



        farters.forEach {
            val (pos, dir) = it

            val tPos = Vector3d(pos).add( 0.5, 0.5, 0.5).sub(ship!!.transform.positionInShip)

            if (tPos.isFinite) {
                physShip.applyRotDependentForceToPos(dir.normal.toJOMLD().mul(-10000.0), tPos)
            }
        }
    }
    var power = 0.0

    var balloons = 0 // Amount of balloons
        set(v) {
            field = v; deleteIfEmpty()
        }

    var balloonpos = mutableListOf<Vector3dc>()

    override fun tick() {
        extraForce = power
        power = 0.0
        consumed = physConsumption * /* should be phyics ticks based*/ 0.1f
        physConsumption = 0.0f
//        balloonTick()
    }

    private fun deleteIfEmpty() {
        if (balloons == 0) {
            ship?.saveAttachment<tournamentShipControl>(null)
        }
    }

    fun addFarter(pos: BlockPos, dir: Direction) {
        farters.add(pos.toJOML() to dir)
    }

    fun removeFarter(pos: BlockPos, dir: Direction) {
        farters.remove(pos.toJOML() to dir)
    }

    companion object {
        fun getOrCreate(ship: ServerShip): tournamentShipControl {
            return ship.getAttachment<tournamentShipControl>()
                ?: tournamentShipControl().also { ship.saveAttachment(it) }
        }

        private val forcePerBalloon get() = 5000 * -GRAVITY

        private const val GRAVITY = -10.0
    }
}
