fun main() {
    var i = 0
    System.`in`.bufferedReader().lineSequence()
        .groupingBy { s -> i.also { if (s.isEmpty()) i += 1 } }
        .aggregate { _, a: StringBuffer?, e, first -> if (first) StringBuffer().append(e) else a!!.append(e) }
        .map { it.value.toSet().also { println(it) } } // answered yes
        .sumOf { it.size }
        .also { println(it) }
}

