package days

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class Day5Test {

    private val day = Day5()
    private val rowSearch = Day5.Lookup(7,'F', 'B')

    @Test
    fun testLastRowSearch() {
        assertThat(rowSearch.search("F", 44,45), `is`(44))
    }

    @Test
    fun test2ndRowSearch() {
        assertThat(rowSearch.search("FF", 44,47), `is`(44))
    }

    @Test
    fun test3rdRowSearch() {
        assertThat(rowSearch.search("BFF", 40,47), `is`(44))
    }

    @Test
    fun test4thRowSearch() {
        assertThat(rowSearch.search("BBFF", 32,47), `is`(44))
    }

    @Test
    fun testRowSearch() {
        assertThat(rowSearch.search("FBFBBFF"), `is`(44))
    }

    private val seatSearch = Day5.Lookup(3, 'L', 'R')

    @Test
    fun testSeatSearch() {
        assertThat(seatSearch.search("RLR", 0, 7), `is`(5))
    }

    @Test
    fun testPartOne() {
        assertThat(day.partOne(), `is`(357))
    }

//    @Test
//    fun testPartTwo() {
//        assertThat(day.partTwo(), `is`("Whatever is expected"))
//    }
}
