package days

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class Day2Test {

    private val day = Day2()

    @Test
    fun testPartOne() {
        assertThat(day.partOne(), `is`(2))
    }

    @Test
    fun testPartTwo() {
        assertThat(day.partTwo(), `is`(1))
    }
}
