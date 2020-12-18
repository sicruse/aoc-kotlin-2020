package days

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class Day18Test {

    private val day = Day18()

//    @Test
//    fun testPartOne() {
//        assertThat(day.partOne(), `is`(13632L ))
//    }

    @Test
    fun testPartTwo() {
        assertThat(day.partTwo(), `is`(231L ))
    }
}
