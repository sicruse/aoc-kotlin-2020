package days

import days.Day
import kotlin.jvm.Throws

class Day5 : Day(5) {

    // Seat Codes look like this
    // FBFBBFFRLR
    // FBFBBFF    == 7 bit Row
    //        RLR == 3 bit Column

    private val rowBits = 7
    private val colBits = 3

    private val rowLookup = Lookup(rowBits, 'F', 'B')
    private val colLookup = Lookup(colBits, 'L', 'R')

    private val seatIDs by lazy {
        inputList.map { seatCode ->
            val row = rowLookup.search(seatCode.take(rowBits))
            val col = colLookup.search(seatCode.drop(rowBits))
            row * 8 + col
        }.sorted()
    }

    class Lookup(
        private val bits: Int,
        private val low: Char,
        private val high: Char
    ) {
        @Throws
        fun search(code: String, lower: Int = 0, upper: Int = (1 shl bits) - 1): Int {
            val mid = lower + (upper - lower + 1) / 2
            return when {
                code.length == 1 && code.first() == low -> lower
                code.length == 1 -> upper
                code.first() == low -> search(code.drop(1), lower, mid - 1)
                code.first() == high -> search(code.drop(1), mid, upper)
                else -> throw IllegalArgumentException("Its all broken")
            }
        }
    }

    override fun partOne(): Any {
        return seatIDs.last()
    }

    override fun partTwo(): Any {
        val allSeatIDs = seatIDs.first()..seatIDs.last()
        return (allSeatIDs - seatIDs).first()
    }
}

