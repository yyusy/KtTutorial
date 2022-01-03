import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

const val BAG_RULES = """
                light red bags contain 1 bright white bag, 2 muted yellow bags.
                dark orange bags contain 3 bright white bags, 4 muted yellow bags.
                bright white bags contain 1 shiny gold bag.
                muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
                shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
                dark olive bags contain 3 faded blue bags, 4 dotted black bags.
                vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
                faded blue bags contain no other bags.
                dotted black bags contain no other bags.
                """
const val BAG_RULES_CONTAIN= """
    shiny gold bags contain 2 dark red bags.
    dark red bags contain 2 dark orange bags.
    dark orange bags contain 2 dark yellow bags.
    dark yellow bags contain 2 dark green bags.
    dark green bags contain 2 dark blue bags.
    dark blue bags contain 2 dark violet bags.
    dark violet bags contain no other bags.
    """

internal class KtTutorialTest {

    @org.junit.jupiter.api.Test
    fun testFindRow() {

        assertEquals(1, (0 + 1 + 1) / 2)
        assertEquals(1, findPos("R", 0..1))
        assertEquals(0, findPos("L", 0..1))
        assertEquals(0, findPos("LL", 0..7))
        assertEquals(2, findPos("LR", 0..7))
        assertEquals(5, findPos("RLR", 0..7))
        assertEquals(44, findPos("FBFBBFF", 0..127))
        assertEquals(4, findPos("FFFFBFF", 0..127))
        assertEquals(5, findPos("FFFFBFB", 0..127))
        assertEquals(112, findPos("BBBFFFF", 0..127))
        assertEquals(113, findPos("BBBFFFB", 0..127))
        assertEquals("BBBFFFB", "BBBFFFBRRR".substring(0, 7))
        assertEquals("RRR", "BBBFFFBRRR".substring(7))
        assertEquals(112, findPos("BBBFFFFRRR".substring(0, 7), 0..127))
        assertEquals(113, findPos("BBBFFFBRRR".substring(0, 7), 0..127))
    }

    @Test
    fun testMergingStringsDelimitedByCrLf() {
        val str = """ 
            a
            a
            a
            
            bbb
            bbb
        """.trimIndent()
        val r = str.splitToSequence(Regex("\n\\s*\n")).map { it.split("\n").joinToString("") }
        assertEquals(listOf("aaa", "bbbbbb"), r.toList())
    }

    @Test
    fun testLinesToGroups() {
        val str = sequenceOf(
            "a",
            "a",
            "a",
            "",
            "bbb",
            "bbb",
        )
        var pos = 0
        val r = str.groupingBy { s -> pos.also { if (s.isEmpty()) pos += 1 } }
            .aggregate { _, accumulator: StringBuffer?, element, first ->
                if (first) StringBuffer().append(element) else accumulator!!.append(element)
            }.map { it.value.toString() }.toList()
        assertEquals(listOf("aaa", "bbbbbb"), r)

    }

    @Test
    fun testUnion() {
        assertEquals("bc".toSet(), "abc".toSet().intersect("bc".toSet()))
        assertEquals(emptySet<Char>(), "abc".toSet().intersect("de".toSet()))

    }

    @Test
    fun testFindWayToEnd() {
        val program = program()
        assertEquals(8, findWayToEnd(program))
    }

    @Test
    fun testProgramPath() {
        val program = program()
        assertEquals(8, findWayToInstruction(program, 8, 9))
        assertEquals(-1, findWayToInstruction(program, 0, 9))
        assertEquals(0, findWayToInstruction(program, 0, 7))
        assertEquals(-1, findWayToInstruction(program, 9, 0, true))

    }

    @Test
    fun testProgramFix() {
        val program = program().toMutableList()
        assertEquals(7, findFixForProgram(program))

        println(program)
    }


    @Test
    fun testBags() {
        val rulesString = BAG_RULES

        val rules = rulesString.splitToSequence("\n").associate { it.toBagRule() }

        assertEquals(9, rules.size)
        assertTrue(rules.containsKey("dotted black"))
        assertEquals(0, rules["dotted black"]!!.size)
        assertEquals(2, rules["light red"]!!.size)
        assertEquals(1, rules["bright white"]!!.size)
        assertEquals(1, rules["bright white"]!![0].second)
        assertEquals(9, rules["muted yellow"]!![1].second)
        assertEquals("faded blue", rules["muted yellow"]!![1].first)
    }


    @Test
    fun testBagsGraph() {
        val rules = BAG_RULES.trimIndent().lines().associate { it.toBagRule() }
        val g = rules.toGraph()
        println(g)

        val fromBag = "shiny gold"
        rules.filter { it.key != fromBag }.count {
            val p = g.findWay(g[fromBag]!!, g[it.key]!!, true)
            println("$fromBag -> ${it.key} : $p")
            p?.isNotEmpty() ?: false
        }.also { println(it) }
    }
    @Test
    fun testBagsContained() {
        val rules = BAG_RULES_CONTAIN.trimIndent().lines().associate { it.toBagRule() }
        val g = rules.toGraph()
        println(g)
        val bag = "shiny gold"
        val visited = mutableSetOf<Vertex<String>>()
        val p = g.findWay(g[bag]!!, Vertex("non-reachable"), visited)
        assertTrue(p.isEmpty())
        val content = visited.map { it.key }.filter{ it != bag }.distinct()
        assertEquals(6, content.count())
        TODO("Implement tree and aggregate on the path back")
    }
    @Test
    fun testBagsGraphVisited() {
        val rules = BAG_RULES.trimIndent().lines().associate { it.toBagRule() }
        val g = rules.toGraph()
        println(g)

        val fromBag = "shiny gold"
        val visited = mutableSetOf<Vertex<String>>()
        val p = g.findWay(g[fromBag]!!, Vertex("non-reachable"), visited, true)
        assertTrue(p.isEmpty())
        assertEquals(4, visited.map { it.key }.filter{ it != fromBag }.distinct().count())
    }

    @Test
    fun testRegexp() {
        val ruleRegexp = "(\\d+) ([\\w\\s]+) bags?".toRegex()
        val rule = "3 bright white bags, 4 muted yellow bags."
        val l = ruleRegexp.findAll(rule).map { it.destructured.toList() }.toList()
        assertEquals(2, l.size)
        println(l)

    }


    private fun program(): List<Instruction> {
        return listOf(
                "nop +0".toInstruction(),
                "acc +1".toInstruction(),
                "jmp +4".toInstruction(),
                "acc +3".toInstruction(),
                "jmp -3".toInstruction(),
                "acc -99".toInstruction(),
                "acc +1".toInstruction(),
                "jmp -4".toInstruction(),
                "acc +6".toInstruction(),
            )

    }
}

fun findWayToEnd(program: List<Instruction>): Int {
    val visited = sortedSetOf<Int>()
    var startAddress = 0
    val endOfProgram = program.size
    while (startAddress in program.indices) {
        val ctx = ExecutionCtx(startAddress)
        if (program.execute(ctx) != endOfProgram) {
            visited.addAll(ctx.trace.values)
            visited.add(startAddress)
            println("Visited : $visited")
            startAddress = (program.indices).asSequence().minus(visited).minOrNull() ?: -1
        } else {
            break
        }
        println("Try next address : $startAddress")
    }
    return startAddress
}

fun findWayToInstruction(program: List<Instruction>, start: Int, end: Int, backward: Boolean = false): Int {
    val g = program.toGraph()
    println(g)
    val v = mutableSetOf<Vertex<Int>>()
    val r = g.findWay(g[start]!!, g[end]!!, v, backward)
    println("Path: $r, visited :$v")
    return r.minOfOrNull { it.key } ?: -1
}
