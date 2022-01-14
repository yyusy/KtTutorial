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

fun List<Instruction>.execute(ctx: ExecutionCtx): Int {
    var address = ctx.getNextAddress()
    var deadlock = false
    try {
        while (address in this.indices) {
            address = this[address].execute(ctx)
        }
    } catch (x: ExecutionDeadlockException) {
        println(x)
        deadlock = true
    }
    println("Finished with ${if (deadlock) "deadlock" else ""} address: ${ctx.getNextAddress()} for range: ${this.indices}")
    return address
}

fun List<Instruction>.toGraph(): Graph<Int, Instruction> = Graph<Int, Instruction>().also {
    this.forEachIndexed { i, k ->
        it.connect(i, k.execute(ExecutionCtx(i)))
    }
}