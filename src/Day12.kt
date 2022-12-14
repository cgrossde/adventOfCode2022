import java.io.File
import java.util.*

data class P(val row: Int, val col: Int, val steps: Int = -1, val prior: P? = null)

data class Map(val input: List<String>) {
    private val start: P
    val visited: Array<BooleanArray>

    init {
        val startRow = input.indexOfFirst { row -> row.indexOf('S') != -1 }
        this.start = P(startRow, input[startRow].indexOf('S'), 0)
        this.visited = Array(input.size) { BooleanArray(input.first().length) }
    }

    fun visited(point: P): Boolean {
        return this.visited[point.row][point.col]
    }

    fun visualize(finalPoint: P) {
        val charMap = Array(input.size) { CharArray(input.first().length) }
        for (row in 0 until input.size) {
            for (col in 0 until input.first().length) {
                if (this.visited(P(row, col))) {
                    charMap[row][col] = '+'
                } else {
                    charMap[row][col] = '.'
                }
            }
        }

        var current = finalPoint.prior!!
        charMap[finalPoint.row][finalPoint.col] = 'X'
        while (current.prior != null) {
            charMap[current.row][current.col] = '#'
            current = current.prior!!
        }
        charMap[current.row][current.col] = '>'
        charMap.forEach { row -> println(row) }
    }

    fun height(point: P): Int {
        return when (input[point.row][point.col]) {
            'S' -> 0
            'E' -> 26
            else -> input[point.row][point.col].code - 'a'.code
        }
    }

    fun getNeighbours(point: P): List<P> {
        val currentHeight = this.height(point)
        val pointInbounds: (P) -> Boolean =
            { point -> point.row >= 0 && point.row < input.size && point.col >= 0 && point.col < input.first().length }
        return mutableListOf(
            P(point.row + 1, point.col, point.steps + 1, point),
            P(point.row, point.col + 1, point.steps + 1, point),
            P(point.row - 1, point.col, point.steps + 1, point),
            P(point.row, point.col - 1, point.steps + 1, point)
        )
            .filter(pointInbounds)
            .filter { !this.visited[it.row][it.col] }
            .filter { this.height(it) <= currentHeight + 1 }
    }

    // BFS/Dijkstra? search
    fun findMinStepsTillE(): Int {
        val searchHeap: PriorityQueue<P> = PriorityQueue(compareBy { it.steps })
        searchHeap.add(start)
        while (true) {
            val currentPoint = searchHeap.poll()
            if (this.visited(currentPoint))
                continue
            val neighbours = this.getNeighbours(currentPoint)
            if (neighbours.any { this.height(it) == 26 }) {
                val goal = neighbours.find { this.height(it) == 26 }
                this.visualize(goal!!)
                println("Start was: (${start.row},${start.col})")
                println("Goal found: (${goal.row},${goal.col})")
                return currentPoint.steps + 1
            }
            this.visited[currentPoint.row][currentPoint.col] = true
            searchHeap.addAll(neighbours)
        }
    }
}


fun main() {
    val map = Map(File("src", "Day12.txt").readLines())
    println("Part 1: ${map.findMinStepsTillE()}")

}
