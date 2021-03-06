import java.util.*

class EdgeWeighted<V>(from: Vertex<V>, to: Vertex<V>, val weight: Int) : Edge<V>(from, to) {
    override fun toString(): String {
        return "${vFrom.key.toString()}->($weight)${vTo.key.toString()}"
    }
}

open class Edge<V> constructor(open val vFrom: Vertex<V>, open val vTo: Vertex<V>) {
    override fun toString(): String {
        return "${vFrom.key.toString()}->${vTo.key.toString()}"
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

open class Vertex<V>(
    val key: V,
    var data: Any? = null
) {
    private val outboundEdges: MutableSet<Edge<V>> = mutableSetOf()
    private val inboundEdges: MutableSet<Edge<V>> = mutableSetOf()

    internal fun addEdge(e: Edge<V>) {
        when (this) {
            e.vFrom -> outboundEdges.add(e)
            e.vTo -> inboundEdges.add(e)
        }
    }

    fun inbound(): Set<Edge<V>> = inboundEdges
    fun outbound(): Set<Edge<V>> = outboundEdges
    fun edges(backward: Boolean = false): Set<Edge<V>> = if (backward) inboundEdges else outboundEdges

    override fun toString(): String {
        return "Vertex(key=$key, out=$outboundEdges, in=$inboundEdges)"
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

class FindPathResult<V, E : Edge<V>>(private val toV: Vertex<V>, backward: Boolean = false) :
    GraphVisitor<V, E>(backward) {
    val path = mutableListOf<Vertex<V>>()
    var found = false
    override fun enter(v: Vertex<V>): Boolean {
        super.enter(v)
        if (v == toV) {
            path.add(v)
            found = true
        }
        // false - found, need to return
        return v != toV
    }

    override fun onChildVisited(edge: E) {
        if (found) path.add(if (backward) edge.vTo else edge.vFrom)
    }
}

interface IGraphVisitor<V, E : Edge<V>> {
    val visited: Set<Vertex<V>>
        get() = visitedEdges.flatMap { listOf(it.vFrom, it.vTo) }.toSet()
    val visitedEdges: Set<E>
    var backward: Boolean
    fun enter(v: Vertex<V>): Boolean
    fun registerVisit(edge: E)
    fun unRegisterVisit(edge: E)
    fun onChildVisited(edge: E)
    fun notVisited(fromV: Vertex<V>) = fromV.edges(backward) as Set<E> - visitedEdges
}
typealias onVisitChildType<E> = (edge: E) -> Unit

open class GraphVisitor<V, E : Edge<V>>(override var backward: Boolean = false) : IGraphVisitor<V, E> {
    override val visitedEdges = mutableSetOf<E>()
    private var callBack: onVisitChildType<E>? = null

    constructor(backward: Boolean, onVisit: onVisitChildType<E>) : this(backward) {
        this.callBack = onVisit
    }

    override fun enter(v: Vertex<V>): Boolean {
        return true
    }

    override fun registerVisit(edge: E) {
        visitedEdges.add(edge)
    }

    override fun unRegisterVisit(edge: E) {
        visitedEdges.remove(edge)
    }

    override fun onChildVisited(edge: E) {
        callBack?.invoke(edge)
    }


}

class GraphWeighted<V> : GraphBase<V, EdgeWeighted<V>>() {
    override val vertices: MutableMap<V, Vertex<V>> = mutableMapOf()
    override operator fun get(key: V): Vertex<V> = vertices[key] ?: throw IllegalArgumentException("Unkwnown vertex $key")
    fun connect(from: V, to: V, weight: Int) = connect(addVertex(from), addVertex(to), weight)

    private fun connect(from: Vertex<V>, to: Vertex<V>, weight: Int) = register(EdgeWeighted(from, to, weight))
    private fun addVertex(vKey: V) = vertices.getOrPut(vKey) { Vertex(vKey) }
}

open class Graph<V> : GraphBase<V, Edge<V>>() {
    override val vertices: MutableMap<V, Vertex<V>> = mutableMapOf()
    override operator fun get(key: V): Vertex<V> = vertices[key]!!
    fun connect(from: V, to: V) = connect(addVertex(from), addVertex(to))

    private fun addVertex(vKey: V) = vertices.getOrPut(vKey) { Vertex(vKey) }
    private fun connect(from: Vertex<V>, to: Vertex<V>) = register(Edge(from, to))
}

abstract class GraphBase<V, E : Edge<V>> {
    abstract val vertices: Map<V, Vertex<V>>
    val edges: MutableSet<E> = mutableSetOf()
    abstract operator fun get(key: V): Vertex<V>
    protected fun register(e: E): E {
        e.vFrom.addEdge(e)
        e.vTo.addEdge(e)
        edges.add(e)
        return e
    }

    val leafs: Set<Vertex<V>>
        get() = (vertices.values.toSet() - edges.map { it.vFrom }.toSet())

    fun findPathResult(
        fromV: Vertex<V>,
        toV: Vertex<V>,
        backward: Boolean = false
    ) = FindPathResult<V, E>(toV, backward).also {
        walk(fromV, it)
    }

    fun findPath(
        fromV: Vertex<V>,
        toV: Vertex<V>,
        backward: Boolean = false
    ) = FindPathResult<V, E>(toV, backward).also {
        walkInternal(fromV, it)
    }.path

    fun walk(start: Vertex<V>, callBack: onVisitChildType<E>) = GraphVisitor(false, callBack).also {
        walkInternal(start, it)
    }

    fun walkBack(start: Vertex<V>, callBack: onVisitChildType<E>) = GraphVisitor(true, callBack).also {
        walkInternal(start, it)
    }


    fun walk(fromV: Vertex<V>, ctx: IGraphVisitor<V, E>): IGraphVisitor<V, E> {
        walkInternal(fromV, ctx)
        return ctx
    }

    /**
     * @return true - continue walk, false : finish
     */


    private fun walkInternal(fromV: Vertex<V>, ctx: IGraphVisitor<V, E>): Boolean {
        if (!ctx.enter(fromV)) return false
        var ret = true
        for (e in ctx.notVisited(fromV)) {
            ctx.registerVisit(e)
            ret = walkInternal(if (ctx.backward) e.vFrom else e.vTo, ctx)
            ctx.onChildVisited(e)
            if (!ret) {
                println("Skip other children after $e")
                break
            }
        }
        return ret
    }

    fun index(from: Vertex<V>, to: Vertex<V>): Long {
        var index = 0L
        walk(from, object : GraphVisitor<V, E>() {
            override fun registerVisit(edge: E) {
                super.registerVisit(edge)
                if (edge.vTo == to) index++
                    //println("${edge} :$index")
                    //println(this.notVisited(edge.vFrom))
            }

            override fun onChildVisited(edge: E) {
                unRegisterVisit(edge)
            }
        })
        return index
    }

    fun index(start : Vertex<V>) :Map<Vertex<V>, Long>
    {
        val indexes = mutableMapOf(start to 1L)
        val notCalculated: Deque<Vertex<V>> = LinkedList<Vertex<V>>().apply { add(start) }
        notCalculated.add(start)

        while (notCalculated.isNotEmpty()) {
            val v = notCalculated.pollFirst()

            if (indexes[v] != null) {
                v.outbound().map { it.vTo }.forEach { if (!notCalculated.contains(it)) notCalculated.offerLast(it) }
                continue
            }
            val vIndex = v.inbound().map { it.vFrom }
                .map { indexes[it] ?: -1 }
                .fold(0L) { acc, i -> if (i < 0 || acc < 0) -1 else acc + i }
            if (vIndex < 0) notCalculated.offerLast(v)
            else {

                indexes[v] = vIndex
                v.outbound().map { it.vTo }.forEach {
                    if (!notCalculated.contains(it)) notCalculated.offerLast(it)
                }
            }
            println("$v : calced : ${vIndex >= 0}, index : ${vIndex}, toCalc : ${notCalculated.size}")
        }
        return indexes
    }

    override fun toString(): String {
        return "Graph(vertices=$vertices, edges=$edges)"
    }
}
