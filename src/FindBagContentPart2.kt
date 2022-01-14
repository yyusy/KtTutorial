class CountBagsVisitor: GraphVisitor<String, List<BagRule>>() {
    var bagsContent = mutableMapOf<String, Int>()
    override fun onReturnFromChild(
        parent: Vertex<String, List<BagRule>>,
        child: Vertex<String, List<BagRule>>,
        edge: Edge<String, List<BagRule>>?
    ) {
        val bagsInChild = bagsContent.getOrDefault(child.key, 0)
        val bagsInParent = bagsContent.getOrDefault(parent.key, 0)
        bagsContent[parent.key] = bagsInParent + edge!!.weight!! * (bagsInChild + 1)
        println("$edge : child= $bagsInChild, total =${bagsContent[parent.key]}")
    }
}

fun main() {
    val rules = System.`in`.bufferedReader().useLines {  it.associate { it.toBagRule() } }
    val g = rules.toGraph()
    println(g)
    println(g.vertices.values.filter { it.inboundEdges.size > 1 })
    val shinyGoldBag = "shiny gold"
    val ctx = CountBagsVisitor()
    g.walk(g[shinyGoldBag], null, ctx)
    val content = ctx.visited.map { it.key }.filter { it != shinyGoldBag }.distinct()
    println(content)
    println( "Different bags : ${content.size}, total : ${ctx.bagsContent[shinyGoldBag]}" )

}
