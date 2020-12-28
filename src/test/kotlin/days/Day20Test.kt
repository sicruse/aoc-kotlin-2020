package days

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class Day20Test {

    private val day = Day20()

    private val testSatelliteImage =
        ".#.#..#.##...#.##..#####\n" +
        "###....#.#....#..#......\n" +
        "##.##.###.#.#..######...\n" +
        "###.#####...#.#####.#..#\n" +
        "##.#....#.##.####...#.##\n" +
        "...########.#....#####.#\n" +
        "....#..#...##..#.#.###..\n" +
        ".####...#..#.....#......\n" +
        "#..#.##..#..###.#.##....\n" +
        "#.####..#.####.#.#.###..\n" +
        "###.#.#...#.######.#..##\n" +
        "#.####....##..########.#\n" +
        "##..##.#...#...#.#.#.#..\n" +
        "...#..#..#.#.##..###.###\n" +
        ".#.#....#.##.#...###.##.\n" +
        "###.#...#..#.##.######..\n" +
        ".#.#.###.##.##.#..#.##..\n" +
        ".####.###.#...###.#..#.#\n" +
        "..#.#..#..#.#.#.####.###\n" +
        "#..####...#.#.#.###.###.\n" +
        "#####..#####...###....##\n" +
        "#.##..#..#...#..####...#\n" +
        ".#.###..##..##..####.##.\n" +
        "...###...##...#...#..###"

    private val flippedSatelliteImage =
        ".####...#####..#...###..\n" +
        "#####..#..#.#.####..#.#.\n" +
        ".#.#...#.###...#.##.##..\n" +
        "#.#.##.###.#.##.##.#####\n" +
        "..##.###.####..#.####.##\n" +
        "...#.#..##.##...#..#..##\n" +
        "#.##.#..#.#..#..##.#.#..\n" +
        ".###.##.....#...###.#...\n" +
        "#.####.#.#....##.#..#.#.\n" +
        "##...#..#....#..#...####\n" +
        "..#.##...###..#.#####..#\n" +
        "....#.##.#.#####....#...\n" +
        "..##.##.###.....#.##..#.\n" +
        "#...#...###..####....##.\n" +
        ".#.##...#.##.#.#.###...#\n" +
        "#.###.#..####...##..#...\n" +
        "#.###...#.##...#.######.\n" +
        ".###.###.#######..#####.\n" +
        "..##.#..#..#.#######.###\n" +
        "#.#..##.########..#..##.\n" +
        "#.#####..#.#...##..#....\n" +
        "#....##..#.#########..##\n" +
        "#...#.....#..##...###.##\n" +
        "#..###....##.#...##.##.#"

    private val testMonster =
        "                  # \n" +
        "#    ##    ##    ###\n" +
        " #  #  #  #  #  #   "

    @Test
    fun testPartOne() {
        assertThat(day.partOne(), `is`(20899048083289L ))
    }

    @Test
    fun testSatellite() {
        val image = Day20.SatelliteImage(testSatelliteImage)
        val waves = image.waveCount
        assertThat(waves, `is`(303L ))
    }

    @Test
    fun testSimpleMonsterDetection() {
        val image = Day20.SatelliteImage(flippedSatelliteImage)
        val mask = Day20.ImageMask(testMonster)

        val detected = Day20.SatelliteImage(mask.applyTo(image))

        val minWaveCount = detected.waveCount
        assertThat(minWaveCount, `is`(273L ))
    }

    @Test
    fun testMonsterDetectionWithImageRotation() {
        val image = Day20.SatelliteImage(testSatelliteImage)
        val mask = Day20.ImageMask(testMonster)

        val images = image.orientations.map { variant ->
            Day20.SatelliteImage(mask.applyTo(variant))
        }

        val index = images.withIndex().minByOrNull { (_, i) -> i.waveCount }?.index
        val detected = images.elementAt( index!! )

        val minWaveCount = detected.waveCount
        assertThat(minWaveCount, `is`(273L ))
    }


    @Test
    fun testPartTwo() {
        assertThat(day.partTwo(), `is`(273L ))
    }
}
