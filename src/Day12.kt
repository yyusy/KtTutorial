import java.io.File
import kotlin.math.absoluteValue
import kotlin.test.assertEquals
import NavInstruction.*
import kotlin.math.roundToInt

fun main() {
    day12()
}

fun day12() {
    val input = File("Day12Input.txt").useLines { l ->
        l.filter { it.isNotBlank() }
            .map { NavCommand(it.substring(0..0).toNavInstruction(), it.substring(1).toInt()) }
            .toList()
    }
    val start = NavigationPoint(Point(0, 0), Direction.EASTWARD)
    val navigator = Navigator()
    var next = start
    input.forEach { next = navigator.navigate(it, next); println("$it -> $next") }
    println("Result : $next")
    assertEquals(590, next.p.x.absoluteValue + start.p.y.absoluteValue)
}

enum class Direction(private val gradus: Int) {
    NORTHWARD(0),
    EASTWARD(90),
    SOUTHWARD(180),
    WESTWARD(270);

    operator fun minus(turn: Int) = plus(turn * -1)

    operator fun plus(turn: Int): Direction {
        if (turn.absoluteValue % 360 !in (enumValues<Direction>().map { it.gradus }))
            throw IllegalArgumentException("gradus $turn shall be in ${enumValues<Direction>().map { it.gradus }}")
        var res = (this.gradus + turn) % 360
        if (res < 0) res += 360
        return enumValues<Direction>().find { it.gradus == res }
            ?: throw IllegalArgumentException("Unknown direction $res")
    }

}

data class NavigationPoint(var p: Point, var d: Direction)

enum class NavInstruction(val code: String) {
    FORWARD("F"),
    NORTH("N"),
    SOUTH("S"),
    EAST("E"),
    WEST("W"),
    RIGHT("R"),
    LEFT("L")
}

data class NavCommand(val instr: NavInstruction, val param: Int)
class Navigator {
    fun navigate(c: NavCommand, np: NavigationPoint) = when (c.instr) {
        FORWARD -> np.apply {
            p = when (d) {
                Direction.NORTHWARD -> p.copy(y = p.y - c.param)
                Direction.SOUTHWARD -> p.copy(y = p.y + c.param)
                Direction.EASTWARD -> p.copy(x = p.x + c.param)
                Direction.WESTWARD -> p.copy(x = p.x - c.param)
            }
        }
        NORTH -> np.apply { p = Point(np.p.x, np.p.y - c.param) }
        SOUTH -> np.apply { p = Point(np.p.x, np.p.y + c.param) }
        EAST -> np.apply { p = Point(np.p.x + c.param, np.p.y) }
        WEST -> np.apply { p = Point(p.x - c.param, p.y) }
        RIGHT -> np.apply { d += c.param }
        LEFT -> np.apply { d -= c.param }
    }
}

fun Point.rotateClockWise(degrees: Int) = with(this) {
    Point(
        (x * Math.cos(Math.toRadians(-degrees.toDouble())) - y * Math.sin(Math.toRadians(-degrees.toDouble()))).roundToInt(),
        (x * Math.sin(Math.toRadians(-degrees.toDouble())) + y * Math.cos(Math.toRadians(-degrees.toDouble()))).roundToInt()
    )
}

fun String.toNavInstruction() =
    enumValues<NavInstruction>().find { it.code == this } ?: throw IllegalArgumentException("Code $this is invalid")


