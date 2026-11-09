
package bg.uni.mobileapps2026

object CalorieCalculator {

    private val activityMultipliers = listOf(
        1.2,
        1.375,
        1.55,
        1.725,
        1.9
    )

    fun calculateBmr(
        weight: Double,
        height: Double,
        age: Int,
        isFemale: Boolean
    ): Double {

        val genderAdjustment = if (isFemale) -161.0 else 5.0

        return 10 * weight +
                6.25 * height -
                5 * age +
                genderAdjustment
    }

    fun calculateMaintenanceCalories(
        bmr: Double,
        activityPosition: Int
    ): Double {

        val multiplier = activityMultipliers[activityPosition]

        return bmr * multiplier
    }

    fun calculateTargetCalories(
        maintenanceCalories: Double,
        goalPosition: Int
    ): Double {

        val multiplier = when (goalPosition) {
            0 -> 0.85
            2 -> 1.10
            else -> 1.0
        }

        return maintenanceCalories * multiplier
    }
}
