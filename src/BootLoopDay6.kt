enum class InstructionType {
    NOP {
        override fun execute(ctx: InstructionCtx, param: Int) = ctx.move(1)
    },
    JMP {
        override fun execute(ctx: InstructionCtx, param: Int) = ctx.move(param)
    },
    ACC {
        override fun execute(ctx: InstructionCtx, param: Int): Int {
            val nextStep = ctx.move(1)
            ctx.accumulator += param
            return nextStep
        }
    };

    abstract fun execute(ctx: InstructionCtx, param: Int): Int
}

data class InstructionCtx(private var address: Int, var accumulator: Int) {
    val trace = mutableMapOf<Int, Int>()
    fun getNextAddress() = address
    fun move(step: Int): Int {
        if (address in trace) throw IllegalStateException("Deadlock : $this on step ${trace.size} of trace : $trace")
        trace[address] = address + step
        address += step
        return address
    }
}

data class Instruction(val instruction: InstructionType, val arg: Int) {
    fun execute(ctx: InstructionCtx) = instruction.execute(ctx, arg)
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

