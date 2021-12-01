fun main() {
    val treeMark = '#'
    // 1 to 1, 1 to 3, 1 to 5, 1 to 7, 1 to 2
    val l = System.`in`.bufferedReader().readLines()
    listOf(1 to 1, 1 to 3, 1 to 5, 1 to 7, 2 to 1).map { step ->
        (0..l.lastIndex step step.first).mapIndexed { i, y -> y to (step.second * i % l[0].length) }
            .count { l[it.first][it.second] == treeMark }.also { println("$step : $it") }
    }.fold(1L) { acc, i -> acc * i }
        .also { println(it) }

}

