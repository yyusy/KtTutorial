import java.io.File
import kotlin.test.assertEquals

fun main() {
    Day6FindAnswers()
}
fun Day6FindAnswers() {

    var r = File("Day6Input.txt").useLines { l ->
        var i = 0
        l.groupingBy { s -> i.also { if (s.isEmpty()) i += 1 } }
            .aggregate { _, a: StringBuffer?, e, first -> if (first) StringBuffer().append(e) else a!!.append(e) }
            .map { it.value.toSet().also { println(it) } } // answered yes
    }.also { println(it) }
    val sum = r.sumOf { it.size }.also { println(it) }
    assertEquals(7283, sum)
}

