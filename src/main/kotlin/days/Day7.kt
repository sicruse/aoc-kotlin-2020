package days

class Day7 : Day(7) {

    private val targetBag = "shiny gold"

    private val bags: Map<String, List<Rule>> by lazy {
        inputList
            .map { description -> bagFromDescription(description) }
            .associateBy({ it.first }, { it.second })
    }

    data class Rule(val name: String, val quantity: Int)

    fun bagFromDescription(description: String) : Pair<String, List<Rule>> {
        val nameAndContents = description.split(" bags contain ")
        val name = nameAndContents.first()
        val contents = nameAndContents.last()
        return if (contents == "no other bags.") Pair(name, emptyList())
        else {
            val bags =
                contents.split(", ")
                    .map{ bag ->
                        val bagName = bag.drop(2).split(" bag").first()
                        val quantity = bag.first().toString().toInt()
                        Rule(bagName, quantity)
                    }
            return Pair(name, bags)
        }
    }

    fun findBagsWhichContain(name: String): List<Pair<String, List<Rule>>> {
        val bagsWhichDirectlyContain = bags.filter { bag -> bag.value.any{ content -> content.name == name } }.toList()
        val bagsWhichIndirectlyContain = bagsWhichDirectlyContain.flatMap { bag -> findBagsWhichContain(bag.first) }
        return (bagsWhichDirectlyContain + bagsWhichIndirectlyContain).distinct()
    }

    fun findBagsRequired(name: String, num: Int): List<Pair<String,Int>> {
        val target = bags[name]
        val additionalBags = target?.flatMap { containedBag ->
            findBagsRequired(containedBag.name, containedBag.quantity * num)
        }
        return when {
            additionalBags == null -> listOf(Pair(name, num))
            else -> listOf(Pair(name, num)) + additionalBags
        }
    }

    override fun partOne(): Any {
        val b = findBagsWhichContain(targetBag)
        return b.count()
    }

    override fun partTwo(): Any {
        val b = findBagsRequired(targetBag, 1)
        return b.fold(0) { acc, pair -> acc + pair.second } - 1
    }
}

