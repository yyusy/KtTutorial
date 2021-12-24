import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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
            .aggregate { key, accumulator: StringBuffer?, element, first ->
                if (first) StringBuffer().append(element) else accumulator!!.append(element)
            }.map { it.value }
        assertEquals(listOf("aaa", "bbbbbb"), r)

    }

    @Test
    fun testUnion()
    {
        assertEquals("bc".toSet(), "abc".toSet().intersect("bc".toSet()));
        assertEquals(emptySet<Char>(), "abc".toSet().intersect("de".toSet()));

    }
}