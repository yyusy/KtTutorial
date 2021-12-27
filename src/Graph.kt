data class Edge<V>(val vFrom: V, val vTo: V)

data class Vertex<V>(val v: V) {
    val outboundEdges = mutableListOf<Edge<Vertex<V>>>()
    val inboundEdges = mutableListOf<Edge<Vertex<V>>>()
    fun addEdge(e: Edge<Vertex<V>>) {
        when (this) {
            e.vFrom -> outboundEdges.add(e)
            e.vTo -> inboundEdges.add(e)
        }
    }
}

data class Graph<V, D>(var vertices: MutableMap<V, Vertex<V>> = mutableMapOf<V, Vertex<V>>()) {

    val edges = mutableListOf<Edge<Vertex<V>>>()
    fun addVertex(vKey: V, vData: D? = null): Vertex<V> {
        val x = Vertex(vKey)
        vertices[vKey] = x
        return x
    }

    operator fun get(key: V): Vertex<V>? {
        return vertices[key]
    }

    fun addEdge(v1: V, v2: V) {
        val x1 = vertices[v1] ?: addVertex(v1)
        val x2 = vertices[v2] ?: addVertex(v2)
        val e = Edge(x1, x2)
        edges.add(e)
        x1.addEdge(e)
        x2.addEdge(e)

    }
}