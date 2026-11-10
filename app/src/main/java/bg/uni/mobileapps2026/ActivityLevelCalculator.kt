package bg.uni.mobileapps2026

/** Pure calculation logic for the activity-level screen. */
data class ActivityLevelResult(
    val position: Int,
    val name: String,
    val points: Int,
    val weeklyWorkoutMinutes: Int,
)

object ActivityLevelCalculator {
    private const val MAX_STEPS_PER_DAY = 50_000
    private const val MAX_WORKOUTS_PER_WEEK = 14
    private const val MAX_MINUTES_PER_WORKOUT = 300

    private const val VERY_ACTIVE_STEP_COUNT = 10_000
    private const val HIGH_STEP_COUNT = 8_000
    private const val MODERATE_STEP_COUNT = 6_000
    private const val LIGHT_STEP_COUNT = 3_000

    private const val VERY_ACTIVE_WEEKLY_MINUTES = 300
    private const val HIGH_WEEKLY_MINUTES = 150
    private const val MODERATE_WEEKLY_MINUTES = 60

    private const val VERY_ACTIVE_POINTS = 4
    private const val HIGH_ACTIVITY_POINTS = 3
    private const val MODERATE_ACTIVITY_POINTS = 2
    private const val LIGHT_ACTIVITY_POINTS = 1

    private const val FIRST_LEVEL_MAX_POINTS = 1
    private const val SECOND_LEVEL_MAX_POINTS = 3
    private const val THIRD_LEVEL_MAX_POINTS = 5
    private const val FOURTH_LEVEL_MAX_POINTS = 7

    private const val SEDENTARY_POSITION = 0
    private const val LIGHT_ACTIVITY_POSITION = 1
    private const val MODERATE_ACTIVITY_POSITION = 2
    private const val HIGH_ACTIVITY_POSITION = 3
    private const val VERY_HIGH_ACTIVITY_POSITION = 4

    private val activityNames =
        listOf(
            "Заседнал начин на живот",
            "Лека активност",
            "Умерена активност",
            "Висока активност",
            "Много висока активност",
        )

    fun areInputsValid(
        stepsPerDay: Int,
        workoutsPerWeek: Int,
        minutesPerWorkout: Int,
    ): Boolean =
        stepsPerDay in 0..MAX_STEPS_PER_DAY &&
            workoutsPerWeek in 0..MAX_WORKOUTS_PER_WEEK &&
            minutesPerWorkout in 0..MAX_MINUTES_PER_WORKOUT

    fun calculate(
        stepsPerDay: Int,
        workoutsPerWeek: Int,
        minutesPerWorkout: Int,
    ): ActivityLevelResult {
        require(areInputsValid(stepsPerDay, workoutsPerWeek, minutesPerWorkout)) {
            "Activity inputs are outside the supported range."
        }

        val weeklyWorkoutMinutes = workoutsPerWeek * minutesPerWorkout
        val points =
            calculateStepPoints(stepsPerDay) +
                calculateWorkoutPoints(weeklyWorkoutMinutes)
        val position = positionForPoints(points)

        return ActivityLevelResult(
            position = position,
            name = activityNames[position],
            points = points,
            weeklyWorkoutMinutes = weeklyWorkoutMinutes,
        )
    }

    private fun calculateStepPoints(stepsPerDay: Int): Int =
        when {
            stepsPerDay >= VERY_ACTIVE_STEP_COUNT -> VERY_ACTIVE_POINTS
            stepsPerDay >= HIGH_STEP_COUNT -> HIGH_ACTIVITY_POINTS
            stepsPerDay >= MODERATE_STEP_COUNT -> MODERATE_ACTIVITY_POINTS
            stepsPerDay >= LIGHT_STEP_COUNT -> LIGHT_ACTIVITY_POINTS
            else -> 0
        }

    private fun calculateWorkoutPoints(weeklyMinutes: Int): Int =
        when {
            weeklyMinutes >= VERY_ACTIVE_WEEKLY_MINUTES -> VERY_ACTIVE_POINTS
            weeklyMinutes >= HIGH_WEEKLY_MINUTES -> HIGH_ACTIVITY_POINTS
            weeklyMinutes >= MODERATE_WEEKLY_MINUTES -> MODERATE_ACTIVITY_POINTS
            weeklyMinutes > 0 -> LIGHT_ACTIVITY_POINTS
            else -> 0
        }

    private fun positionForPoints(points: Int): Int =
        when {
            points <= FIRST_LEVEL_MAX_POINTS -> SEDENTARY_POSITION
            points <= SECOND_LEVEL_MAX_POINTS -> LIGHT_ACTIVITY_POSITION
            points <= THIRD_LEVEL_MAX_POINTS -> MODERATE_ACTIVITY_POSITION
            points <= FOURTH_LEVEL_MAX_POINTS -> HIGH_ACTIVITY_POSITION
            else -> VERY_HIGH_ACTIVITY_POSITION
        }
}
