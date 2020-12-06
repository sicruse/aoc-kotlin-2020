package days

import days.Day

class Day6 : Day(6) {

    private val groups: List<List<String>> by lazy {
        inputString
            // split the input at blank lines to form record groups
            .split("\\n\\n".toRegex())
            .map { group ->
                // for every record text block gather the individual responses by splitting at newlines
                group.split("\\n".toRegex())
            }
    }

    override fun partOne(): Any {
        // return the sum "yes" answers by group
        return groups
                // join group of responses to form unique set of answers
            .map { groupOfResponses -> groupOfResponses.joinToString(separator = "").toSet() }
                // sum the unique answers per group
            .fold(0) { acc, set -> acc + set.size }
    }

    override fun partTwo(): Any {
        // return the sum of questions which *everyone* in a group answered yes
        return groups
                // for each group of responses convert each participants response to a set of unique answers
            .map { groupOfResponses -> groupOfResponses.map { response -> response.toSet() } }
                // for each set of responses in each group determine the set of questions which every participant responded yes
            .map { groupOfResponses -> groupOfResponses.fold(groupOfResponses.first()) { acc, set -> acc.intersect(set) } }
                // sum the sets of answers which *everyone* in the group answered yes
            .fold(0) { acc, set -> acc + set.size }
    }

}
