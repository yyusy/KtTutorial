//class WEdge<V>(override val vFrom: Vertex<V>, override val vTo: Vertex<V>, val weight: Int) : Edge<V>(vFrom, vTo)

open class Edge<V>(open val vFrom: Vertex<V>, open val vTo: Vertex<V>, val weight: Int? = null) {
    init {
        vFrom.addEdge(this)
        vTo.addEdge(this)
    }

    override fun toString(): String {
        return "${vFrom.key.toString()}->(${weight ?: ""})${vTo.key.toString()}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Edge<*>

        if (vFrom != other.vFrom) return false
        if (vTo != other.vTo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vFrom.hashCode()
        result = 31 * result + vTo.hashCode()
        return result
    }
}

class VertexWithData<V, D>(key: V, val data: D) : Vertex<V>(key)

open class Vertex<V>(
    val key: V
) {
    //var data: D? = null // not in eqals or toString
    private val outboundEdges: MutableSet<Edge<V>> = mutableSetOf()
    private val inboundEdges: MutableSet<Edge<V>> = mutableSetOf()

    internal fun addEdge(e: Edge<V>) {
        when (this) {
            e.vFrom -> outboundEdges.add(e)
            e.vTo -> inboundEdges.add(e)
        }
    }

    fun getEdges(backward: Boolean = false): Set<Edge<V>> = if (backward) inboundEdges else outboundEdges

    override fun toString(): String {
        return "Vertex(key=$key, ${if (outboundEdges.isNotEmpty()) "out=" + outboundEdges else ""}, inboundEdges=$inboundEdges)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vertex<*>

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key?.hashCode() ?: 0
    }
}

class FindPathVisitor<V>(var toV: Vertex<V>, backward: Boolean = false) : GraphVisitor<V>(backward) {
    val path = mutableListOf<Vertex<V>>()
    private var found = false
    override fun onVisit(v: Vertex<V>, edge: Edge<V>?): Boolean {
        super.onVisit(v, edge)
        if (v == toV) {
            path.add(v)
            found = true
        }
        // false - found, need to return
        return v != toV
    }

    override fun onReturnFromChild(parent: Vertex<V>, child: Vertex<V>, edge: Edge<V>?) {
        if (found) path.add(parent)
    }
}

interface IGraphVisitor<V> {
    val visited: MutableSet<Vertex<V>>
    val visitedEdges: MutableSet<Edge<V>>
    var backward: Boolean

    fun onVisit(v: Vertex<V>, edge: Edge<V>?): Boolean {
        visited.add(v)
        if (edge != null) visitedEdges.add(edge)
        return true
    }

    fun onReturnFromChild(
        parent: Vertex<V>,
        child: Vertex<V>,
        edge: Edge<V>? = null
    )

    fun verticlesToVisit(fromV: Vertex<V>): Set<Vertex<V>> =
        fromV.getEdges(backward).map { if (backward) it.vFrom else it.vTo }
            .filter { it !in visited }
            .toSet()

    fun edgesToVisit(fromV: Vertex<V>): Set<Edge<V>> =
        fromV.getEdges(backward)
            .filter { it !in visitedEdges }
            .toSet()
}

abstract class GraphVisitor<V>(override var backward: Boolean = false) : IGraphVisitor<V> {
    override val visited = mutableSetOf<Vertex<V>>()
    override val visitedEdges = mutableSetOf<Edge<V>>()
}

class WGraph<V, D>() : Graph<V, D>()

open class Graph<V, D>(
) {
    var vertices: MutableMap<V, Vertex<V>> = mutableMapOf()
    val edges: MutableSet<Edge<V>> = mutableSetOf()

    val leafs: Set<Vertex<V>>
        get() = (vertices.values - edges.map { it.vFrom }).toSet()

    fun addVertex(vKey: V, vData: D): Vertex<V> {
        return vertices.getOrPut(vKey) {
            VertexWithData<V, D>(vKey, vData)
        }
    }

    operator fun get(key: V): Vertex<V> = vertices[key]!!

    fun connect(v1: V, v2: V, weight: Int? = null): Boolean {
        val x1 = vertices.getOrPut(v1) { Vertex(v1) }
        val x2 = vertices.getOrPut(v2) { Vertex(v2) }
        return edges.add(Edge(x1, x2, weight))
    }

    fun findPath(
        fromV: Vertex<V>,
        toV: Vertex<V>,
        visited: MutableSet<Vertex<V>>,
        backward: Boolean = false
    ): List<Vertex<V>> {
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

    fun walk(fromV: Vertex<V>, edge: Edge<V>?, ctx: IGraphVisitor<V>): Boolean {
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

    override fun toString(): String {
        return "Graph(vertices=$vertices, edges=$edges)"
    }
}
