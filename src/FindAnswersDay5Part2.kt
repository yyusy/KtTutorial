fun main() {
    var i = 0
    System.`in`.bufferedReader().lineSequence()
        .groupingBy { s -> i.also { if (s.isEmpty()) i += 1 } }
        .aggregate { _, a: Set<Char>?, e: String, first ->
            if (first) e.toSet() else {
                if (!e.isEmpty()) a!!.intersect(e.toSet())
                else a!!
            }
        }
        .map { println("$it"); it.value.size }
        .sum()
        .also { println(it) }
}

