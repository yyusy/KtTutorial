import java.io.File
import SeatState.*
import kotlin.test.assertEquals

fun main() {
    day11()
}

fun day11() {
    var nextStep = File("Day11Input.txt").useLines { l ->
        l.filter { it.isNotBlank() }.map { it.mapNotNull { it.toSeatState() } }.toList()
    }

    var prevStep: List<List<SeatState>>
    var step = 1
    do {
        prevStep = nextStep
        nextStep = prevStep.process { x, y -> convertPosition(y..x) }
        println("Step : ${step++}")
        nextStep.println()
    } while (nextStep != prevStep)
    val occupied = nextStep.sumOf { it.count { it == TAKEN } }
    assertEquals(2211, occupied)
    println("Occupied $occupied")
}


enum class SeatState(val code: Char) {
    TAKEN('#'),
    FLOOR('.'),
    EMPTY('L');

    override fun toString(): String {
        return code.toString()
    }
}

fun Char.toSeatState() = when (this) {
    TAKEN.code -> TAKEN
    FLOOR.code -> FLOOR
    EMPTY.code -> EMPTY
    else -> null
}

inline operator fun List<List<SeatState>>.get(position: IntRange) = this[position.first][position.last]
inline fun List<List<SeatState>>.println() {
    this.forEach { println(it.joinToString("")) }
}

fun List<List<SeatState>>.adjucent(position: IntRange): List<SeatState> {
    val firstMaxIndex = this.lastIndex
    val lastMaxIndex = this[0].lastIndex
    return listOf(
        position.first - 1..position.last,
        position.first - 1..position.last - 1,
        position.first - 1..position.last + 1,
        position.first..position.last - 1,
        position.first..position.last + 1,
        position.first + 1..position.last,
        position.first + 1..position.last - 1,
        position.first + 1..position.last + 1,
    )
        .filter { it.first in (0..firstMaxIndex) && it.last in (0..lastMaxIndex) }
        .map { this[it] }
}

fun List<List<SeatState>>.convertPosition(position: IntRange) = when (this[position]) {
    FLOOR -> FLOOR
    EMPTY -> if (adjucent(position).count { it == TAKEN } == 0) TAKEN else EMPTY
    TAKEN -> if (adjucent(position).count { it == TAKEN } >= 4) EMPTY else TAKEN

}

fun List<List<SeatState>>.process(positionConverter: List<List<SeatState>>.(x: Int, y: Int) -> SeatState) = this.mapIndexed() { y, it ->
    it.mapIndexed { x, it -> positionConverter(x, y) }
}
