import java.io.File
import kotlin.test.assertEquals

fun main() {
    val max = File("Day10Input.txt").useLines { l ->
        l.filter { it.isNotBlank() }
            .map { it.toInt() }
            .maxOrNull()!!
    }
    val ret = File("Day10Input.txt").useLines { l ->
        l.filter { it.isNotBlank() }
            .map { it.toInt() }
            .plus(sequenceOf(max + 3, 0))
            .sorted()
            .zipWithNext { a, b -> b - a }
            .groupBy { it }
            .map { (k, v) -> k to v.size }
            .toMap()
    }
    println(ret)
    println("diff[1] * diff[3] = ${ret[1]!! * ret[3]!!}")
    assertEquals(1836, ret[1]!! * ret[3]!!)
}
