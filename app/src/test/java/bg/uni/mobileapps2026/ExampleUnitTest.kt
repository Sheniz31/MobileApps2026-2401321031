
package bg.uni.mobileapps2026

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun calculateBmrForWoman() {
        val result =
            CalorieCalculator.calculateBmr(
                60.0,
                165.0,
                25,
                true,
            )

        assertEquals(1345.25, result, 0.01)
    }

    @Test
    fun calculateBmrForMan() {
        val result =
            CalorieCalculator.calculateBmr(
                60.0,
                165.0,
                25,
                false,
            )

        assertEquals(1511.25, result, 0.01)
    }

    @Test
    fun calculateMaintenanceCalories() {
        val result =
            CalorieCalculator.calculateMaintenanceCalories(
                1345.25,
                2,
            )

        assertEquals(2085.14, result, 0.01)
    }

    @Test
    fun calculateCaloriesForWeightLoss() {
        val result =
            CalorieCalculator.calculateTargetCalories(
                2000.0,
                0,
            )

        assertEquals(1700.0, result, 0.01)
    }

    @Test
    fun calculateCaloriesForWeightGain() {
        val result =
            CalorieCalculator.calculateTargetCalories(
                2000.0,
                2,
            )

        assertEquals(2200.0, result, 0.01)
    }

    @Test
    fun calculateCaloriesForMaintenanceGoal() {
        val result =
            CalorieCalculator.calculateTargetCalories(
                2000.0,
                1,
            )

        assertEquals(2000.0, result, 0.01)
    }

    @Test
    fun calculateMaintenanceCaloriesForEveryActivityLevel() {
        val multipliers = listOf(1.2, 1.375, 1.55, 1.725, 1.9)

        multipliers.forEachIndexed { position, multiplier ->
            val result =
                CalorieCalculator.calculateMaintenanceCalories(
                    2000.0,
                    position,
                )
            assertEquals(2000.0 * multiplier, result, 0.001)
        }
    }

    @Test
    fun unknownGoalPositionKeepsMaintenanceCalories() {
        assertEquals(
            2000.0,
            CalorieCalculator.calculateTargetCalories(2000.0, -1),
            0.001,
        )
        assertEquals(
            2000.0,
            CalorieCalculator.calculateTargetCalories(2000.0, 3),
            0.001,
        )
    }
}
