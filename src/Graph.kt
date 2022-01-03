class Edge<V>(val vFrom: Vertex<V>, val vTo: Vertex<V>) {
    init {
        vFrom.addEdge(this)
        vTo.addEdge(this)
    }

    override fun toString(): String {
        return "${vFrom.key.toString()}->${vTo.key.toString()}"
    }
}

data class Vertex<V>(
    val key: V
) {
    private val outboundEdges: MutableSet<Edge<V>> = mutableSetOf()
    private val inboundEdges: MutableSet<Edge<V>> = mutableSetOf()

    fun addEdge(e: Edge<V>) {
        when (this) {
            e.vFrom -> outboundEdges.add(e)
            e.vTo -> inboundEdges.add(e)
        }
    }

    fun getEdges(backward : Boolean = false): Set<Edge<V>> = if (backward) inboundEdges else outboundEdges
}

data class Graph<V, D>(
    var vertices: MutableMap<V, Vertex<V>> = mutableMapOf(),
    val edges: MutableSet<Edge<V>> = mutableSetOf()
) {
    fun addVertex(vKey: V, vData: D? = null): Vertex<V> = vertices.computeIfAbsent(vKey) { Vertex(vKey) }

    operator fun get(key: V): Vertex<V>? = vertices[key]

    fun addEdge(v1: V, v2: V): Boolean {
        val x1 = vertices.getOrPut(v1) { Vertex(v1) }
        val x2 = vertices.getOrPut(v2) { Vertex(v2) }
        return edges.add(Edge(x1, x2))
    }

    fun findWay(fromV: Vertex<V>, toV: Vertex<V>, backward: Boolean = false): List<Vertex<V>> =
        findWay(fromV, toV, mutableSetOf(), backward)

    fun findWay(fromV: Vertex<V>, toV: Vertex<V>, visited: MutableSet<Vertex<V>>, backward: Boolean = false): List<Vertex<V>> {
        if (fromV == toV) return listOf(toV)
        visited.add(fromV)
        val x = fromV.getEdges(backward).map { if (backward) it.vFrom else it.vTo }.filter { it !in visited }.toSet()
        for (v in x) {
            val path = findWay(v, toV, visited, backward)
            if (path.isNotEmpty()) return path + fromV
        }
        return emptyList()
    }

}
