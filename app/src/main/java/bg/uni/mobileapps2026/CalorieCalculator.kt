package bg.uni.mobileapps2026

object CalorieCalculator {
    private const val WEIGHT_FACTOR = 10.0
    private const val HEIGHT_FACTOR = 6.25
    private const val AGE_FACTOR = 5.0
    private const val FEMALE_ADJUSTMENT = -161.0
    private const val MALE_ADJUSTMENT = 5.0
    private const val WEIGHT_LOSS_MULTIPLIER = 0.85
    private const val WEIGHT_GAIN_MULTIPLIER = 1.10
    private const val MAINTENANCE_MULTIPLIER = 1.0

    private val activityMultipliers =
        listOf(
            1.2,
            1.375,
            1.55,
            1.725,
            1.9,
        )

    fun calculateBmr(
        weight: Double,
        height: Double,
        age: Int,
        isFemale: Boolean,
    ): Double {
        val genderAdjustment = if (isFemale) FEMALE_ADJUSTMENT else MALE_ADJUSTMENT

        return WEIGHT_FACTOR * weight +
            HEIGHT_FACTOR * height -
            AGE_FACTOR * age +
            genderAdjustment
    }

    fun calculateMaintenanceCalories(
        bmr: Double,
        activityPosition: Int,
    ): Double {
        val multiplier = activityMultipliers[activityPosition]
        return bmr * multiplier
    }

    fun calculateTargetCalories(
        maintenanceCalories: Double,
        goalPosition: Int,
    ): Double {
        val multiplier =
            when (goalPosition) {
                0 -> WEIGHT_LOSS_MULTIPLIER
                2 -> WEIGHT_GAIN_MULTIPLIER
                else -> MAINTENANCE_MULTIPLIER
            }

        return maintenanceCalories * multiplier
    }
}
