import java.io.File
import kotlin.test.assertEquals

fun main() {
    Day8Part2BootFindBug()
}

fun Day8Part2BootFindBug() {
    val program = File("Day8Input.txt").useLines {
        it.filter { it.isNotBlank() }
            .map { it.toInstruction() }.toMutableList()
    }.also { println(it) }

    val address = findFixForProgram(program)
    println("Fix address : $address ${program[address]}")
    program[address] =
        program[address].copy(type = (if (program[address].type == InstructionType.NOP) InstructionType.JMP else InstructionType.NOP))
    val ctx = ExecutionCtx(0, 0)
    program.execute(ctx)
    println(ctx)
    assertEquals(758, ctx.accumulator)
}


fun findFixForProgram(program: List<Instruction>): Int {

    val g = program.toGraph()
    println(g)
    val forwardVisited = g.findPathResult(g[0], g[program.size], false).visited
    println("fw visited :$forwardVisited")
    val backwardVisited = g.findPathResult(g[program.size], g[0], true).visited
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