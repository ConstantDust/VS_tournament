package org.valkyrienskies.tournament.api

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.world.level.Level
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.valkyrienskies.mod.common.getShipManagingPos
import kotlin.math.absoluteValue

object Helper3d {

    fun VecToPosition(vec: Vector3d): BlockPos {
        return BlockPos(vec.x,vec.y,vec.z)
    }

    fun VecDCtoD(vec: Vector3dc): Vector3d {
        return Vector3d(vec.x(),vec.y(),vec.z())
    }

    fun VecDtoI(vec: Vector3d): Vector3i {
        return Vector3i(vec.x.toInt(),vec.y.toInt(),vec.z.toInt())
    }

    fun VecItoD(vec: Vector3i): Vector3d {
        return Vector3d(vec.x.toDouble(),vec.y.toDouble(),vec.z.toDouble())
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
        if (s == null) {return PositionToVec(pos)} else {
        return s.shipToWorld.transformPosition(PositionToVec(pos))}
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

    fun drawQuadraticParticleCurve(A: Vector3d, C: Vector3d, length: Double, segments:Double , level: Level, particle: ParticleOptions) {
        val lengthAC:Double = (A.sub(C, Vector3d()).length() * segments).absoluteValue
        val lengthTOT:Double = length * segments
        var B:Vector3d = C.sub(C.sub(A, Vector3d()).div(2.0, Vector3d()), Vector3d())
        if(lengthAC < lengthTOT){ B.y -= lengthTOT - lengthAC }


        for (i in 1..lengthAC.toInt()) {
            val t = i / lengthAC

            val D:Vector3d = A.lerp(B, t, Vector3d())
            val E:Vector3d = B.lerp(C, t, Vector3d())
            val X:Vector3d = D.lerp(E, t, Vector3d())
            //println(X)

            level.addParticle(particle, X.x, X.y, X.z, 0.0, 0.0, 0.0)
        }
    }

}