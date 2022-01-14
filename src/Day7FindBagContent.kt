import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun main() {
    Day7FindBag()
}
fun Day7FindBag()
{
    val g = File("Day7Input.txt").useLines {
        it.associate { it.toBagRule() }.toGraph()
    }.also { println(it) }

    val FROM_BAG = "shiny gold"
    val visited = mutableSetOf<Vertex<String>>()
    var p = g.findPath(g[FROM_BAG], Vertex("non-existing"), visited, true)
    assertTrue(p.isEmpty())
    assertEquals(128, visited.map { it.key }.filter() { it != FROM_BAG }.distinct().count().also { println(it) })

}
