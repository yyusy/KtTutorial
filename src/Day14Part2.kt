import java.io.File
import kotlin.test.assertEquals

fun main() {
    day14Part2()
}

fun day14Part2() {

    val res = File("Day14Input.txt").useLines { l ->
        var mask: BitMask? = null
        l.filter { it.isNotBlank() }
            .filter { cmd ->
                if (cmd.startsWith("mask")) {
                    mask = cmd.toBitmask(); println("mask = $mask"); false
                } else true
            }
            .map { cmd -> cmd.toMemInstruction() }
            .fold(mutableMapOf<Long, Long>()) { c, i -> c.putAll(i.applyToAddress(mask!!)); c}
    }

    assertEquals(3278997609887, res.values.sum())
}
