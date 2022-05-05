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
            .map { (k, v) -> r2.importRule(k.trim(), v.trim()) }
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
    fun importRule(id: String, ruleStr: String): RuleValidator =
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

    fun matches(input: String): Boolean = (findValid(input, 0).any { it.endInclusive == input.length })
    abstract fun findValid(input: String, start: Int): List<IntRange>

    class UnknownRuleValidator(id: String, registry: RulesRegistry) : RuleValidator(id, registry) {
        override fun findValid(input: String, start: Int): List<IntRange> {
            throw IllegalStateException("Unknown rule $id")
        }

        override fun toString() = "$id:unk"
    }

    class ConstValidator(id: String, private val c: Char, registry: RulesRegistry) : RuleValidator(id, registry) {

        override fun findValid(input: String, start: Int) =
            if (start in input.indices && c == input[start]) listOf(IntRange(start, start + 1)) else emptyList()

        override fun toString() = "$id:\"$c\""
    }

    class OrValidator(id: String, val ruleIds: List<String>, registry: RulesRegistry) :
        RuleValidator(id, registry) {

        override fun findValid(input: String, start: Int) =
            ruleIds.map { registry[it] }
                .map { v -> v.findValid(input, start) }
                .flatten()

        override fun toString() = "$id:or(${ruleIds.joinToString { registry[it].toString() }})"
    }

    class CompositeValidator(id: String, val ruleIds: List<String>, registry: RulesRegistry) :
        RuleValidator(id, registry) {

        override fun findValid(input: String, start: Int): List<IntRange> {
            if (start !in input.indices) return emptyList()
            var endList = listOf(start)
            for (id in ruleIds) {
                endList = endList.map { s -> registry[id].findValid(input, s) }.flatten().map { it.last }
                if (endList.isEmpty()) return emptyList()
            }
            //println("Found : $this at $start..$endList")
            return endList.map { start..it }
        }

        override fun toString() = "$id:seq$ruleIds"
    }
}
