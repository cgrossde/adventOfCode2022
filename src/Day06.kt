import java.io.File
import kotlin.math.max

fun main() {
    val inputStream = File("src", "Day06.txt").readLines().first()

    // Testing
    assertThat("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 7, 19)
    assertThat("bvwbjplbgvbhsrlpgdmjqwftvncz", 5, 23)
    assertThat("nppdvjthqldpwncqszvftbrmjlhg", 6, 23)
    assertThat("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 10, 29)
    assertThat("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 11, 26)

    val startPacketIndex = findIndexAfterDistinctSequence(inputStream, 4)
    println("Part1: $startPacketIndex.")

    val msgPacketIndex = findIndexAfterDistinctSequence(inputStream, 14)
    println("Part2: $msgPacketIndex.")

}

private fun findIndexAfterDistinctSequence(inputStream: String, distinctChars: Int): Int {
    for (i in distinctChars..inputStream.length) {
        if (inputStream.substring(i - distinctChars, i).toSet().count() == distinctChars)
            return i
    }
    return -1
}

fun assertThat(input: String, startExpected: Int, msgExpected: Int) {
    val actualStart = findIndexAfterDistinctSequence(input, 4)
    if (actualStart != startExpected)
        throw java.lang.Exception(
            "Testcase failed. Startpacket expected $startExpected, but was $actualStart. ...${
                input.substring(
                    max(0, actualStart - 10),
                    actualStart
                )
            } "
        )
    val actualMsg = findIndexAfterDistinctSequence(input, 14)
    if (actualMsg != msgExpected)
        throw java.lang.Exception(
            "Testcase failed. Messagepacket expected $msgExpected, but was $actualMsg. ...${
                input.substring(
                    max(0, actualMsg - 10),
                    actualMsg
                )
            } "
        )
}
