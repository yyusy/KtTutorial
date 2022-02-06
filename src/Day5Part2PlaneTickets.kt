import java.io.File
import kotlin.test.assertEquals

data class Ticket(val code: String, val seat: Pair<Int, Int>) {
    val id: Int = seat.first * 8 + seat.second
}
fun main() {
    day5Part2()
}
fun day5Part2() {
    val ret = File("Day5Input.txt").useLines { l ->
        l.filter { it.isNotBlank() && it.length == 10 }
            .map { s ->
                Ticket(s, findPos(s.substring(0, 7), 0..127) to findPos(s.substring(7), 0..7))
            }
            .sortedBy { it.id }
            //.map { it.also { println(it) } }
            .zipWithNext()
            .filter { it.second.id - it.first.id > 1 }
            .onEach { println("$it, ${it.first.id}" ) }
            .first()
    }
    assertEquals(717, ret.first.id + 1)
}

tailrec fun findPos(s: String, range: IntRange): Int =
    if (range.isEmpty() || s.isEmpty()) range.first
    else findPos(
        s.substring(1),
        when (s[0]) {
            'F', 'L' -> range.first..range.first + ((range.last - range.first) shr 1)
            'B', 'R' -> range.first + ((range.last - range.first + 1) shr 1)..range.last
            else -> range
        }
    )
