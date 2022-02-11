import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val t = measureTime {
        day15()
    }
    println("Executed $t")
}

fun elvesGame(seed: List<Int>) = sequence {
    val input = mutableMapOf<Int, Int>().apply { seed.forEachIndexed { i, v -> this[v] = i } }
    yieldAll(seed)
    var lastIdx = seed.lastIndex
    var nextContains: Int? = null
    while (true) {
        val next = if (nextContains == null) 0 else (lastIdx - nextContains)
        nextContains = input[next]
        input[next] = ++lastIdx
        yield(next)
    }
}

fun day15() {

    // part1
    assertEquals(1665, elvesGame(listOf(0, 1, 4, 13, 15, 12, 16)).take(2020).last())
    //part2
    assertEquals(16439, elvesGame(listOf(0, 1, 4, 13, 15, 12, 16)).take(30000000).last())
}
