package days

class Day7 : Day(7) {

    // The target of our investigation is...
    private val targetBag = "shiny gold"

    // create a "Bag name" lookup map for convenience
    private val bags: Map<String, List<Rule>> by lazy {
        inputList
            .map { description -> bagFromDescription(description) }
            .associateBy({ it.first }, { it.second })
    }

    // simple representation of "sub-bag" rules
    data class Rule(val name: String, val quantity: Int)

    fun bagFromDescription(description: String) : Pair<String, List<Rule>> {
        val nameAndContents = description.split(" bags contain ")
        val name = nameAndContents.first()
        val contents = nameAndContents.last()
        return when {
            // when the contents contain the phrase "no other bags" there is no further rule to extract
            contents == "no other bags." -> Pair(name, emptyList())
            // otherwise, proceed to extract each "sub-bag" rule
            else -> Pair(name,
                    contents
                    // each bag rule is separated with a comma
                    .split(", ")
                    .map { bag ->
                        // Bag quantity is the first char
                        val quantity = bag.first().toString().toInt()
                        // Bag name comes after first two chars and before the word "bag"
                        val bagName = bag.drop(2).split(" bag").first()
                        Rule(bagName, quantity)
                    })
        }
    }

    // Find the set of Bags which contain the target bag
    fun findBagsWhichContain(target: String): Set<String> {
        val bagsWhichDirectlyContain = bags.filter { bag -> bag.value.any{ content -> content.name == target } }.keys.toSet()
        val bagsWhichIndirectlyContain = bagsWhichDirectlyContain.flatMap { bagName -> findBagsWhichContain( bagName ) }.toSet()
        return bagsWhichDirectlyContain.union(bagsWhichIndirectlyContain)
    }

    // Find the set of bags which must be packed in the target bag
    fun findBagsRequired(target: String, num: Int): List<Pair<String,Int>> {
        val rules = bags[target]
        val additionalBags = rules?.flatMap { bag ->
            findBagsRequired(bag.name, bag.quantity * num)
        }
        return when {
            additionalBags == null -> listOf(Pair(target, num))
            else -> listOf(Pair(target, num)) + additionalBags
        }
    }

    override fun partOne(): Any {
        return findBagsWhichContain(targetBag)
            .count()
    }

    override fun partTwo(): Any {
        return findBagsRequired(targetBag, 1)
            .fold(0) { acc, pair -> acc + pair.second } - 1
    }
}

