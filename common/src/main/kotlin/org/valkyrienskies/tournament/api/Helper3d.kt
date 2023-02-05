package org.valkyrienskies.tournament.api

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.level.Level
import org.joml.Vector3d
import org.valkyrienskies.mod.common.getShipManagingPos

object Helper3d {

    fun VecToPosition(vec: Vector3d): BlockPos {
        return BlockPos(vec.x,vec.y,vec.z)
    }

    fun VecBlockMid(vec: Vector3d): Vector3d {
        return vec.add(0.5,0.5,0.5)
    }

    fun PositionToVec(pos: BlockPos): Vector3d {
        var vec = Vector3d()
        vec.x = pos.x.toDouble()
        vec.y = pos.y.toDouble()
        vec.z = pos.z.toDouble()
        return vec
    }

    fun MaybeShipToWorldspace(level: Level, pos: BlockPos): Vector3d {
        val s = level.getShipManagingPos(pos)
        if (s == null) {return PositionToVec(pos)}
        return s.shipToWorld.transformPosition(PositionToVec(pos))
    }

    fun MaybeShipToWorldspace(level: Level, vec: Vector3d): Vector3d {
        return MaybeShipToWorldspace(level, VecToPosition(vec))
    }

    fun drawParticleLine(a: Vector3d, b: Vector3d, level: Level, particle: ParticleOptions) {
        val le = a.distance(b) * 3
        for (i in 1..le.toInt()) {
            val pos = a.lerp(b, i / le)
            level.addParticle(particle, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0)
        }
    }

    fun drawQuadraticParticleCurve(A: Vector3d, B: Vector3d, length: Double, segments:Double , level: Level, particle: ParticleOptions) {
        val lengthAB = A.distance(B) * segments
        var C = A.lerp(B, 0.5)
        C.y -= length * segments - lengthAB

        for (i in 1..lengthAB.toInt()) {
            val t = i / lengthAB

            val D = A.lerp(C, t)
            val E = C.lerp(B, t)
            val X = D.lerp(E, t)

            level.addParticle(particle, X.x, X.y, X.z, 0.0, 0.0, 0.0)
        }
    }

}