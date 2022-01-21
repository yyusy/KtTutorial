data class BagRule(val bagName: String, val count: Int)typealias  BagRules = Pair<String, List<BagRule>>

fun Map<String, List<BagRule>>.toGraph(): GraphWeightedWithData<String, List<BagRule>> {
    val g = GraphWeightedWithData<String, List<BagRule>>()
    this.forEach { (bagName, bagContent) -> g.vertex(bagName, bagContent) }
    this.forEach { (bagName, bagContent) ->
        for (cBag in bagContent) {
            g.connect(g[bagName], g[cBag.bagName], cBag.count)
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