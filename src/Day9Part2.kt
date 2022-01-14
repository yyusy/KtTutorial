import java.io.File

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    val windowSize = 25
    val res = File("Day9Input.txt").useLines {
        invalidNumber(it, windowSize)
    }
    println(res)
}
