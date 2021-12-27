enum class InstructionType {
    NOP {
        override fun execute(ctx: ExecutionCtx, param: Int) = ctx.move(1)
    },
    JMP {
        override fun execute(ctx: ExecutionCtx, param: Int) = ctx.move(param)
    },
    ACC {
        override fun execute(ctx: ExecutionCtx, param: Int): Int {
            val nextStep = ctx.move(1)
            ctx.accumulator += param
            return nextStep
        }
    };

    abstract fun execute(ctx: ExecutionCtx, param: Int): Int
}

class ExecutionDeadlockException(val address: Int, desc: String) : Throwable(desc)

data class ExecutionCtx(private var address: Int, var accumulator: Int = 0) {
    val trace = mutableMapOf<Int, Int>()
    var deadLockAddress: Int? = null
    fun getNextAddress() = address
    fun move(step: Int): Int {
        if (address in trace) {
            deadLockAddress = address
            throw ExecutionDeadlockException(address, "Deadlock at $this on step ${trace.size} of trace : $trace")
        }
        trace[address] = address + step
        address += step
        return address
    }
}

data class Instruction(val type: InstructionType, val arg: Int) {
    fun execute(ctx: ExecutionCtx) = type.execute(ctx, arg)
}

fun String.toInstruction() =
    this.split(" ").let { (k, v) -> Instruction(InstructionType.valueOf(k.uppercase()), v.toInt()) }

fun main() {
    val program: List<Instruction> = System.`in`.bufferedReader().readLines().filter { it.isNotBlank() }
        .map { it.split(" ").let { (k, v) -> Instruction(InstructionType.valueOf(k.uppercase()), v.toInt()) } }
        .also { println(it) }
    var address = 0
    val ctx = ExecutionCtx(address, 0)
    while (address in program.indices) {
        address = program[address].execute(ctx)
    }
    println("Finished with address: $address for range: ${program.indices}")
}

