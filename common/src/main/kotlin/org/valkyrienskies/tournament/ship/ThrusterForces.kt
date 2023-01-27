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
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.tournamentConfig
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
class ThrusterForces() : ShipForcesInducer {
    val thrusters = CopyOnWriteArrayList<Pair<Vector3ic, Vector3dc>>()

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl

        thrusters.forEach {
            val (pos, force) = it


            val tForce = physShip.transform.shipToWorld.transformDirection(force, Vector3d()) //.shipToWorld.transformDirection(force, Vector3d())
            val tPos = Vector3d(pos).add( 0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)

            if(force.isFinite && physShip.poseVel.vel.length()< 50){
                println(force)

                physShip.applyInvariantForceToPos(tForce.mul(tournamentConfig.SERVER.ThrusterSpeed, Vector3d()), tPos)
            }
        }
    }


    fun addBlock(pos: BlockPos, force: Vector3dc) {
        thrusters.add(pos.toJOML() to force)
    }

    fun removeBlock(pos: BlockPos, force: Vector3dc) {
        thrusters.remove(pos.toJOML() to force)
    }

    companion object {
        fun getOrCreate(ship: ServerShip): ThrusterForces =
            ship.getAttachment<ThrusterForces>()
                ?: ThrusterForces().also { ship.saveAttachment(it) }
    }
}