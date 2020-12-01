package days

import java.lang.IllegalArgumentException
import kotlin.jvm.Throws

class Day1 : Day(1) {

    private val target = 2020
    private val expenses: List<Int> by lazy { inputList.map { it.toInt() } }

    @Throws
    override fun partOne(): Any {
        for (expense in expenses) {
            if (expenses.contains(target - expense)) return expense * (target - expense)
        }
        throw IllegalArgumentException("No expense matches expected pattern")
    }

    @Throws
    override fun partTwo(): Any {
        for ((index, expense1) in expenses.withIndex()) {
            for (expense2 in expenses.drop(index)) {
                if (expenses.contains(target - expense1 - expense2)) return expense1 * expense2 * (target - expense1 - expense2)
            }
        }
        throw IllegalArgumentException("No expense matches expected pattern")
    }

}
