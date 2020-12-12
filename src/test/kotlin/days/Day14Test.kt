package days

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class Day14Test {

    private val day = Day14()

    @Test
    fun testPartOne() {
        assertThat(day.partOne(), `is`(0 ))
    }

//    @Test
//    fun testPartTwo() {
//        assertThat(day.partTwo(), `is`(0 ))
//    }
}
