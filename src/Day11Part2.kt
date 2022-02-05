import java.io.File
import SeatState.*
import kotlin.math.absoluteValue
import kotlin.test.assertEquals

fun main() {
    day11Part2()
}

fun day11Part2() {
    val input = File("Day11Input.txt").useLines { l ->
        l.filter { it.isNotBlank() }.map { it.mapNotNull { it.toSeatState() } }.toList()
    }
    val ret = input.process { p -> convertPositionPart2(p) }
    val occupied = ret.sumOf { it.count { it == TAKEN } }
    assertEquals(1995, occupied)
    println("Occupied $occupied")
}

data class Point(val x: Int, val y: Int) {
    val mdistance
        get() = x.absoluteValue + y.absoluteValue

    operator fun plus(another: Point) = Point(x + another.x, y + another.y)

}

data class Area(val x: ClosedRange<Int>, val y: ClosedRange<Int>) {
    operator fun contains(point: Point) = point.x in x && point.y in y
    fun trace(start: Point, step: Point) = Trace(start, step)

    inner class Trace(start: Point, private val step: Point) : Iterator<Point> {
        private var next: Point = start + step
        override fun hasNext() = next in this@Area

        override fun next(): Point {
            if (!hasNext()) throw NoSuchElementException("$next is out of range with ${this@Area}")
            return next.also { next += step }
        }
    }
}

fun List<List<SeatState>>.visible(position: Point, direction: Point) =
    Area(0..this[0].lastIndex, 0..this.lastIndex).trace(position, direction).asSequence()
        .firstOrNull { this[it] != FLOOR }?.let { this[it] }

fun List<List<SeatState>>.visible(position: Point) = listOf(
    Point(-1, 0),
    Point(-1, -1),
    Point(-1, +1),
    Point(0, -1),
    Point(0, +1),
    Point(+1, -1),
    Point(+1, +1),
    Point(+1, 0),
).mapNotNull {
    Area(0..this[0].lastIndex, 0..this.lastIndex)
        .trace(position, it).asSequence()
        .firstOrNull { this[it] != FLOOR }
}.map { this[it] }


operator fun List<List<SeatState>>.get(p: Point) = this[p.y][p.x]

fun List<List<SeatState>>.convertPositionPart2(position: Point) = when (this[position]) {
    FLOOR -> FLOOR
    EMPTY -> if (visible(position).count { it == TAKEN } == 0) TAKEN else EMPTY
    TAKEN -> if (visible(position).count { it == TAKEN } >= 5) EMPTY else TAKEN
}

fun List<List<SeatState>>.process(positionConverter: List<List<SeatState>>.(Point) -> SeatState): List<List<SeatState>> {
    var nextStep = this
    var prevStep: List<List<SeatState>>
    var step = 1
    do {
        prevStep = nextStep
        nextStep = prevStep.process { x, y -> positionConverter(Point(x, y)) }
        println("Step : ${step++}")
        //nextStep.println()
    } while (nextStep != prevStep)
    return nextStep
}
