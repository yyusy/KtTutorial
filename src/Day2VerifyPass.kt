import java.io.File
import kotlin.test.assertEquals

fun main() {
    val ret = File("Day2Input.txt").useLines { lines ->
        lines.filter { it.isNotBlank() }
        .map { it.split(' ', '-') }
        .count {
            isValidRange(it[0].toInt()..it[1].toInt(), it[2][0], it[3])
                .also { r -> println("${it[3]} :$r") }

        }
        .also { println("Valid : $it") }
    }
    assertEquals(524, ret)
}

private fun isValidRange(r: IntProgression, ch: Char, toVerify: String) = toVerify.count { it == ch } in r


