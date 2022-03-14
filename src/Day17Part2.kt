import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val t = measureTime {
        day17Part2()
    }
    println("Executed $t")
}

fun day17Part2() {
    var nextStep = File("Day17Input.txt").readLines().toCubeND(4)
    println("Input : $nextStep")
    repeat(6) {
        nextStep = nextStep.convert()
    }
    println("Result : $nextStep")
    assertEquals(1812, nextStep.activePositions.size)
}


fun List<String>.toCubeND(n: Int) = Cube(CubeND(n)).also { c ->
    this.forEachIndexed { y, l ->
        l.forEachIndexed { x, ch ->
            c[CubePosND(n, List(n) {
                when (it) {
                    0 -> x
                    1 -> y
                    else -> 0
                }
            })] = ch.toCubeState()
        }
    }
}


data class CubePosND(val n: Int, val coord: List<Int> = mutableListOf()) : ICubePos<CubePosND> {
    constructor (x: Int, y: Int, z: Int) : this(3, listOf(x, y, z))

    override val x: Int
        get() = coord[0]
    override val y: Int
        get() = coord[1]
    override val z: Int
        get() = coord[2]

    override fun compose(p: CubePosND?, f: (v1: Int, v2: Int) -> Int): CubePosND {
        return p?.let {
            (0 until n).map { f(coord[it], p.coord[it]) }.toList().let { CubePosND(n, it) }
        } ?: this
    }

    override fun neighbourgs(margin: Int): List<CubePosND> = buildList {
        (this@CubePosND - margin).forEach(this@CubePosND + margin) { if (it != this@CubePosND) add(it) }
    }


    override fun forEach(to: CubePosND, action: (p: CubePosND) -> Unit) {
        fun forEachLayer(i: Int, p: List<Int>) {
            if (i == n) action(CubePosND(n, p))
            else (coord[i]..to.coord[i]).forEach { forEachLayer(i + 1, p + it) }
        }
        forEachLayer(0, emptyList())
    }

    operator fun plus(i: Int) = CubePosND(n, coord.map { it + i })
    operator fun minus(i: Int) = plus(-i)
}

class CubeND(val n: Int) : ICube<CubePosND> {
    private var minPos: CubePosND? = null
    private var maxPos: CubePosND? = null
    override fun adjustTo(p: CubePosND) {
        minPos = p.compose(minPos, ::min)
        maxPos = p.compose(maxPos, ::max)
    }

    override fun forEach(margin: Int, action: (CubePosND) -> Unit) {
        (minPos!! - margin).forEach(maxPos!! + margin) { action(it) }
    }

    override fun toString(posStr: (CubePosND) -> String): String {
        val c = MutableList(n) { -1 }
        var ret = ""
        fun applyLevel(i: Int) {
            if (i <= 1) ret += "(${minPos!!.x}:${minPos!!.y} to ${maxPos!!.x}:${maxPos!!.y})\n" +
                    layerToString(c, posStr) +
                    "\n"
            else (minPos!!.coord[i]..maxPos!!.coord[i]).forEach { c[i] = it; ret += "z$i:$it "; applyLevel(i - 1) }
        }
        applyLevel(n - 1)
        return ret
    }

    private fun layerToString(c: MutableList<Int>, posStr: (CubePosND) -> String) = (minPos!!.y..maxPos!!.y)
        .joinToString("\n") { y ->
            (minPos!!.x..maxPos!!.x).joinToString("") { x -> c[0] = x; c[1] = y; posStr(CubePosND(c.size, c)) }
        }

    override fun new() = CubeND(this.n)
}

