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
        it.addEdge(i, k.execute(ExecutionCtx(i)))
    }
}


fun main() {
    val program: MutableList<Instruction> = System.`in`.bufferedReader().readLines().filter { it.isNotBlank() }
        .map { it.toInstruction() }.toMutableList()
        .also { println(it) }

    val address = findFixForProgram(program)
    println("Fix address : $address ${program[address]}")
    program[address] =
        program[address].copy(type = (if (program[address].type == InstructionType.NOP) InstructionType.JMP else InstructionType.NOP))
    val ctx = ExecutionCtx(0, 0)
    program.execute(ctx)
    println("Executed :  $ctx")
}


fun findFixForProgram(program: List<Instruction>): Int {

    val g = program.toGraph()
    println(g)

    val forwardVisited = mutableSetOf<Vertex<Int, Instruction>>()
    g.findPath(g[0], g[program.size], forwardVisited, false)
    println("fw visited :$forwardVisited")
    val backwardVisited = mutableSetOf<Vertex<Int, Instruction>>()
    g.findPath(g[program.size], g[0], backwardVisited, true)
    println("bk visited :$backwardVisited")
    val jmpToChangeToNOP = forwardVisited
        .filter { program[it.key].type == InstructionType.JMP }
        .filter { jmp -> backwardVisited.any { it.key == jmp.key + 1 } }
        .toSet()
    println("jmpToChangeToNOP :$jmpToChangeToNOP")
    val nopToChangeToJUMP = forwardVisited
        .filter { program[it.key].type == InstructionType.NOP }
        .filter { nop ->
            backwardVisited.any { it.key == nop.key + program[nop.key].arg }
        }
        .toSet()
    println("nopToChangeToJUMP :$nopToChangeToJUMP")
    return (jmpToChangeToNOP.firstOrNull() ?: nopToChangeToJUMP.firstOrNull())!!.key

}