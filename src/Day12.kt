import java.io.File
import kotlin.math.absoluteValue
import kotlin.test.assertEquals

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
    var next = start
    input.forEach { next = it.instr.navigate(next, it.param); println("$it -> $next") }
    println("Result : $next")
    assertEquals(590, next.p.x.absoluteValue + start.p.y.absoluteValue)
}

enum class Direction(private val gradus: Int) {
    NORTHWARD(0),
    EASTWARD(90),
    SOUTHWARD(180),
    WESTWARD(270);

    operator fun minus(turn: Int)= plus(turn * -1)

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
    FORWARD("F") {
        override fun navigate(np: NavigationPoint, param: Int) = np.apply {
            p = when (d) {
                Direction.NORTHWARD -> p.copy(y = p.y - param)
                Direction.SOUTHWARD -> p.copy(y = p.y + param)
                Direction.EASTWARD -> p.copy(x = p.x + param)
                Direction.WESTWARD -> p.copy(x = p.x - param)
            }
        }
    },
    NORTH("N") {
        override fun navigate(np: NavigationPoint, param: Int) = np.apply { p = Point(np.p.x, np.p.y - param) }
    },
    SOUTH("S") {
        override fun navigate(np: NavigationPoint, param: Int) = np.apply { p = Point(np.p.x, np.p.y + param) }
    },
    EAST("E") {
        override fun navigate(np: NavigationPoint, param: Int) = np.apply { p = Point(np.p.x + param, np.p.y) }

    },
    WEST("W") {
        override fun navigate(np: NavigationPoint, param: Int) = np.apply { p = Point(p.x - param, p.y) }
    },
    RIGHT("R") {
        override fun navigate(np: NavigationPoint, param: Int) = np.apply { d += param }
    },
    LEFT("L") {
        override fun navigate(np: NavigationPoint, param: Int) = np.apply { d -= param }
    };

    abstract fun navigate(np: NavigationPoint, param: Int): NavigationPoint
}

data class NavCommand(val instr: NavInstruction, val param: Int)

fun String.toNavInstruction() =
    enumValues<NavInstruction>().find { it.code == this } ?: throw IllegalArgumentException("Code $this is invalid")


