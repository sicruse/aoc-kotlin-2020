package days

class Day20 : Day(20) {

    private val tiles: List<Tile> by lazy {
        inputString
            // split the file at blank lines
            .split("\\n\\n".toRegex())
            .map { tileText -> Tile(tileText) }
    }

    private fun cornerTiles(): List<Tile> {
        return tiles
            .filter { tile -> tile.matchingBordersIn(tiles) == 2 }
    }

    open class CharMatrix(val body: Array<CharArray>) {
        val height = body.size
        val width = body[0].size

        private val top = body.first().joinToString("")
        private val right = body.map { row -> row.last() }.joinToString("")
        private val bottom = body.last().joinToString("")
        private val left = body.map { row -> row.first() }.joinToString("")

        fun allBorders(): Set<String> = originalBorders() + reversedBorders()
        fun originalBorders(): Set<String> = setOf(top, left, bottom, right)
        fun hasBorder(pattern: String): Boolean = pattern in allBorders()

        private fun reversedBorders(): Set<String> = originalBorders().map { it.reversed() }.toSet()

        enum class Border {
            Top, Right, Bottom, Left;

            val opposite: Border
                get() = when (this) {
                    Top -> Bottom
                    Left -> Right
                    Bottom -> Top
                    Right -> Left
                }
        }

        fun border(side: Border) = when (side) {
            Border.Top -> top
            Border.Right -> right
            Border.Bottom -> bottom
            Border.Left -> left
        }

        enum class Corner {
            TopLeft, TopRight, BottomRight, BottomLeft;

            val opposite: Corner
                get() = when (this) {
                    TopLeft -> BottomRight
                    TopRight -> BottomLeft
                    BottomRight -> TopLeft
                    BottomLeft -> TopRight
                }
        }

        fun borders(corner: Corner) = when (corner) {
            Corner.TopLeft -> Pair(top, left)
            Corner.TopRight -> Pair(top, right)
            Corner.BottomRight -> Pair(bottom, right)
            Corner.BottomLeft -> Pair(bottom, left)
        }

        val borderless: Array<CharArray> =
            body
                .drop(1).dropLast(1)
                .map { row -> row.drop(1).dropLast(1).toCharArray() }.toTypedArray()

        override fun toString(): String =
            body.joinToString("\n") { row -> row.joinToString("") }

        val orientations: Sequence<CharMatrix> = sequence {
            // Take the original image and provide each possible rotation
            var last = this@CharMatrix
            yield(last)

            repeat(3) {
                last = rotated(last)
                yield(last)
            }

            // Flip the original image & continue through each possible rotation
            last = flipped(this@CharMatrix)
            yield(last)

            repeat(3) {
                last = rotated(last)
                yield(last)
            }
        }

        private fun flipped(image: CharMatrix): CharMatrix = CharMatrix(flipped(image.body))
        private fun flipped(image: Array<CharArray>): Array<CharArray> =
            image
                .map { it.reversed().toCharArray() }
                .toTypedArray()

        private fun rotated(image: CharMatrix): CharMatrix = CharMatrix(rotated(image.body))
        private fun rotated(image: Array<CharArray>): Array<CharArray> =
            image.mapIndexed { x, row ->
                row
                    .mapIndexed { y, _ -> image[y][x] }
                    .reversed()
                    .toCharArray()
            }.toTypedArray()

        companion object {
            fun bodyFromText(text: String): Array<CharArray> =
                text
                    .split("\\n".toRegex())
                    .map { it.toCharArray() }
                    .toTypedArray()
        }
    }

    class Tile(val id: Int, body: Array<CharArray>) : CharMatrix(body) {
        constructor(text: String) : this(
            id = text.split(":\n")
                .first()
                .split(' ')
                .last()
                .toInt(),
            body = bodyFromText(text.split(":\n").last())
        )

        override fun toString(): String =
            id.toString() + ":\n" +
                    super.toString()

        fun matchingBordersIn(tiles: List<Tile>): Int =
            originalBorders()
                .sumOf { side ->
                    tiles
                        .filterNot { tile -> tile.id == id }
                        .count { tile -> tile.hasBorder(side) }
                }

        fun orientCorner(corner: Corner, tiles: List<Tile>): Int? {
            // filter self out of list of tiles
            val otherTiles = tiles.filterNot { it.id == this.id }

            // Determine whole border set for remaining tiles
            val allBorders = otherTiles.fold(emptySet<String>()) { acc, tile -> acc + tile.allBorders() }

            // Determine if we have an orientation that will place us as the corner tile
            // and return the orientation index
            return orientations.withIndex().firstOrNull { (_, tile) ->
                val tileBorders = tile.borders(corner.opposite)
                (tileBorders.first in allBorders) && (tileBorders.second in allBorders)
            }?.index
        }

        fun findAndOrientNeighbor(myBorder: Border, myOrientation: Int, tiles: List<Tile>): Pair<Tile, Int>? {
            // filter self out of list of tiles
            val otherTiles = tiles.filterNot { it.id == this.id }

            // Identify my border when oriented
            val borderPattern = orientations.elementAt(myOrientation).border(myBorder)

            // Identify any tile that has a matching border
            val match = otherTiles.firstOrNull { tile -> tile.allBorders().contains(borderPattern) }
                ?: return null

            // figure out the appropriate tile orientation for matching neighbor
            val index = match.orientations.withIndex()
                .firstOrNull { (_, tile) -> tile.border(myBorder.opposite) == borderPattern }?.index
                ?: return null

            return Pair(match, index)
        }
    }

    class SatelliteImage(body: Array<CharArray>) : CharMatrix(body) {
        constructor(text: String) : this(body = bodyFromText(text))
        constructor(body: CharMatrix) : this(body = body.body)
        constructor(tiles: List<Tile>, corner: Tile) : this(body = assembleImageFromTiles(tiles, corner))

        val waveCount: Long = body.fold(0L) { acc, row -> acc + row.count { c -> c == '#' } }

        companion object {
            // Rather than use a math library we can use a really dumb lookup for SQRT
            private val dumbSQRT = mapOf(
                1 to 1,
                2 * 2 to 2,
                3 * 3 to 3,
                4 * 4 to 4,
                5 * 5 to 5,
                6 * 6 to 6,
                7 * 7 to 7,
                8 * 8 to 8,
                9 * 9 to 9,
                10 * 10 to 10,
                11 * 11 to 11,
                12 * 12 to 12,
            )

            @Throws
            private fun assembleImageFromTiles(tiles: List<Tile>, corner: Tile): Array<CharArray> {
                // Note that if we have more tiles in the input than dumbSQRT supports we will halt here!
                val width = dumbSQRT[tiles.count()]
                    ?: throw IndexOutOfBoundsException("More tiles in input than dumbSQRT supports")

                // Start by orienting the corner tile
                val orientation = corner.orientCorner(Corner.TopLeft, tiles)
                    ?: throw IllegalArgumentException("No corner detected in supplied data")

                var currentTile = Pair(corner, orientation)
                var rowHeader = currentTile

                // Loop through seeking to orient tiles to each others edges
                val grid =
                    (0 until width).map { row ->
                        (0 until width).map { col ->
                            when {
                                row == 0 && col == 0 -> {
                                }
                                col == 0 -> {
                                    rowHeader = rowHeader.first.findAndOrientNeighbor(Border.Bottom, rowHeader.second, tiles)
                                        ?: throw IllegalArgumentException("No row header neighbor detected in supplied data")
                                    currentTile = rowHeader
                                }
                                else -> {
                                    currentTile = currentTile.first.findAndOrientNeighbor(Border.Right, currentTile.second, tiles)
                                        ?: throw IllegalArgumentException("No row body neighbor detected in supplied data")
                                }
                            }
                            currentTile.first.orientations.elementAt(currentTile.second).borderless
                        }
                    }

                return grid.flatMap { row ->
                    // use the first tile in each row to provide a range to iterate over
                    row.first().indices.map {
                        // for each tile in the row assemble the data for the big picture
                        row.map { tile -> tile[it] }.reduce { acc, v -> acc + v }
                    }
                }.toTypedArray()
            }
        }
    }

    class ImageMask(body: Array<CharArray>) : CharMatrix(body) {
        constructor(text: String) : this(body = bodyFromText(text))

        private val matchThreshold by lazy { body.fold(0) { acc, row -> acc + row.count { c -> c == '#' } } }

        fun applyTo(area: CharMatrix): CharMatrix {
            if (area.height < height || area.width < width) return area

            val maskedPoints = mutableSetOf<Pair<Int, Int>>()

            repeat(area.height - height) { y ->
                repeat(area.width - width) { x ->
                    val points = body.flatMapIndexed { row, chars ->
                        chars.asList().mapIndexedNotNull { col, char ->
                            val point = Pair(col + x, row + y)
                            when {
                                char == '#' && area.body[point.second][point.first] == char -> point
                                else -> null
                            }
                        }
                    }
                    // If we have matched every mask element then we have matched the mask pattern
                    if (points.size == matchThreshold) maskedPoints += points
                }
            }

            return CharMatrix(area.body.mapIndexed { y, chars ->
                chars.mapIndexed { x, c -> if (Pair(x, y) in maskedPoints) 'O' else c }.toCharArray()
            }.toTypedArray())
        }
    }

    override fun partOne(): Any {
        val corners = cornerTiles()
        return corners.fold(1L) { acc, tile -> acc * tile.id }
    }

    @Throws
    override fun partTwo(): Any {
        val corners = cornerTiles()
        val image = SatelliteImage(tiles, corners.first())

        // Use the monster mask to transform all possible variants of the satellite image
        val monster =
            "                  # \n" +
                    "#    ##    ##    ###\n" +
                    " #  #  #  #  #  #   "
        val mask = ImageMask(monster)
        val images = image.orientations.map { variant -> SatelliteImage(mask.applyTo(variant)) }

        // Once we have applied the monster mask to every satellite image variant one will have a lower wave count
        // Find the satellite image that has the lowest wave count as that one has monsters!
        val index = images.withIndex().minByOrNull { (_, i) -> i.waveCount }?.index
            ?: throw IllegalArgumentException("Data contains no monsters!")

        // report the wave final count discounting monsters
        return images.elementAt(index).waveCount
    }
}
