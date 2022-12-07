import java.io.File

fun String.isStack(): Boolean {
    return this[1] != '1' && (this[0] == ' ' || this[0] == '[')
}

fun String.isInstruction(): Boolean {
    return this[0] == 'm'
}

fun String.toInstruction(): Instruction {
    val instructionRegex = """move (\d+) from (\d+) to (\d+)""".toRegex()
    val (amount, from, to) = instructionRegex
        .matchEntire(this)
        ?.destructured
        ?: throw IllegalArgumentException("Incorrect input line $this")
    return Instruction(from.toInt(), to.toInt(), amount.toInt())
}

data class Instruction(val from: Int, val to: Int, val amount: Int) {
    fun executeOn(stack: MutableMap<Int, String>, oneByOne: Boolean) {
        val fromStack = stack[from] ?: throw java.lang.IllegalArgumentException("Stack $from doesn't exist")
        val splitIndex = fromStack.length - amount
        val subStack = if (oneByOne) fromStack.substring(splitIndex).reversed() else fromStack.substring(splitIndex)
        stack[from] = fromStack.substring(0, splitIndex)
        stack[to] += subStack
    }
}

fun main() {
    val stack = mutableMapOf<Int, String>()
    val input = File("src", "Day05.txt").readLines()
    val stackNum = (input[0].length + 2) / 4
    val instructions = mutableListOf<Instruction>()
    input.forEach { line ->
        when {
            (line.isBlank()) -> stack.forEach { stack[it.key] = it.value.trim() } // trim whitespaces of stacks
            (line.isStack()) -> { // Add crates to stacks
                for (i in 1..stackNum) {
                    stack[i] = line[1 + (i - 1) * 4] + (stack[i] ?: "")
                }
            }

            (line.isInstruction()) -> instructions.add(line.toInstruction())
        }
    }

    val stack1 = part1(stack.toMutableMap(), instructions)
    val solution1 = stack1.values.map { it.last() }.joinToString("")
    println("Part1: ${solution1}")

    val stack2 = part2(stack.toMutableMap(), instructions)
    val solution2 = stack2.values.map { it.last() }.joinToString("")
    println("Part2: ${solution2}")
}

fun part1(stack: MutableMap<Int, String>, instructions: MutableList<Instruction>): MutableMap<Int, String> {
    instructions.forEach { it.executeOn(stack, true) }
    return stack
}

fun part2(stack: MutableMap<Int, String>, instructions: MutableList<Instruction>): MutableMap<Int, String> {
    instructions.forEach { it.executeOn(stack, false) }
    return stack
}



