import java.io.File
import kotlin.test.assertEquals


fun main() {
    Day8BootLoop()
}

fun Day8BootLoop() {
    val program = File("Day8Input.txt").useLines { l ->
        l.filter { it.isNotBlank() }
            .map { it.toInstruction() }
            .toList()
    }.also { println(it) }


    val ctx = ExecutionCtx(0, 0)
    var address = program.execute(ctx)
    println("Finished with address: $address for range: ${program.indices}")
    assertEquals(1594, ctx.accumulator)
}

