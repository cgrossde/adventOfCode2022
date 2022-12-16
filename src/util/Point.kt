package util

import kotlin.math.abs

data class Point(var x: Int, var y: Int) {
    fun manhattenDistance(to: Point): Int {
        return abs(this.x - to.x) + abs(this.y - to.y)
    }
}