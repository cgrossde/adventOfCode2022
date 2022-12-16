import util.Point
import java.io.File
import java.lang.Integer.min
import kotlin.math.max

fun main() {
    val sandSpawn = Point(500, 0)
    var maxX = 500
    var maxY = 0
    var shapes = File("src", "Day14.txt").readLines()
        .map { shape -> shape.split(" -> ") }
        .map { points ->
            points.map { point ->
                val (x, y) = point.split(',').map { it.toInt() }
                maxX = max(maxX, x)
                maxY = max(maxY, y)
                Point(x, y)
            }
        }.toMutableList()
    maxX += maxY
//    val (grid, sandUnits) = simulate(maxY, maxX, shapes, sandSpawn)
//    visualizeGrid(grid, 450)
//    println("Part 1: Units of sand $sandUnits")

    maxY += 2
    shapes.add(listOf(Point(0, maxY), Point(maxX, maxY)))
    var (grid2, sandUnits2) = simulate(maxY, maxX, shapes, sandSpawn)
    visualizeGrid(grid2, 450)
    println("Part 2: Units of sand $sandUnits2")
}

private fun simulate(
    maxY: Int,
    maxX: Int,
    shapes: List<List<Point>>,
    sandSpawn: Point
): Pair<Array<IntArray>, Int> {
    var grid = drawShapesOntoGrid(maxY, maxX, shapes)
    println("maxX: ${maxX}, maxY: $maxY")
    var sandUnits = 0
    sandflow@ while (true) {
        sandUnits++
        val posSand = PointD9(sandSpawn.x, sandSpawn.y)
        var newPos = posSand
        while ({ newPos = sandMove(posSand, grid); newPos }() != posSand) {
            val droppedOnIntoVoid = newPos.y == grid.size - 1
            if (droppedOnIntoVoid) { // lost one to the void
                sandUnits--
                break@sandflow
            }
            grid[posSand.y][posSand.x] = 0
            posSand.x = newPos.x
            posSand.y = newPos.y
            grid[posSand.y][posSand.x] = 2
//            visualizeGrid(grid, 450)
        }
        val backedUpTillSpawn = newPos.y == sandSpawn.y
        if (backedUpTillSpawn) break@sandflow
    }
    return Pair(grid, sandUnits)
}

fun sandMove(posSand: PointD9, grid: Array<IntArray>): PointD9 {
    var (newX, newY) = posSand
    if (grid[posSand.y + 1][posSand.x] == 0)    // Down
        newY++
    else if (grid[posSand.y + 1][posSand.x-1] == 0) { // Left
        newX--
        newY++
    }
    else if (grid[posSand.y + 1][posSand.x+1] == 0) { // Right
        newX++
        newY++
    } else {
        return posSand    // At rest
    }
    return PointD9(newX, newY)
}

private fun drawShapesOntoGrid(
    maxY: Int,
    maxX: Int,
    shapes: List<List<Point>>
): Array<IntArray> {
    var grid = Array(maxY + 1) { IntArray(maxX + 1) }
    shapes.forEach { points ->
        points.reduce { from, to ->
            drawLine(from, to, grid)
            to
        }
    }
    return grid
}

fun drawLine(from: Point, to: Point, grid: Array<IntArray>) {
    for (y in min(from.y,to.y)..max(from.y,to.y)) {
        for (x in min(from.x,to.x)..max(from.x,to.x)) {
            grid[y][x] = 1
        }
    }
}

fun visualizeGrid(grid: Array<IntArray>, cropX: Int = 0) {
    for (y in 0 until grid.size) {
        for (x in cropX until grid.first().size) {
            val c = when (grid[y][x]) {
                1 -> '#'    // Wall
                2 -> 'o'    // Sand
                else -> '.'
            }
            print(c)
        }
        print("\n")
    }
}