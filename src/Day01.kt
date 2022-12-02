import java.io.File

fun main() {
    val elfCalories = mutableMapOf<Int, List<Int>>()
    var currentElf = 1

    File("src", "Day01.txt").forEachLine { line  ->
        if (line.isBlank()) {
            currentElf++
            return@forEachLine
        }

        val calories: List<Int> = elfCalories[currentElf] ?: listOf()
        elfCalories[currentElf] = calories.plus(line.toInt())
    }
    val elfSumCalories = elfCalories.mapValues { it.value.sum() }

    // Part 1
    val max = elfSumCalories.maxBy { it.value }

    // Part 2
    val top3 = elfSumCalories.map { Pair(it.key, it.value) }.sortedByDescending {  it.second }.subList(0, 3)
    val sumTop3 = elfSumCalories.values.sortedDescending().subList(0, 3).sum()

    println("Max calorie elf: $max")
    println("Top 3: $top3")
    println("SumTop3: $sumTop3")
}