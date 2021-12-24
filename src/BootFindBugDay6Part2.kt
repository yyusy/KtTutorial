
class Executor(List<Instruction> program,  val startAccumulator = 0 )
{
    fun run():Int {

    }
}

fun main() {
    val program: List<Instruction> = System.`in`.bufferedReader().readLines().filter { it.isNotBlank() }
        .map { it.split(" ").let { (k, v) -> Instruction(InstructionType.valueOf(k.uppercase()), v.toInt()) } }
        .also { println(it) }

    var address = 0
    val ctx = InstructionCtx(address, 0)
    while (address in program.indices) {
        address = program[address].execute(ctx)
    }
    println("Finished with address: $address for range: ${program.indices}")
}

