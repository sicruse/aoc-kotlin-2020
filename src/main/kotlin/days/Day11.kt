package days

class Day11 : Day(11) {

    // landscape bitmap states
    // - floor : can never have person occupancy
    // - chair : can be empty or host a person

    // distribution states
    // - no-person
    // - person

    // I imagine that each map "frame" can be modelled as a sequence

    private val rows = inputList.size
    private val cols = inputList.first().length

    private val landscapeMask: Array<IntArray> by lazy {
        Array(rows) { row -> IntArray(cols) { col -> if (inputList[row][col] == '#' || inputList[row][col] == 'L') 1 else 0 } }
    }

    private fun applyMask(data: Array<IntArray>): Array<IntArray> {
        // assume both arrays are the same size
        return Array(rows) { row -> IntArray(cols) { col -> if (landscapeMask[row][col] + data[row][col] == 2) 1 else 0 } }
    }

    private fun printFrame(data: Array<IntArray>) {
        val output = Array(rows) { row -> String(CharArray(cols) { col -> when {
            landscapeMask[row][col] + data[row][col] == 2 -> '#'
            landscapeMask[row][col] == 1 -> 'L'
            else -> '.'
        }})}
        println(output.joinToString("\n") + "\n")
    }

    private fun occupyByNeighbor(frame: Array<IntArray>, row: Int, col:Int): Boolean {
        // If a seat is empty (L) and there are no occupied seats adjacent to it, the seat becomes occupied.
        // If a seat is occupied (#) and four or more seats adjacent to it are also occupied, the seat becomes empty.
        // Otherwise, the seat's state does not change.
        if (landscapeMask[row][col] == 0) return false // no chair!
        val occupied = frame[row][col] == 1
        val rowrange = ((row - 1)..(row + 1)).filter { it in 0..rows-1 }
        val colrange = ((col - 1)..(col + 1)).filter { it in 0..cols-1 }
        val surroundingOccupancy = rowrange.fold(0) { acc1, r ->
            acc1 + colrange.fold(0) { acc2, c -> acc2 + frame[r][c] }
        } - frame[row][col]
        return when(occupied) {
            true -> surroundingOccupancy < 4
            false -> surroundingOccupancy == 0
        }
    }

    private val populateByNeighbor: (Array<IntArray>) -> Array<IntArray> = { frame ->
        Array(rows) { row -> IntArray(cols) { col -> if (occupyByNeighbor(frame, row, col)) 1 else 0 } }
    }

    private fun paths(row: Int, col:Int): List<List<Pair<Int, Int>>> {
        // consider the first seat in each of [those] eight directions.
        val nesw = listOf (
            if (row > 0) ((row - 1) downTo 0).map { Pair(it, col) } else emptyList(),
            if (col < cols) ((col +1)..cols-1).map { Pair(row, it) } else emptyList(),
            if (row < rows) ((row + 1)..rows-1).map { Pair(it, col) } else emptyList(),
            if (col > 0) ((col - 1) downTo 0).map { Pair(row, it) } else emptyList(),
        )
        val neseswnw = listOf(
            nesw[0].map { it.first } zip nesw[1].map { it.second },
            nesw[2].map { it.first } zip nesw[1].map { it.second },
            nesw[2].map { it.first } zip nesw[3].map { it.second },
            nesw[0].map { it.first } zip nesw[3].map { it.second },
        )
        return nesw + neseswnw
    }

    private fun visibleChairs(row: Int, col: Int) = paths(row, col).mapNotNull { path -> path.find { (r, c) -> landscapeMask[r][c] == 1 } }

    private fun occupyByVisibility(frame: Array<IntArray>, row: Int, col:Int): Boolean {
        if (landscapeMask[row][col] == 0) return false // no chair!
        val visibleOccupancy = visibleChairs(row, col).fold(0) { acc, (r,c) -> acc + frame[r][c] }
        return if (frame[row][col] == 1) visibleOccupancy < 5 else visibleOccupancy == 0
    }

    private val populateByVisibility: (Array<IntArray>) -> Array<IntArray> = { frame ->
        Array(rows) { row -> IntArray(cols) { col -> if (occupyByVisibility(frame, row, col)) 1 else 0 } }
    }

    private fun frames(populate: (Array<IntArray>) -> Array<IntArray>): Sequence<Array<IntArray>> = sequence {
        var currentFrame = Array(rows) { IntArray(cols) { 0 } }
        var priorFrame: Array<IntArray>? = null
        yield(currentFrame)
        while (priorFrame == null || !priorFrame.contentDeepEquals(currentFrame)) {
            priorFrame = currentFrame
            currentFrame = applyMask(populate(priorFrame))
            yield(currentFrame)
        }
    }

    override fun partOne(): Any {
        return frames(populateByNeighbor).last().fold(0) { acc1, r ->
            acc1 + r.fold(0, { acc2, c -> acc2 + c })
        }
    }

    override fun partTwo(): Any {
        return frames(populateByVisibility).last().fold(0) { acc1, r ->
            acc1 + r.fold(0, { acc2, c -> acc2 + c })
        }
    }
}


