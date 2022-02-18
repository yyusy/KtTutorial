import java.io.File
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val t = measureTime {
        day16Part2()
    }
    println("Executed $t")
}

fun day16Part2() {

    val rules = File("Day16Input.txt").useLines { l ->
        var otherSectionFound = false
        l.filter { it.isNotBlank() }
            .takeWhile { otherSectionFound = (it == "nearby tickets:" || it == "your ticket:"); !otherSectionFound }
            .map { rs ->
                rs.trim().toTicketRule()
            }
            .toList()
    }
    println("Rules : $rules")
    var tickets: List<List<Int>>? = null
    val rulesByColumn = File("Day16Input.txt").useLines { l ->
        val x = rules.associateWith { mutableListOf<Int>() }
        var nearbyTicketsSectionFound = false
        tickets = l.filter { it.isNotBlank() }
            .dropWhile { nearbyTicketsSectionFound = (it == "nearby tickets:"); !nearbyTicketsSectionFound }
            .drop(1)
            .map { it.split(",").filter { it.isNotBlank() }.map { it.trim().toInt() } }
            .filter { t -> t.count { v -> !rules.isValid(v) } == 0 }
            .onEach { t ->
                t.forEachIndexed { i, v -> rules.filter { !it.isValid(v) }.forEach { x[it]!!.add(i) } }
            }.toList()
        println(x)
        val taken = mutableListOf<Int>()
        x.mapValues { v -> ((0..rules.lastIndex) - v.value) }
            .asSequence().sortedBy { it.value.size }
            .map { val take = (it.value - taken).first(); taken.add(take); it.key to take }
            .toMap()
    }

    rulesByColumn.asSequence().sortedBy { it.value }.forEach { println("${it.value} : ${it.key}") }
    assertEquals(0, ((0..(rulesByColumn.size - 1)) - rulesByColumn.map { it.value }).size)
    //validate
    tickets!!.forEach { t ->
        rulesByColumn.forEach { r ->
            assertEquals(true, r.key.isValid(t[r.value]))
        }
    }
    val ticket = File("Day16Input.txt").useLines { l ->
        var yourTicketSectionFound = false
        l.dropWhile { yourTicketSectionFound = (it == "your ticket:"); !yourTicketSectionFound }
            .drop(1)
            .filter { it.isNotBlank() }
            .first()
            .let {
                it.split(",").filter { it.isNotBlank() }.map { it.trim().toInt() }
            }
    }
    println("Ticket : $ticket")
    val ret =
        rulesByColumn.filter { it.key.name.startsWith("departure") }
            .map { r -> ticket[r.value].also { println("$r : $it") } }.fold(1L) { a, i -> a * i }
    assertEquals(314360510573L, ret)
}
