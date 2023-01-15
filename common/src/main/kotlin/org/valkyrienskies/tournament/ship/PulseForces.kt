package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ServerShipUser
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import java.util.concurrent.CopyOnWriteArrayList


@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
class PulseForces(@JsonIgnore override var ship: ServerShip?) : ShipForcesInducer, ServerShipUser {
    private val Pulses = CopyOnWriteArrayList<Pair<Vector3d, Vector3d>>()

    override fun applyForces(physShip: PhysShip) {

        Pulses.forEach {
            val (pos, force) = it
            
            physShip.applyInvariantForce(force)
            println(force)
        }

        Pulses.clear()
    }


    fun addPulse(pos: Vector3d, force: Vector3d) {
        Pulses.add(pos to force)
    }

    companion object {
        fun getOrCreate(ship: ServerShip): PulseForces =
            ship.getAttachment<PulseForces>()
                ?: PulseForces(ship).also { ship.saveAttachment(it) }
    }
}