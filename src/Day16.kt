import java.io.File
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val t = measureTime {
        day16()
    }
    println("Executed $t")
}

fun day16() {

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

    val invalidValues = File("Day16Input.txt").useLines { l ->
        var nearbyTicketsSectionFound = false
        l.filter { it.isNotBlank() }
            .dropWhile { nearbyTicketsSectionFound = (it == "nearby tickets:"); !nearbyTicketsSectionFound }
            .drop(1)
            .map { it.split(",").filter { it.isNotBlank() }.map { it.trim().toInt() } }
            .flatMap { t ->
                t.filter { v -> !rules.isValid(v) }
            }.toList()
    }
    println("Invalid values : $invalidValues")
    assertEquals(21081, invalidValues.sum())
}

data class TicketRule(val name: String, val range1: IntRange, val range2: IntRange) {
    fun isValid(v: Int) = v in range1 || v in range2
}

fun List<TicketRule>.isValid(v: Int) = this.count { it.isValid(v) } > 0


fun String.toTicketRule() = this
    .split(":").takeIf { it.size >= 2 }
    ?.let { (name, ranges) ->
        ranges.split("or")
            .map { rstr -> rstr.split("-").map { it.trim().toInt() } }
            .map { (i1, i2) -> i1..i2 }
            .let { (r1, r2) -> TicketRule(name.trim(), r1, r2) }
    } ?: throw IllegalArgumentException("$this cannot be converted to TicketRule")

