package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ServerShipUser
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
class SpinnerForces(@JsonIgnore override var ship: ServerShip?) : ShipForcesInducer, ServerShipUser {
    private val Spinners = mutableListOf<Pair<Vector3i, Direction>>()

    override fun applyForces(physShip: PhysShip) {
        val ship = ship as ServerShip
        Spinners.forEach {
            val (pos, dir) = it

            val tDir = ship.shipToWorld.transformDirection(dir.normal.toJOMLD())

            val Multiplier = 10000.0

            physShip.applyInvariantTorque(tDir.mul(Multiplier))

        }
    }


    fun addBlock(pos: BlockPos, dir: Direction) {
        Spinners.add(pos.toJOML() to dir)
    }

    fun removeBlock(pos: BlockPos, dir: Direction) {
        Spinners.remove(pos.toJOML() to dir)
    }

    companion object {
        fun getOrCreate(ship: ServerShip): SpinnerForces =
            ship.getAttachment<SpinnerForces>()
                ?: SpinnerForces(ship).also { ship.saveAttachment(it) }
    }
}