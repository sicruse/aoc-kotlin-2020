package days

import days.Day

class Day15 : Day(15) {

    private val numbers: List<Int> by lazy {
        inputString.split(",").map{ it.toInt() }
    }

    val spoken: Sequence<Int> = sequence {
        val history = mutableMapOf<Int, MutableList<Int>>()
        var turn = 1
        var current:Int? = null
        while(true) {
            if (current == null || turn <= numbers.size) {
                // We are in the preamble of the game, just read from our input list
                current = numbers[turn - 1]
            } else {
                // Take the last value of current and deduce the next current
                val lastTime = history[current]!!
                if (lastTime.size == 1) {
                    current = 0
                } else {
                    val plays = lastTime.takeLast(2)
                    current = plays.last() - plays.first()
                }
            }

            // Update the history
            if (history[current] == null)
                history[current] = mutableListOf(turn)
            else
                history[current]!!.add(turn)

            yield(current)
            turn++
        }
    }

    override fun partOne(): Any {
        val result = spoken.elementAt(2020 - 1)

        return result
    }

    override fun partTwo(): Any {
        val result = spoken.elementAt(30000000 - 1)

        return result
    }
}
