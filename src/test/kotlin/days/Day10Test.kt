package days

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class Day10Test {

    private val day = Day10()

    @Test
    fun testPartOne() {
        assertThat(day.partOne(), `is`("Whatever is expected"))
    }

    @Test
    fun testPartTwo() {
        assertThat(day.partTwo(), `is`("Whatever is expected"))
    }
}