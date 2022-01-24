import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
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
const val BAG_RULES_CONTAIN = """
    shiny gold bags contain 2 dark red bags.
    dark red bags contain 2 dark orange bags.
    dark orange bags contain 2 dark yellow bags.
    dark yellow bags contain 2 dark green bags.
    dark green bags contain 2 dark blue bags.
    dark blue bags contain 2 dark violet bags.
    dark violet bags contain no other bags.
    """

internal class KtTutorialTest {

    @Test
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
        val r = findWayToInstruction(program, 0, 9)
        assertEquals(-1, r)
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
        val rulesString = BAG_RULES.trimIndent()

        val rules = rulesString.splitToSequence("\n").associate { it.toBagRule() }

        assertEquals(9, rules.size)
        assertTrue(rules.containsKey("dotted black"))
        assertEquals(0, rules["dotted black"]!!.size)
        assertEquals(2, rules["light red"]!!.size)
        assertEquals(1, rules["bright white"]!!.size)
        assertEquals(1, rules["bright white"]!![0].count)
        assertEquals(9, rules["muted yellow"]!![1].count)
        assertEquals("faded blue", rules["muted yellow"]!![1].bagName)
    }

    @Test
    fun testWeightedGraph() {
        val g = Graph<Int>()
        g.connect(1, 1)
        g.connect(1, 2)
        g.connect(2, 3)
        g.connect(1, 2)
        assertEquals(3, g.edges.size)
        println(g)
        //assertTrue(e1to2 === e1to2again)
        val gw = GraphWeighted<Int>()
        gw.connect(1, 2, 25)
        println(g)
        assertEquals(1, gw.edges.size)

    }

    @Test
    fun testBagsGraph() {
        val rules = BAG_RULES.trimIndent().lines().associate { it.toBagRule() }
        val g = rules.toGraph()
        println(g)
        assertEquals(2, g.leafs.size)
        val fromBag = "shiny gold"
        val countPaths = rules.filter { it.key != fromBag }.count {
            val path = g.findPath(g[fromBag], g[it.key], true)
            println("$fromBag -> ${it.key} : ${path}")
            path.isNotEmpty()
        }.also { println(it) }
        assertEquals(4, countPaths)
    }

    @Test
    fun testBagsContained() {
        val rules = BAG_RULES_CONTAIN.trimIndent().lines().associate { it.toBagRule() }
        val g = rules.toGraph()

        println(g)
        assertEquals(1, g.leafs.size)
        val shinyGoldBag = "shiny gold"
        val bagsContent = mutableMapOf<String, Int>()
        val ctx = g.walk(start = g[shinyGoldBag]) { edge ->
            val bagsInChild = bagsContent.getOrDefault(edge.vTo.key, 0)
            val bagsInParent = bagsContent.getOrDefault(edge.vFrom.key, 0)
            bagsContent[edge.vFrom.key] = bagsInParent + edge.weight * (bagsInChild + 1)
            println("$edge : child= $bagsInChild, total =${bagsContent[edge.vFrom.key]}")
        }
        assertEquals(7, ctx.visited.size)
        val content = ctx.visited.map { it.key }.filter { it != shinyGoldBag }.distinct()
        assertEquals(6, content.count())
        println(bagsContent[shinyGoldBag])
    }

    @Test
    fun testBagsGraphVisited() {
        val rules = BAG_RULES.trimIndent().lines().associate { it.toBagRule() }
        val g = rules.toGraph()
        println(g)
        val fromBag = "shiny gold"
        val ctx = g.walkBack(g[fromBag]) {}
        assertEquals(4, ctx.visited.map { it.key }.filter { it != fromBag }.distinct().count())
    }


    @Test
    fun testRegexp() {
        val ruleRegexp = "(\\d+) ([\\w\\s]+) bags?".toRegex()
        val rule = "3 bright white bags, 4 muted yellow bags."
        val l = ruleRegexp.findAll(rule).map { it.destructured.toList() }.toList()
        assertEquals(2, l.size)
        println(l)

    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testDay9() {
        val windowSize = 5
        val inStr = """
            35
            20
            15
            25
            47
            40
            62
            55
            65
            95
            102
            117
            150
            182
            127
            219
            299
            277
            309
            576
            """.trimIndent()
        inStr.lineSequence().map { it.toInt() }.windowed(windowSize + 1) { l ->
            l.last().takeIf { l.take(windowSize).toPairs().count { it.first + it.second == l.last() } > 0 }
        }.filterNotNull().forEach { println(it) }
    }

    class IndexPathResult<V, E : EdgeWeighted<V>>(private val toV: Vertex<V>) : GraphVisitor<V, E>() {
        var index = 0
        override fun registerVisit(edge: E) {
            val ret = super.registerVisit(edge)
            if (edge.vTo == toV) index++
            println("${edge} :$index")
            return ret
        }

        override fun onChildVisited(edge: E) {
            unRegisterVisit(edge)

        }
    }

    @Test
    fun testDay10Part2Walk() {
        val g = buildDay10Graph()
        println(g)
        assertEquals(13, g.vertices.size)
        assertEquals(16, g.edges.size)
        assertEquals(8, g.index(g[0], g[22]))


    }

    @Test
    fun testDay10Part2Index() {
        val g = buildDay10Graph()
        println(g)
        assertEquals(13, g.vertices.size)
        assertEquals(16, g.edges.size)
        val ctx = IndexPathResult(g[22])
        g.walk(g[0], ctx)
        assertEquals(8, ctx.index)

        val indexes = mutableMapOf(g[0] to 1)
        val notCalculated: Deque<Vertex<Int>> = LinkedList()
        notCalculated.add(g[0])


        while (notCalculated.isNotEmpty()) {
            val v = notCalculated.pollFirst()

            if (indexes[v] != null) {
                v.outbound().map { it.vTo }.forEach { if (!notCalculated.contains(it)) notCalculated.offerLast(it) }
                continue
            }
            val vIndex = v.inbound().map { it.vFrom }
                .map { indexes[it] ?: -1 }
                .fold(0) { acc, i -> if (i < 0 || acc < 0) -1 else acc + i }
            if (vIndex < 0) notCalculated.offerLast(v)
            else {

                indexes[v] = vIndex
                v.outbound().map { it.vTo }.forEach {
                    if (!notCalculated.contains(it)) notCalculated.offerLast(it)
                }
            }
            println("$v : calced : ${vIndex >= 0}, index : ${vIndex}, toCalc : ${notCalculated.size}")
        }

        assertEquals(8, indexes[g[22]])
    }

    private fun buildDay10Graph(): GraphWeighted<Int> {
        val input = """
                16
                10
                15
                5
                1
                11
                7
                19
                6
                12
                4
                0
                22
            """.trimIndent()


        val g = input.lineSequence()
            .filter { it.isNotBlank() }
            .map { it.toInt() }
            .toWiredAdaptersGraph()
        return g
    }

    @ExperimentalStdlibApi
    @Test
    fun testListToPairs() {
        val l = listOf(1, 2, 3, 4, 5)
        val lp = l.toPairs()
        assertEquals(4 + 3 + 2 + 1, lp.size)
        assertEquals(4 + 3 + 2 + 1, lp.toSet().size)
        println(lp)
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
    val r = g.findPath(g[start], g[end], backward)
    println("Path: $r")
    return r.minOfOrNull { it.key } ?: -1
}
