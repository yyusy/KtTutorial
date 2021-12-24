fun main() {
    System.`in`.bufferedReader().useLines { lines ->
        lines.filter { it.isNotBlank() }
        .map { it.split(' ', '-') }
        .count {
            //isValidRange(it[0].toInt()..it[1].toInt(), it[2][0], it[3])
            isValidPosition(it[0].toInt() - 1 to it[1].toInt() - 1, it[2][0], it[3])
                .also { r -> println("${it[3]} :$r") }

        }
        .also { println("Valid : $it") }
    }
}

private fun isValidRange(r: IntProgression, ch: Char, toVerify: String) = toVerify.count { it == ch } in r

private fun isValidPosition(r: Pair<Int, Int>, ch: Char, toVerify: String) =
    (toVerify[r.first] == ch).xor(toVerify[r.second] == ch)


