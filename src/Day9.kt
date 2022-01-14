import java.io.File
import kotlin.test.assertEquals

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    val windowSize = 25
    val res = File("Day9Input.txt").useLines {
        invalidNumber(it, windowSize)
    }
    println(res)
    assertEquals(18272118, res)
}

@ExperimentalStdlibApi
fun invalidNumber(it: Sequence<String>, windowSize: Int) =
    it.map { it.toLong() }.asSequence()
        .windowed(windowSize + 1) { l ->
            l.last().takeIf { l.take(windowSize).toPairs().count { it.first + it.second == l.last() } == 0 }
        }.filterNotNull().first()

@ExperimentalStdlibApi
fun <V> List<V>.toPairs() = buildList<Pair<V, V>> {
    for (i in this@toPairs.indices)
        for (j in i + 1..this@toPairs.lastIndex) this.add(Pair(this@toPairs[i], this@toPairs[j]))
}
