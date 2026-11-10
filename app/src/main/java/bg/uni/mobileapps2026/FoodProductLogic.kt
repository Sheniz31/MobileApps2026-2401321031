package bg.uni.mobileapps2026

import java.util.Locale

/** Product information needed by the food-search calculation, without Android or network types. */
data class FoodProductRecord(
    val name: String,
    val caloriesKcalPer100g: Double,
    val energyKjPer100g: Double,
)

object FoodProductLogic {
    private const val KILOJOULES_PER_KILOCALORIE = 4.184
    private const val GRAMS_PER_HUNDRED_GRAMS = 100.0

    fun translateSearchTerm(searchName: String): String =
        when (searchName.trim().lowercase(Locale.ROOT)) {
            "банан", "банани" -> "banana"
            "ябълка", "ябълки" -> "apple"
            "мляко" -> "milk"
            "хляб" -> "bread"
            "яйце", "яйца" -> "egg"
            "ориз" -> "rice"
            "картоф", "картофи" -> "potato"
            "домат", "домати" -> "tomato"
            "сирене" -> "cheese"
            else -> searchName.trim()
        }

    /** Returns the first product whose name contains every search word and has calorie data. */
    fun findMatchingProduct(
        searchName: String,
        products: List<FoodProductRecord>,
    ): Pair<String, Double>? {
        val searchWords = tokenize(translateSearchTerm(searchName))
        if (searchWords.isEmpty()) {
            return null
        }

        return products.firstNotNullOfOrNull { product ->
            val productName = product.name.trim()
            val productWords = tokenize(productName)
            val calories =
                caloriesPer100Grams(
                    product.caloriesKcalPer100g,
                    product.energyKjPer100g,
                )

            if (productName.isNotEmpty() &&
                searchWords.all(productWords::contains) &&
                calories != null
            ) {
                productName to calories
            } else {
                null
            }
        }
    }

    /** Uses kcal when available, otherwise converts kJ to kcal. Negative values are rejected. */
    fun caloriesPer100Grams(
        kcal: Double,
        kj: Double,
    ): Double? {
        val calories =
            when {
                kcal.isFinite() -> kcal
                kj.isFinite() -> kj / KILOJOULES_PER_KILOCALORIE
                else -> return null
            }

        return calories.takeIf { it >= 0.0 }
    }

    /** Calculates calories for a portion, returning null for invalid input. */
    fun calculateTotalCalories(
        caloriesPer100Grams: Double,
        grams: Double,
    ): Double? {
        if (!isValidCalorieValue(caloriesPer100Grams) || !isValidPortion(grams)) {
            return null
        }

        return caloriesPer100Grams * grams / GRAMS_PER_HUNDRED_GRAMS
    }

    private fun isValidCalorieValue(calories: Double): Boolean = calories.isFinite() && calories >= 0.0

    private fun isValidPortion(grams: Double): Boolean = grams.isFinite() && grams > 0.0

    private fun tokenize(value: String): List<String> =
        value
            .lowercase(Locale.ROOT)
            .split(Regex("[^\\p{L}\\p{N}]+"))
            .filter { it.isNotBlank() }
}
