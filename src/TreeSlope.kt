fun main() {
    val stepX = 3
    val stepY = 1
    val treeMark = '#'
    var x = 0
    System.`in`.bufferedReader().useLines { l ->
        l.filterIndexed { i, _ -> (i + 1) % stepY == 0 }
            .count { s ->
                s[x.also { x = (x + stepX) % s.length }] == treeMark
            }.also { println(it) }
    }
}
