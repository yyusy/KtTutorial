import java.io.File
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val t = measureTime {
        day19Part2()
    }
    println("Executed $t")
}

fun day19Part2() {
    val r2 = RulesRegistry()
    File("Day19Input.txt").useLines { l ->
        l.takeWhile { it.isNotBlank() }
            .map { it.split(":") }
            .map { (k, v) -> r2.importRule(k.trim(), v.trim()) }
            .forEach { ; }
    }
    r2.importRule("8", "42 | 42 8")
    r2.importRule("11", "42 31 | 42 11 31")
    println(r2)
    val c = File("Day19Input.txt").useLines { l ->
        l.dropWhile { it.isNotBlank() }
            .filter { it.isNotBlank() }
            .count { r2["0"].matches(it) }

    }
    assertEquals(405, c)
}
