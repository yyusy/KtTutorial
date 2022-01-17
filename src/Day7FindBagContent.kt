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

    val fromBag = "shiny gold"
    val ctx = g.walkBack(g[fromBag]) {}
    assertEquals(128, ctx.visited.map { it.key }.filter() { it != fromBag }.distinct().count().also { println(it) })

}
