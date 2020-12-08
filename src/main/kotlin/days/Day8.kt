package days

import kotlin.jvm.Throws

class Day8 : Day(8) {

    data class Operation(val op: OpCode, val inc: Int) {
        companion object {
            @Throws
            fun fromAssemblyCode(code: String): Operation {
                val op = code.take(3)
                val i = if (code[4] == '+') 1 else -1
                val inc = i * code.drop(5).toInt()
                return when (op) {
                    "nop" -> Operation(OpCode.nop, inc)
                    "jmp" -> Operation(OpCode.jmp, inc)
                    "acc" -> Operation(OpCode.acc, inc)
                    else -> throw IllegalArgumentException("Unrecognized Assembly Code: $op")
                }
            }
        }
    }

    data class Result(val address: Int, val acc: Int, val exitCode: ExitCode = ExitCode.INFINITE_LOOP)

    enum class ExitCode {
        INFINITE_LOOP,
        ADDRESS_EXCEEDS_BOUNDS;
    }

    enum class OpCode {
        nop {
            override fun execute(address: Int, acc: Int, inc: Int): Result = Result(address + 1, acc)
        },
        jmp {
            override fun execute(address: Int, acc: Int, inc: Int): Result = Result(address + inc, acc)
        },
        acc {
            override fun execute(address: Int, acc: Int, inc: Int): Result = Result(address +1, acc + inc)
        };

        abstract fun execute(address: Int, acc: Int, inc: Int): Result
    }

    private val program: List<Operation> by lazy {
        inputList.map { code -> Operation.fromAssemblyCode(code) }
    }

    private fun debug(prog: List<Operation>): Sequence<Result> = sequence {
        var address = 0
        var acc = 0
        val trace = mutableSetOf<Int>()
        while ( address < prog.size && !trace.contains(address) ) {
            val instruction = prog[address]
            val result = instruction.op.execute(address, acc, instruction.inc)
            trace.add(address)
            address = result.address
            acc = result.acc

            yield(result)
        }
        // Indicate that the addressable range is exceeded as needed
        if (address >= prog.size) yield(Result(address, acc, ExitCode.ADDRESS_EXCEEDS_BOUNDS ))
    }

    override fun partOne(): Any {
        // Debug the code & report the resultant accumulator
        return debug(program).last().acc
    }

    @Throws
    override fun partTwo(): Any {
        for ((address, instruction) in program.withIndex()) {
            when (instruction.op) {
                OpCode.acc -> continue
                else -> {
                    // Monkeypatch the program
                    val monkey = Operation(if (instruction.op == OpCode.jmp) OpCode.nop else OpCode.jmp, instruction.inc)

                    // splice the code around the monkeypatched line
                    val preOps = if (address > 0) program.subList(0, address) else emptyList()
                    val postOps = if (address < program.size) program.subList(address + 1, program.size) else emptyList()

                    // create representation of code including the monkeypatch
                    val monkeyPatchedProgram = preOps + monkey + postOps

                    // Debug the code
                    val result = debug(monkeyPatchedProgram).last()
                    // Debug the code & report the resultant accumulator
                    if (result.exitCode == ExitCode.ADDRESS_EXCEEDS_BOUNDS) return result.acc
                }
            }
        }
        throw IllegalArgumentException("Program has no solution...")
    }
}
