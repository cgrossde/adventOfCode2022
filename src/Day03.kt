import java.io.File


fun Char.toPriority() = when {
    (this.code in 97..122) -> this.code - 97 + 1    // a-z, starting with val 1
    (this.code in 65..90) -> this.code - 65 + 27    // A-Z, starting with val 27
    else -> throw IllegalArgumentException("Not in [a-zA-Z]")
}

fun main() {
    part1()
    part2()
}

private fun part2() {
    var sumPriorities = 0
    var rucksackNumber = 0
    var rucksackList = Array(3) { "" }
    File("src", "Day03.txt").forEachLine { rucksack ->
        rucksackNumber++
        var member = rucksackNumber % 3
        rucksackList[member] = rucksack
        if (member !== 0) {
            return@forEachLine
        }
        val teamBadge =
            rucksackList[0].partition { c -> rucksackList[1].contains(c) && rucksackList[2].contains(c) }.first.first()
        sumPriorities += teamBadge.toPriority()
        //println("Team: ${(rucksackNumber-member)/3}, teamBadge: $teamBadge, rucksackNumber: $rucksackNumber, teamRucksacklist: ${rucksackList.joinToString(",")}")
    }
    println("sumPriorities: $sumPriorities")
}

private fun part1() {
    var prioritySum = 0
    File("src", "Day03.txt").forEachLine { rucksack ->
        val compA = rucksack.subSequence(0, rucksack.length / 2)
        val compB = rucksack.subSequence(rucksack.length / 2, rucksack.length)
        val wrongItem = compA.partition { compB.contains(it) }.first.first()
        val priority = wrongItem.toPriority()
        prioritySum += priority
        //println("CompA: $compA, CompB: $compB, wrongItem: $wrongItem, priority: $priority" )
    }
    println("prioritySum: $prioritySum")
}