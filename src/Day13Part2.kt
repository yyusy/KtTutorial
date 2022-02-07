import java.io.File
import kotlin.test.assertEquals

fun main() {
    day13Part2()
}

fun day13Part2() {
    val l = File("Day13Input.txt").readLines()
    val timeTable = l[1].split(",")
        .mapIndexed { i, v -> i to v.toIntOrNull() }
        .filter { it.second != null }.map { it as Pair<Int, Int> }
        .toMap()
    println(timeTable)
    val res = timeTable.findDenominator()

    println("Result : $res")
    assertEquals(1001569619313439, res)
}

fun <T : Number> findDenomSeq(p1: Pair<Int, T>, p2: Sequence<Long>) = p2
    .map { it to (it + p1.first) % p1.second.toLong() }
    .filter { it.second == 0L }
    .map { it.first }
    .zipWithNext()
    .onEach { println("$it : ${it.second - it.first}") }
    .first()
    .let { i -> generateSequence(i.first) { it + (i.second - i.first) } }

fun <T : Number> Map<Int, T>.findDenominator(): Long {
    val firstPeriod = this[0]!!.toLong()
    return this.entries.drop(1)
        .fold(
            generateSequence(firstPeriod) { it + firstPeriod }
        )
        { acc, e ->
            findDenomSeq(e.toPair(), acc)
        }
        .first()
}