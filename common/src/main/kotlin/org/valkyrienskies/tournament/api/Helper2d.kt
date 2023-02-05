package org.valkyrienskies.tournament.api

import net.minecraft.util.Tuple
import org.joml.Vector2d
import org.joml.Vector3d

object Helper2d {

    fun vec2to3(vec: Vector2d, y: Double) : Vector3d {
        return Vector3d(vec.x, y, vec.y)
    }

    fun vec3to2(vec: Vector3d) : Vector2d {
        return Vector2d(vec.x, vec.z)
    }

}