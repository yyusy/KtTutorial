import java.io.File
import kotlin.test.assertEquals

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    val windowSize = 25
    val expectedSum = File("Day9Input.txt").useLines {
        findInvalidNumber(it, windowSize)
    }
    println("Find sum of : $expectedSum")
    var found = false
    val res = ArrayDeque<Long>()
    File("Day9Input.txt").useLines {
        for (i in it.map { it.toLong() }) {
            res.addLast(i)
            while (res.sum() > expectedSum) res.removeFirst()

            if (res.sum() == expectedSum) {
                found = true; break
            }
        }
    }
    println("${if (found) "Found : " else "Not found. "}, $res")
    println("min : ${res.minOrNull()} + max : ${res.maxOrNull()} = ${(res.minOrNull() ?: 0) + (res.maxOrNull() ?: 0)}")
    assertEquals(2186361, (res.minOrNull() ?: 0) + (res.maxOrNull() ?: 0))
}
