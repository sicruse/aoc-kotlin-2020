package days

import java.util.*

fun Int.exclusiveRangeTo(other: Int): IntRange = IntRange(this + 1, other - 1)

class Day18 : Day(18) {

    private val problems: List<Problem> by lazy {
        inputList.map { problemText -> Problem(problemText) }
    }

    class Problem(private val expression: String) {

        enum class PrecedenceRules {
            BYORDER {
                override fun regex():List<Regex> = listOf("(\\d+) ([+*]) (\\d+)".toRegex())
            },
            MULTIPLYFIRST {
                override fun regex(): List<Regex> = listOf("(\\d+) ([+]) (\\d+)".toRegex(), "(\\d+) ([*]) (\\d+)".toRegex())
            };

            abstract fun regex(): List<Regex>
        }

        fun evaluate(rules: PrecedenceRules, expression: String = this.expression): String {
            var adjustedExpression = ""

            // Find, evaluate & remove parentheses
            if (expression.contains('(')) {
                val stack = Stack<Int>()
                for ((i,c) in expression.withIndex()) {
                    when (c) {
                        '(' -> stack.push(i)
                        ')' -> {
                            val open = stack.pop()
                            if (stack.empty()) {
                                val subExpression = expression.substring(open.exclusiveRangeTo(i))
                                val head = expression.take(open)
                                val tail = if (i < expression.length) expression.drop(i + 1) else ""
                                adjustedExpression = evaluate (rules, head + evaluate(rules, subExpression) + tail )
                                break
                            }
                        }
                        else -> {}
                    }
                }
            } else adjustedExpression = expression

            // We now have adjusted the expression so that it contains only values & operations (no parentheses)
            // Evaluate the adjusted expression according to the required precedence rules
            for (match in rules.regex()) {
                var operation = match.find(adjustedExpression)
                while (operation != null) {
                    val result = operation.destructured
                        .let { (_p1, _op, _p2) ->
                            val p1 = _p1.toLong()
                            val p2 = _p2.toLong()
                            when (_op) {
                                "+" -> p1 + p2
                                "*" -> p1 * p2
                                else -> throw IllegalArgumentException("Bad input '$adjustedExpression'")
                            }
                        }
                    adjustedExpression = match.replaceFirst(adjustedExpression, result.toString())
                    operation = match.find(adjustedExpression)
                }
            }
            return adjustedExpression
        }
    }

    override fun partOne(): Any {
        return problems
            .map { it.evaluate(Problem.PrecedenceRules.BYORDER).toLong() }
            .sum()
    }

    override fun partTwo(): Any {
        return problems
            .map { it.evaluate(Problem.PrecedenceRules.MULTIPLYFIRST).toLong() }
            .sum()
    }
}
