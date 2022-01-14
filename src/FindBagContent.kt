import kotlin.test.assertTrue

data class BagRule( val bagName : String, val count : Int)
typealias  BagRules = Pair<String, List<BagRule>>


fun Map<String, List<BagRule>>.toGraph(): Graph<String, List<BagRule>> {
    val g = Graph<String, List<BagRule>>()
    this.forEach { bagName, bagContent ->
        g.addVertex(bagName, bagContent)
    }
    this.forEach { bagName, bagContent ->
        for (cBag in bagContent) {
            g.addEdge(bagName, cBag.bagName, cBag.count)
        }
    }
    return g
}

fun String.toBagRule(): BagRules {
    val bagRegExp = "([\\w\\s]+) bags contain (.*)".toRegex()
    val ruleRegexp = "(\\d+) ([\\w\\s]+) bag".toRegex()

    val (name, ruleString) = bagRegExp.find(this)?.destructured
        ?: throw IllegalArgumentException("No bag rule in $this")
    return name to ruleRegexp.findAll(ruleString)
        .map { BagRule(it.destructured.component2(), it.destructured.component1().toInt()) }
        .toList()

}

fun main() {
    val rules = System.`in`.bufferedReader().useLines {  it.associate { it.toBagRule() } }
    val g = rules.toGraph()
    println(g)

    val FROM_BAG = "shiny gold"

    val visited = mutableSetOf<Vertex<String, List<BagRule>>>()
    var p = g.findPath(g[FROM_BAG], Vertex("non-existing"), visited, true)
    assertTrue (p.isEmpty() )
    println(visited.map { it.key }.filter() { it != FROM_BAG }.distinct().count())

}
