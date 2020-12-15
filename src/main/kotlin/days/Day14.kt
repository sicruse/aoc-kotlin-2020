package days

class Day14 : Day(14) {

    class BitMask(val bitMask: Long, val setBits: Long, val floatingBits: Set<Long>) {

        constructor(pattern: String):this(maskFromString(pattern), valueFromString(pattern), floatingFromString(pattern)) {}

        fun apply(data: Long): Long = data.and(bitMask).or(setBits)

        fun combinationUtil(input: LongArray, tempData: LongArray, result: MutableList<Set<Long>>, start: Int, end: Int, index: Int, r: Int) {
            if (index == r) {
                result += (0 until r).map{j -> tempData[j]}.toSet()
                return
            }

            var i = start
            while (i <= end && end - i + 1 >= r - index) {
                tempData[index] = input[i]
                combinationUtil(input, tempData, result, i + 1, end, index + 1, r)
                i++
            }
        }

        fun combination(input: LongArray, n: Int, r: Int): List<Set<Long>> {
            // A temporary array to store all combination one by one
            val data = LongArray(r)
            val result = mutableListOf<Set<Long>>()

            // Generate all combinations using temprary array 'data[]'
            combinationUtil(input, data, result, 0, n - 1, 0, r)

            return result
        }

        fun allCombinations(data: Set<Long>): List<Set<Long>> {
            return (1 .. data.size).flatMap{combination(data.toLongArray(), data.size, it)}
        }

        fun decode(address: Long): Set<Long> {
            // first apply the bitmask
            val base = address.or(setBits)

            // then apply the floating
            val floatingMask = floatingBits.sum().inv()
            val baseMasked = base.and(floatingMask)

            val addresses = allCombinations(floatingBits).map{ it.sum() or baseMasked }
            return (addresses + baseMasked).toSet()
        }

        companion object {
            fun maskFromString(pattern: String): Long {
                val maskPattern = pattern.replace('X','1')
                val result = maskPattern.toLong(2)
                return result
            }

            fun valueFromString(pattern: String): Long {
                val maskPattern = pattern.replace('X','0')
                val result = maskPattern.toLong(2)
                return result
            }

            fun floatingFromString(pattern: String): Set<Long> {
                val bits = pattern.reversed().mapIndexedNotNull { i, c ->
                    if (c == 'X') 1L.shl(i) else null
                }.toSet()
                return bits
            }
        }
    }

    class Procedure(val mask: BitMask, val instructions: Map<Long, Long>) {

        constructor(data: String): this(extractMask(data), extractInstructions(data))

        fun execute(memory: MutableMap<Long, Long>) {
            for (instruction in instructions)
                memory[instruction.key] = mask.apply(instruction.value)
        }

        fun decode(memory: MutableMap<Long, Long>) {
            for (instruction in instructions) {
                val addresses = mask.decode(instruction.key)
                for (address in addresses) memory[address] = instruction.value
            }
        }

        companion object {
            val destructuredRegex = "mem\\[(\\d+)] = (\\d+)".toRegex()

            fun extractMask(data: String): BitMask {
                val mask = data.split("\n").first()
                return BitMask(mask)
            }

            fun extractInstructions(text: String): Map<Long, Long> {
                val values = text.split("\n").drop(1).filter{ it.length > 0 }
                return values.map {
                    destructuredRegex.matchEntire(it)
                        ?.destructured
                        ?.let { (_address, _value) ->
                            _address.toLong() to _value.toLong()
                        }
                        ?: throw IllegalArgumentException("Bad input '$text'")
                }.toMap()
            }
        }
    }

    private val procedures: List<Procedure> by lazy {
        val blocks = inputString.split("mask = ").drop(1)
        blocks.map{ Procedure( it )}
    }

    override fun partOne(): Any {
        val memory = mutableMapOf<Long, Long>()
        for (procedure in procedures) {
            procedure.execute(memory)
        }

        return memory.values.sum()
    }

    override fun partTwo(): Any {

        val memory = mutableMapOf<Long, Long>()
        for (procedure in procedures) {
            procedure.decode(memory)
        }

        return memory.values.sum()
    }
}
