import java.io.File
import java.lang.IllegalArgumentException
import kotlin.math.abs

data class Point(val x: Int, val y: Int) {
    fun moveByOne(direction: Direction): Point {
        return Point(this.x + direction.x, this.y + direction.y)
    }

    fun directionTo(target: Point): Direction {
        return Direction.fromDelta(target.x - this.x,target.y- this.y)
    }

    fun adjacent(target: Point): Boolean {
        val deltaX = abs(this.x - target.x)
        val deltaY = abs(this.y - target.y)
        return (deltaX == 1 && deltaY <= 1) || (deltaY == 1 && deltaX <= 1)
    }

    fun dragByOneTo(target: Point): Point {
        if (this == target)
            return this
        if (this.adjacent(target))
            return this
        return this.moveByOne(this.directionTo(target))
    }

    override fun toString(): String {
        return "[$x,$y]"
    }
}

enum class Direction(val x: Int, val y: Int) {
    UP(0, 1), UPRIGHT(1, 1), RIGHT(1, 0), RIGHTDOWN(1, -1), DOWN(0, -1), DOWNLEFT(-1, -1), LEFT(-1, 0), LEFTUP(-1, 1);

    companion object {
        fun fromDelta(deltaX: Int, deltaY: Int): Direction {
            val normX = if (deltaX == 0) 0 else deltaX / abs(deltaX)
            val normY = if (deltaY == 0) 0 else deltaY / abs(deltaY)
            return Direction.values().find { d -> d.x == normX && d.y == normY } ?: throw IllegalArgumentException()
        }

        fun fromString(input: String): List<Direction> {
            val (dir, steps) = input.split(" ")
            val direction = when(dir) {
                "U" -> UP
                "R" -> RIGHT
                "D" -> DOWN
                "L" -> LEFT
                else -> throw IllegalArgumentException()
            }
            return List(steps.toInt()) { direction }
        }
    }
}

data class Rope(val knots: MutableList<Point>, val visitedByTail: MutableSet<Point> = mutableSetOf()) {
    constructor(knots: Int, start: Point) : this(MutableList<Point>(knots) {start}, mutableSetOf(start))

    fun move(direction: Direction) {
        knots[0] = knots[0].moveByOne(direction)
        for (i in 1 until knots.size) {
            knots[i] = knots[i].dragByOneTo(knots[i-1])
        }
        visitedByTail.add(knots.last())
    }
}

fun main() {
    val rope2Knots = Rope(2, Point(0, 0))
    val rope10Knots = Rope(10, Point(0, 0))
    val instructions = File("src", "Day09.txt").readLines()
        .flatMap { line -> Direction.fromString(line) }
    instructions
        .forEach { direction ->
            rope2Knots.move(direction)
            rope10Knots.move(direction)
        }
    //debug(instructions, visitedByTail)
    println("Part 1: Tail visited ${rope2Knots.visitedByTail.size} points")
    println("Part 2: Tail visited ${rope10Knots.visitedByTail.size} points")
}

private fun debug(
    instructions: List<Direction>,
    visitedByTail: MutableSet<Point>
) {
    println(instructions)
    println(visitedByTail)
    val topX = visitedByTail.maxOf { it.x }
    val topY = visitedByTail.maxOf { it.y }
    for (y in topY downTo 0) {
        for (x in 0..topX) {
            val m = if (visitedByTail.contains(Point(x, y))) "#" else "."
            print(m)
        }
        print("\n")
    }
}







