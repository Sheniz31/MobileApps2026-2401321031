package bg.uni.mobileapps2026

import kotlin.math.roundToInt

/** Recalculates stored calorie values after the user changes their weight in history. */
object CalculationUpdater {
    fun withUpdatedWeight(
        calculation: Calculation,
        newWeight: Double,
    ): Calculation? {
        if (!CalculationLimits.isValidWeight(newWeight)) {
            return null
        }

        val isFemale = calculation.gender == "Жена"
        val bmr =
            CalorieCalculator.calculateBmr(
                newWeight,
                calculation.height,
                calculation.age,
                isFemale,
            )

        val activityPosition =
            when (calculation.activityLevel) {
                "Заседнал начин на живот" -> 0
                "Лека активност" -> 1
                "Умерена активност" -> 2
                "Висока активност" -> 3
                else -> 4
            }

        val maintenance =
            CalorieCalculator.calculateMaintenanceCalories(
                bmr,
                activityPosition,
            )

        val goalPosition =
            when (calculation.goal) {
                "Сваляне на тегло" -> 0
                "Качване на тегло" -> 2
                else -> 1
            }

        val target =
            CalorieCalculator.calculateTargetCalories(
                maintenance,
                goalPosition,
            )

        return calculation.copy(
            weight = newWeight,
            bmr = bmr.roundToInt(),
            maintenanceCalories = maintenance.roundToInt(),
            targetCalories = target.roundToInt(),
        )
    }
}
