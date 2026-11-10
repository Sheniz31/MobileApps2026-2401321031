package bg.uni.mobileapps2026

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.math.roundToInt

class CalculationUpdaterTest {
    private fun sampleCalculation(
        gender: String = "Жена",
        activityLevel: String = "Умерена активност",
        goal: String = "Сваляне на тегло",
    ) = Calculation(
        id = 7,
        gender = gender,
        age = 25,
        height = 165.0,
        weight = 60.0,
        activityLevel = activityLevel,
        goal = goal,
        bmr = 1345,
        maintenanceCalories = 2085,
        targetCalories = 1772,
        date = 123456L,
    )

    @Test
    fun recalculatesFemaleValuesAfterWeightChange() {
        val result = CalculationUpdater.withUpdatedWeight(sampleCalculation(), 70.0)

        assertEquals(70.0, result?.weight ?: -1.0, 0.001)
        assertEquals(1445, result?.bmr)
        assertEquals(2240, result?.maintenanceCalories)
        assertEquals(1904, result?.targetCalories)
    }

    @Test
    fun recalculatesMaleValuesAfterWeightChange() {
        val result =
            CalculationUpdater.withUpdatedWeight(
                sampleCalculation(gender = "Мъж", goal = "Поддържане на тегло"),
                70.0,
            )

        assertEquals(1611, result?.bmr)
        assertEquals(2497, result?.maintenanceCalories)
        assertEquals(2497, result?.targetCalories)
    }

    @Test
    fun supportsEveryActivityLevelAndGoal() {
        val activityLevels =
            listOf(
                "Заседнал начин на живот",
                "Лека активност",
                "Умерена активност",
                "Висока активност",
                "Много висока активност",
            )
        val multipliers = listOf(1.2, 1.375, 1.55, 1.725, 1.9)
        val goals =
            listOf(
                "Сваляне на тегло" to 0.85,
                "Поддържане на тегло" to 1.0,
                "Качване на тегло" to 1.10,
            )
        val expectedBmr = 1445.25

        activityLevels.forEachIndexed { index, activityLevel ->
            goals.forEach { (goal, goalMultiplier) ->
                val result =
                    CalculationUpdater.withUpdatedWeight(
                        sampleCalculation(activityLevel = activityLevel, goal = goal),
                        70.0,
                    )
                val expectedMaintenance = expectedBmr * multipliers[index]
                val expectedTarget = expectedMaintenance * goalMultiplier

                assertEquals(expectedBmr.roundToInt(), result?.bmr)
                assertEquals(expectedMaintenance.roundToInt(), result?.maintenanceCalories)
                assertEquals(expectedTarget.roundToInt(), result?.targetCalories)
            }
        }
    }

    @Test
    fun keepsUnchangedCalculationFields() {
        val original = sampleCalculation()
        val result = CalculationUpdater.withUpdatedWeight(original, 70.0)

        assertEquals(original.id, result?.id)
        assertEquals(original.gender, result?.gender)
        assertEquals(original.age, result?.age)
        assertEquals(original.height, result?.height ?: -1.0, 0.001)
        assertEquals(original.activityLevel, result?.activityLevel)
        assertEquals(original.goal, result?.goal)
        assertEquals(original.date, result?.date)
        assertNotEquals(original.targetCalories, result?.targetCalories)
    }

    @Test
    fun rejectsInvalidWeights() {
        val calculation = sampleCalculation()

        assertNull(CalculationUpdater.withUpdatedWeight(calculation, 0.0))
        assertNull(CalculationUpdater.withUpdatedWeight(calculation, 350.1))
        assertNull(CalculationUpdater.withUpdatedWeight(calculation, Double.NaN))
        assertNull(CalculationUpdater.withUpdatedWeight(calculation, Double.POSITIVE_INFINITY))
    }

    @Test
    fun usesTheHighestActivityMultiplierForAnUnknownSavedLabel() {
        val result =
            CalculationUpdater.withUpdatedWeight(
                sampleCalculation(activityLevel = "Непознато ниво", goal = "Непозната цел"),
                70.0,
            )
        val expectedBmr = 1445.25
        val expectedMaintenance = expectedBmr * 1.9

        assertEquals(expectedMaintenance.roundToInt(), result?.maintenanceCalories)
        assertEquals(expectedMaintenance.roundToInt(), result?.targetCalories)
    }
}
