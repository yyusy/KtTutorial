import java.io.File
import kotlin.test.assertEquals

fun main() {
    day14()
}

fun day14() {

    val res = File("Day14Input.txt").useLines { l ->
        var mask: BitMask? = null
        l.filter { it.isNotBlank() }
            .filter { cmd ->
                if (cmd.startsWith("mask")) {
                    mask = cmd.toBitmask(); println("mask = $mask"); false
                } else true
            }
            .map { cmd -> cmd.toMemInstruction() }
            .map { it.applyToValue(mask!!) }
            .toMap()
    }

    assertEquals(13496669152158, res.values.sum())
}

data class MemInstruction(val address: Long, val value: Long) {
    fun applyToValue(mask: BitMask) = mask.applyToValue(value)
        .let {
            println("mem[$address] : $value  -> $it")
            address to it
        }
}

fun String.toMemInstruction() = this.split("=")
    .let { (instr, value) ->
        MemInstruction(
            "mem\\[(\\d+)".toRegex().find(instr)?.destructured!!.component1().toLong(),
            value.trim().toLong()
        )
    }

fun String.toBitmask() = this.split("=").let { (_, mask) ->
    BitMask().also {
        mask.reversed().mapIndexed { i, v -> i to v.toString().toIntOrNull() }
            .filter { it.second != null }
            .map { (i, v) -> it.set(i, BitMaskEntry(i, v == 1)) }
    }
}

class BitMask {
    private val v = mutableMapOf<Int, BitMaskEntry>()
    operator fun set(bitPos: Int, bitMask: BitMaskEntry) {
        v.put(bitPos, bitMask)
    }

    operator fun get(bitPos: Int) = v.get(bitPos)
    fun applyToValue(value: Long) = v.values.fold(value) { acc, v -> v.apply(acc) }
    fun applyToAddress(address: Long) = v.values.fold(address) { acc, v ->
        v.applyToAddress(acc)
    }.let { addr ->
        (0..35).minus(v.keys).fold(listOf(addr)) { al, p ->
            al.flatMap { a ->
                listOf(
                    BitMaskEntry(p, true).apply(a)
                        .also { println("set bit $p : ${a.toString(2)} -> ${it.toString(2)}") },
                    BitMaskEntry(p, false).apply(a)
                        .also { println("drop bit $p : ${a.toString(2)} -> ${it.toString(2)}") }
                )
            }
        }
    }

    override fun toString() = (34 downTo 0)
        .map { v[it]?.let { if (it.bitVal) "1" else "0" } ?: "X" }
        .joinToString("")
}


data class BitMaskEntry(val bitPos: Int, val bitVal: Boolean) {
    private val mask = if (bitVal) 1L shl bitPos else (1L shl bitPos).inv()
    fun apply(value: Long) = if (bitVal) value or mask else value and mask
    fun applyToAddress(value: Long) = if (bitVal) value or mask else value
}


