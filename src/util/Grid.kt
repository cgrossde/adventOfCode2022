package util

import java.lang.IllegalArgumentException
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Grid(val height: Int, val width: Int) {

    private val content: Array<IntArray>
    private val legend : MutableMap<Int, Char> = mutableMapOf(0 to '.')

    init {
        content = Array(height*2+1) { IntArray(width*2+1)}
    }


    fun inBounds(point: Point): Boolean {
        return point.x in -width until width && point.y in -height until height
    }

    fun get(point: Point): Int {
        return get(point.x, point.y)
    }

    fun getRow(row: Int): IntArray {
        return content[row+height]
    }

    fun get(x: Int, y: Int): Int {
        if (!inBounds(Point(x,y)))
            throw IllegalArgumentException("Point not in grid: x=$x, y=$y")
        return content[y+height][x+width]
    }

    fun set(x: Int, y: Int, value: Int) {
        if (!inBounds(Point(x,y)))
            throw IllegalArgumentException("Point not in grid: x=$x, y=$y")
        content[y+height][x+width] = value
    }

    fun addLegend(value: Int, char: Char) {
        legend.put(value, char)
    }

    fun drawLine(from: Point, to: Point, value: Int = 1) {
        for (y in Integer.min(from.y, to.y)..max(from.y, to.y)) {
            for (x in Integer.min(from.x, to.x)..max(from.x, to.x)) {
                set(x,y,value)
            }
        }
    }

    fun drawDiamond(center: Point, size: Int, value: Int = 1) {
        for (y in max(-height, center.y-size)..min(center.y+size, height-1)) {
            var delta = size - abs(y - center.y)
            for (x in max(-width, center.x-delta)..min(center.x+delta, width-1)) {
                set(x,y,value)
            }
        }
    }

    fun getContent(): Array<IntArray> {
        return content
    }

    fun visualizeGrid(offsetX: Int = -width, offsetY: Int = -height) {
        for (y in offsetY until height) {
            for (x in offsetX until width) {
                val c = if (legend.contains(get(x,y))) legend.get(get(x,y)) else get(x,y)
                print(c)
            }
            print("\n")
        }
    }
}