import java.io.File
import kotlin.math.absoluteValue
import kotlin.test.assertEquals
import NavInstruction.*
import kotlin.math.roundToInt

fun main() {
    day13()
}

fun day13() {
    val l = File("Day13Input.txt").readLines()

    val myTime = l[0].toLong()
    val res = l[1].split(",").filter { it != "x" }
        .map { it.toLong() }
        .map { it to (myTime / it + 1) * it - myTime }
        .fold(Pair(-1L, Long.MAX_VALUE)) { r, i ->
            if (i.second < r.second) i else r
        }
    println(res)
    assertEquals(3215, res.first * res.second)
}