data class Edge<V, D>(val vFrom: Vertex<V, D>, val vTo: Vertex<V, D>, val weight: Int? = null) {
    init {
        vFrom.addEdge(this)
        vTo.addEdge(this)
    }

    override fun toString(): String {
        return "${vFrom.key.toString()}->(${weight ?: ""})${vTo.key.toString()}"
    }
}

class VertexWithData<V, D>(key: V, data: D) : Vertex<V, D>(key) {
    init {
        super.data = data // not in eqals or toString
    }
}

open class Vertex<V, D>(
    val key: V
) {
    var data: D? = null // not in eqals or toString
    val outboundEdges: MutableSet<Edge<V, D>> = mutableSetOf()
    val inboundEdges: MutableSet<Edge<V, D>> = mutableSetOf()

    internal fun addEdge(e: Edge<V, D>) {
        when (this) {
            e.vFrom -> outboundEdges.add(e)
            e.vTo -> inboundEdges.add(e)
        }
    }

    fun getEdges(backward: Boolean = false): Set<Edge<V, D>> = if (backward) inboundEdges else outboundEdges

    override fun toString(): String {
        return "Vertex(key=$key, ${if (outboundEdges.isNotEmpty()) "out=" + outboundEdges else ""}, inboundEdges=$inboundEdges)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vertex<*, *>

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key?.hashCode() ?: 0
    }
}

class FindPathVisitor<V, D>(var toV: Vertex<V, D>, backward: Boolean = false) : GraphVisitor<V, D>(backward) {
    val path = mutableListOf<Vertex<V, D>>()
    private var found = false
    override fun onVisit(v: Vertex<V, D>, edge: Edge<V, D>?): Boolean {
        super.onVisit(v, edge)
        if (v == toV) {
            path.add(v)
            found = true
        }
        // false - found, need to return
        return v != toV
    }

    override fun onReturnFromChild(parent: Vertex<V, D>, child: Vertex<V, D>, edge: Edge<V, D>?) {
        if (found) path.add(parent)
    }
}

interface IGraphVisitor<V, D> {
    val visited: MutableSet<Vertex<V, D>>
    val visitedEdges: MutableSet<Edge<V, D>>
    var backward: Boolean

    fun onVisit(v: Vertex<V, D>, edge: Edge<V, D>?): Boolean {
        visited.add(v)
        if (edge != null) visitedEdges.add(edge)
        return true
    }

    fun onReturnFromChild(
        parent: Vertex<V, D>,
        child: Vertex<V, D>,
        edge: Edge<V, D>? = null
    )

    fun verticlesToVisit(fromV: Vertex<V, D>): Set<Vertex<V, D>> =
        fromV.getEdges(backward).map { if (backward) it.vFrom else it.vTo }
            .filter { it !in visited }
            .toSet()

    fun edgesToVisit(fromV: Vertex<V, D>): Set<Edge<V, D>> =
        fromV.getEdges(backward)
            .filter { it !in visitedEdges }
            .toSet()
}

abstract class GraphVisitor<V, D>(override var backward: Boolean = false) : IGraphVisitor<V, D> {
    override val visited = mutableSetOf<Vertex<V, D>>()
    override val visitedEdges = mutableSetOf<Edge<V, D>>()
}

data class Graph<V, D>(
    var vertices: MutableMap<V, Vertex<V, D>> = mutableMapOf(),
    val edges: MutableSet<Edge<V, D>> = mutableSetOf()

) {
    val leafs: Set<Vertex<V, D>>
        get() = (vertices.values - edges.map { it.vFrom }).toSet()

    fun addVertex(vKey: V, vData: D? = null): Vertex<V, D> {
        return vertices.getOrPut(vKey) {
            Vertex<V, D>(vKey).apply { data = vData }
        }
    }

    operator fun get(key: V): Vertex<V, D> = vertices[key]!!


    fun connect(v1: V, v2: V, weight: Int? = null): Boolean {
        val x1 = vertices.getOrPut(v1) { Vertex(v1) }
        val x2 = vertices.getOrPut(v2) { Vertex(v2) }
        return edges.add(Edge(x1, x2, weight))
    }

    fun findPath(
        fromV: Vertex<V, D>,
        toV: Vertex<V, D>,
        visited: MutableSet<Vertex<V, D>>,
        backward: Boolean = false
    ): List<Vertex<V, D>> {
        if (fromV == toV) return listOf(toV)
        visited.add(fromV)
        val x = fromV.getEdges(backward).map { if (backward) it.vFrom else it.vTo }.filter { it !in visited }.toSet()
        for (v in x) {
            val path = findPath(v, toV, visited, backward)
            if (path.isNotEmpty()) return path + fromV
        }
        return emptyList()
    }

    /**
     * @return true - continue walk, false : finish
     */

    fun walk(fromV: Vertex<V, D>, edge: Edge<V, D>?, ctx: IGraphVisitor<V, D>): Boolean {
        if (!ctx.onVisit(fromV, edge)) return false
        var ret = true
        for (e in ctx.edgesToVisit(fromV)) {
            val v = if (ctx.backward) e.vFrom else e.vTo
            ret = walk(v, e, ctx)
            ctx.onReturnFromChild(fromV, v, e)
            if (!ret) {
                println("Skip other childred after $e")
                break
            }
        }
        return ret
    }
}
