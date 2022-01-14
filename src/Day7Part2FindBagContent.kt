import java.io.File
import kotlin.test.assertEquals

class CountBagsVisitor : GraphVisitor<String>() {
    var bagsContent = mutableMapOf<String, Int>()
    override fun onReturnFromChild(
        parent: Vertex<String>,
        child: Vertex<String>,
        edge: Edge<String>?
    ) {
        val bagsInChild = bagsContent.getOrDefault(child.key, 0)
        val bagsInParent = bagsContent.getOrDefault(parent.key, 0)
        bagsContent[parent.key] = bagsInParent + edge!!.weight!! * (bagsInChild + 1)
        println("$edge : child= $bagsInChild, total =${bagsContent[parent.key]}")
    }
}

fun main() {
    Day7Part2FindBagContent()
}

fun Day7Part2FindBagContent() {
    val g = File("Day7Input.txt").useLines {
        it.associate { it.toBagRule() }
            .toGraph()
    }
    println(g)
    println(g.vertices.values.filter { it.getEdges().size > 1 })
    val shinyGoldBag = "shiny gold"
    val ctx = CountBagsVisitor()
    g.walk(g[shinyGoldBag], null, ctx)
    val content = ctx.visited.map { it.key }.filter { it != shinyGoldBag }.distinct()
    println(content)
    println("Different bags : ${content.size}, total : ${ctx.bagsContent[shinyGoldBag]}")
    assertEquals(20189, ctx.bagsContent[shinyGoldBag])

}
