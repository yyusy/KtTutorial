import java.io.File
import kotlin.test.assertEquals

fun main() {
    val stepX = 3
    val stepY = 1
    val treeMark = '#'
    var x = 0
    val l = File("Day3Input.txt").readLines()
    val ret = (0..l.lastIndex step stepY).count { y ->
        l[y][x.also { x = (x + stepX) % l[0].length }] == treeMark
    }.also { println(it) }
    assertEquals(205, ret)

}

