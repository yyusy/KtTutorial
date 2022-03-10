import CubeState.ACTIVE
import CubeState.INACTIVE
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val t = measureTime {
        day17()
    }
    println("Executed $t")
}

fun day17() {
    var nextStep = File("Day17Input.txt").readLines().toCube()
    println("Input : $nextStep")
    repeat(6) {
        nextStep = nextStep.convert()
    }
    println("Result : $nextStep")
    assertEquals(267, nextStep.activePositions.size)
}

fun List<String>.toCube() = Cube().also { c ->
    this.forEachIndexed { y, l ->
        l.forEachIndexed { x, ch -> c[CubePos3D(x, y, 0)] = ch.toCubeState() }
    }
}


enum class CubeState(val code: Char) {
    ACTIVE('#'),
    INACTIVE('.'),
    ;

    override fun toString(): String {
        return code.toString()
    }
}

fun Char.toCubeState() = CubeState.values().firstOrNull { it.code == this }!!


interface ICubePos {
    fun compose(p: ICubePos?, f: (v1: Int, v2: Int) -> Int): ICubePos
    fun neighbourgs(): List<ICubePos>
    fun forEach(to: ICubePos, action: (p: ICubePos) -> Unit)
}

data class CubePos3D(val x: Int, val y: Int, val z: Int) : ICubePos {
    override fun compose(p: ICubePos?, f: (v1: Int, v2: Int) -> Int) =
        p?.let { CubePos3D(f(x, (p as CubePos3D).x), f(y, p.y), f(z, p.z)) } ?: this

    override fun neighbourgs() = (-1..1).map { CubePos3D(x + it, y, z) }
        .flatMap { p -> (-1..1).map { with(p) { CubePos3D(x, y + it, z) } } }
        .flatMap { p -> (-1..1).map { with(p) { CubePos3D(x, y, z + it) } } }
        .filter { it != this }
        .toList()

    override fun forEach(to: ICubePos, action: (p: ICubePos) -> Unit) {
        to as CubePos3D
        (this.z - 1..to.z + 1).forEach { z ->
            (this.y - 1..to.y + 1).forEach { y ->
                (this.x - 1..to.x + 1).forEach { x ->
                    action(CubePos3D(x, y, z))
                }
            }
        }
    }
}

data class CubePosND(val n: Int, val coord: List<Int> = mutableListOf()) : ICubePos {
    override fun compose(p: ICubePos?, f: (v1: Int, v2: Int) -> Int): ICubePos {
        p as CubePosND
        return (1..n).map { f(coord[it], p.coord[it]) }.toList().let { CubePosND(n, it) }
    }

    override fun neighbourgs(): List<ICubePos> {
        TODO("Not yet implemented")
    }

    override fun forEach(to: ICubePos, action: (p: ICubePos) -> Unit) {
        to as CubePosND
        fun forEachLayer(i: Int, p: List<Int>) {
            if (i == n) action(CubePosND(n, p))
            else (coord[i] - 1..to.coord[i] + 1).forEach { forEachLayer(i + 1, p + it) }
        }
        forEachLayer(0, emptyList<Int>())
    }
}

class CubeND(val n: Int) {
    private var minPos = CubePosND(n, List(n, { Int.MAX_VALUE }))
    private var maxPos = CubePosND(n, List(n, { Int.MIN_VALUE }))
}

class Cube3D {
    private var minPos : CubePos3D? = null
    private var maxPos : CubePos3D? = null
    fun adjustTo(p: ICubePos) {
        p as CubePos3D
        minPos = p.compose(minPos, ::min)
        maxPos = p.compose(minPos, ::max)
    }

    fun forEach(action: (ICubePos) -> Unit) = minPos!!.forEach(maxPos!!) { action(it) }
    fun toString(posStr: (ICubePos) -> String) = ((minPos!!.z..maxPos!!.z)
        .joinToString("\n") { z ->
            "\nz:$z (${minPos!!.x}:${minPos!!.y} to ${maxPos!!.x}:${maxPos!!.y})\n" + layerToString(z, posStr)
        })

    private fun layerToString(z: Int, posStr: (ICubePos) -> String) = (minPos!!.y..maxPos!!.y)
        .joinToString("\n") { y ->
            (minPos!!.x..maxPos!!.x).joinToString("") { x -> posStr(CubePos3D(x, y, z)) }
        }
}


class Cube {
    val activePositions = mutableSetOf<ICubePos>()
    private var area = Cube3D()

    operator fun get(p: ICubePos) = if (activePositions.contains(p)) ACTIVE else INACTIVE
    operator fun set(p: ICubePos, s: CubeState) { if (s == ACTIVE) setActive(p) }

    private fun setActive(p: ICubePos) {
        activePositions.add(p)
        area.adjustTo(p)
    }

    override fun toString() = area.toString { this[it].toString() }

    fun convert() = Cube().also { area.forEach { p -> it[p] = convertPosition(p) } }

    private fun enabledNeighbours(position: ICubePos) = position.neighbourgs().filter { activePositions.contains(it) }

    private fun convertPosition(position: ICubePos) = when (this[position]) {
        ACTIVE -> if (enabledNeighbours(position).size in (2..3)) ACTIVE else INACTIVE
        INACTIVE -> if (enabledNeighbours(position).size == 3) ACTIVE else INACTIVE
    }
}
