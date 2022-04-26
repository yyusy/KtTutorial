import java.io.File
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val t = measureTime {
        day19()
    }
    println("Executed $t")
}

fun day19() {
    val r2 = RulesRegistry()
    File("Day19Input.txt").useLines { l ->
        l.takeWhile { it.isNotBlank() }
            .map { it.split(":") }
            .map { (k, v) -> r2.parseToRuleValidator(k.trim(), v.trim()) }
            .forEach { ; }
    }
    println(r2)
    val c = File("Day19Input.txt").useLines { l ->
        l.dropWhile { it.isNotBlank() }
            .filter { it.isNotBlank() }
            .count { r2["0"].matches(it) }

    }
    assertEquals(239, c)
}

data class RulesRegistry(val v: MutableMap<String, RuleValidator> = mutableMapOf()) {
    operator fun get(k: String) = v.getOrDefault(k, RuleValidator.UnknownRuleValidator(k, this))
    operator fun set(k: String, r: RuleValidator) {
        v[k] = r
    }

    operator fun invoke() = v
    fun parseToRuleValidator(id: String, ruleStr: String): RuleValidator =
        if (Regex("""((\s*\d+\s*)+(\|)?)+""").matches(ruleStr)) {
            val x = ruleStr.split("|")
            if (x.size > 1) {
                x.mapIndexed { p, s -> RuleValidator.CompositeValidator("$id-$p", s.toIds(), this);"$id-$p" }
                    .toList()
                    .let { RuleValidator.OrValidator(id, it, this) }
            } else {
                RuleValidator.CompositeValidator(id, ruleStr.toIds(), this)
            }

        } else if (Regex("\"\\w+\"").matches(ruleStr)) {
            val c = ruleStr.replace("\"", "").trim().first()
            RuleValidator.ConstValidator(id, c, this)
        } else {
            throw IllegalArgumentException("Unknown rule : $ruleStr")
        }

    private fun String.toIds() = this.split(" ").filter { it.isNotBlank() }.map { it.trim() }

}

sealed class RuleValidator(val id: String, val registry: RulesRegistry) {
    init {
        if (this is UnknownRuleValidator) registry.v.putIfAbsent(id, this)
        else registry[id] = this
    }

    fun matches(input: String, start: Int = 0): Boolean = (findValid(input, start)?.endInclusive == input.length)
    abstract fun findValid(input: String, start: Int): IntRange?

    class UnknownRuleValidator(id: String, registry: RulesRegistry) : RuleValidator(id, registry) {
        override fun findValid(input: String, start: Int): IntRange? {
            throw IllegalStateException("Unknown rule $id")
        }

        override fun toString() = "$id:unk"
    }

    class ConstValidator(id: String, private val c: Char, registry: RulesRegistry) : RuleValidator(id, registry) {

        override fun findValid(input: String, start: Int) =
            if (start in input.indices && c == input[start]) IntRange(start, start + 1) else null

        override fun toString() = "$id:\"$c\""
    }

    class OrValidator(id: String, val ruleIds: List<String>, registry: RulesRegistry) :
        RuleValidator(id, registry) {

        override fun findValid(input: String, start: Int) =
            ruleIds.map { registry[it] }.firstNotNullOfOrNull { v -> v.findValid(input, start) }

        override fun toString() = "$id:or$ruleIds"
    }

    class CompositeValidator(id: String, val ruleIds: List<String>, registry: RulesRegistry) :
        RuleValidator(id, registry) {

        override fun findValid(input: String, start: Int): IntRange? {
            if (start !in input.indices ) return null
            var end = start
            for (id in ruleIds) {
                end = registry[id].findValid(input, end)?.endInclusive ?: return null
            }
            return IntRange(start, end)
        }

        override fun toString() = "$id:seq$ruleIds"
    }
}
