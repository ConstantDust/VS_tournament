package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ServerShipUser
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.tournamentConfig
import java.util.concurrent.ConcurrentHashMap

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
class BalloonForces(@JsonIgnore override var ship: ServerShip?) : ShipForcesInducer, ServerShipUser {
    val Balloons = mutableListOf<Pair<Vector3i, Double>>()

    override fun applyForces(physShip: PhysShip) {
        val ship = ship as ServerShip
        Balloons.forEach {
            val (pos, pow) = it


            val tPos = Vector3d(pos).add( 0.5, 0.5, 0.5).sub(ship.transform.positionInShip)

            physShip.applyInvariantForceToPos(Vector3d(0.0,(pow + 1.0) * tournamentConfig.SERVER.BalloonPower,0.0), tPos)

        }
    }


    fun addBlock(pos: BlockPos, pow: Double) {
        Balloons.add(pos.toJOML() to pow)
    }

    fun removeBlock(pos: BlockPos, pow: Double) {
        Balloons.remove(pos.toJOML() to pow)
    }

    companion object {
        fun getOrCreate(ship: ServerShip): BalloonForces =
            ship.getAttachment<BalloonForces>()
                ?: BalloonForces(ship).also { ship.saveAttachment(it) }
    }
}