import java.io.File
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val t = measureTime {
        day18()
    }
    println("Executed $t")
}

fun day18() {
    val v = File("Day18Input.txt").useLines { l ->
        l.map { it.splitToLexems().parseExpres() }
            .map { it.evaluate() }
            .toList()
    }
    assertEquals(3348222486398, v.sum())
}

fun String.toOperation() =
    Operation.values().firstOrNull { it.code == this } ?: throw IllegalArgumentException("Illegal operation '$this'")

fun String.toOperationOrNull() =
    Operation.values().firstOrNull { it.code == this }

fun String.isLong() = this.toLongOrNull() != null

sealed class Expression {
    abstract fun evaluate(): Long
}

class ConstLongExpression(val v: Long) : Expression() {
    override fun evaluate() = v
    override fun toString() = v.toString()
}

class BinaryExpression(val leftOp: Expression, val op: Operation, val rightOp: Expression) : Expression() {
    override fun evaluate(): Long {
        val ret = op.apply(leftOp.evaluate(), rightOp.evaluate())
        //println("$leftOp ${op.code} $rightOp = $ret")
        return ret
    }

    override fun toString() = "(${leftOp} ${op.code} ${rightOp})"
}


enum class Operation(val code: String) {
    PLUS("+") {
        override fun apply(op1: Long, op2: Long) = op1 + op2
    },

    MINUS("-") {
        override fun apply(op1: Long, op2: Long) = op1 - op2
    },

    MULT("*") {
        override fun apply(op1: Long, op2: Long) = op1 * op2
    };

    abstract fun apply(op1: Long, op2: Long): Long
}

fun List<String>.findOpening(startPos: Int): Int {
    var p = 1
    return (startPos downTo 0).firstOrNull {
        when (this[it]) {
            "(" -> p--
            ")" -> p++
        }
        p == 0
    } ?: -1
}

fun List<String>.parseExpres(): Expression {
    val input = this
    var opPos = input.lastIndex
    fun parseOperand(input: List<String>): Expression {
        return when {
            input.last().isLong() -> {
                opPos -= 1; ConstLongExpression(input.last().toLong())
            }
            input.last() == ")" -> {
                val i = input.findOpening(input.lastIndex - 1)
                if (i == -1) throw IllegalArgumentException("No opening bracket. Closing bracket at ${input.size}")
                opPos = i - 1
                return input.drop(i + 1).dropLast(1).parseExpres()
            }
            else -> throw IllegalArgumentException("Invalid operand : ${input.last()}")
        }
    }

    val rightOp = parseOperand(input)
    return if (opPos < 0) rightOp
    else BinaryExpression(
        input.take(opPos).parseExpres(),
        input[opPos].toOperation(),
        rightOp
    )
}

fun String.splitToLexems() = Regex("""\d+|\+|-|\*|\(|\)""")
    .findAll(this).filter { it.value.isNotBlank() }.map { it.value }.toList()
