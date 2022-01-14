import java.io.File
import kotlin.test.assertEquals

fun main() {
    Day6Part2FindAnswers()
}
fun Day6Part2FindAnswers() {

    val r = File("Day6Input.txt").useLines { l ->
        var i = 0
        l.groupingBy { s -> i.also { if (s.isEmpty()) i += 1 } }
            .aggregate { _, a: Set<Char>?, e: String, first ->
                if (first) e.toSet() else {
                    if (!e.isEmpty()) a!!.intersect(e.toSet())
                    else a!!
                }
            }
            .map { println("$it"); it.value.size }
    }.also { println(it) }

    assertEquals(3520, r.sum().also { println(it) })

}

