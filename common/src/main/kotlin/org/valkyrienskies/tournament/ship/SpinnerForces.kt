package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ServerShipUser
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import java.util.concurrent.ConcurrentHashMap

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
class SpinnerForces() : ShipForcesInducer {
    private val Spinners = ConcurrentHashMap<Vector3ic, Vector3dc>()

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl
        Spinners.forEach {
            val (pos, torque) = it

            val torqueGlobal = physShip.transform.shipToWorldRotation.transform(torque, Vector3d())

            physShip.applyInvariantTorque(torqueGlobal)

        }
    }


    fun addBlock(pos: Vector3ic, torque: Vector3dc) {
        Spinners[pos] = torque
    }

    fun removeBlock(pos: Vector3ic) {
        Spinners.remove(pos)
    }

    companion object {
        fun getOrCreate(ship: ServerShip): SpinnerForces =
            ship.getAttachment<SpinnerForces>()
                ?: SpinnerForces().also { ship.saveAttachment(it) }
    }
}