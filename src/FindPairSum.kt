fun main() {
    val s: List<Int> =
        System.`in`.bufferedReader().readLines().filter { it.isNotBlank() }.map { it.toInt() }.sorted()
    val ES = 2020
    var res: Pair<Int, Int>? = null
    for (i in 1..s.lastIndex) {
        if (s[i - 1] + s[i] < ES) continue
        for (j in 0 until i) {
            if (s[j] + s[i] == ES) {
                res = Pair(s[j], s[i])
                break
            } else if (s[j] + s[i] > ES) break
        }
        if (res != null) break
    }
    println("Result : ${res ?: "<empty>"}, Multiplied : ${res?.let { res.first * res.second }} ")
}
