package days

import days.Day

class Day3 : Day(3) {

    private fun treeCount(right: Int = 3, down: Int = 1): Long =
        inputList
            .windowed(size = 1, step = down, transform = { lines -> lines.first() })
            .foldIndexed(0) { i, acc, line ->
                acc + if (line[(i * right) % line.length] == '#') 1 else 0
            }

    override fun partOne(): Any = treeCount()

    override fun partTwo(): Any =
        // List of Right, Down vectors
        arrayOf(
            Pair(1, 1),
            Pair(3, 1),
            Pair(5, 1),
            Pair(7, 1),
            Pair(1, 2),
        )
            .fold(1) { acc: Long, (right, down) ->
                acc * treeCount(right, down)
            }
}


