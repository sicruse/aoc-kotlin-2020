import days.Day

class Template : Day(1) {

    override fun partOne(): Any {
        return inputList.take(2)
            .map { it.toUpperCase() }
            .joinToString(" ")
    }

    override fun partTwo(): Any {
        return inputString.split("\n")
            .filterNot { it.isEmpty() }
            .map { it.toUpperCase() }
            .last()
    }
}
