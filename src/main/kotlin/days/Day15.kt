package days

import days.Day

class Day15 : Day(15) {

    override fun partOne(): Any {
        return inputList.take(2)
            .map { it.toUpperCase() }
            .joinToString(" ")
    }

    override fun partTwo(): Any {
        return inputList.take(2)
            .map { it.toUpperCase() }
            .joinToString(" ")
    }
}
