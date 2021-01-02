package days

class Day21 : Day(21) {

    private val recipies: Map<Set<String>, Set<String>> by lazy {
        inputList.map { text ->
            val ingredients = text.split(" (").first().split(" ").toSet()
            val allergens = text.split("(contains ").last().dropLast(1).split(", ").toSet()
            ingredients to allergens
        }.toMap()
    }
    private val allIngredients: Set<String> by lazy { recipies.keys.flatten().toSet() }
    private val allAllergens: Set<String> by lazy { recipies.values.flatten().toSet() }
    private val safeIngredients: Set<String> by lazy { allIngredients subtract unsafeIngredients }
    private val unsafeIngredients by lazy {
        allAllergens.flatMap { allergen ->
            recipies
                .mapNotNull { (ingredients, allergens) ->
                    if (allergen in allergens) ingredients else null
                }
                .reduce { remainingIngredients, ingredients -> ingredients intersect remainingIngredients }
        }.toSet()
    }

    override fun partOne(): Any {
        val allRecipies = recipies.keys
        return allRecipies.sumOf { ingredients ->
            ingredients.count { ingredient -> ingredient in safeIngredients }
        }
    }

    override fun partTwo(): Any {
        // Build the map of Allergens to Ingredients
        val dangerousIngredientList: MutableMap<String, String> = mutableMapOf()

        // First construct a mutable map of allergens to possible ingredients
        val allergensToIngredients =
            allAllergens.map { allergen ->
                val possibleIngredients =
                    recipies
                        .mapNotNull{ (ingredients, allergens) ->
                            if (allergen in allergens) ingredients - safeIngredients else null
                        }
                        .reduce { remainingIngredients, ingredient -> remainingIngredients intersect ingredient }
                        .toMutableSet()
                allergen to possibleIngredients
            }.toMap().toMutableMap()

        // Iterate through the map of allergens to possible ingredients
        // identify allergens which match a single ingredient
        while (allergensToIngredients.isNotEmpty()) {
            val match = allergensToIngredients
                .mapNotNull { (allergen, ingredients) ->
                    if (ingredients.size == 1) allergen to ingredients.first() else null
                }
                .toMap()

            // remove the matched allergens from the search set
            val matchedAllergens = match.keys
            allergensToIngredients.keys.removeAll(matchedAllergens)

            // remove the matched ingredients from the possible ingredients search sets
            val matchedIngredients = match.values
            val possibleIngredients = allergensToIngredients.values
            possibleIngredients.forEach { ingredientSet -> ingredientSet.removeAll(matchedIngredients) }

            // update our result set with the match data
            dangerousIngredientList += match
        }

        return dangerousIngredientList
            .entries
            .sortedBy { (allergen, _) -> allergen }
            .joinToString(",") { (_, ingredient) -> ingredient }
    }
}
