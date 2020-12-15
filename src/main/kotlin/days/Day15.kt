package days

import days.Day

class Day15 : Day(15) {

    private val numbers: List<Int> by lazy {
        inputString.split(",").map{ it.toInt() }
    }

    val spoken: Sequence<Int> = sequence {
        val history = mutableMapOf<Int, List<Int>>()
        var turn = 1
        var current:Int? = null
        while(true) {
            if (turn <= numbers.size) {
                // We are in the preamble of the game, just read from our input list
                current = numbers[turn - 1]
            } else {
                // Take the last value of current and deduce the next current
                val lastTime = history[current]!!
                if (lastTime.size == 1) {
                    current = 0
                } else {
                    val plays = lastTime
                    current = plays.last() - plays.first()
                }
            }

            // Update the history
            val plays = history[current]
            if (plays == null)
                history[current] = listOf(turn)
            else {
                history[current] = listOf(plays.last(), turn)
            }

            yield(current)
            turn++
        }
    }

    override fun partOne(): Any {
        return spoken.elementAt(2020 - 1)
    }

    override fun partTwo(): Any {
        return spoken.elementAt(30000000 - 1)
    }
}
