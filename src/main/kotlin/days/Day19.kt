package days

class Day19 : Day(19) {

    private val rules: Map<Int, Rule> by lazy {
        inputString
            .split("\\n\\n".toRegex())
            .first()
            .split("\\n".toRegex())
            .map{ ruleText ->
                val textComponents = ruleText.split(": ")
                val thing = textComponents.first().toInt() to Rule.fromText(textComponents.last())
                thing
            }.toMap()
    }

    private val messages: List<String> by lazy {
        inputString
            .split("\\n\\n".toRegex())
            .last()
            .split("\\n".toRegex())
    }

    sealed class Rule {
        class ORFOUR(private val p1: Int, private val p2: Int, private val p3: Int, private val p4: Int): Rule() {
            companion object {
                val regex = "(\\d+) (\\d+) \\| (\\d+) (\\d+)".toRegex()

                fun fromText(text: String): ORFOUR {
                    regex.matchEntire(text)
                        ?.destructured
                        ?.let { (_p1, _p2, _p3, _p4) ->
                            return ORFOUR(_p1.toInt(), _p2.toInt(), _p3.toInt(), _p4.toInt())
                        }
                        ?: throw IllegalArgumentException("Bad input '$text'")
                }
            }

            override fun match(rules: Map<Int, Rule>): String = "(${rules[p1]?.match(rules)}${rules[p2]?.match(rules)}|${rules[p3]?.match(rules)}${rules[p4]?.match(rules)})"
        }
        class ORTWO(private val p1: Int, private val p2: Int): Rule() {
            companion object {
                val regex = "(\\d+) \\| (\\d+)".toRegex()

                fun fromText(text: String): ORTWO {
                    regex.matchEntire(text)
                        ?.destructured
                        ?.let { (_p1, _p2) ->
                            return ORTWO(_p1.toInt(), _p2.toInt())
                        }
                        ?: throw IllegalArgumentException("Bad input '$text'")
                }
            }

            override fun match(rules: Map<Int, Rule>): String = "(${rules[p1]?.match(rules)}|${rules[p2]?.match(rules)})"
        }
        class THREE(private val p1: Int, private val p2: Int, private val p3: Int): Rule() {
            companion object {
                val regex = "(\\d+) (\\d+) (\\d+)".toRegex()

                fun fromText(text: String): THREE {
                    regex.matchEntire(text)
                        ?.destructured
                        ?.let { (_p1, _p2, _p3) ->
                            return THREE(_p1.toInt(), _p2.toInt(), _p3.toInt())
                        }
                        ?: throw IllegalArgumentException("Bad input '$text'")
                }
            }

            override fun match(rules: Map<Int, Rule>): String = "${rules[p1]?.match(rules)}${rules[p2]?.match(rules)}${rules[p3]?.match(rules)}"
        }
        class TWO(private val p1: Int, private val p2: Int): Rule() {
            companion object {
                val regex = "(\\d+) (\\d+)".toRegex()

                fun fromText(text: String): TWO {
                    regex.matchEntire(text)
                        ?.destructured
                        ?.let { (_p1, _p2) ->
                            return TWO(_p1.toInt(), _p2.toInt())
                        }
                        ?: throw IllegalArgumentException("Bad input '$text'")
                }
            }

            override fun match(rules: Map<Int, Rule>): String = "${rules[p1]?.match(rules)}${rules[p2]?.match(rules)}"
        }
        class ONE(private val p1: Int): Rule() {
            companion object {
                val regex = "(\\d+)".toRegex()

                fun fromText(text: String): ONE {
                    regex.matchEntire(text)
                        ?.destructured
                        ?.let { (_p1) ->
                            return ONE(_p1.toInt())
                        }
                        ?: throw IllegalArgumentException("Bad input '$text'")
                }
            }

            override fun match(rules: Map<Int, Rule>): String = "${rules[p1]?.match(rules)}"

        }
        class CHAR(private val p1: String): Rule() {
            companion object {
                val regex = "\"([ab])\"".toRegex()

                fun fromText(text: String): CHAR {
                    regex.matchEntire(text)
                        ?.destructured
                        ?.let { (_p1) ->
                            return CHAR(_p1)
                        }
                        ?: throw IllegalArgumentException("Bad input '$text'")
                }
            }

            override fun match(rules: Map<Int, Rule>): String = p1
        }

        abstract fun match(rules: Map<Int, Rule>): String

        companion object {
            @Throws
            fun fromText(text: String): Rule {
                return when {
                    ORFOUR.regex.matchEntire(text)   != null -> { ORFOUR.fromText(text) }
                    ORTWO.regex.matchEntire(text)    != null -> { ORTWO.fromText(text) }
                    THREE.regex.matchEntire(text)    != null -> { THREE.fromText(text) }
                    TWO.regex.matchEntire(text)      != null -> { TWO.fromText(text) }
                    ONE.regex.matchEntire(text)      != null -> { ONE.fromText(text) }
                    CHAR.regex.matchEntire(text)     != null -> { CHAR.fromText(text) }
                    else -> { throw IllegalArgumentException("Bad input '$text'") }
                }
            }
        }
    }

    override fun partOne(): Any {
        val pattern = rules[0]?.match(rules)?.toRegex()
        return messages.count { message -> pattern?.matchEntire(message) != null }
    }

    override fun partTwo(): Any {
        // Cannot just inject changes to pattern 8 & 11 as will create infinite loop!
        // 8: 42 | 42 8
        // 11: 42 31 | 42 11 31
        // Instead craft a "big enough pattern" to cover max message size as if we had changed pattern 8 & 11.
        // by using patterns 31 & 42
        val maxMessageSize = messages.maxOfOrNull { it.length } ?: 0
        val pattern31 = rules[31]?.match(rules)
        val pattern42 = rules[42]?.match(rules)
        val pattern = (1 until maxMessageSize/2)
            .joinToString("|") { i ->
                "(?:$pattern42){${i + 1},}(?:$pattern31){$i}"
            }.toRegex()
        return messages.count(pattern::matches)
    }
}
