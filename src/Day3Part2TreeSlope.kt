import java.io.File
import kotlin.test.assertEquals

fun main() {
    day3Part2()
}

fun day3Part2() {
    val treeMark = '#'
    val l = File("Day3Input.txt").readLines()
    val ret = listOf(1 to 1, 1 to 3, 1 to 5, 1 to 7, 2 to 1).map { step ->
        (0..l.lastIndex step step.first).mapIndexed { i, y -> y to (step.second * i % l[0].length) }
            .count { l[it.first][it.second] == treeMark }.also { println("$step : $it") }
    }.fold(1L) { acc, i -> acc * i }
    assertEquals(3952146825, ret)
}

