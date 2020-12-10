package days

import java.util.*

class Day10 : Day(10) {

    private val sortedAdapters: List<Int> by lazy {
        inputList.map { code -> code.toInt() }.sorted()
    }

    private val adapterDifferences: List<Int> by lazy {
        var priorValue = 0
        sortedAdapters.map { value ->
            val difference = value - priorValue
            priorValue = value
            difference
        } + 3
    }

    override fun partOne(): Any {
         return adapterDifferences.count { difference -> difference == 1 } * adapterDifferences.count { difference -> difference == 3 }
    }

    override fun partTwo(): Any {
        val paths = sortedAdapters.fold(TreeMap(mapOf(Pair(0, 1L)))) { acc, adapter ->
                acc[adapter] = 0
                for (i in adapter - 3 until adapter)
                    acc[adapter] = when(acc[i]) {
                        null -> acc[adapter]!!
                        else -> acc[i]!! + acc[adapter]!!
                    }
                acc
        }

        return paths.lastEntry().value
    }
}
