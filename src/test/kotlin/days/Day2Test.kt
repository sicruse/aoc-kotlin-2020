package days

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.hamcrest.core.IsNull.notNullValue
import org.junit.Test

class Day2Test {

    private val dayOne = Day2()

    @Test
    fun testPartOne() {
        assertThat(dayOne.partOne(), `is`(2))
    }

    @Test
    fun testPartTwo() {
        assertThat(dayOne.partTwo(), `is`(1))
    }
}
