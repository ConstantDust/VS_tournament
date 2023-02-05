package org.valkyrienskies.tournament.api

import org.joml.Vector3d
import kotlin.math.abs

object Bresenham3D {

    fun bresenham3D(start: Vector3d, end: Vector3d): List<Vector3d> {
        val listOfPoints = mutableListOf<Vector3d>()
        listOfPoints.add(start)
        var x1 = start.x
        var y1 = start.y
        var z1 = start.z
        val x2 = end.x
        val y2 = end.y
        val z2 = end.z
        val dx = abs(x2 - x1)
        val dy = abs(y2 - y1)
        val dz = abs(z2 - z1)
        var xs = if (x2 > x1) 1.0 else -1.0
        var ys = if (y2 > y1) 1.0 else -1.0
        var zs = if (z2 > z1) 1.0 else -1.0
        var p1: Double
        var p2: Double
        if (dx >= dy && dx >= dz) {
            p1 = 2 * dy - dx
            p2 = 2 * dz - dx
            while (x1 != x2) {
                x1 += xs
                if (p1 >= 0) {
                    y1 += ys
                    p1 -= 2 * dx
                }
                if (p2 >= 0) {
                    z1 += zs
                    p2 -= 2 * dx
                }
                p1 += 2 * dy
                p2 += 2 * dz
                listOfPoints.add(Vector3d(x1, y1, z1))
            }
        } else if (dy >= dx && dy >= dz) {
            p1 = 2 * dx - dy
            p2 = 2 * dz - dy
            while (y1 != y2) {
                y1 += ys
                if (p1 >= 0) {
                    x1 += xs
                    p1 -= 2 * dy
                }
                if (p2 >= 0) {
                    z1 += zs
                    p2 -= 2 * dy
                }
                p1 += 2 * dx
                p2 += 2 * dz
                listOfPoints.add(Vector3d(x1, y1, z1))
            }
        } else {
            p1 = 2 * dy - dz
            p2 = 2 * dx - dz
            while (z1 != z2) {
                z1 += zs
                if (p1 >= 0) {
                    y1 += ys
                    p1 -= 2 * dz
                }
                if (p2 >= 0) {
                    x1 += xs
                    p2 -= 2 * dz
                }
                p1 += 2 * dy
                p2 += 2 * dx
                listOfPoints.add(Vector3d(x1, y1, z1))
            }
        }
        return listOfPoints
    }
}