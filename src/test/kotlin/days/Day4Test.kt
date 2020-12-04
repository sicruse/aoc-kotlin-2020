package days

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class Day4Test {

    private val day = Day4()

    @Test
    fun testPartOne() {
        assertThat(day.partOne(), `is`(2))
    }

//    @Test
//    fun testPartTwo_InvalidPassports() {
//        assertThat(day.partTwo(), `is`(0 ))
//    }

//    @Test
//    fun testPartTwo_ValidPassports() {
//        assertThat(day.partTwo(), `is`(4 ))
//    }
}
