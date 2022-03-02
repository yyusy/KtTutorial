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
        l.forEachIndexed { x, ch -> c[CubePos(x, y, 0)] = ch.toCubeState() }
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

data class CubePos(val x: Int, val y: Int, val z: Int)

class Cube {
    val activePositions = mutableSetOf<CubePos>()
    private var minPos = CubePos(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
    private var maxPos = CubePos(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)


    operator fun get(p: CubePos) = if (activePositions.contains(p)) ACTIVE else INACTIVE
    operator fun set(p: CubePos, s: CubeState) {
        if (s == ACTIVE) setActive(p)
    }

    private fun setActive(p: CubePos) {
        activePositions.add(p)
        minPos = CubePos(min(minPos.x, p.x), min(minPos.y, p.y), min(minPos.z, p.z))
        maxPos = CubePos(max(maxPos.x, p.x), max(maxPos.y, p.y), max(maxPos.z, p.z))
    }

    override fun toString() = "$activePositions\n" + ((minPos.z..maxPos.z)
        .joinToString("\n") { z ->
            "\nz:$z (${minPos.x}:${minPos.y} to ${maxPos.x}:${maxPos.y})\n" + layerToString(z)
        })

    private fun layerToString(z: Int) = (minPos.y..maxPos.y)
        .joinToString("\n") { y ->
            (minPos.x..maxPos.x).joinToString("") { x -> this[CubePos(x, y, z)].toString() }
        }

    fun convert(): Cube {
        val ret = Cube()
        (minPos.z - 1..maxPos.z + 1).forEach { z ->
            (minPos.y - 1..maxPos.y + 1).forEach { y ->
                (minPos.x - 1..maxPos.x + 1).forEach { x ->
                    ret[CubePos(x, y, z)] = convertPosition(CubePos(x, y, z))
                }
            }
        }
        return ret
    }

    private fun enabledNeighbours(position: CubePos): List<CubePos> {
        return (-1..1).map { with(position) { CubePos(x + it, y, z) } }
            .flatMap { p -> (-1..1).map { with(p) { CubePos(x, y + it, z) } } }
            .flatMap { p -> (-1..1).map { with(p) { CubePos(x, y, z + it) } } }
            .filter { it != position }
            .filter { activePositions.contains(it) }
    }

    private fun convertPosition(position: CubePos) = when (this[position]) {
        ACTIVE -> if (enabledNeighbours(position).size in (2..3)) ACTIVE else INACTIVE
        INACTIVE -> if (enabledNeighbours(position).size == 3) ACTIVE else INACTIVE
    }
}