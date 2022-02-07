import java.io.File
import kotlin.test.assertEquals

fun main() {
    day14()
}

fun day14() {

    val res = File("Day14Input.txt").useLines { l ->
        var mask: BitMaskHolder? = null
        l.filter { it.isNotBlank() }
            .map { it.split("=").map { it.trim() } }
            .filter { (cmd, value) ->
                if (cmd.startsWith("mask")) {
                    mask = value.toBitmask(); println("mask = $mask"); false
                } else true
            }
            .map { (instr, value) ->
                "mem\\[(\\d+)".toRegex().find(instr)?.destructured!!.component1().toInt() to value.toLong()
            }
            .map { (addr, value) ->
                val newVal = mask!!.apply(value)
                println("mem[$addr] : $value -> $newVal")
                addr to newVal
            }
            .toMap()
    }

    assertEquals(13496669152158, res.values.sum())
}

fun String.toBitmask() = BitMaskHolder().also {
    this.reversed().mapIndexed { i, v -> i to v.toString().toIntOrNull() }
        .filter { it.second != null }
        .map { (i, v) -> it.set(i, BitMask(i, v == 1)) }
}

class BitMaskHolder {
    private val v = mutableMapOf<Int, BitMask>()
    operator fun set(bitPos: Int, bitMask: BitMask) {
        v.put(bitPos, bitMask)
    }

    operator fun get(bitPos: Int) = v.get(bitPos)
    fun apply(value: Long) = v.values.fold(value) { acc, v -> v.apply(acc) }

    override fun toString() = generateSequence(34) { it - 1 }.takeWhile { it >= 0 }
        .map {
            v[it]?.let { if (it.bitVal) "1" else "0" } ?: "X"
        }.joinToString("")
}


data class BitMask(val bitPos: Int, val bitVal: Boolean) {
    private val mask = if (bitVal) 1L shl bitPos else (1L shl bitPos).inv()
    fun apply(value: Long) = if (bitVal) value or mask else value and mask
}

