package org.valkyrienskies.tournament.api

import org.joml.Vector2d

object algo2d {

    // todo: broken:
    fun filledCircle(vec: Vector2d?, r: Double): List<Vector2d>? {
        val targetVectors = circle(vec!!, r).toMutableList()
        val filledVectors = fillVectors(vec, targetVectors)
        targetVectors.toMutableList()
        targetVectors.addAll(filledVectors)
        return targetVectors.toList()
    }

    fun fill(pos: Vector2d, targetVectors: List<Vector2d>, filledVectors: MutableList<Vector2d>, maxX: Int, maxY: Int) {
        if (pos.x < 0 || pos.x >= maxX || pos.y < 0 || pos.y >= maxY) {
            return
        }

        if (targetVectors.contains(Vector2d(pos.x, pos.y))) {
            return
        }

        filledVectors.add(Vector2d(pos.x, pos.y))

        fill(Vector2d(pos.x + 1, pos.y), targetVectors, filledVectors, maxX, maxY)
        fill(Vector2d(pos.x - 1, pos.y), targetVectors, filledVectors, maxX, maxY)
        fill(Vector2d(pos.x, pos.y + 1), targetVectors, filledVectors, maxX, maxY)
        fill(Vector2d(pos.x, pos.y - 1), targetVectors, filledVectors, maxX, maxY)
    }

    fun getFilledVectors(pos: Vector2d, targetVectors: List<Vector2d>, maxX: Int, maxY: Int): List<Vector2d> {
        val filledVectors = mutableListOf<Vector2d>()
        fill(pos, targetVectors, filledVectors, maxX, maxY)
        println("pos: $pos")
        return filledVectors
    }

    fun fillVectors(pos: Vector2d, targetVectors: List<Vector2d>): List<Vector2d> {
        val maxX = targetVectors.map { it.x }.max()
        val maxY = targetVectors.map { it.y }.max()

        return getFilledVectors(pos, targetVectors, maxX.toInt(), maxY.toInt())
    }


    fun circle(vec: Vector2d, r: Double): List<Vector2d> {
        val x0 = vec.x
        val y0 = vec.y

        val result = mutableListOf<Vector2d>()
        var x = r
        var y = 0
        var decisionOver2 = 1 - x

        while (y <= x) {
            result.add(Vector2d(x + x0, y + y0))
            result.add(Vector2d(y + x0, x + y0))
            result.add(Vector2d(-x + x0, y + y0))
            result.add(Vector2d(-y + x0, x + y0))
            result.add(Vector2d(-x + x0, -y + y0))
            result.add(Vector2d(-y + x0, -x + y0))
            result.add(Vector2d(x + x0, -y + y0))
            result.add(Vector2d(y + x0, -x + y0))
            y++
            if (decisionOver2 <= 0) {
                decisionOver2 += 2 * y + 1
            } else {
                x--
                decisionOver2 += 2 * (y - x) + 1
            }
        }

        return result
    }

}