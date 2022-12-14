import java.io.File
import kotlin.math.min

data class Packet(val content: MutableList<Packet>?, val number: Int? = -1) : Comparable<Packet> {
    fun add(packet: Packet) {
        content!!.add(packet)
    }

    override fun compareTo(right: Packet): Int {
        val left = this
        if (left.toString() == right.toString())
            return 0
        if (left.content!!.isEmpty())
            return 1
        if (right.content!!.isEmpty())
            return -1
        // Compare items in list
        val items = min(left.content.size, right.content.size)
        for (i in 0 until items) {
            var nextL = left.content.get(i)
            var nextR = right.content.get(i)
            if (nextL.isNumber() && nextR.isList()) {
                nextL = Packet(mutableListOf(nextL))
            }
            if (nextL.isList() && nextR.isNumber()) {
                nextR = Packet(mutableListOf(nextR))
            }

            if (nextL.isList() && nextR.isList()) {
                if (nextL.toString() == nextR.toString())
                    continue
                return nextL.compareTo(nextR)
            }

            // Equal numbers => continue
            if (nextL.number!! == nextR.number!!) {
                continue
            }

            return if (nextL.number!! < nextR.number!!) 1 else -1
        }
        // Left or right ran out of items
        return if (left.content.size == items) 1 else -1
    }

    override fun toString(): String {
        if (this.isNumber())
            return this.number.toString()
        return "[" + this.content!!.joinToString(",") + "]"
    }

    fun isNumber(): Boolean {
        return number != -1
    }

    fun isList(): Boolean {
        return content != null
    }

    companion object {
        fun fromString(input: String): Packet {
            var remaining = input
            val root = Packet(mutableListOf())
            val stack = mutableListOf(root)
            while (remaining.isNotEmpty()) {
                when (remaining.first()) {
                    '[' -> {
                        val list = Packet(mutableListOf())
                        stack.last().add(list)
                        stack.add(list)
                        remaining = remaining.drop(1)
                    }

                    ']' -> {
                        stack.removeLast()
                        remaining = remaining.drop(1)
                    }

                    ',' -> {
                        remaining = remaining.drop(1)
                    }

                    else -> {
                        val number = remaining.takeWhile { it.isDigit() }
                        stack.last().add(Packet(null, number.toInt()))
                        remaining = remaining.drop(number.length)
                    }
                }
            }
            return root
        }
    }
}


fun main() {
    var indexOfCorrectOrder = File("src", "Day13.txt").readText().split("\n\n")
        .map { it.split("\n").map { it.substring(1, it.length - 1) } }
        .mapIndexed { index, pair ->
            val (left, right) = pair
            val leftVal = Packet.fromString(left)
            val rightVal = Packet.fromString(right)
            if (leftVal > rightVal) {
                return@mapIndexed index + 1
            }
            0
        }.filter { i -> i != 0 }
    println("Part 1: In correct order ${indexOfCorrectOrder.sum()}")
    println("Part 1: In correct order ${naiveApproach()} (naive, incorrect)")

    val divider1 = Packet.fromString("[[2]]")
    val divider2 = Packet.fromString("[[6]]")
    val packets = File("src", "Day13.txt").readLines()
        .filter { line -> line.isNotEmpty() }
        .map(Packet::fromString)
        .plus(divider1)
        .plus(divider2)
        .sortedDescending()
    println("Index 1: ${(packets.indexOf(divider1) + 1)}, Index 2: ${(packets.indexOf(divider2) + 1)}")
    val decoderKey = (packets.indexOf(divider1) + 1) * (packets.indexOf(divider2) + 1)
    println("Part 2: Decoder key $decoderKey")
}

// Not working
private fun naiveApproach() = File("src", "Day13.txt").readText().split("\n\n")
    .map { it.split("\n") }//.map { it.substring(1, it.length-1) } }
    .mapIndexed { index, pair ->
        val (left, right) = pair
        val correctOrder = checkOrderNaive(left, right)
//        println("Left: $left, right: $right, correct=$correctOrder")
        if (correctOrder) {
            return@mapIndexed index + 1
        }
        0
    }.sum()

fun checkOrderNaive(left: String, right: String): Boolean {
    var i = 1
    while (i < left.length) {
        if (left[i].isDigit() && right[i].isDigit()) {
            if (left[i] == right[i])
                i++
            else
                return left[i].toInt() < right[i].toInt()
        } else if (left[i].isDigit() && right[i] == '[') {
            return left[i].toInt() < right[i + 1].toInt()
        } else if (left[i] == '[' && right[i].isDigit()) {
            return left[i + 1].toInt() < right[i].toInt()
        } else if (left[i] == '[' && right[i] == '[') {
            i++
        } else if ((left[i].isDigit() || left[i] == ',' || left[i] == '[') && right[i] == ']') {
            return false
        } else if (left[i] == ']' && (right[i].isDigit() || right[i] == ',' || right[i] == '[')) {
            return true
        } else if (left[i] == ',' && right[i] == ',') {
            i++
        } else if (left[i] == ']' && right[i] == ']') {
            i++
        } else {
            throw IllegalStateException("Unkown state - left: $left, right: $right, i: $i, leftC: ${left[i]} rightC:${right[i]}")
        }
    }
    return false
}




