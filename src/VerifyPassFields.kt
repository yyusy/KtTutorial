import java.io.File
import kotlin.test.assertEquals

fun main() {
    val fields = mapOf<String, (String) -> Boolean>(
        "pid" to { v -> Regex("\\s*\\d{9}").matches(v) },
        "byr" to { v -> v.toIntOrNull()?.let { it in (1920..2002) } ?: false },
        "iyr" to { v -> v.toIntOrNull()?.let { it in (2010..2020) } ?: false },
        "eyr" to { v -> v.toIntOrNull()?.let { it in (2020..2030) } ?: false },
        "hgt" to { v ->
            Regex("(\\d+)(cm|in)").matchEntire(v)?.let {
                val (hight, notation) = it.destructured
                notation == "cm" && hight.toInt() in 150..193
                        || notation == "in" && hight.toInt() in 59..76
            } == true
        },
        "hcl" to { Regex("#[0-9,a-f]{6}").matches(it) },
        "ecl" to { Regex("amb|blu|brn|gry|grn|hzl|oth").matches(it) },
    )
    val ret = File("Day2Input.txt").useLines { lines ->
        var tmp = ""
        lines.plusElement("")
            .mapNotNull { s ->
                if (s.isBlank()) tmp.also { tmp = "" } else {
                    tmp += " $s"; null
                }
            }
            .map {
                it.split(" ").filter { it.isNotBlank() }
                    .associate { it.split(":").let { (k, v) -> k to v } }
            }
            .count {
                println(it)
                it.keys.containsAll(fields.keys)
                       && it.count { (k, v) -> (fields[k]?.invoke(v) ?: true).not() } == 0
            }
            .also { println("Valid : $it") }
    }
    assertEquals(524, ret)
}



