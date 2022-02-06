import java.io.File
import kotlin.test.assertEquals

fun main() {
    val ret = File("Day2Input.txt").useLines { lines ->
        lines.filter { it.isNotBlank() }
        .map { it.split(' ', '-') }
        .count {
             isValidPosition(it[0].toInt() - 1 to it[1].toInt() - 1, it[2][0], it[3])
                .also { r -> println("${it[3]} :$r") }

        }
        .also { println("Valid : $it") }
    }
    assertEquals(485, ret)
}

private fun isValidPosition(r: Pair<Int, Int>, ch: Char, toVerify: String) =
    (toVerify[r.first] == ch).xor(toVerify[r.second] == ch)


