import util.Grid
import util.Point
import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min
import java.math.BigDecimal
import kotlin.math.abs


data class Sensor(val position: Point, val range: Int) {
    fun coveredRangeOnLevel(y: Int): IntRange {
        val distanceToLevel = abs(y - position.y)
        if (distanceToLevel > range)
            return IntRange.EMPTY
        var rangeAtY = range - distanceToLevel
        val covered = position.x - rangeAtY..position.x + rangeAtY
        return covered
    }
}

fun main() {
    val sensors = mutableListOf<Sensor>()
    val beacons = mutableListOf<Point>()
    File("src", "Day15.txt").readLines()
        .forEach { line ->
            val lsOutputRegex = """Sensor at x=(\w+), y=(\w+): closest beacon is at x=([-\d]+), y=([-\d]+)""".toRegex()
            val (sX, sY, bX, bY) = lsOutputRegex
                .matchEntire(line)
                ?.destructured
                ?: throw IllegalArgumentException("Could not parse line: $line")
            val sensor = Point(sX.toInt(), sY.toInt())
            val beacon = Point(bX.toInt(), bY.toInt())
            sensors.add(Sensor(sensor, sensor.manhattenDistance(beacon)))
            beacons.add(beacon)
        }

    val coveredPointsAtLevel = checkCoveredSpotsAt(10, sensors, beacons)
    println("Part 1: Not beacon positions in row 2000000: $coveredPointsAtLevel")

    val lowerBound = 0
    val upperBound = 4000000

    for (y in lowerBound..upperBound) {
        val uncoveredSpot = findUncoveredSpotWithinBounds(y, sensors, beacons, lowerBound, upperBound)
        if (uncoveredSpot != -1) {
            val tuningFrequency = BigDecimal(uncoveredSpot).times(BigDecimal(upperBound)).plus(BigDecimal(y))
            println("Part 2: Found uncovered spot on y=$y, x=$uncoveredSpot. Tuning frequency $tuningFrequency")
        }
    }
}

fun checkCoveredSpotsAt(y: Int, sensors: MutableList<Sensor>, beacons: MutableList<Point>): Int {
    val coverageRanges = sensors.map { it.coveredRangeOnLevel(y) }
        .filter { !it.isEmpty() }
    val coveredPoints = coverageRanges
        .map { range -> range.toSet() }
        .reduce {acc, ints -> acc.plus(ints) }
        .toMutableSet()

    beacons
        .filter{ it.y == y }
        .forEach { beacon -> coveredPoints.remove(beacon.x) }
    return coveredPoints.size
}

fun findUncoveredSpotWithinBounds(y: Int, sensors: MutableList<Sensor>, beacons: MutableList<Point>, min: Int, max: Int): Int {
    val coverageRanges = sensors.map { it.coveredRangeOnLevel(y) }
        .filter { !it.isEmpty() }
        .map { range -> IntRange(max(min, range.first), min(max, range.last)) }
        .sortedBy { it.first }
    var priorRange = coverageRanges.first()
    val uniqueRanges = mutableListOf<IntRange>()
    for (range in coverageRanges) {
        if (priorRange.last in range.first-1..range.first) {
            priorRange = IntRange(priorRange.first, max(priorRange.last, range.last))
        }
        else if (priorRange.last < range.first) {
            uniqueRanges.add(priorRange)
            priorRange = range
        }
        else if (priorRange.last > range.first && priorRange.last < range.last)
            priorRange = IntRange(priorRange.first, range.last)
    }
    uniqueRanges.add(priorRange)
    if (uniqueRanges.size > 1) {
        val potentialSpot = uniqueRanges.first().last + 1
        if (beacons.any { b -> b.y == y && b.x == potentialSpot })
            return -1
        return potentialSpot
    }
    return -1
}

// Java heap space -.-
private fun naiveApproach() {
    var maxX = 0
    var maxY = 0
    val sensors = File("src", "Day15_s.txt").readLines()
        .map { line ->
            val lsOutputRegex = """Sensor at x=(\w+), y=(\w+): closest beacon is at x=([-\d]+), y=([-\d]+)""".toRegex()
            val (sX, sY, bX, bY) = lsOutputRegex
                .matchEntire(line)
                ?.destructured
                ?: throw IllegalArgumentException("Could not parse line: $line")
            maxX = listOf(maxX, sX.toInt(), bX.toInt()).max()
            maxY = listOf(maxY, sY.toInt(), bY.toInt()).max()
            val sensor = Point(sX.toInt(), sY.toInt())
            val beacon = Point(bX.toInt(), bY.toInt())
            Pair(sensor, beacon)
        }
    val grid = Grid(maxY + 1, maxX + 10)
    grid.addLegend(5, 'S')  // Sensor
    grid.addLegend(4, 'B')  // Beacon
    grid.addLegend(3, 'X')  // (0,0)
    grid.addLegend(7, '|')  // (0,0)
    grid.addLegend(6, '-')  // (0,0)
    grid.addLegend(1, '#')  // Sensor range
    grid.set(0, 0, 3)
    sensors.forEach { (sensor, beacon) ->
        grid.drawDiamond(sensor, sensor.manhattenDistance(beacon))
        grid.set(sensor.x, sensor.y, 5)
        grid.set(beacon.x, beacon.y, 4)
    }
//    grid.drawLine(Point(-1,-1), Point(-1,21), 7)
//    grid.drawLine(Point(-1,-1), Point(21,-1), 6)
//    grid.drawLine(Point(21,-1), Point(21,21), 7)
//    grid.visualizeGrid()
    val notBeacon = grid.getRow(2000000).filter { v -> v == 1 }.sum()
    println("Part 1: Not beacon positions in row 2000000: $notBeacon")
}