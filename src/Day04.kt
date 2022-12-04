import java.io.File

fun IntRange.contains(range: IntRange): Boolean {
    return this.contains(range.first) && this.contains(range.endInclusive)
}

fun IntRange.overlaps(range: IntRange): Boolean {
    return this.contains(range.first) || this.contains(range.endInclusive)
}

fun main() {
    var containingRanges = 0
    var overlappingRanges = 0
    File("src", "Day04.txt").forEachLine { input ->
        val (firstRange, secondRange) = input.split(",").map { i ->
            val (lower, upper) = i.split("-").map{ it.toInt()}
            IntRange(lower, upper)
        }
        if (firstRange.contains(secondRange) || secondRange.contains(firstRange))
            containingRanges++
        if (firstRange.overlaps(secondRange) || secondRange.overlaps(firstRange))
            overlappingRanges++

    }
    println("containingRanges: $containingRanges")
    println("overlappingRanges: $overlappingRanges")
}


