import java.io.File
import kotlin.test.assertEquals

fun main() {
    day13Part2()
}

fun day13Part2() {
    val l = File("Day13Input.txt").readLines()
    val timeTable = l[1].split(",")
        .mapIndexed { i, v -> i to v }
        .filter { it.second.toLongOrNull() != null }
        .associate { (i, v) -> i to v.toLong() }
    println(timeTable)
    val res = timeTable.findDenominator()

    println("Result : $res")
    assertEquals(1001569619313439, res)
}

fun <T : Number> findDenomSeq(p1: Pair<Int, T>, p2: Sequence<Long>) = p2
    .filter { (it + p1.first) % p1.second.toLong() == 0L }
    .zipWithNext()
    .first()
    .let { i -> generateSequence(i.first) { it + (i.second - i.first) } }

fun <T : Number> Map<Int, T>.findDenominator(): Long {
    val firstSequence = generateSequence(this[0]!!.toLong()) { it + this[0]!!.toLong() }
    return this.entries.drop(1)
        .fold(firstSequence) { acc, e -> findDenomSeq(e.toPair(), acc) }
        .first()
}