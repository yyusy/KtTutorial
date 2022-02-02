import NavInstruction.*
import java.io.File
import kotlin.math.absoluteValue
import kotlin.test.assertEquals

fun main() {
    day12Part2()
}

fun day12Part2() {
    val input = File("Day12Input.txt").useLines { l ->
        l.filter { it.isNotBlank() }
            .map { NavCommand(it.substring(0..0).toNavInstruction(), it.substring(1).toInt()) }
            .toList()
    }

    val ship = input.navigate(Point(10, 1), Point(0, 0))
    println("Result : $ship")
    assertEquals(42013, ship.x.absoluteValue + ship.y.absoluteValue)
}

class NavigatorWaypoint(var wayPoint: Point) {
    fun navigate(c: NavCommand, ship: Point): Point {
        var nextShip = ship
        when (c.instr) {
            FORWARD -> nextShip = Point(ship.x + wayPoint.x * c.param, ship.y + wayPoint.y * c.param)
            NORTH -> wayPoint = wayPoint.copy(y = wayPoint.y + c.param)
            SOUTH -> wayPoint = wayPoint.copy(y = wayPoint.y - c.param)
            EAST -> wayPoint = wayPoint.copy(x = wayPoint.x + c.param)
            WEST -> wayPoint = wayPoint.copy(x = wayPoint.x - c.param)
            RIGHT -> wayPoint = wayPoint.rotateClockWise(c.param)
            LEFT -> wayPoint = wayPoint.rotateClockWise(-c.param)
        }
        return nextShip
    }

    override fun toString(): String {
        return "nav(wayPoint=$wayPoint)"
    }
}

fun List<NavCommand>.navigate(wayPoint: Point, shipPoint: Point): Point {
    var next = shipPoint
    val navigator = NavigatorWaypoint(wayPoint)
    this.forEach { next = navigator.navigate(it, next); println("$it -> $navigator, ship=$next") }
    return next
}