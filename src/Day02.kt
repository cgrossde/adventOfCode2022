import java.io.File

enum class Choice(val letter: String, private val shapeValue: Int) : Comparable<Choice> {
    ROCK("AX", 1),
    PAPER("BY", 2),
    SCISSOR("CZ", 3);

    companion object {
        fun fromString(input: String): Choice {
            return values().find { it.letter.contains(input) }
                ?: throw IllegalArgumentException("No matching choice")
        }
    }

    private val beats: Choice
        get() = when (this) {
            ROCK -> SCISSOR
            PAPER -> ROCK
            SCISSOR -> PAPER
        }

    private val looses: Choice
        get() = when (this) {
            ROCK -> PAPER
            PAPER -> SCISSOR
            SCISSOR -> ROCK
        }


    fun scoreGiven(yourChoice: Choice) = when {
        (this.beats == yourChoice) -> yourChoice.shapeValue + Result.LOSS.points
        (yourChoice.beats == this) -> yourChoice.shapeValue + Result.WIN.points
        else -> yourChoice.shapeValue + Result.DRAW.points
    }

    fun scoreExpecting(desiredResult: Result) = when {
        (desiredResult == Result.WIN) -> this.looses.shapeValue + Result.WIN.points
        (desiredResult == Result.LOSS) -> this.beats.shapeValue + Result.LOSS.points
        else -> this.shapeValue + Result.DRAW.points
    }
}

enum class Result(val letter: String, val points: Int) {
    WIN("Z", 6),
    DRAW("Y", 3),
    LOSS("X", 0);

    companion object {
        fun fromString(input: String): Result {
            return values().find { it.letter == input }
                ?: throw IllegalArgumentException("No matching result")
        }
    }
}

fun String.toScorePart1(): Int {
    val opponentChoice = Choice.fromString(substringBefore(" "))
    val strategyChoice = Choice.fromString(substringAfter(" "))
    return opponentChoice.scoreGiven(strategyChoice)
}

fun String.toScorePart2(): Int {
    val opponentChoice = Choice.fromString(substringBefore(" "))
    val desiredResult = Result.fromString(substringAfter(" "))
    return opponentChoice.scoreExpecting(desiredResult)
}

fun main() {
    val scoresPart1 = File("src", "Day02.txt").readLines().map { line ->
        line.toScorePart1()
    }
    val scoresPart2 = File("src", "Day02.txt").readLines().map { line ->
        line.toScorePart2()
    }

    // Part 1
    val totalScore1 = scoresPart1.sum()

    // Part 2
    val totalScore2 = scoresPart2.sum()

    println("Total Score1: $totalScore1")
    println("Total Score2: $totalScore2")
}