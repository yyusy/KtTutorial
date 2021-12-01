fun main(args: Array<String>) {
    System.`in`.bufferedReader().readLines().filter { it.isNotBlank() }
        .map { it.split(' ', '-') }
        .filter { it.size >= 4 }
        .map {
            //PassEntry(it[0].toInt()..it[1].toInt(), it[2][0], it[3])
            PassEntry(it[0].toInt() -1..it[1].toInt()-1 step it[1].toInt()-1, it[2][0], it[3])
        }
        .count { it.isValidPosition1() }
        .also { println("Valid : $it") }
}

data class PassEntry(val r: IntProgression, val ch: Char, val toVerify: String) {
    fun isValidRange() = toVerify.sumOf { if (it == ch) 1.toInt() else 0 } in r

    fun isValidPosition() = (toVerify.getOrNull(r.first - 1)?.equals(ch) == true)
        .xor(toVerify.getOrNull(r.last - 1)?.equals(ch) == true)

    fun isValidPosition1() = toVerify.filterIndexed{ i, c -> i in r && c.equals(ch) }
        .count() == 1
    }

