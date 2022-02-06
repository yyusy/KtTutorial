import java.io.File
import kotlin.test.assertEquals

fun main() {
    val stepX = 3
    val stepY = 1
    val treeMark = '#'
    var x = 0
    var count = 0
    val l = File("Day3Input.txt").readLines()
    for (y in 0..l.lastIndex step stepY) {
        count += if (l[y][x] == treeMark) 1 else 0
        x += if (x + stepX >= l[0].length) stepX - l[0].length else stepX
    }
    println(count)
    assertEquals(205, count )
}
