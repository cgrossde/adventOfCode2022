import java.io.File


fun main() {
    val instructions = mutableListOf<Int>()
    File("src", "Day10.txt").forEachLine { line ->
        instructions.add(0)
        if (line != "noop") {
            val deltaX = line.split(" ").last().toInt()
            instructions.add(deltaX)
        }
    }
    var finalScore = part1(instructions)
    println("Part 1: Final score $finalScore")

    var screen = part2(instructions)
    println(screen)

}

private fun part2(instructions: MutableList<Int>): String {
    var spritePos = 1
    var screen = ""
    for (cycle in 0 until instructions.size) {
        val pos = cycle % 40
        if (pos == 0) {
            screen += "\n"
        }
        val char = if (pos >= spritePos - 1 && pos <= spritePos + 1) '#' else '.'
        screen += char
        spritePos += instructions[cycle]
    }

    return screen
}

private fun part1(cycles: MutableList<Int>): Int {
    var finalScore = 0
    finalScore += calcX(cycles, 20) * 20
    for (i in 1..5) {
        val cycle = 20 + i * 40
        finalScore += calcX(cycles, cycle) * cycle
    }
    return finalScore
}

private fun calcX(cycles: MutableList<Int>, cycle: Int) = (1 + cycles.subList(0, cycle - 1).sum())

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







