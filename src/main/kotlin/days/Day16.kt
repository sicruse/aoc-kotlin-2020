package days

import days.Day

class Day16 : Day(16) {

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
