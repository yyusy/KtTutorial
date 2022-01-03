import kotlin.test.assertTrue

typealias  BagRule = Pair<String, List<Pair<String, Int>>>
typealias  BagRuleList = Map<String, List<Pair<String, Int>>>

fun BagRuleList.toGraph(): Graph<String, Int> {
    val g = Graph<String, Int>()
    this.forEach { bag, content -> for (cBag in content) g.addEdge(bag, cBag.first) }
    return g
}

fun String.toBagRule(): BagRule {
    val bagRegExp = "([\\w\\s]+) bags contain (.*)".toRegex()
    val ruleRegexp = "(\\d+) ([\\w\\s]+) bag".toRegex()

    val (name, ruleString) = bagRegExp.find(this)?.destructured
        ?: throw IllegalArgumentException("No bag rule in $this")
    return name to ruleRegexp.findAll(ruleString)
        .map { it.destructured.component2() to it.destructured.component1().toInt() }
        .toList()

}

fun main() {
    val rules = System.`in`.bufferedReader().useLines {  it.associate { it.toBagRule() } }
    val g = rules.toGraph()
    println(g)

    val FROM_BAG = "shiny gold"

    val visited = mutableSetOf<Vertex<String>>()
    var p = g.findWay(g[FROM_BAG]!!, Vertex("non-existing"), visited, true)
    assertTrue (p.isEmpty() )
    println(visited.map { it.key }.filter() { it != FROM_BAG }.distinct().count())

}
