package org.valkyrienskies.tournament.api

import org.joml.Vector3d
import kotlin.math.absoluteValue

object algo3d {

    fun cone(pos: Vector3d, radius: Double, height: Double) : List<Vector3d> {
        var result = ArrayList<Vector3d>()

        var y = pos.y
        for (i in 0..height.absoluteValue.toInt()) {
            result.addAll( Helper2d.vec2listTo3(algo2d.filledCircleDirty(Helper2d.vec3to2(pos), radius / i.toDouble())!!, y) )

            if (height < 0) {y -= 1}
            else {y += 1}
        }

        return result
    }

    fun sphereLikeShape(pos: Vector3d, radius: Double, height: Double) : List<Vector3d> {
        var result = ArrayList<Vector3d>()

        var y = pos.y
        for (i in 0..height.absoluteValue.toInt()) {
            result.addAll( Helper2d.vec2listTo3(algo2d.filledCircleDirty(Helper2d.vec3to2(pos), i.toDouble() / y + (pos.y / 2))!! , y) )

            if (height < 0) {y -= 1}
            else {y += 1}
        }

        return result
    }

}