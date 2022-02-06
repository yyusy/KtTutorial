fun main() {
    val s: List<Int> =
        System.`in`.bufferedReader().readLines().filter { it.isNotBlank() }.map { it.toInt() }.sorted()
    println(s)
    val ES = 2020
    var res: Triple<Int, Int, Int>? = null

    for (i in 2 until s.size) {
        if (s[i] > ES) break
        if (s[i - 2] + s[i - 1] + s[i] < ES) continue
        for (j in 1 until i) {
            if (s[j] + s[i] > ES) break
            for (k in 0 until j) {
                if (s[j] + s[i] + s[k] == ES) {
                    res = Triple(s[j], s[i], s[k])
                    break
                } else if (s[j] + s[i] + s[k] > ES) break
            }
            if (res != null) break
        }
        if (res != null) break
    }

    println("Result : ${res ?: "<empty>"}, Multiplied : ${res?.let { res.first * res.second * res.third }} ")
}
