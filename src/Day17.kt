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
    var nextStep = File("Day17Input.txt").readLines().toCube3D()
    println("Input : $nextStep")
    repeat(6) {
        nextStep = nextStep.convert()
    }
    println("Result : $nextStep")
    assertEquals(267, nextStep.activePositions.size)
}

fun List<String>.toCube3D() = Cube(Cube3D()).also { c ->
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

interface ICubePos<T : ICubePos<T>> {
    val x: Int
    val y: Int
    val z: Int
    fun compose(p: T?, f: (v1: Int, v2: Int) -> Int): T
    fun neighbourgs(margin: Int): List<T>
    fun forEach(to: T, action: (p: T) -> Unit)
}

data class CubePos3D(override val x: Int, override val y: Int, override val z: Int) : ICubePos<CubePos3D> {
    override fun compose(p: CubePos3D?, f: (v1: Int, v2: Int) -> Int) =
        p?.let { CubePos3D(f(x, p.x), f(y, p.y), f(z, p.z)) } ?: this

    override fun neighbourgs(margin: Int) = (-margin..margin).map { CubePos3D(x + it, y, z) }
        .flatMap { p -> (-margin..margin).map { with(p) { CubePos3D(x, y + it, z) } } }
        .flatMap { p -> (-margin..margin).map { with(p) { CubePos3D(x, y, z + it) } } }
        .filter { it != this }
        .toList()

    override fun forEach(to: CubePos3D, action: (p: CubePos3D) -> Unit) {
        (this.z..to.z).forEach { z ->
            (this.y..to.y).forEach { y ->
                (this.x..to.x).forEach { x -> action(CubePos3D(x, y, z)) }
            }
        }
    }

    operator fun plus(i: Int) = CubePos3D(x + i, y + i, z + i)
    operator fun minus(i: Int) = plus(-i)
}

interface ICube<T : ICubePos<T>> {
    fun adjustTo(p: T)
    fun forEach(margin: Int, action: (T) -> Unit)
    fun toString(posStr: (T) -> String): String
    fun new(): ICube<T>
}

class Cube3D : ICube<CubePos3D> {
    private var minPos: CubePos3D? = null
    private var maxPos: CubePos3D? = null
    override fun adjustTo(p: CubePos3D) {
        minPos = p.compose(minPos, ::min)
        maxPos = p.compose(maxPos, ::max)
    }

    override fun forEach(margin: Int, action: (CubePos3D) -> Unit) =
        (minPos!! - margin).forEach(maxPos!! + margin) { action(it) }

    override fun toString(posStr: (CubePos3D) -> String) = ((minPos!!.z..maxPos!!.z)
        .joinToString("\n") { z ->
            "\nz:$z (${minPos!!.x}:${minPos!!.y} to ${maxPos!!.x}:${maxPos!!.y})\n" + layerToString(z, posStr)
        })

    private fun layerToString(z: Int, posStr: (CubePos3D) -> String) = (minPos!!.y..maxPos!!.y)
        .joinToString("\n") { y ->
            (minPos!!.x..maxPos!!.x).joinToString("") { x -> posStr(CubePos3D(x, y, z)) }
        }

    override fun new() = Cube3D()

}

class Cube<T : ICubePos<T>>(private val area: ICube<T>) {
    val activePositions = mutableSetOf<T>()

    operator fun get(p: T) = if (activePositions.contains(p)) ACTIVE else INACTIVE
    operator fun set(p: T, s: CubeState) {
        if (s == ACTIVE) {
            activePositions.add(p)
            area.adjustTo(p)
        }
    }

    override fun toString() = area.toString { this[it].toString() }

    fun convert() = Cube(area.new()).also { area.forEach(margin = 1) { p -> it[p] = convertPosition(p) } }

    private fun enabledNeighbours(position: T) = position.neighbourgs(1).filter { activePositions.contains(it) }

    private fun convertPosition(position: T) = when (this[position]) {
        ACTIVE -> if (enabledNeighbours(position).size in (2..3)) ACTIVE else INACTIVE
        INACTIVE -> if (enabledNeighbours(position).size == 3) ACTIVE else INACTIVE
    }
}
