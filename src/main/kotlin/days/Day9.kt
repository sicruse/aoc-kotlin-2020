package days

class Day9 : Day(9) {

    private val codes: List<Long> by lazy {
        inputList.map { code -> code.toLong() }
    }

    private val codeAccumulation: List<Long> by lazy {
        var acc = 0L
        codes.map {  code ->
            val result = code + acc
            acc += code
            result
        }
    }

    private val codeAccumulationSet: Set<Long> by lazy {
        codeAccumulation.toSet()
    }

    fun findWeakness(seedSize: Int): Long? {
        val data = codes.subList(seedSize, codes.size)
        val result = data.withIndex().find { (index, code) ->
            val preamble = codes.subList(index, index + seedSize).toSet()
            val found = preamble.find { seed ->
                val otherSeeds = preamble - seed
                val targetSeed = code - seed
                val found = otherSeeds.contains(targetSeed)
                found
            }
            found == null
        }
        return result?.value
    }

    fun findExploit(target: Long): Long? {
        var acc = codeAccumulation[0]
        var start = 1

        for (code in codes.drop(1)) {
            acc += code
            start++
            if ( codeAccumulationSet.contains(target + acc) ) break
        }

        val end = codeAccumulation.indexOf(target + acc)
        val contiguousRange = codes.subList(start, end + 1)
        val smallest = contiguousRange.minOrNull()
        val largest = contiguousRange.maxOrNull()
        return smallest?.plus(largest ?: 0)
    }

    @Throws
    override fun partOne(): Any {
        return findWeakness(25)!!
    }

    override fun partTwo(): Any {
        val target = findWeakness(25)!!
        return findExploit(target)!!
    }
}
