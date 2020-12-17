package days

class Day17 : Day(17) {

    // 4D Array!!
    // 3D Array?
    // 2D Array?

    class Matrix(private val space: List<List<List<List<Boolean>>>>, private val dimensions: Dimensions) {
        enum class Dimensions {THREE, FOUR}

        fun active(x: Int, y: Int, z: Int, w: Int): Boolean {
            return if (x in xRange && y in yRange && z in zRange && w in wRange) space[w][z][y][x] else false
        }

        fun count(x: Int, y: Int, z: Int, w: Int): Int = if (active(x,y,z,w)) 1 else 0

        fun activeNeighbors(x: Int, y: Int, z: Int, w: Int): Int {
            val xBoxRange = (x-1)..(x+1)
            val yBoxRange = (y-1)..(y+1)
            val zBoxRange = (z-1)..(z+1)
            val wBoxRange = if (dimensions == Dimensions.FOUR) (w-1)..(w+1) else 0..0
            val values = xBoxRange.flatMap { xb -> yBoxRange.flatMap { yb -> zBoxRange.flatMap { zb -> wBoxRange.map { wb-> count(xb, yb, zb, wb) } } } }
            return values.fold(0) { acc, v -> acc + v } - count(x,y,z,w)
        }

        // cycle is where the magic happens
        fun cycle(previous: Matrix): Matrix {
            // Every cycle will build a matrix two units larger in every dimension
            val xNewRange = 0..previous.xRange.endInclusive + 2
            val yNewRange = 0..previous.yRange.endInclusive + 2
            val zNewRange = 0..previous.zRange.endInclusive + 2
            val wNewRange = if (dimensions == Dimensions.FOUR) 0..previous.wRange.endInclusive + 2 else 0..0

            val data = wNewRange.map { w ->
                val wb = if (dimensions == Dimensions.FOUR) w - 1 else 0
                zNewRange.map { z ->
                    yNewRange.map { y ->
                        xNewRange.map { x ->
                            val count = previous.activeNeighbors(x - 1, y - 1, z - 1, wb)
                            // If a cube is active and exactly 2 or 3 of its neighbors are also active, the cube remains active. Otherwise, the cube becomes inactive.
                            // If a cube is inactive but exactly 3 of its neighbors are active, the cube becomes active. Otherwise, the cube remains inactive.
                            if (previous.active(x - 1, y - 1, z - 1, wb))
                                count in 2..3
                            else
                                count == 3
                        }
                    }
                }
            }
            return Matrix(data, dimensions)
        }

        // We will assume that x & y dimensions are represented by
        // the first entry in each list
        val xRange: IntRange
            get() = 0 until space[0][0][0].size
        val yRange: IntRange
            get() = 0 until space[0][0].size
        val zRange: IntRange
            get() = 0 until space[0].size
        val wRange: IntRange
            get() = 0 until space.size

        // Count all active cells
        val totalActive: Int
            get() =
                xRange.flatMap { x -> yRange.flatMap { y -> zRange.flatMap { z -> wRange.map{ w -> count(x, y, z, w) } } } }
                .fold(0) { acc, v -> acc + v }

        // Helper for debug printing
        fun to2DMap(z: Int, w: Int) : String =
            space[w][z].map { row ->
                buildString {
                    row.forEach { v -> if (v) append('#') else append('.') }
                    append("\n")
                }
            }.joinToString("")

        companion object {
            // take input and create 3D space representation
            // (z, y, x)
            fun from2DMap(data: List<String>, dimensions: Dimensions): Matrix {
                return Matrix(listOf<List<List<List<Boolean>>>>(
                    listOf<List<List<Boolean>>>(
                    data.map { rowText ->
                        rowText.map { c -> c == '#' }
                    }
                )), dimensions)
            }
        }
    }

    fun frames(dimensions: Matrix.Dimensions = Matrix.Dimensions.THREE): Sequence<Matrix> = sequence {
        // The first frame in the sequence is loaded from file
        var frame = Matrix.from2DMap(inputList, dimensions)
        yield(frame)
        while (true) {
            frame = frame.cycle(frame)
            yield(frame)
        }
    }

    override fun partOne(): Any {
        return frames(Matrix.Dimensions.THREE).elementAt(6).totalActive
    }

    override fun partTwo(): Any {
        return frames(Matrix.Dimensions.FOUR).elementAt(6).totalActive
    }
}
