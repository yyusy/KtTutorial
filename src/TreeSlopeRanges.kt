fun main() {
    val stepX = 3
    val stepY = 1
    val treeMark = '#'
    var x = 0
    var count = 0
    val l = System.`in`.bufferedReader().readLines();
    for (y in 0..l.lastIndex step stepY) {
        count += if (l[y][x] == treeMark) 1 else 0
        x += if (x + stepX >= l[0].length) stepX - l[0].length else stepX
    }
    println(count)
}
