package days

import kotlin.jvm.Throws

class Day5 : Day(5) {

    // Seat Codes look like this
    // FBFBBFFRLR
    // FBFBBFF    == First 7 characters represent the row number
    //        RLR == Last 3 characters represent the column number

    private val rowSequenceLength = 7
    private val rowLowerHalfCode = 'F'
    private val rowUpperHalfCode = 'B'

    private val colSequenceLength = 3
    private val colLowerHalfCode = 'L'
    private val colUpperHalfCode = 'R'

    private val rowLookup = Lookup(rowSequenceLength, rowLowerHalfCode, rowUpperHalfCode)
    private val colLookup = Lookup(colSequenceLength, colLowerHalfCode, colUpperHalfCode)

    private val seatIDs by lazy {
        inputList.map { seatCode ->
            val row = rowLookup.search(seatCode.take(rowSequenceLength))
            val col = colLookup.search(seatCode.takeLast(colSequenceLength))
            row * 8 + col
        }.sorted()
    }

    class Lookup(
        private val sequenceLength: Int,
        private val lowerHalfCode: Char,
        private val upperHalfCode: Char
    ) {
        @Throws
        fun search(codeSequence: String, lower: Int = 0, upper: Int = (1 shl sequenceLength) - 1): Int {
            val mid = lower + (upper - lower + 1) / 2
            return when {
                // final code in the sequence
                codeSequence.length == 1 && codeSequence.first() == lowerHalfCode -> lower
                codeSequence.length == 1 -> upper
                // more to process
                codeSequence.first() == lowerHalfCode -> search(codeSequence.drop(1), lower, mid - 1)
                codeSequence.first() == upperHalfCode -> search(codeSequence.drop(1), mid, upper)
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

