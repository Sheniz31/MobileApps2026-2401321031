package bg.uni.mobileapps2026

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FoodProductLogicTest {
    @Test
    fun translatesSupportedBulgarianProductNames() {
        assertEquals("banana", FoodProductLogic.translateSearchTerm("банан"))
        assertEquals("banana", FoodProductLogic.translateSearchTerm(" БАНАНИ "))
        assertEquals("apple", FoodProductLogic.translateSearchTerm("ябълки"))
        assertEquals("milk", FoodProductLogic.translateSearchTerm("мляко"))
        assertEquals("bread", FoodProductLogic.translateSearchTerm("хляб"))
        assertEquals("egg", FoodProductLogic.translateSearchTerm("яйца"))
        assertEquals("rice", FoodProductLogic.translateSearchTerm("ориз"))
        assertEquals("potato", FoodProductLogic.translateSearchTerm("картофи"))
        assertEquals("tomato", FoodProductLogic.translateSearchTerm("домати"))
        assertEquals("cheese", FoodProductLogic.translateSearchTerm("сирене"))
    }

    @Test
    fun leavesOtherSearchTermsTrimmed() {
        assertEquals("oat drink", FoodProductLogic.translateSearchTerm(" oat drink "))
    }

    @Test
    fun findsProductWhenEverySearchWordIsPresent() {
        val products =
            listOf(
                FoodProductRecord("Organic banana chips", 520.0, Double.NaN),
                FoodProductRecord("Fresh banana", 89.0, Double.NaN),
            )

        val result = FoodProductLogic.findMatchingProduct("banana", products)

        assertEquals("Organic banana chips", result?.first)
        assertEquals(520.0, result?.second ?: 0.0, 0.001)
    }

    @Test
    fun skipsBlankAndNonMatchingProductNames() {
        val products =
            listOf(
                FoodProductRecord("   ", 100.0, Double.NaN),
                FoodProductRecord("Green apple", 52.0, Double.NaN),
            )

        assertNull(FoodProductLogic.findMatchingProduct("banana", products))
    }

    @Test
    fun requiresEverySearchWordToMatch() {
        val products =
            listOf(
                FoodProductRecord("Banana drink", 45.0, Double.NaN),
            )

        assertNull(FoodProductLogic.findMatchingProduct("banana milk", products))
    }

    @Test
    fun returnsNullForAnEmptySearch() {
        assertNull(FoodProductLogic.findMatchingProduct("   ", emptyList()))
    }

    @Test
    fun prefersKcalWhenBothEnergyValuesArePresent() {
        assertEquals(
            95.0,
            FoodProductLogic.caloriesPer100Grams(95.0, 500.0) ?: -1.0,
            0.001,
        )
    }

    @Test
    fun convertsKjWhenKcalIsUnavailable() {
        assertEquals(
            100.0,
            FoodProductLogic.caloriesPer100Grams(Double.NaN, 418.4) ?: -1.0,
            0.001,
        )
    }

    @Test
    fun rejectsMissingOrNegativeEnergyValues() {
        assertNull(FoodProductLogic.caloriesPer100Grams(Double.NaN, Double.NaN))
        assertNull(FoodProductLogic.caloriesPer100Grams(-5.0, 418.4))
        assertNull(FoodProductLogic.caloriesPer100Grams(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY))
    }

    @Test
    fun findsTheNextMatchingProductWhenTheFirstHasNoEnergyData() {
        val products =
            listOf(
                FoodProductRecord("Banana", Double.NaN, Double.NaN),
                FoodProductRecord("Banana ripe", 88.0, Double.NaN),
            )

        val result = FoodProductLogic.findMatchingProduct("банан", products)

        assertEquals("Banana ripe", result?.first)
        assertEquals(88.0, result?.second ?: 0.0, 0.001)
    }

    @Test
    fun calculatesCaloriesForTheEnteredPortion() {
        assertEquals(
            133.5,
            FoodProductLogic.calculateTotalCalories(89.0, 150.0) ?: -1.0,
            0.001,
        )
    }

    @Test
    fun rejectsInvalidPortionOrCalorieValues() {
        assertNull(FoodProductLogic.calculateTotalCalories(89.0, 0.0))
        assertNull(FoodProductLogic.calculateTotalCalories(89.0, -10.0))
        assertNull(FoodProductLogic.calculateTotalCalories(Double.NaN, 100.0))
        assertNull(FoodProductLogic.calculateTotalCalories(-1.0, 100.0))
        assertNull(FoodProductLogic.calculateTotalCalories(89.0, Double.POSITIVE_INFINITY))
    }
}
