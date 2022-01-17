class WEdge<V>(override val vFrom: Vertex<V>, override val vTo: Vertex<V>, val w: Int) : Edge<V>(vFrom, vTo)

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

class FindPathResult<V>(var toV: Vertex<V>, backward: Boolean = false) : GraphVisitor<V>(backward) {
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

    override fun onReturnFromChild(edge: Edge<V>) {
        if (found) path.add(if (backward) edge.vTo else edge.vFrom)
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

    fun onReturnFromChild(edge: Edge<V>)


    fun edgesToVisit(fromV: Vertex<V>): Set<Edge<V>> =
        fromV.getEdges(backward)
            .filter { it !in visitedEdges }
            .toSet()
}

abstract class GraphVisitor<V>(override var backward: Boolean = false) : IGraphVisitor<V> {
    override val visited = mutableSetOf<Vertex<V>>()
    override val visitedEdges = mutableSetOf<Edge<V>>()
}

class WGraph<V, D>() : Graph<V, D>() {
    fun connect(v1: V, v2: V, weight: Int): Boolean {
        return super.connect(v1, v2, weight)
    }
}

class XGraphVisitor<V>(backward: Boolean, val onReturnFromChildCallBack: (edge: Edge<V>) -> Unit) :
    GraphVisitor<V>(backward) {
    override fun onReturnFromChild(edge: Edge<V>) {
        onReturnFromChildCallBack(edge)
    }

}


open class Graph<V, D>() {
    var vertices: MutableMap<V, Vertex<V>> = mutableMapOf()
    val edges: MutableSet<Edge<V>> = mutableSetOf()

    val leafs: Set<Vertex<V>>
        get() = (vertices.values - edges.map { it.vFrom }).toSet()

    fun addVertex(vKey: V, vData: D) = vertices.getOrPut(vKey) { VertexWithData(vKey, vData) }

    fun addVertex(vKey: V) = vertices.getOrPut(vKey) { Vertex(vKey) }

    operator fun get(key: V): Vertex<V> = vertices[key]!!

    fun connect(v1: V, v2: V, weight: Int? = null) = edges.add(Edge(addVertex(v1), addVertex(v2), weight))

    fun findPathResult(
        fromV: Vertex<V>,
        toV: Vertex<V>,
        backward: Boolean = false
    ) = FindPathResult(toV, backward).also {
        walk(fromV, null, it)
    }

    fun findPath(
        fromV: Vertex<V>,
        toV: Vertex<V>,
        backward: Boolean = false
    ) = FindPathResult(toV, backward).also {
        walk(fromV, null, it)
    }.path

    fun walk(start: Vertex<V>, callBack: (edge: Edge<V>) -> Unit) = XGraphVisitor<V>(false, callBack).also {
        walk(start, null, it)

    }

    fun walkBack(start: Vertex<V>, callBack: (edge: Edge<V>) -> Unit) = XGraphVisitor<V>(true, callBack).also {
        walk(start, null, it)
    }

    /**
     * @return true - continue walk, false : finish
     */

    private fun walk(fromV: Vertex<V>, edge: Edge<V>?, ctx: IGraphVisitor<V>): Boolean {
        if (!ctx.onVisit(fromV, edge)) return false
        var ret = true
        for (e in ctx.edgesToVisit(fromV)) {
            val v = if (ctx.backward) e.vFrom else e.vTo
            ret = walk(v, e, ctx)
            ctx.onReturnFromChild(e)
            if (!ret) {
                println("Skip other children after $e")
                break
            }
        }
        return ret
    }

    override fun toString(): String {
        return "Graph(vertices=$vertices, edges=$edges)"
    }
}
