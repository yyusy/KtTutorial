import java.io.File
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val t = measureTime {
        day18Part2()
    }
    println("Executed $t")
}

fun day18Part2() {
    val v = File("Day18Input.txt").useLines { l ->
        l.map { Parser(it.splitToLexems()).parse() }
            .map { it.evaluate() }
            .toList()
    }
    assertEquals(43423343619505, v.sum())
}

class Parser(val groups: List<String>) {
    var pos = 0
    fun parse(): Expression {
        val result = parseExpression()
        if (pos < groups.size) throw IllegalStateException()
        return result
    }

    private val lowPriorityOperations = setOf(Operation.MULT)

    private fun parseExpression(): Expression {
        var left = parseItem()
        while (pos < groups.size) {
            val op = groups[pos].toOperationOrNull()
            if (op != null && op in lowPriorityOperations) {
                pos++
                left = BinaryExpression(left, op, parseItem())
            } else return left
        }
        return left
    }

    private fun parseItem(): Expression {
        var left = parseFactor()
        while (pos < groups.size) {
            val op = groups[pos].toOperationOrNull()
            if (op != null && op !in lowPriorityOperations) {
                pos++
                left = BinaryExpression(left, op, parseFactor())
            } else return left
        }
        return left
    }

    private fun parseFactor(): Expression =
        if (pos >= groups.size) throw IllegalStateException()
        else {
            when (val group = groups[pos++]) {
                "(" -> {
                    val arg = parseExpression()
                    val next = groups[pos++]
                    if (next == ")") arg
                    else throw IllegalStateException()
                }
                else -> ConstLongExpression(group.toLong())
            }
        }

}
