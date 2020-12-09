package days

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class Day9Test {

    private val day = Day9()

    @Test
    fun testPartOne() {
        assertThat(day.findWeakness(5), `is`(127L))
    }

    @Test
    fun testPartTwo() {
        assertThat(day.findExploit(127L), `is`(62))
    }
}
