import java.io.File
import kotlin.test.assertEquals

fun main() {
    val stepX = 3
    val stepY = 1
    val treeMark = '#'

    val ret = File("Day3Input.txt").useLines { l ->
        var x = 0
        l.filterIndexed { i, _ -> (i + 1) % stepY == 0 }
            .count { s ->
                s[x.also { x = (x + stepX) % s.length }] == treeMark
            }.also { println(it) }
    }
    assertEquals(205, ret)
}
