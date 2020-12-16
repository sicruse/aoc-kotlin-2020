package days

import days.Day
import kotlin.math.absoluteValue

class Day16 : Day(16) {

    private val rules: List<Rule> by lazy {
        inputString
            .split("\\n\\n".toRegex())
            .first()
            .split("\\n".toRegex())
            .map{ ruleText -> Rule.deserialize(ruleText) }
    }

    private val myTicket: List<Int> by lazy {
        inputString
            .split("your ticket:\\n".toRegex())
            .last()
            .split("\\n".toRegex())
            .first()
            .split(",")
            .map { it.toInt() }
    }

    private val tickets: List<List<Int>> by lazy {
        inputString
            .split("nearby tickets:\\n".toRegex())
            .last()
            .split("\\n".toRegex())
            .map{ ticket ->
                ticket.split(",")
                .map { it.toInt() }
            }
    }

    private val validTickets = tickets.filter { ticket ->
        ticket.all { field ->
            rules.any { rule -> rule.isValid(field) }
        }
    }

    private val validTicketFields: Sequence<List<Int>> = sequence {
        // We will assume that myTicket has the same number of fields as every other valid ticket
        for ((index, _) in myTicket.withIndex()) {
            val values = validTickets.map { ticket -> ticket[index] }
            yield(values)
        }
    }

    private val rulesInOrder: List<Rule> by lazy {
        // Map Rules to possible field columns
        val ruleMap: Map<Rule, MutableList<Int>> = rules.map { rule ->
            rule to validTicketFields.mapIndexed { index, fields ->
                        if (fields.all{ rule.isValid(it) }) index else null
                    }
                    .filterNotNull()
                    .toMutableList()

        }.toMap()

        // Rules may be valid for more than one field therefore we need to rationalize the mapping
        // While there are multiple field mappings per rule
        while (ruleMap.values.any {it.count() > 1})
            // For each rule that maps to one and ONLY one field
            ruleMap.filter { it.value.count() == 1 }
                .forEach { map ->
                    // visit each rule that has multiple field mappings
                    ruleMap.filter { it.value.count() > 1 && it.value.contains( map.value.first() ) }
                        // and remove reference to the single field
                        .forEach{ ruleMapping -> ruleMapping.value.remove( map.value.first() ) } }

        // Sort the rules according to field sequence
        ruleMap.entries
            .sortedBy { ruleMapping -> ruleMapping.value.first() }
            .map{ ruleMapping -> ruleMapping.key }
    }

    class Rule(val name: String, val lower: IntRange, val upper: IntRange) {

        fun isValid(value: Int): Boolean = value in lower || value in upper

        companion object {
            val destructuredRegex = "([\\s\\w]+): (\\d+)-(\\d+) or (\\d+)-(\\d+)".toRegex()

            fun deserialize(text: String): Rule {
                destructuredRegex.matchEntire(text)
                    ?.destructured
                    ?.let { (_name, _lowerFrom, _lowerTo, _upperFrom, _upperTo) ->
                        return Rule(_name,
                            _lowerFrom.toInt() .. _lowerTo.toInt(),
                            _upperFrom.toInt() .. _upperTo.toInt())
                    }
                    ?: throw IllegalArgumentException("Bad input '$text'")
            }
        }
    }

    override fun partOne(): Any {
        // for all tickets
        val badValues = tickets.flatMap { ticket ->
            // check each field
            ticket.filterNot { field ->
                // and roll-up any values that don't meet ALL of the rules
                rules.any { rule -> rule.isValid(field) }
            }
        }

        return badValues.sum()
    }

    override fun partTwo(): Any {
        // for all rules that start with "departure" figure out the index of field it applies too
        return rulesInOrder.withIndex().mapNotNull { (i, rule) ->
            if (rule.name.startsWith("departure")) i else null
        }
            // grab the field values in my own ticket
            .map{ fieldIndex -> myTicket[fieldIndex] }
            // calculate the product (don't forget to coerce type to Long [initial value as 1L]
            // otherwise result will be Integer based and therefore too low!)
            .fold(1L) { acc, v -> acc * v }
    }
}
