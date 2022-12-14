import java.io.File
import java.util.function.Function
import kotlin.math.floor
import kotlin.system.exitProcess

data class Monkey(
    var items: MutableList<Long>,
    val operation: Function<Long, Long>,
    val divTest: Long,
    val trueTarget: Int,
    val falseTarget: Int,
    var inspections: Int = 0
) {

    fun turn(monkeys: List<Monkey>, worryReducer: Double = 1.0) {
        for (item in items) {
            val newWorryLevel: Long = floor(operation.apply(item) / worryReducer).toLong()
            val targetMonkey = if (newWorryLevel.mod(divTest) == 0L) trueTarget else falseTarget
            monkeys[targetMonkey].items.add(newWorryLevel)
            inspections++
        }
        this.items.clear()
    }

    companion object {
        fun fromString(input: String): Monkey {
            val monkeyRegex = """Monkey \d+:
  Starting items: ([\d, ]+)
(.+)
  Test: divisible by (\d+)
    If true: throw to monkey (\d+)
    If false: throw to monkey (\d+)""".toRegex()
            val (items, operation, divTest, trueTarget, falseTarget) = monkeyRegex
                .matchEntire(input)
                ?.destructured
                ?: throw IllegalArgumentException("Incorrect input line $input")
            return Monkey(
                items.split(", ").map { it.toLong() }.toMutableList(),
                Operation.fromString(operation),
                divTest.toLong(),
                trueTarget.toInt(),
                falseTarget.toInt()
            )
        }
    }
}

enum class Operation {
    MULTIPLY_SELF,
    MULTIPLY_PARAM,
    ADD_PARAM;

    fun createFunction(param: Int = 1): Function<Long, Long> {
        return when (this) {
            MULTIPLY_SELF -> Function { old: Long -> old * old }
            MULTIPLY_PARAM -> Function { old: Long -> old * param }
            ADD_PARAM -> Function { old: Long -> old + param }
        }
    }

    companion object {
        fun fromString(input: String): Function<Long, Long> {
            val operationRegex = """  Operation: new = old ([+*]) (\w+)""".toRegex()
            val (operator, param) = operationRegex
                .matchEntire(input)
                ?.destructured
                ?: throw IllegalArgumentException("Incorrect input line $input")
            return when {
                operator == "+" -> ADD_PARAM.createFunction(param.toInt())
                operator == "*" && param == "old" -> MULTIPLY_SELF.createFunction()
                else -> MULTIPLY_PARAM.createFunction(param.toInt())
            }
        }
    }
}


fun main() {
    val monkeys = File("src", "Day11.txt").readText()
        .split("\n\n")
        .map { Monkey.fromString(it) }
    for (i in 0 until 20)
        roundPart1(monkeys)
    val (m1, m2) = monkeys.sortedByDescending { it.inspections }.subList(0, 2)
    println("Part1: ${m1.inspections*m2.inspections}")

    // Unfinished
    for (i in 0 until 100000)
        roundPart2(monkeys, i)
    val (x1, x2) = monkeys.sortedByDescending { it.inspections }.subList(0, 2)
    println("Part2: ${x1.inspections*x2.inspections}")
}

fun roundPart1(monkeys: List<Monkey>) {
    monkeys.forEach { it.turn(monkeys, 3.0) }
}

fun roundPart2(monkeys: List<Monkey>, round: Int) {
    monkeys.forEach { it.turn(monkeys, 1.0) }
    if (monkeys.flatMap { it.items }.any { it < 0 }) {
        println("\nOVERFLOW: " + monkeys.flatMap { it.items })
        monkeys.forEachIndexed { index, monkey -> println("Monkey $index: ${monkey.inspections}") }
        exitProcess(1)
    }
    while (monkeys.flatMap { it.items }.all { it.mod(2) == 0 }) {
        monkeys.forEach { it.items = it.items.map { it / 2 }.toMutableList() }
        print('-')
    }
    while (monkeys.flatMap { it.items }.all { it.mod(3) == 0 }) {
        monkeys.forEach { it.items = it.items.map { it / 3 }.toMutableList() }
        print('/')
    }
}









