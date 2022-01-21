import java.io.File
import kotlin.test.assertEquals

fun main() {
    Day7Part2FindBagContent()
}

fun Day7Part2FindBagContent() {
    val g = File("Day7Input.txt").useLines {
        it.associate { it.toBagRule() }.toGraph()
    }
    println(g)
    println(g.vertices.values.filter { it.edges().size > 1 })
    val shinyGoldBag = "shiny gold"

    val bagsContent = mutableMapOf<String, Int>()
    val ctx = g.walk(start = g[shinyGoldBag]) { edge ->
        val parent = edge.vFrom
        val child = edge.vTo
        val bagsInChild = bagsContent.getOrDefault(child.key, 0)
        val bagsInParent = bagsContent.getOrDefault(parent.key, 0)
        bagsContent[parent.key] = bagsInParent + edge.weight * (bagsInChild + 1)
        println("$edge : child= $bagsInChild, total =${bagsContent[parent.key]}")
    }
    val content = ctx.visited.map { it.key }.filter { it != shinyGoldBag }.distinct()
    println(content)
    println("Different bags : ${content.size}, total : ${bagsContent[shinyGoldBag]}")
    assertEquals(20189, bagsContent[shinyGoldBag])

}
