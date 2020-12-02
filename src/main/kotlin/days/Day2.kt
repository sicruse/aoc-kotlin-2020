package days

class Day2 : Day(2) {

    private val passwords: List<Password> by lazy { inputList.map { Password.deserialize(it) } }

    class Password(
        val parameter1: Int,
        val parameter2: Int,
        val element: Char,
        val password: String
    ) {
        companion object {
            private val destructuredRegex = "([0-9]+)-([0-9]+)\\s([a-z]):\\s([a-z]+)".toRegex()
            fun deserialize(text: String): Password {
                destructuredRegex.matchEntire(text)
                    ?.destructured
                    ?.let { (_parameter1, _parameter2, _element, _password) ->
                        return Password(_parameter1.toInt(), _parameter2.toInt(), _element[0], _password)
                    }
                    ?: throw IllegalArgumentException("Bad input '$text'")
            }
        }
    }

    override fun partOne(): Any {
        return passwords.count { pw ->
            val occurrences = pw.password.count { it == pw.element }
            pw.parameter1 <= occurrences && occurrences <= pw.parameter2
        }
    }

    override fun partTwo(): Any {
        return passwords.count { pw ->
            val element1 = pw.password[pw.parameter1 - 1]
            val element2 = pw.password[pw.parameter2 - 1]
            when {
                (element1 == element2) -> false
                (element1 == pw.element || element2 == pw.element) -> true
                else -> false
            }
        }
    }
}
