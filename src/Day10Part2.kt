import java.io.File
import kotlin.test.assertEquals

fun main() {
    day10Part2()
}

fun day10Part2() {
    val max = File("Day10Input.txt").useLines { l ->
        l.filter { it.isNotBlank() }
            .map { it.toInt() }
            .maxOrNull()!!
    }
    val g = File("Day10Input.txt").useLines { l ->
        l.filter { it.isNotBlank() }
            .map { it.toInt() }
            .plus(sequenceOf(max + 3, 0))
            .toWiredAdaptersGraph()

    }
}

fun Sequence<Int>.toWiredAdaptersGraph(): GraphWeightedWithData<Int, Int> {
    val g = GraphWeightedWithData<Int, Int>()
    this.sorted()
        .windowed(4, 1, true)
        .forEach {
            val from = it.first()
            for (to in it.takeLast(it.size - 1)) {
                if (to - from <= 3)
                    g.connect(g.vertex(from, -1), g.vertex(to, -1), to - from)
            }
        }
    return g
}