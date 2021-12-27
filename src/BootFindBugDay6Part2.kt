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


fun main() {
    val program: List<Instruction> = System.`in`.bufferedReader().readLines().filter { it.isNotBlank() }
        .map { it.toInstruction() }
        .also { println(it) }
    val startAddress = findWayToEnd(program)
    println("Found way to finish starting with $startAddress")
}

fun findWayToEnd(program: List<Instruction>): Int {
    val visited = sortedSetOf<Int>()
    var startAddress = 0
    val endOfProgram = program.size
    while (startAddress in program.indices) {
        val ctx = ExecutionCtx(startAddress)
        if (program.execute(ctx) != endOfProgram) {
            visited.addAll(ctx.trace.values)
            visited.add(startAddress)
            println("Visited : $visited")
            startAddress = (program.indices).asSequence().minus(visited).minOrNull() ?: -1
        } else {
            break
        }
        println("Try next address : $startAddress")
    }
    return startAddress
}

fun findWayToEndGraph(program: List<Instruction>): Int {

    val g = Graph<Int, Instruction>()
    // build graph
    program.forEachIndexed { i, k ->
        val iTo = k.execute(ExecutionCtx(i))
        g.addEdge(i, iTo)
    }
    println(g)
    return 1
}